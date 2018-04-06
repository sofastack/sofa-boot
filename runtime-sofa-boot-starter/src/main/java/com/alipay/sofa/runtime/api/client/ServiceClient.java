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
package com.alipay.sofa.runtime.api.client;

import com.alipay.sofa.runtime.api.client.param.ServiceParam;

/**
 * This class is used to create a SOFA service. There are to ways to get an instance of this class in a Spring bean:
 * <ol>
 * <li>Get the instance of {@link ClientFactory}, then invoke {@link ClientFactory#getClient(Class)} to get the instance
 * of this class.</li>
 * <li>Annotate a field of type {@link ServiceClient} with annotation
 * {@link com.alipay.sofa.runtime.api.annotation.SofaClientFactory}.</li>
 * </ol>
 *
 * @author xuanbei 18/2/28
 */
public interface ServiceClient {

    /**
     * Creating a SOFA service, sample usage:
     *
     * <pre>
     * ServiceParam serviceParam = new ServiceParam();
     * SampleService sampleService = new SampleServiceImpl();
     * serviceParam.setInstance(sampleService);
     * serviceClient.service(serviceParam);
     * </pre>
     *
     * @param serviceParam The service parameter used to create a SOFA service.
     */
    void service(ServiceParam serviceParam);

    /**
     * Equivalent to {@link ServiceClient#removeService(Class, String, int)} except that the uniqueId parameter is
     * default to an empty string.
     */
    void removeService(Class<?> interfaceClass, int millisecondsToDelay);

    /**
     * Remove a service component from SOFA runtime.
     *
     * @param interfaceClass the interface type of the service component
     * @param uniqueId the uniqueId of the service component
     * @param millisecondsToDelay milliseconds to delay while remove a service component, only non-negative value is
     * accepted. it is useful when remove a RPC service component since remove registry info from config server is a
     * async-procedure
     */
    void removeService(Class<?> interfaceClass, String uniqueId, int millisecondsToDelay);
}
