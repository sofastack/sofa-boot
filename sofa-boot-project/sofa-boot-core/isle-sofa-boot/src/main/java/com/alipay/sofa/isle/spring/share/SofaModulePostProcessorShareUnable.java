package com.alipay.sofa.isle.spring.share;

import java.lang.annotation.*;

/**
 * use this annotation to avoid BeanFactoryPostProcessor/BeanPostProcessor added to submodules
 *
 * Created by TomorJM on 2019-10-09.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface SofaModulePostProcessorShareUnable {
}
