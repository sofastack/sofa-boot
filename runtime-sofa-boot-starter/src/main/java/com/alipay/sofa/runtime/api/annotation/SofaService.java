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
package com.alipay.sofa.runtime.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to create a SOFA service of a spring bean. Sample usage:
 *
 * <pre>
 *
 * &#064;SofaService(uniqueId = &quot;aop&quot;)
 * public class SampleServiceImpl implements SampleService {
 *
 *     &#064;Override
 *     public String say() {
 *         return &quot;sampleService&quot;;
 *     }
 * }
 * </pre>
 *
 * @author xuanbei 18/3/1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SofaService {

    /**
     * The interface type of the SOFA service to be create. Default to the only interface of the annotated Spring bean
     * when not specified. When the annotated Spring bean has more than one interface, this field must be specified.
     * When you want to create a SOFA service which's interface type is not a java interface but and concrete java
     * class, this field must be specified.
     */
    Class<?> interfaceType() default void.class;

    /**
     * The unique id of the SOFA service to be created. Default to an empty string when not specified.
     */
    String uniqueId() default "";
}
