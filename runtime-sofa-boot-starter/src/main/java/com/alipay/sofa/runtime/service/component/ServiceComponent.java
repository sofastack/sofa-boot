/**
 * Copyright Notice: This software is developed by Ant Small and Micro Financial Services Group Co., Ltd. This software and all the relevant information, including but not limited to any signs, images, photographs, animations, text, interface design,
 *  audios and videos, and printed materials, are protected by copyright laws and other intellectual property laws and treaties.
 *  The use of this software shall abide by the laws and regulations as well as Software Installation License Agreement/Software Use Agreement updated from time to time.
 *   Without authorization from Ant Small and Micro Financial Services Group Co., Ltd., no one may conduct the following actions:
 *
 *   1) reproduce, spread, present, set up a mirror of, upload, download this software;
 *
 *   2) reverse engineer, decompile the source code of this software or try to find the source code in any other ways;
 *
 *   3) modify, translate and adapt this software, or develop derivative products, works, and services based on this software;
 *
 *   4) distribute, lease, rent, sub-license, demise or transfer any rights in relation to this software, or authorize the reproduction of this software on other’s computers.
 */
package com.alipay.sofa.runtime.service.component;

import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.api.component.Property;
import com.alipay.sofa.runtime.model.ComponentType;
import com.alipay.sofa.runtime.service.impl.BindingFactoryContainer;
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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Service Component
 *
 * @author xuanbei 18/3/9
 */
public class ServiceComponent extends AbstractComponent {

    public static final String        UNREGISTER_DELAY_MILLISECONDS = "UNREGISTER_DELAY_MILLISECONDS";
    public static final ComponentType SERVICE_COMPONENT_TYPE        = new ComponentType("service");

    private Service                   service;
    private BindingAdapterFactory     bindingAdapterFactory;
    private Map<String, Property>     properties                    = new ConcurrentHashMap<String, Property>();

    public ServiceComponent(Implementation implementation, Service service,
                            SofaRuntimeContext sofaRuntimeContext) {
        this.componentName = ComponentNameFactory.createComponentName(SERVICE_COMPONENT_TYPE,
            service.getInterfaceType(), service.getUniqueId());
        this.implementation = implementation;
        this.service = service;
        this.bindingAdapterFactory = BindingFactoryContainer.getBindingAdapterFactory();
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
            Iterator<Binding> it = bindings.iterator();
            while (it.hasNext()) {
                Binding binding = it.next();
                BindingAdapter<Binding> bindingAdapter = this.bindingAdapterFactory
                    .getBindingAdapter(binding.getBindingType());

                if (bindingAdapter == null) {
                    throw new ServiceRuntimeException("Can't find BindingAdapter of type "
                        + binding.getBindingType()
                        + " while registering service " + service
                        + ".");
                }

                SofaLogger.info(" <<PreOut Binding [{0}] Begins - {1}.", binding.getBindingType(), service);
                bindingAdapter.preOutBinding(service, binding, target, getContext());
                SofaLogger.info(" <<PreOut Binding [{0}] Ends - {1}.", binding.getBindingType()
                    , service);
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
            Set<Binding> bindings = service.getBindings();
            Iterator<Binding> it = bindings.iterator();
            while (it.hasNext()) {
                Binding binding = it.next();
                BindingAdapter<Binding> bindingAdapter = this.bindingAdapterFactory
                    .getBindingAdapter(binding.getBindingType());

                if (bindingAdapter == null) {
                    throw new ServiceRuntimeException("Can't find BindingAdapter of type "
                        + binding.getBindingType()
                        + " while registering service " + service
                        + ".");
                }

                SofaLogger.info(" <<Out Binding [{0}] Begins - {1}.", binding.getBindingType()
                    , service);
                Object outBindingResult = bindingAdapter.outBinding(service, binding, target,
                    getContext());
                if (!Boolean.FALSE.equals(outBindingResult)) {
                    SofaLogger.info(" <<Out Binding [{0}] Ends - {1}.", binding.getBindingType()
                        , service);
                } else {
                    binding.setHealthy(false);
                    SofaLogger.info(" <<Out Binding [{0}] Fails, Don't publish service - {1}.",
                        binding.getBindingType()
                        , service);
                }
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

                SofaLogger.info(" <<Pre un-out Binding [{0}] Begins - {1}.", binding.getBindingType()
                    , service);
                bindingAdapter.preUnoutBinding(service, binding, target, getContext());
                SofaLogger.info(" <<Pre un-out Binding [{0}] Ends - {1}.", binding.getBindingType()
                    , service);
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

                SofaLogger.info(" <<Post un-out Binding [{0}] Begins - {1}.", binding.getBindingType()
                    , service);
                bindingAdapter.postUnoutBinding(service, binding, target, getContext());
                SofaLogger.info(" <<Post un-out Binding [{0}] Ends - {1}.", binding.getBindingType()
                    , service);
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
        HealthResult healthResult = new HealthResult(componentName.getRawName());
        healthResult.setHealthy(true);
        return healthResult;
    }

    public void setComponentName(ComponentName componentName) {
        this.componentName = componentName;
    }
}