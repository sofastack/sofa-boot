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
import org.springframework.beans.factory.annotation.Value;

/**
 * <p>
 * Annotation to get the whole SOFA configuration of current application or to get a specific configuration from the
 * SOFA configuration of current application.
 * </p>
 * <p>
 * Sample code of get the whole SOFA configuration of current application:
 *
 * <pre>
 *
 * public class SampleConfigurationBean implements AppConfigurationAware {
 *
 *     &#064;AppConfig
 *     private AppConfiguration appConf;
 *
 *     public String getWorldFormAnnotatedAppConfig() {
 *         return appConf.getPropertyValue(&quot;Hello&quot;);
 *     }
 * }
 * </pre>
 * </p>
 * <p>
 * Sample code of get a specific configuration from the SOFA configuration of current application:
 *
 * <pre>
 *
 * public class SampleConfigurationBean implements AppConfigurationAware {
 *
 *     &#064;AppConfig(&quot;Hello&quot;)
 *     private String world;
 *
 *     public String getWorld() {
 *         return world;
 *     }
 * }
 *
 * </pre>
 * </p>
 *
 * Deprecated, you should use {@link Value} instead.
 * @author xuanbei 18/3/5
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Deprecated
public @interface AppConfig {

    /**
     * The key of the configuration value to get from the SOFA configuration of current application. If this value is
     * not specified or is an empty string, {@link AppConfig} will inject to whole SOFA configuration of current
     * application to the annotated field.
     */
    String value() default "";
}
