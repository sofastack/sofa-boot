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
package com.alipay.sofa.boot.actuator.diagnostic;

import com.alipay.sofa.common.thread.ThreadPoolConfig;
import com.alipay.sofa.common.thread.ThreadPoolGovernor;
import com.alipay.sofa.common.thread.ThreadPoolMonitorWrapper;
import com.alipay.sofa.runtime.model.ComponentStatus;
import com.alipay.sofa.runtime.service.component.Reference;
import com.alipay.sofa.runtime.service.component.ReferenceComponent;
import com.alipay.sofa.runtime.service.component.Service;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.springframework.boot.actuate.endpoint.OperationResponseBody;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.info.ProcessInfo;
import org.springframework.lang.Nullable;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * {@link Endpoint @Endpoint} to expose runtime diagnostic summary of SOFABoot application,
 * including component status, thread pool statistics, JVM info, and memory usage.
 *
 * @author xiaosiyuan
 * @version SofaDiagnosticEndpoint.java, v 0.1 2026年04月01日 xiaosiyuan Exp $
 */
@Endpoint(id = "sofa-diagnostic")
public class SofaDiagnosticEndpoint {

    private static final String                  SERVICES_PATH               = "services";

    private static final String                  SERVICE_DETAIL_PATH         = "serviceDetail";

    private final SofaRuntimeContext              sofaRuntimeContext;

    private final ThreadPoolGovernor              threadPoolGovernor;

    public SofaDiagnosticEndpoint(SofaRuntimeContext sofaRuntimeContext,
                                  ThreadPoolGovernor threadPoolGovernor) {
        this.sofaRuntimeContext = sofaRuntimeContext;
        this.threadPoolGovernor = threadPoolGovernor;
    }

    @ReadOperation
    public DiagnosticSummary summary() {
        return new DiagnosticSummary(getComponentStats(), getThreadPoolStats(), getJvmInfo(),
            getMemoryStats());
    }

    /**
     * Reads service info by category: {@code services/{type}} (published | referenced | all)
     * or {@code serviceDetail/{interfaceId}}.
     */
    @ReadOperation
    @Nullable
    public OperationResponseBody read(@Selector String category, @Selector String param) {
        ComponentManager componentManager = sofaRuntimeContext.getComponentManager();
        return switch (category) {
            case SERVICES_PATH -> getServicesByType(componentManager, param);
            case SERVICE_DETAIL_PATH -> findServiceDetail(componentManager, param);
            default -> throw new IllegalArgumentException("Unknown path: " + category
                                                          + ". Supported paths: services, serviceDetail");
        };
    }

    private ServicesDescriptor getServicesByType(ComponentManager componentManager, String type) {
        return switch (type) {
            case "published" -> new ServicesDescriptor(getPublishedServices(componentManager), null);
            case "referenced" ->
                new ServicesDescriptor(null, getReferencedServices(componentManager));
            case "all" -> new ServicesDescriptor(getPublishedServices(componentManager),
                getReferencedServices(componentManager));
            default -> throw new IllegalArgumentException("Unknown service type: " + type
                                                          + ". Supported types: published, referenced, all");
        };
    }

    @Nullable
    private ServiceDetailDescriptor findServiceDetail(ComponentManager componentManager,
                                                      String interfaceId) {
        List<ServiceDetailInfo> publishedDetails = getPublishedServiceDetails(componentManager,
            interfaceId);
        List<ReferenceDetailInfo> referencedDetails = getReferencedServiceDetails(componentManager,
            interfaceId);
        if (publishedDetails.isEmpty() && referencedDetails.isEmpty()) {
            return null;
        }
        return new ServiceDetailDescriptor(interfaceId, publishedDetails, referencedDetails);
    }

    private List<ServiceInfo> getPublishedServices(ComponentManager componentManager) {
        Collection<ComponentInfo> components = componentManager
            .getComponentInfosByType(ServiceComponent.SERVICE_COMPONENT_TYPE);
        return components.stream().filter(ServiceComponent.class::isInstance)
            .map(ServiceComponent.class::cast).map(this::toServiceInfo)
            .toList();
    }

    private List<ReferenceInfo> getReferencedServices(ComponentManager componentManager) {
        Collection<ComponentInfo> components = componentManager
            .getComponentInfosByType(ReferenceComponent.REFERENCE_COMPONENT_TYPE);
        return components.stream().filter(ReferenceComponent.class::isInstance)
            .map(ReferenceComponent.class::cast).map(this::toReferenceInfo)
            .toList();
    }

    private ServiceInfo toServiceInfo(ServiceComponent serviceComponent) {
        Service service = serviceComponent.getService();
        return new ServiceInfo(service.getInterfaceType().getName(), service.getUniqueId(),
            serviceComponent.getState().toString(), toBindingInfos(service.getBindings()));
    }

    private ReferenceInfo toReferenceInfo(ReferenceComponent referenceComponent) {
        Reference reference = referenceComponent.getReference();
        return new ReferenceInfo(reference.getInterfaceType().getName(), reference.getUniqueId(),
            referenceComponent.getState().toString(), reference.isJvmFirst(),
            reference.isRequired(), toBindingInfos(reference.getBindings()));
    }

    private List<BindingInfo> toBindingInfos(Set<? extends Binding> bindings) {
        if (bindings == null || bindings.isEmpty()) {
            return List.of();
        }
        return bindings.stream()
            .map(b -> new BindingInfo(b.getBindingType().getType(), b.getURI(),
                b.healthCheck().isHealthy()))
            .toList();
    }

    private List<ServiceDetailInfo> getPublishedServiceDetails(ComponentManager componentManager,
                                                               String interfaceId) {
        return componentManager.getComponentInfosByType(ServiceComponent.SERVICE_COMPONENT_TYPE)
            .stream().filter(ServiceComponent.class::isInstance).map(ServiceComponent.class::cast)
            .filter(sc -> sc.getService().getInterfaceType().getName().equals(interfaceId))
            .map(this::toServiceDetailInfo).toList();
    }

    private List<ReferenceDetailInfo> getReferencedServiceDetails(ComponentManager componentManager,
                                                                   String interfaceId) {
        return componentManager.getComponentInfosByType(ReferenceComponent.REFERENCE_COMPONENT_TYPE)
            .stream().filter(ReferenceComponent.class::isInstance).map(ReferenceComponent.class::cast)
            .filter(rc -> rc.getReference().getInterfaceType().getName().equals(interfaceId))
            .map(this::toReferenceDetailInfo).toList();
    }

    private ServiceDetailInfo toServiceDetailInfo(ServiceComponent sc) {
        Service service = sc.getService();
        String implClass = service.getTarget() != null
            ? service.getTarget().getClass().getName() : null;
        return new ServiceDetailInfo(service.getInterfaceType().getName(),
            service.getUniqueId(), sc.getState().toString(), implClass,
            toBindingInfos(service.getBindings()), sc.isHealthy().isHealthy(),
            sc.isHealthy().getHealthReport());
    }

    private ReferenceDetailInfo toReferenceDetailInfo(ReferenceComponent rc) {
        Reference reference = rc.getReference();
        return new ReferenceDetailInfo(reference.getInterfaceType().getName(),
            reference.getUniqueId(), rc.getState().toString(), reference.isJvmFirst(),
            reference.isRequired(), toBindingInfos(reference.getBindings()),
            rc.isHealthy().isHealthy(), rc.isHealthy().getHealthReport());
    }

    private ComponentStats getComponentStats() {
        ComponentManager componentManager = sofaRuntimeContext.getComponentManager();
        Collection<ComponentInfo> components = componentManager.getComponents();

        int total = components.size();
        int activated = 0;
        int resolved = 0;
        int registered = 0;
        int unregistered = 0;

        for (ComponentInfo component : components) {
            ComponentStatus status = component.getState();
            switch (status) {
                case ACTIVATED -> activated++;
                case RESOLVED -> resolved++;
                case REGISTERED -> registered++;
                case UNREGISTERED -> unregistered++;
            }
        }

        return new ComponentStats(total, activated, resolved, registered, unregistered);
    }

    private List<ThreadPoolStats> getThreadPoolStats() {
        return threadPoolGovernor.getAllThreadPoolWrappers().stream()
            .map(this::toThreadPoolStats).toList();
    }

    private ThreadPoolStats toThreadPoolStats(ThreadPoolMonitorWrapper wrapper) {
        ThreadPoolConfig config = wrapper.getThreadPoolConfig();
        ThreadPoolExecutor executor = wrapper.getThreadPoolExecutor();
        return new ThreadPoolStats(config.getThreadPoolName(), config.getSpaceName(),
            executor.getCorePoolSize(), executor.getMaximumPoolSize(),
            executor.getPoolSize(), executor.getActiveCount(), executor.getQueue().size(),
            executor.getQueue().remainingCapacity());
    }

    private JvmInfo getJvmInfo() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return new JvmInfo(System.getProperty("java.version"),
            System.getProperty("java.vm.name"), runtimeMXBean.getUptime(),
            Runtime.getRuntime().availableProcessors(), ProcessHandle.current().pid());
    }

    private ProcessInfo.MemoryInfo getMemoryStats() {
        return new ProcessInfo().getMemory();
    }

    public record DiagnosticSummary(ComponentStats components, List<ThreadPoolStats> threadPools,
                                    JvmInfo jvm,
                                    ProcessInfo.MemoryInfo memory) implements OperationResponseBody {
    }

    public record ComponentStats(int total, int activated, int resolved, int registered,
                                 int unregistered) {
    }

    public record ThreadPoolStats(String threadPoolName, String spaceName, int coreSize,
                                  int maxSize, int poolSize, int activeCount, int queueSize,
                                  int queueRemainingCapacity) {
    }

    public record JvmInfo(String javaVersion, String vmName, long uptimeMillis,
                          int availableProcessors, long pid) {
    }

    public record ServicesDescriptor(@Nullable List<ServiceInfo> published,
                                     @Nullable List<ReferenceInfo> referenced) implements OperationResponseBody {
    }

    public record ServiceInfo(String interfaceType, String uniqueId, String status,
                              List<BindingInfo> bindings) {
    }

    public record ReferenceInfo(String interfaceType, String uniqueId, String status,
                                boolean jvmFirst, boolean required, List<BindingInfo> bindings) {
    }

    public record BindingInfo(String bindingType, String uri, boolean healthy) {
    }

    public record ServiceDetailDescriptor(String interfaceId,
                                          @Nullable List<ServiceDetailInfo> publishedDetails,
                                          @Nullable List<ReferenceDetailInfo> referencedDetails) implements OperationResponseBody {
    }

    public record ServiceDetailInfo(String interfaceType, String uniqueId, String status,
                                    @Nullable String implementationClass,
                                    List<BindingInfo> bindings, boolean healthy,
                                    String healthReport) {
    }

    public record ReferenceDetailInfo(String interfaceType, String uniqueId, String status,
                                      boolean jvmFirst, boolean required,
                                      List<BindingInfo> bindings, boolean healthy,
                                      String healthReport) {
    }

}
