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
import com.alipay.sofa.rpc.bootstrap.ConsumerBootstrap;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.context.RpcRuntimeContext;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.context.SpringContextComponent;
import com.alipay.sofa.runtime.model.ComponentStatus;
import com.alipay.sofa.runtime.service.component.Reference;
import com.alipay.sofa.runtime.service.component.ReferenceComponent;
import com.alipay.sofa.runtime.service.component.Service;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.ComponentNameFactory;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spring.factory.ServiceFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.actuate.endpoint.Access;
import org.springframework.boot.actuate.endpoint.OperationResponseBody;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * {@link Endpoint @Endpoint} to expose runtime diagnostic summary of SOFABoot application,
 * including component status, thread pool statistics, JVM info, and memory usage.
 *
 * @author xiaosiyuan
 * @version SofaDiagnosticEndpoint.java, v 0.1 2026年04月01日 xiaosiyuan Exp $
 */
@Endpoint(id = "sofa-diagnostic", defaultAccess = Access.READ_ONLY)
public class SofaDiagnosticEndpoint {

    private static final String                  CLEAR_CACHE_COMMAND         = "clear-cache";

    private static final String                  REFRESH_COMPONENT_COMMAND   = "refresh-component";

    private static final String                  GC_COMMAND                  = "gc";

    private static final String                  THREAD_DUMP_COMMAND         = "thread-dump";

    private static final String                  HEAP_DUMP_COMMAND           = "heap-dump";

    private static final String                  SERVICES_PATH               = "services";

    private static final String                  SERVICE_DETAIL_PATH         = "serviceDetail";

    private static final String                  RPC_ROUTER_CACHE_TYPE       = "rpc-router";

    private static final String                  SERVICE_METADATA_CACHE_TYPE = "service-metadata";

    private final SofaRuntimeContext              sofaRuntimeContext;

    private final ThreadPoolGovernor              threadPoolGovernor;

    private final ApplicationContext              applicationContext;

    public SofaDiagnosticEndpoint(SofaRuntimeContext sofaRuntimeContext,
                                  ThreadPoolGovernor threadPoolGovernor,
                                  ApplicationContext applicationContext) {
        this.sofaRuntimeContext = sofaRuntimeContext;
        this.threadPoolGovernor = threadPoolGovernor;
        this.applicationContext = applicationContext;
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

    @WriteOperation
    public OperationResult execute(@Selector String command, @Nullable String type,
                                   @Nullable String interfaceType,
                                   @Nullable String uniqueId, @Nullable String path,
                                   @Nullable Boolean liveOnly) {
        return switch (command) {
            case CLEAR_CACHE_COMMAND -> clearCache(type, interfaceType, uniqueId);
            case REFRESH_COMPONENT_COMMAND -> refreshComponent(interfaceType, uniqueId);
            case GC_COMMAND -> triggerGc();
            case THREAD_DUMP_COMMAND -> getThreadDump();
            case HEAP_DUMP_COMMAND -> triggerHeapDump(path, liveOnly);
            default -> throw new IllegalArgumentException("Unknown command: " + command
                    + ". Supported commands: " + GC_COMMAND + ", " + THREAD_DUMP_COMMAND + ", "
                    + HEAP_DUMP_COMMAND + ", " + CLEAR_CACHE_COMMAND + ", " + REFRESH_COMPONENT_COMMAND);
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

    private OperationResult triggerGc() {
        Runtime runtime = Runtime.getRuntime();
        long beforeUsed = runtime.totalMemory() - runtime.freeMemory();
        System.gc();
        long afterUsed = runtime.totalMemory() - runtime.freeMemory();
        return new OperationResult(true, "GC triggered successfully",
            Map.of("memoryBeforeGc", beforeUsed, "memoryAfterGc", afterUsed,
                "memoryFreed", beforeUsed - afterUsed));
    }

    private OperationResult clearCache(@Nullable String type, @Nullable String interfaceType,
                                       @Nullable String uniqueId) {
        String cacheType = (type == null || type.isEmpty()) ? "all" : type;
        return switch (cacheType) {
            case "all" -> clearAllCaches(interfaceType, uniqueId);
            case RPC_ROUTER_CACHE_TYPE -> clearRpcRouterCache(interfaceType, uniqueId);
            case SERVICE_METADATA_CACHE_TYPE -> clearServiceMetadataCache(interfaceType, uniqueId);
            default -> throw new IllegalArgumentException("Unknown cache type: " + cacheType
                                                          + ". Supported cache types: all, rpc-router, service-metadata");
        };
    }

    private OperationResult clearAllCaches(@Nullable String interfaceType,
                                           @Nullable String uniqueId) {
        OperationResult rpcResult = clearRpcRouterCache(interfaceType, uniqueId);
        OperationResult metaResult = clearServiceMetadataCache(interfaceType, uniqueId);
        boolean success = rpcResult.success() && metaResult.success();
        return new OperationResult(success,
            success ? "All caches cleared successfully" : "Some caches failed to clear",
            Map.of("rpcRouter", rpcResult, "serviceMetadata", metaResult));
    }

    @SuppressWarnings("rawtypes")
    private OperationResult clearRpcRouterCache(@Nullable String interfaceType,
                                                @Nullable String uniqueId) {
        List<ConsumerBootstrap> matched = findMatchedConsumerBootstraps(interfaceType, uniqueId);
        if (matched.isEmpty()) {
            return new OperationResult(false, "No matching consumer references found",
                buildFilterData(interfaceType, uniqueId));
        }

        List<String> cleared = new ArrayList<>();
        List<String> failed = new ArrayList<>();
        int clearedProviderCount = 0;
        for (ConsumerBootstrap bootstrap : matched) {
            ConsumerConfig consumerConfig = bootstrap.getConsumerConfig();
            String consumerKey = buildConsumerKey(consumerConfig);
            try {
                int providerCount = bootstrap.getCluster().getAddressHolder().getAllProviderSize();
                bootstrap.getCluster().updateAllProviders(List.of());
                cleared.add(consumerKey);
                clearedProviderCount += providerCount;
            } catch (Exception e) {
                failed.add(consumerKey + ": " + e.getClass().getSimpleName());
            }
        }

        Map<String, Object> data = buildFilterData(interfaceType, uniqueId);
        data.put("clearedCount", cleared.size());
        data.put("clearedProviderCount", clearedProviderCount);
        data.put("components", cleared);
        data.put("failed", failed);
        if (!failed.isEmpty()) {
            return new OperationResult(false,
                cleared.isEmpty() ? "Failed to clear RPC router cache"
                    : "RPC router cache cleared partially", data);
        }
        return new OperationResult(true, "RPC router cache cleared successfully", data);
    }

    private OperationResult clearServiceMetadataCache(@Nullable String interfaceType,
                                                      @Nullable String uniqueId) {
        ComponentManager componentManager = sofaRuntimeContext.getComponentManager();
        List<ServiceComponent> matched = findRegisteredServiceComponents(componentManager,
            interfaceType, uniqueId);

        if (matched.isEmpty()) {
            return new OperationResult(false, "No matching service components found",
                buildFilterData(interfaceType, uniqueId));
        }

        List<String> cleared = new ArrayList<>();
        List<String> failed = new ArrayList<>();
        for (ServiceComponent serviceComponent : matched) {
            String rawName = serviceComponent.getName().getRawName();
            try {
                componentManager.unregister(serviceComponent);
                cleared.add(rawName);
            } catch (Exception e) {
                failed.add(rawName + ": " + e.getClass().getSimpleName());
            }
        }

        Map<String, Object> data = buildFilterData(interfaceType, uniqueId);
        data.put("clearedCount", cleared.size());
        data.put("components", cleared);
        data.put("failed", failed);
        if (!failed.isEmpty()) {
            return new OperationResult(false,
                cleared.isEmpty() ? "Failed to clear service metadata cache"
                    : "Service metadata cache cleared partially", data);
        }
        return new OperationResult(true, "Service metadata cache cleared", data);
    }

    private OperationResult refreshComponent(@Nullable String interfaceType,
                                             @Nullable String uniqueId) {
        ComponentManager componentManager = sofaRuntimeContext.getComponentManager();
        List<ServiceFactoryBean> matched = findServiceFactoryBeans(componentManager,
            interfaceType, uniqueId);

        if (matched.isEmpty()) {
            return new OperationResult(false,
                "No matching Spring-managed service factories found for refresh",
                buildFilterData(interfaceType, uniqueId));
        }

        List<String> refreshed = new ArrayList<>();
        List<String> skipped = new ArrayList<>();
        for (ServiceFactoryBean sfb : matched) {
            ComponentName componentName = ComponentNameFactory.createComponentName(
                ServiceComponent.SERVICE_COMPONENT_TYPE, sfb.getInterfaceClass(),
                sfb.getUniqueId());
            String rawName = componentName.getRawName();
            if (componentManager.isRegistered(componentName)) {
                skipped.add(rawName);
                continue;
            }
            try {
                sfb.afterPropertiesSet();
                refreshed.add(rawName);
            } catch (Exception e) {
                Map<String, Object> data = buildFilterData(interfaceType, uniqueId);
                data.put("refreshedCount", refreshed.size());
                data.put("failedComponent", rawName);
                data.put("error", e.getMessage());
                return new OperationResult(false, "Failed to refresh component", data);
            }
        }

        Map<String, Object> data = buildFilterData(interfaceType, uniqueId);
        data.put("refreshedCount", refreshed.size());
        data.put("components", refreshed);
        data.put("skipped", skipped);
        return new OperationResult(true, "Component refreshed successfully", data);
    }

    private OperationResult getThreadDump() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(true, true);
        Map<Long, Thread> threadMap = Thread.getAllStackTraces().keySet().stream()
            .collect(Collectors.toMap(Thread::getId, t -> t, (a, b) -> a));

        List<ThreadEntry> threads = Arrays.stream(threadInfos)
            .map(info -> toThreadEntry(info, threadMap.get(info.getThreadId())))
            .toList();

        return new OperationResult(true, "Thread dump generated successfully",
            Map.of("threadCount", threadInfos.length, "threads", threads));
    }

    private ThreadEntry toThreadEntry(ThreadInfo info, Thread thread) {
        return new ThreadEntry(info.getThreadId(), info.getThreadName(),
            info.getThreadState().name(), thread != null && thread.isDaemon(),
            thread != null ? thread.getPriority() : 0,
            Arrays.stream(info.getStackTrace()).map(StackTraceElement::toString).toList(),
            info.getLockName(), info.getLockOwnerName(), info.getLockOwnerId());
    }

    private OperationResult triggerHeapDump(@Nullable String path, @Nullable Boolean liveOnly) {
        String dumpPath = (path == null || path.isEmpty())
            ? System.getProperty("java.io.tmpdir") + File.separator + "heapdump-"
              + System.currentTimeMillis() + ".hprof"
            : path;
        boolean live = liveOnly == null || liveOnly;
        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            ObjectName objectName = new ObjectName("com.sun.management:type=HotSpotDiagnostic");
            server.invoke(objectName, "dumpHeap", new Object[] { dumpPath, live },
                new String[] { String.class.getName(), boolean.class.getName() });
            return new OperationResult(true, "Heap dump generated successfully",
                Map.of("path", dumpPath, "size", new File(dumpPath).length(), "liveOnly", live));
        } catch (Exception e) {
            return new OperationResult(false, "Failed to generate heap dump: " + e.getMessage(),
                Map.of("error", e.getClass().getName()));
        }
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

    private MemoryStats getMemoryStats() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heap = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeap = memoryMXBean.getNonHeapMemoryUsage();
        return new MemoryStats(
            new HeapMemoryInfo(heap.getUsed(), heap.getCommitted(), heap.getMax()),
            new NonHeapMemoryInfo(nonHeap.getUsed(), nonHeap.getCommitted()));
    }

    private List<ServiceComponent> findRegisteredServiceComponents(ComponentManager componentManager,
                                                                   @Nullable String interfaceType,
                                                                   @Nullable String uniqueId) {
        Collection<ComponentInfo> components = componentManager
            .getComponentInfosByType(ServiceComponent.SERVICE_COMPONENT_TYPE);
        return components.stream().filter(ServiceComponent.class::isInstance)
            .map(ServiceComponent.class::cast)
            .filter(sc -> matchesCriteria(interfaceType, sc.getService().getInterfaceType().getName())
                          && matchesCriteria(uniqueId, sc.getService().getUniqueId()))
            .toList();
    }

    private List<ServiceFactoryBean> findServiceFactoryBeans(ComponentManager componentManager,
                                                             @Nullable String interfaceType,
                                                             @Nullable String uniqueId) {
        Map<String, ServiceFactoryBean> factories = new LinkedHashMap<>();
        for (ApplicationContext ctx : getSpringApplicationContexts(componentManager)) {
            for (String beanName : ctx.getBeanDefinitionNames()) {
                String factoryBeanName = BeanFactory.FACTORY_BEAN_PREFIX + beanName;
                try {
                    if (!ctx.isTypeMatch(factoryBeanName, ServiceFactoryBean.class)) {
                        continue;
                    }
                    ServiceFactoryBean sfb = ctx.getBean(factoryBeanName, ServiceFactoryBean.class);
                    Class<?> iface = sfb.getInterfaceClass();
                    if (iface != null
                        && matchesCriteria(interfaceType, iface.getName())
                        && matchesCriteria(uniqueId, sfb.getUniqueId())) {
                        factories.put(ctx.getId() + ":" + beanName, sfb);
                    }
                } catch (BeansException ignored) {
                }
            }
        }
        return new ArrayList<>(factories.values());
    }

    @SuppressWarnings("rawtypes")
    private List<ConsumerBootstrap> findMatchedConsumerBootstraps(@Nullable String interfaceType,
                                                                  @Nullable String uniqueId) {
        return RpcRuntimeContext.getConsumerConfigs().stream()
            .filter(cb -> matchesCriteria(interfaceType, cb.getConsumerConfig().getInterfaceId())
                          && matchesCriteria(uniqueId, cb.getConsumerConfig().getUniqueId()))
            .toList();
    }

    private List<ApplicationContext> getSpringApplicationContexts(ComponentManager componentManager) {
        LinkedHashSet<ApplicationContext> contexts = new LinkedHashSet<>();

        componentManager.getComponentInfosByType(SpringContextComponent.SPRING_COMPONENT_TYPE)
            .stream().filter(SpringContextComponent.class::isInstance)
            .map(SpringContextComponent.class::cast)
            .map(SpringContextComponent::getApplicationContext).filter(Objects::nonNull)
            .forEach(ctx -> addContextHierarchy(contexts, ctx));

        addContextHierarchy(contexts,
            sofaRuntimeContext.getSofaRuntimeManager().getRootApplicationContext());
        addContextHierarchy(contexts, this.applicationContext);

        return new ArrayList<>(contexts);
    }

    private void addContextHierarchy(Set<ApplicationContext> contexts,
                                     @Nullable ApplicationContext context) {
        ApplicationContext current = context;
        while (current != null) {
            contexts.add(current);
            current = current.getParent();
        }
    }

    private boolean matchesCriteria(@Nullable String criteria, @Nullable String actual) {
        return criteria == null || criteria.isEmpty() || Objects.equals(criteria, actual);
    }

    private String buildConsumerKey(ConsumerConfig<?> consumerConfig) {
        String uniqueId = consumerConfig.getUniqueId();
        return uniqueId == null || uniqueId.isEmpty()
            ? consumerConfig.getInterfaceId()
            : consumerConfig.getInterfaceId() + ":" + uniqueId;
    }

    private Map<String, Object> buildFilterData(@Nullable String interfaceType,
                                                @Nullable String uniqueId) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("interfaceType", interfaceType);
        data.put("uniqueId", uniqueId);
        return data;
    }

    public record DiagnosticSummary(ComponentStats components, List<ThreadPoolStats> threadPools,
                                    JvmInfo jvm,
                                    MemoryStats memory) implements OperationResponseBody {
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

    public record MemoryStats(HeapMemoryInfo heap, NonHeapMemoryInfo nonHeap) {
    }

    public record HeapMemoryInfo(long used, long committed, long max) {
    }

    public record NonHeapMemoryInfo(long used, long committed) {
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

    public record OperationResult(boolean success, String message,
                                  @Nullable Map<String, Object> data) implements OperationResponseBody {
    }

    public record ThreadEntry(long id, String name, String state, boolean daemon, int priority,
                              List<String> stackTrace, @Nullable String lockName,
                              @Nullable String lockOwnerName, long lockOwnerId) {
    }

}
