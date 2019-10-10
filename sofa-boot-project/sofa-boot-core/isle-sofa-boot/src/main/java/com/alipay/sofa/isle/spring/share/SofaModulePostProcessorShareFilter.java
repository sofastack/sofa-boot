package com.alipay.sofa.isle.spring.share;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import java.util.Collections;
import java.util.List;

/**
 * Created by TomorJM on 2019-10-09.
 */
public interface SofaModulePostProcessorShareFilter {

    /**
     * filter {@link BeanPostProcessor} to avoid being added to submodules
     * @return
     */
    public default List<Class<? extends BeanPostProcessor>> filterBeanPostProessorClass() {
        return Collections.EMPTY_LIST;
    }

    /**
     * filter {@link BeanFactoryPostProcessor} to avoid being added to submodules
     * @return
     */
    public default List<Class<? extends BeanFactoryPostProcessor>> filterBeanFactoryPostProessorClass() {
        return Collections.EMPTY_LIST;
    }

    /**
     * filter beans with the name in the list to avoid being added to submodules
     * @return
     */
    public default List<String> filterBeanName() {
        return Collections.EMPTY_LIST;
    }


}
