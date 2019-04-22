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

import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.component.Property;
import com.alipay.sofa.runtime.model.ComponentType;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.binding.BindingAdapter;
import com.alipay.sofa.runtime.spi.binding.BindingAdapterFactory;
import com.alipay.sofa.runtime.spi.component.AbstractComponent;
import com.alipay.sofa.runtime.spi.component.Implementation;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.health.HealthResult;
import com.alipay.sofa.runtime.spi.log.SofaLogger;
import com.alipay.sofa.runtime.spi.util.ComponentNameFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Service Component
 *
 * @author xuanbei 18/3/9
 */
@SuppressWarnings("unchecked")
public class ServiceComponent extends AbstractComponent {
    public static final String        UNREGISTER_DELAY_MILLISECONDS = "UNREGISTER_DELAY_MILLISECONDS";
    public static final ComponentType SERVICE_COMPONENT_TYPE        = new ComponentType("service");

    private Service                   service;
    private BindingAdapterFactory     bindingAdapterFactory;
    private Map<String, Property>     properties                    = new ConcurrentHashMap<>();

    public ServiceComponent(Implementation implementation, Service service,
                            BindingAdapterFactory bindingAdapterFactory,
                            SofaRuntimeContext sofaRuntimeContext) {
        this.componentName = ComponentNameFactory.createComponentName(SERVICE_COMPONENT_TYPE,
            service.getInterfaceType(), service.getUniqueId());
        this.implementation = implementation;
        this.service = service;
        this.bindingAdapterFactory = bindingAdapterFactory;
        this.sofaRuntimeContext = sofaRuntimeContext;
    }

    @Override
    public ComponentType getType() {
        return SERVICE_COMPONENT_TYPE;
    }

    @Override
    public Map<String, Property> getProperties() {
        return properties;
    }

    @Override
    public boolean resolve() {
        resolveBinding();
        return super.resolve();
    }

    private void resolveBinding() {

        Object target = service.getTarget();

        if (target == null) {
            throw new ServiceRuntimeException(
                "Must contains the target object whiling registering Service.");
        }

        if (service.hasBinding()) {
            Set<Binding> bindings = service.getBindings();
            boolean allPassed = true;
            for (Binding binding : bindings) {
                BindingAdapter<Binding> bindingAdapter = this.bindingAdapterFactory
                    .getBindingAdapter(binding.getBindingType());

                if (bindingAdapter == null) {
                    throw new ServiceRuntimeException("Can't find BindingAdapter of type "
                                                      + binding.getBindingType()
                                                      + " while registering service " + service
                                                      + ".");
                }

                SofaLogger.info(" <<PreOut Binding [{0}] Begins - {1}.", binding.getBindingType(),
                    service);
                try {
                    bindingAdapter.preOutBinding(service, binding, target, getContext());
                } catch (Throwable t) {
                    allPassed = false;
                    SofaLogger.error(t, " <<PreOut Binding [{0}] for [{1}] occur exception.",
                        binding.getBindingType(), service);
                    continue;
                }
                SofaLogger.info(" <<PreOut Binding [{0}] Ends - {1}.", binding.getBindingType(),
                    service);
            }

            if (!allPassed) {
                throw new ServiceRuntimeException(" <<PreOut Binding [" + service
                                                  + "] occur exception.");
            }
        }
    }

    @Override
    public void activate() throws ServiceRuntimeException {

        activateBinding();
        super.activate();
    }

    private void activateBinding() {

        Object target = service.getTarget();

        if (target == null) {
            throw new ServiceRuntimeException(
                "Must contains the target object whiling registering Service.");
        }

        if (service.hasBinding()) {
            boolean allPassed = true;
            Set<Binding> bindings = service.getBindings();
            for (Binding binding : bindings) {
                BindingAdapter<Binding> bindingAdapter = this.bindingAdapterFactory
                    .getBindingAdapter(binding.getBindingType());

                if (bindingAdapter == null) {
                    throw new ServiceRuntimeException("Can't find BindingAdapter of type "
                                                      + binding.getBindingType()
                                                      + " while registering service " + service
                                                      + ".");
                }

                Object outBindingResult;
                SofaLogger.info(" <<Out Binding [{0}] Begins - {1}.", binding.getBindingType(),
                    service);
                try {
                    outBindingResult = bindingAdapter.outBinding(service, binding, target,
                        getContext());
                } catch (Throwable t) {
                    allPassed = false;
                    SofaLogger.error(t, " <<Out binding [{0}] for [{1}] occur exception.",
                        binding.getBindingType(), service);
                    continue;
                }
                if (!Boolean.FALSE.equals(outBindingResult)) {
                    SofaLogger.info(" <<Out Binding [{0}] Ends - {1}.", binding.getBindingType(),
                        service);
                } else {
                    binding.setHealthy(false);
                    SofaLogger.info(" <<Out Binding [{0}] Fails, Don't publish service - {1}.",
                        binding.getBindingType(), service);
                }
            }

            if (!allPassed) {
                throw new ServiceRuntimeException(" <<Out Binding [" + service
                                                  + "] occur exception.");
            }
        }

        SofaLogger.info("Register Service - {0}", service);
    }

    @Override
    public void deactivate() throws ServiceRuntimeException {
        Object target = service.getTarget();

        if (target == null) {
            throw new ServiceRuntimeException(
                "Must contains the target object whiling registering Service.");
        }

        if (service.hasBinding()) {
            boolean allPassed = true;
            Set<Binding> bindings = service.getBindings();
            for (Binding binding : bindings) {
                BindingAdapter<Binding> bindingAdapter = this.bindingAdapterFactory
                    .getBindingAdapter(binding.getBindingType());

                if (bindingAdapter == null) {
                    throw new ServiceRuntimeException("Can't find BindingAdapter of type "
                                                      + binding.getBindingType()
                                                      + " while deactivate service " + service
                                                      + ".");
                }

                SofaLogger.info(" <<Pre un-out Binding [{0}] Begins - {1}.",
                    binding.getBindingType(), service);
                try {
                    bindingAdapter.preUnoutBinding(service, binding, target, getContext());
                } catch (Throwable t) {
                    allPassed = false;
                    SofaLogger.error(t, " <<Pre un-out Binding [{0}] for [{1}] occur exception.",
                        binding.getBindingType(), service);
                    continue;
                }
                SofaLogger.info(" <<Pre un-out Binding [{0}] Ends - {1}.",
                    binding.getBindingType(), service);
            }

            if (!allPassed) {
                throw new ServiceRuntimeException(" <<Pre un-out Binding [" + service
                                                  + "] occur exception.");
            }
        }

        super.deactivate();
    }

    @Override
    public void unregister() throws ServiceRuntimeException {
        super.unregister();

        Property unregisterDelayMillisecondsProperty = properties
            .get(UNREGISTER_DELAY_MILLISECONDS);

        if (unregisterDelayMillisecondsProperty != null) {
            int unregisterDelayMilliseconds = unregisterDelayMillisecondsProperty.getInteger();

            try {
                TimeUnit.MILLISECONDS.sleep(unregisterDelayMilliseconds);
            } catch (InterruptedException e) {
                throw new ServiceRuntimeException("Unregiter component " + toString()
                                                  + " got an error", e);
            }
        }

        Object target = service.getTarget();

        if (target == null) {
            throw new ServiceRuntimeException(
                "Must contains the target object whiling registering Service.");
        }

        if (service.hasBinding()) {
            boolean allPassed = true;
            Set<Binding> bindings = service.getBindings();
            for (Binding binding : bindings) {
                BindingAdapter<Binding> bindingAdapter = this.bindingAdapterFactory
                    .getBindingAdapter(binding.getBindingType());

                if (bindingAdapter == null) {
                    throw new ServiceRuntimeException("Can't find BindingAdapter of type "
                                                      + binding.getBindingType()
                                                      + " while unregister service " + service
                                                      + ".");
                }

                SofaLogger.info(" <<Post un-out Binding [{0}] Begins - {1}.",
                    binding.getBindingType(), service);
                try {
                    bindingAdapter.postUnoutBinding(service, binding, target, getContext());
                } catch (Throwable t) {
                    allPassed = false;
                    SofaLogger.error(t, " <<Post un-out Binding [{0}] for [{1}] occur exception.",
                        binding.getBindingType(), service);
                    continue;
                }
                SofaLogger.info(" <<Post un-out Binding [{0}] Ends - {1}.",
                    binding.getBindingType(), service);
            }

            if (!allPassed) {
                throw new ServiceRuntimeException(" <<Post un-out Binding [" + service
                                                  + "] occur exception.");
            }
        }
    }

    @Override
    public String dump() {
        StringBuilder sb = new StringBuilder(super.dump());

        Collection<Binding> bindings = service.getBindings();

        for (Binding binding : bindings) {
            sb.append("\n|------>[binding]-").append(binding.dump());
        }

        return sb.toString();
    }

    public Service getService() {
        return service;
    }

    @Override
    public HealthResult isHealthy() {
        if (!isActivated()) {
            return super.isHealthy();
        }

        HealthResult healthResult = new HealthResult(componentName.getRawName());
        if (this.e == null) {
            healthResult.setHealthy(true);
        } else {
            healthResult.setHealthy(false);
            healthResult.setHealthReport(e.getMessage());
        }
        return healthResult;
    }
}