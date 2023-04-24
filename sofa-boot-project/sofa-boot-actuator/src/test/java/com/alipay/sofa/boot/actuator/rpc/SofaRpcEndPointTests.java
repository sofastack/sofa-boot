package com.alipay.sofa.boot.actuator.rpc;

import com.alipay.sofa.boot.actuator.sample.SampleService;
import com.alipay.sofa.boot.actuator.sample.SampleServiceImpl;
import com.alipay.sofa.rpc.bootstrap.ConsumerBootstrap;
import com.alipay.sofa.rpc.bootstrap.ProviderBootstrap;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.config.RegistryConfig;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.alipay.sofa.rpc.context.RpcRuntimeContext;
import com.alipay.sofa.rpc.registry.RegistryFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link SofaRpcEndPoint}.
 *
 * @author huzijie
 * @version SofaRpcEndPointTests.java, v 0.1 2023年04月24日 10:56 AM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class SofaRpcEndPointTests {

    private final SampleServiceImpl target = new SampleServiceImpl();

    private final SofaRpcEndPoint sofaRpcEndPoint = new SofaRpcEndPoint();

    @Mock
    private ProviderBootstrap providerBootstrap;

    @Mock
    private ProviderConfig providerConfig;

    @Mock
    private ConsumerBootstrap consumerBootstrap;

    @Mock
    private ConsumerConfig consumerConfig;

    @Mock
    private ServerConfig serverConfig;

    @Mock
    private RegistryConfig registryConfig;

    @AfterEach
    public void clear() {
        RpcRuntimeContext.invalidateProviderConfig(providerBootstrap);
        RpcRuntimeContext.invalidateConsumerConfig(consumerBootstrap);
        RegistryFactory.destroyAll();
    }

    @Test
    public void providers() {
        RpcRuntimeContext.cacheProviderConfig(providerBootstrap);
        when(providerBootstrap.getProviderConfig()).thenReturn(providerConfig);

        when(providerConfig.getInterfaceId()).thenReturn(SampleService.class.getName());
        when(providerConfig.getUniqueId()).thenReturn("A");
        when(providerConfig.getServer()).thenReturn(List.of(serverConfig));
        when(providerConfig.getRegistry()).thenReturn(List.of(registryConfig));
        when(providerConfig.getSerialization()).thenReturn("hessian");
        when(providerConfig.getRef()).thenReturn(target);
        when(providerConfig.isRegister()).thenReturn(false);
        when(serverConfig.getProtocol()).thenReturn("bolt");
        when(registryConfig.getProtocol()).thenReturn("nacos");

        SofaRpcEndPoint.RpcServicesDescriptor rpcServicesDescriptor = sofaRpcEndPoint.rpcServices();
        List<SofaRpcEndPoint.ProviderDescriptor> providerDescriptors = rpcServicesDescriptor.getProvider();
        assertThat(providerDescriptors.size()).isEqualTo(1);
        SofaRpcEndPoint.ProviderDescriptor descriptor = providerDescriptors.get(0);

        assertThat(descriptor.getInterfaceId()).isEqualTo(SampleService.class.getName());
        assertThat(descriptor.getUniqueId()).isEqualTo("A");
        assertThat(descriptor.getProtocols()).containsExactly("bolt");
        assertThat(descriptor.getRegistries()).containsExactly("nacos");
        assertThat(descriptor.getSerialization()).isEqualTo("hessian");
        assertThat(descriptor.getTargetClassName()).isEqualTo(SampleServiceImpl.class.getName());
        assertThat(descriptor.isRegister()).isFalse();
        assertThat(descriptor.getExtraInfos().isEmpty()).isTrue();
    }

    @Test
    public void consumers() {
        RpcRuntimeContext.cacheConsumerConfig(consumerBootstrap);
        when(consumerBootstrap.getConsumerConfig()).thenReturn(consumerConfig);

        when(consumerConfig.getInterfaceId()).thenReturn(SampleService.class.getName());
        when(consumerConfig.getUniqueId()).thenReturn("A");
        when(consumerConfig.getProtocol()).thenReturn("bolt");
        when(consumerConfig.getRegistry()).thenReturn(List.of(registryConfig));
        when(consumerConfig.getSerialization()).thenReturn("hessian");
        when(consumerConfig.isSubscribe()).thenReturn(false);
        when(consumerConfig.getDirectUrl()).thenReturn("http://127.0.0.1:8888");
        when(consumerConfig.getInvokeType()).thenReturn("sync");
        when(consumerConfig.getTimeout()).thenReturn(1000);
        when(consumerConfig.getRetries()).thenReturn(5);
        when(registryConfig.getProtocol()).thenReturn("nacos");

        SofaRpcEndPoint.RpcServicesDescriptor rpcServicesDescriptor = sofaRpcEndPoint.rpcServices();
        List<SofaRpcEndPoint.ConsumerDescriptor> consumerDescriptors = rpcServicesDescriptor.getConsumer();
        assertThat(consumerDescriptors.size()).isEqualTo(1);
        SofaRpcEndPoint.ConsumerDescriptor descriptor = consumerDescriptors.get(0);

        assertThat(descriptor.getInterfaceId()).isEqualTo(SampleService.class.getName());
        assertThat(descriptor.getUniqueId()).isEqualTo("A");
        assertThat(descriptor.getProtocol()).isEqualTo("bolt");
        assertThat(descriptor.getRegistries()).containsExactly("nacos");
        assertThat(descriptor.getSerialization()).isEqualTo("hessian");
        assertThat(descriptor.getDirectUrl()).isEqualTo("http://127.0.0.1:8888");
        assertThat(descriptor.getInvokeType()).isEqualTo("sync");
        assertThat(descriptor.isSubscribe()).isFalse();
        assertThat(descriptor.getTimeout()).isEqualTo(1000);
        assertThat(descriptor.getRetries()).isEqualTo(5);
        assertThat(descriptor.getExtraInfos().isEmpty()).isTrue();
    }

    @Test
    public void registries() {
        when(registryConfig.getProtocol()).thenReturn("mesh");
        when(registryConfig.getAddress()).thenReturn("http://127.0.0.1:8888");
        when(registryConfig.getIndex()).thenReturn("abc");

        RegistryFactory.getRegistry(registryConfig);

        SofaRpcEndPoint.RpcServicesDescriptor rpcServicesDescriptor = sofaRpcEndPoint.rpcServices();
        List<SofaRpcEndPoint.RegistryDescriptor> registryDescriptors = rpcServicesDescriptor.getRegistry();
        assertThat(registryDescriptors.size()).isEqualTo(1);
        SofaRpcEndPoint.RegistryDescriptor descriptor = registryDescriptors.get(0);

        assertThat(descriptor.getProtocol()).isEqualTo("mesh");
        assertThat(descriptor.getAddress()).isEqualTo("http://127.0.0.1:8888");
        assertThat(descriptor.getIndex()).isEqualTo("abc");

    }

}
