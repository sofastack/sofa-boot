/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.runtime.service.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import com.alipay.sofa.boot.error.ErrorCode;
import com.alipay.sofa.runtime.SofaRuntimeProperties;
import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.api.component.Property;
import com.alipay.sofa.runtime.invoke.DynamicJvmServiceProxyFinder;
import com.alipay.sofa.runtime.log.SofaLogger;
import com.alipay.sofa.runtime.model.ComponentType;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.service.helper.ReferenceRegisterHelper;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.binding.BindingAdapter;
import com.alipay.sofa.runtime.spi.binding.BindingAdapterFactory;
import com.alipay.sofa.runtime.spi.component.AbstractComponent;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.DefaultImplementation;
import com.alipay.sofa.runtime.spi.component.Implementation;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.health.HealthResult;
import com.alipay.sofa.runtime.spi.util.ComponentNameFactory;

/**
 * reference component
 *
 * @author xuanbei 18/3/1
 */
@SuppressWarnings("unchecked")
public class ReferenceComponent extends AbstractComponent {
    public static final ComponentType REFERENCE_COMPONENT_TYPE = new ComponentType("reference");

    private BindingAdapterFactory     bindingAdapterFactory;
    private Reference                 reference;
    private CountDownLatch            latch                    = new CountDownLatch(1);

    public ReferenceComponent(Reference reference, Implementation implementation,
                              BindingAdapterFactory bindingAdapterFactory,
                              SofaRuntimeContext sofaRuntimeContext) {
        this.componentName = ComponentNameFactory.createComponentName(
            REFERENCE_COMPONENT_TYPE,
            reference.getInterfaceType(),
            reference.getUniqueId() + "#"
                    + ReferenceRegisterHelper.generateBindingHashCode(reference));
        this.reference = reference;
        this.implementation = implementation;
        this.sofaRuntimeContext = sofaRuntimeContext;
        this.bindingAdapterFactory = bindingAdapterFactory;
    }

    @Override
    public ComponentType getType() {
        return REFERENCE_COMPONENT_TYPE;
    }

    @Override
    public Map<String, Property> getProperties() {
        return null;
    }

    @Override
    public HealthResult isHealthy() {
        HealthResult result = new HealthResult(componentName.getRawName());
        List<HealthResult> bindingHealth = new ArrayList<>();

        JvmBinding jvmBinding = null;
        HealthResult jvmBindingHealthResult = null;
        if (reference.hasBinding()) {
            for (Binding binding : reference.getBindings()) {
                bindingHealth.add(binding.healthCheck());
                if (JvmBinding.JVM_BINDING_TYPE.equals(binding.getBindingType())) {
                    jvmBinding = (JvmBinding) binding;
                    jvmBindingHealthResult = bindingHealth.get(bindingHealth.size() - 1);
                }
            }
        }

        // check reference has a corresponding service
        if (!SofaRuntimeProperties.isSkipJvmReferenceHealthCheck(sofaRuntimeContext)
            && jvmBinding != null) {
            Object serviceTarget = getServiceTarget();
            if (serviceTarget == null && !jvmBinding.hasBackupProxy()) {
                jvmBindingHealthResult.setHealthy(false);
                jvmBindingHealthResult.setHealthReport("can not find corresponding jvm service");
            }
        }

        List<HealthResult> failedBindingHealth = new ArrayList<>();

        for (HealthResult healthResult : bindingHealth) {
            if (healthResult != null && !healthResult.isHealthy()) {
                failedBindingHealth.add(healthResult);
            }
        }

        result.setHealthy(failedBindingHealth.size() == 0);

        String report = aggregateBindingHealth(reference.getBindings());
        if (e != null) {
            report += " [" + e.getMessage() + "]";
            result.setHealthy(false);
        }

        result.setHealthReport(report);
        return result;
    }

    @Override
    public void activate() throws ServiceRuntimeException {
        if (reference.hasBinding()) {
            Binding candidate = null;
            Set<Binding> bindings = reference.getBindings();
            if (bindings.size() == 1) {
                candidate = bindings.iterator().next();
            } else if (bindings.size() > 1) {
                Object backupProxy = null;
                for (Binding binding : bindings) {
                    if (JvmBinding.JVM_BINDING_TYPE.getType().equals(binding.getName())) {
                        candidate = binding;
                    } else {
                        // Under normal RPC reference (local-first/jvm-first is not set to false) binding,
                        // backup proxy is the RPC proxy, which will be invoked if Jvm service is not found
                        backupProxy = createProxy(reference, binding);
                    }
                }
                if (candidate != null) {
                    ((JvmBinding) candidate).setBackupProxy(backupProxy);
                }
            }

            Object proxy = null;
            if (candidate != null) {
                proxy = createProxy(reference, candidate);
            }

            this.implementation = new DefaultImplementation();
            implementation.setTarget(proxy);
        }

        super.activate();
        latch.countDown();
    }

    @Override
    public void unregister() throws ServiceRuntimeException {
        super.unregister();
        if (reference.hasBinding()) {
            for (Binding binding : reference.getBindings()) {
                BindingAdapter<Binding> bindingAdapter = this.bindingAdapterFactory
                    .getBindingAdapter(binding.getBindingType());
                if (bindingAdapter == null) {
                    throw new ServiceRuntimeException(ErrorCode.convert("01-00100",
                        binding.getBindingType(), reference));
                }
                SofaLogger.info(" >>Un-in Binding [{}] Begins - {}.", binding.getBindingType(),
                    reference);
                try {
                    bindingAdapter.unInBinding(reference, binding, sofaRuntimeContext);
                } finally {
                    SofaLogger.info(" >>Un-in Binding [{}] Ends - {}.", binding.getBindingType(),
                        reference);
                }
            }
        }
    }

    @Override
    public void exception(Exception e) throws ServiceRuntimeException {
        super.exception(e);
        latch.countDown();
    }

    @Override
    public Implementation getImplementation() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new ServiceRuntimeException(ErrorCode.convert("01-00101"), e);
        }

        if (e != null) {
            throw new ServiceRuntimeException(ErrorCode.convert("01-00102"), e);
        }

        return super.getImplementation();
    }

    public Reference getReference() {
        return reference;
    }

    private Object createProxy(Reference reference, Binding binding) {
        BindingAdapter<Binding> bindingAdapter = bindingAdapterFactory.getBindingAdapter(binding
            .getBindingType());
        if (bindingAdapter == null) {
            throw new ServiceRuntimeException(ErrorCode.convert("01-00100",
                binding.getBindingType(), reference));
        }
        SofaLogger.info(" >>In Binding [{}] Begins - {}.", binding.getBindingType(), reference);
        Object proxy;
        try {
            proxy = bindingAdapter.inBinding(reference, binding, sofaRuntimeContext);
        } finally {
            SofaLogger.info(" >>In Binding [{}] Ends - {}.", binding.getBindingType(), reference);
        }
        return proxy;
    }

    /**
     * get service target
     *
     * @return service target
     */
    private Object getServiceTarget() {
        Object serviceTarget = null;
        ComponentName componentName = ComponentNameFactory.createComponentName(
            ServiceComponent.SERVICE_COMPONENT_TYPE, reference.getInterfaceType(),
            reference.getUniqueId());
        ComponentInfo componentInfo = sofaRuntimeContext.getComponentManager().getComponentInfo(
            componentName);

        if (componentInfo != null) {
            serviceTarget = componentInfo.getImplementation().getTarget();
        }

        if (serviceTarget == null) {
            serviceTarget = DynamicJvmServiceProxyFinder.getDynamicJvmServiceProxyFinder()
                .findServiceProxy(sofaRuntimeContext.getAppClassLoader(), reference);
        }
        return serviceTarget;
    }
}
