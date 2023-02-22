package com.alipay.sofa.runtime.spring;

import com.alipay.sofa.runtime.api.annotation.SofaClientFactory;
import com.alipay.sofa.runtime.api.client.ClientFactory;
import com.alipay.sofa.runtime.api.client.ServiceClient;
import com.alipay.sofa.runtime.impl.ClientFactoryImpl;
import com.alipay.sofa.runtime.service.client.ServiceClientImpl;
import com.alipay.sofa.runtime.spi.client.ClientFactoryInternal;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link ClientFactoryAnnotationBeanPostProcessor}.
 *
 * @author huzijie
 * @version ClientFactoryAnnotationBeanPostProcessorTests.java, v 0.1 2023年02月22日 11:02 AM huzijie Exp $
 */
public class ClientFactoryAnnotationBeanPostProcessorTests {

    private final ClientFactoryInternal clientFactoryInternal = new ClientFactoryImpl();

    private final ClientFactoryAnnotationBeanPostProcessor clientFactoryAnnotationBeanPostProcessor =
            new ClientFactoryAnnotationBeanPostProcessor(clientFactoryInternal);

    @Test
    public void checkInjectClientFactory() {
        ClientFactoryAnnotationBean bean = new ClientFactoryAnnotationBean();
        assertThat(bean.getClientFactory()).isNull();
        assertThat(bean.getClientFactoryInternal()).isNull();

        clientFactoryAnnotationBeanPostProcessor.postProcessBeforeInitialization(bean, "bean");

        assertThat(bean.getClientFactory()).isEqualTo(clientFactoryInternal);
        assertThat(bean.getClientFactoryInternal()).isEqualTo(clientFactoryInternal);
    }

    @Test
    public void checkInjectServiceClient() {
        ServiceClientAnnotationBean bean = new ServiceClientAnnotationBean();
        assertThat(bean.getServiceClient()).isNull();

        assertThatThrownBy(() -> clientFactoryAnnotationBeanPostProcessor.postProcessBeforeInitialization(bean, "bean"))
                .isInstanceOf(RuntimeException.class).hasMessageContaining("01-02000");

        ServiceClient serviceClient = new ServiceClientImpl(null, null, null);
        clientFactoryInternal.registerClient(ServiceClient.class, serviceClient);
        clientFactoryAnnotationBeanPostProcessor.postProcessBeforeInitialization(bean, "bean");
        assertThat(bean.getServiceClient()).isEqualTo(serviceClient);
    }

    static class ClientFactoryAnnotationBean {

        @SofaClientFactory
        private ClientFactory clientFactory;

        @SofaClientFactory
        private ClientFactoryInternal clientFactoryInternal;


        public ClientFactory getClientFactory() {
            return clientFactory;
        }

        public ClientFactoryInternal getClientFactoryInternal() {
            return clientFactoryInternal;
        }
    }

    static class ServiceClientAnnotationBean {

        @SofaClientFactory
        private ServiceClient serviceClient;

        public ServiceClient getServiceClient() {
            return serviceClient;
        }
    }
}
