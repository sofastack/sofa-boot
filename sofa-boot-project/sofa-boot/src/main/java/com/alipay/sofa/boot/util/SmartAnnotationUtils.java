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
package com.alipay.sofa.boot.util;

import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Utils to find suitable annotations.
 *
 * @author huzijie
 * @version SmartAnnotationUtils.java, v 0.1 2023年06月20日 5:44 PM huzijie Exp $
 */
public class SmartAnnotationUtils {

    /**
     * 使用 {@link MergedAnnotations.SearchStrategy#TYPE_HIERARCHY} 搜索策略获取最近的注解集合
     * <p> 如果元素上无注解，返回空的集合
     * <p> 如果元素上仅有一个注解，返回包含该注解的集合
     * <p> 如果元素上有多个注解，通过 {@link MergedAnnotations#get(Class)} 方法拿到最近的注解，返回的集合仅包含最近的注解所在元素上的注解
     *
     * @param element 注解所在元素
     * @param annotationType 注解类
     * @param <T> 注解类型
     * @return 注解集合，可能为空或者多个，如果存在多个注解，仅保留最高优先级元素上的注解
     */
    public static <T extends Annotation> Collection<T> getAnnotations(AnnotatedElement element,
                                                                      Class<T> annotationType) {
        return getAnnotations(element, annotationType,
            MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);
    }

    /**
     * 使用指定搜索策略获取最近的注解集合
     * <p> 如果元素上无注解，返回空的集合
     * <p> 如果元素上仅有一个注解，返回包含该注解的集合
     * <p> 如果元素上有多个注解，通过 {@link MergedAnnotations#get(Class)} 方法拿到最近的注解，返回的集合仅包含最近的注解所在元素上的注解
     *
     * @param element 注解所在元素
     * @param annotationType 注解类
     * @param searchStrategy 搜索策略
     * @param <T> 注解类型
     * @return 注解集合，可能为空或者多个，如果存在多个注解，仅保留最高优先级元素上的注解
     */
    public static <T extends Annotation> Collection<T> getAnnotations(AnnotatedElement element, Class<T> annotationType,
                                                                                MergedAnnotations.SearchStrategy searchStrategy) {
        MergedAnnotations annotations = MergedAnnotations.from(element, searchStrategy);
        List<T> sofaServiceList = annotations.stream(annotationType).map(MergedAnnotation::synthesize).collect(Collectors.toList());
        if (sofaServiceList.size() > 1) {
            // 如果存在多个注解，先通过 get 方法拿到最高优先级的注解所在的 element
            Object source = annotations.get(annotationType).getSource();
            // 排除非最高优先级 element 以外的注解
            return annotations.stream(annotationType)
                    .filter(annotation -> Objects.equals(annotation.getSource(), source))
                    .map(MergedAnnotation::synthesize).collect(Collectors.toList());
        } else {
            // 如果不存在注解或者只有一个注解，直接返回
            return sofaServiceList;
        }
    }
}
