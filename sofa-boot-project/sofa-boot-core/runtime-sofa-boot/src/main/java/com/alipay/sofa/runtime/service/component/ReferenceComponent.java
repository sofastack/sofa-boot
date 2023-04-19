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

import com.alipay.sofa.boot.log.ErrorCode;
import com.alipay.sofa.boot.log.SofaBootLoggerFactory;
import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.component.Property;
import com.alipay.sofa.runtime.model.ComponentType;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.service.binding.JvmServiceSupport;
import com.alipay.sofa.runtime.service.helper.ReferenceRegisterHelper;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.binding.BindingAdapter;
import com.alipay.sofa.runtime.spi.binding.BindingAdapterFactory;
import com.alipay.sofa.runtime.spi.component.AbstractComponent;
import com.alipay.sofa.runtime.spi.component.ComponentDefinitionInfo;
import com.alipay.sofa.runtime.spi.component.ComponentNameFactory;
import com.alipay.sofa.runtime.spi.component.DefaultImplementation;
import com.alipay.sofa.runtime.spi.component.Implementation;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.health.HealthResult;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * Reference component.
 *
 * @author xuanbei 18/3/1
 */
@SuppressWarnings("unchecked")
public class ReferenceComponent extends AbstractComponent {

    private static final Logger         LOGGER                   = SofaBootLoggerFactory
                                                                     .getLogger(ReferenceComponent.class);

    public static final ComponentType   REFERENCE_COMPONENT_TYPE = new ComponentType("reference");

    private final BindingAdapterFactory bindingAdapterFactory;

    private final Reference             reference;

    private final CountDownLatch        latch                    = new CountDownLatch(1);

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
        return properties;
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
        if (jvmBinding != null && reference.isRequired() && !isSkipReferenceHealthCheck(sofaRuntimeContext.getProperties())) {
            Object serviceTarget = getServiceTarget();
            if (serviceTarget == null && !jvmBinding.hasBackupProxy()) {
                jvmBindingHealthResult.setHealthy(false);
                StringBuilder healthReport = new StringBuilder(64);
                healthReport.append("can not find corresponding jvm service");
                if (sofaRuntimeContext.getProperties().isReferenceHealthCheckMoreDetailEnable()) {
                    Property sourceProperty = getProperties().get(ComponentDefinitionInfo.SOURCE);
                    if (sourceProperty != null && sourceProperty.getValue() != null
                        && sourceProperty.getValue() instanceof ComponentDefinitionInfo definitionInfo) {
                        healthReport.append(".");
                        healthReport
                            .append(String
                                .format(
                                    "Which first declared through:%s beanId:%s,beanClassName:%s,location:%s",
                                    definitionInfo.getInterfaceMode(),
                                    definitionInfo.info(ComponentDefinitionInfo.BEAN_ID),
                                    definitionInfo.info(ComponentDefinitionInfo.BEAN_CLASS_NAME),
                                    definitionInfo.info(ComponentDefinitionInfo.LOCATION)));
                    }
                }
                jvmBindingHealthResult.setHealthReport(healthReport.toString());
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
                LOGGER.info(" >>Un-in Binding [{}] Begins - {}.", binding.getBindingType(),
                    reference);
                try {
                    bindingAdapter.unInBinding(reference, binding, sofaRuntimeContext);
                } finally {
                    LOGGER.info(" >>Un-in Binding [{}] Ends - {}.", binding.getBindingType(),
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
        LOGGER.info(" >>In Binding [{}] Begins - {}.", binding.getBindingType(), reference);
        Object proxy;
        try {
            proxy = bindingAdapter.inBinding(reference, binding, sofaRuntimeContext);
        } finally {
            LOGGER.info(" >>In Binding [{}] Ends - {}.", binding.getBindingType(), reference);
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

        ServiceComponent serviceComponent = JvmServiceSupport.foundServiceComponent(
            sofaRuntimeContext.getComponentManager(), reference);

        if (serviceComponent != null) {
            serviceTarget = serviceComponent.getImplementation().getTarget();
        }

        if (serviceTarget == null) {
            serviceTarget = sofaRuntimeContext.getServiceProxyManager().getDynamicServiceProxy(
                reference, sofaRuntimeContext.getAppClassLoader());
        }
        return serviceTarget;
    }

    private boolean isSkipReferenceHealthCheck(SofaRuntimeContext.Properties properties) {
        if (properties.isSkipJvmReferenceHealthCheck()) {
            return true;
        }
        // skip check reference for the specified interface with unique id
        List<String> skipCheckList = properties.getSkipJvmReferenceHealthCheckList();
        String interfaceType = reference.getInterfaceType().getName();
        String uniqueId = reference.getUniqueId();
        if (StringUtils.hasText(uniqueId)) {
            interfaceType = interfaceType + ":" + uniqueId;
        }
        return skipCheckList.contains(interfaceType);
    }
}
