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
package com.alipay.sofa.test.mock.injector.definition;

import com.alipay.sofa.test.mock.injector.annotation.MockBeanInjector;
import com.alipay.sofa.test.mock.injector.annotation.SpyBeanInjector;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.annotation.MergedAnnotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * see {@link org.springframework.boot.test.mock.mockito.QualifierDefinition}
 *
 * @author huzijie
 * @version QualifierDefinition.java, v 0.1 2023年08月15日 10:57 AM huzijie Exp $
 */
public class QualifierDefinition {

    private final Field                field;

    private final DependencyDescriptor descriptor;

    private final Set<Annotation>      annotations;

    public QualifierDefinition(Field field, Set<Annotation> annotations) {
        // We can't use the field or descriptor as part of the context key
        // but we can assume that if two fields have the same qualifiers then
        // it's safe for Spring to use either for qualifier logic
        this.field = field;
        this.descriptor = new DependencyDescriptor(field, true);
        this.annotations = annotations;
    }

    public boolean matches(ConfigurableListableBeanFactory beanFactory, String beanName) {
        return beanFactory.isAutowireCandidate(beanName, this.descriptor);
    }

    public void applyTo(RootBeanDefinition definition) {
        definition.setQualifiedElement(this.field);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || !getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        QualifierDefinition other = (QualifierDefinition) obj;
        return this.annotations.equals(other.annotations);
    }

    @Override
    public int hashCode() {
        return this.annotations.hashCode();
    }

    public static QualifierDefinition forElement(AnnotatedElement element) {
        if (element != null && element instanceof Field field) {
            Set<Annotation> annotations = getQualifierAnnotations(field);
            if (!annotations.isEmpty()) {
                return new QualifierDefinition(field, annotations);
            }
        }
        return null;
    }

    private static Set<Annotation> getQualifierAnnotations(Field field) {
        // Assume that any annotations other than @MockBean/@SpyBean are qualifiers
        Annotation[] candidates = field.getDeclaredAnnotations();
        Set<Annotation> annotations = new HashSet<>(candidates.length);
        for (Annotation candidate : candidates) {
            if (!isMockOrSpyAnnotation(candidate.annotationType())) {
                annotations.add(candidate);
            }
        }
        return annotations;
    }

    private static boolean isMockOrSpyAnnotation(Class<? extends Annotation> type) {
        if (type.equals(MockBeanInjector.class) || type.equals(SpyBeanInjector.class)) {
            return true;
        }
        MergedAnnotations metaAnnotations = MergedAnnotations.from(type);
        return metaAnnotations.isPresent(MockBeanInjector.class)
               || metaAnnotations.isPresent(SpyBeanInjector.class);
    }

}
