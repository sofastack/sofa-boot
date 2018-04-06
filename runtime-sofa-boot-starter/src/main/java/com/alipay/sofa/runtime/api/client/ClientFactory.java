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

/**
 * A SOFA programming API's client factory. There are two ways to get an instance of this class in a Spring bean:
 * <ol>
 * <li>Annotate a field of the type {@link ClientFactory} with annotation
 * {@link com.alipay.sofa.runtime.api.annotation.SofaClientFactory}.</li>
 * <li>Implement the {@link com.alipay.sofa.runtime.api.aware.ClientFactoryAware} interface.</li>
 * </ol>
 *
 * @author xuanbei 18/2/28
 */
public interface ClientFactory {

    /**
     * Get an instance of a specific SOFA programming API client such as {@link ReferenceClient} or
     * {@link ServiceClient}.
     *
     * @param klass The type of the client to get.
     * @param <T> The type of the client to get.
     * @return The instance of the client that matches the type specified.
     */
    <T> T getClient(Class<T> klass);
}