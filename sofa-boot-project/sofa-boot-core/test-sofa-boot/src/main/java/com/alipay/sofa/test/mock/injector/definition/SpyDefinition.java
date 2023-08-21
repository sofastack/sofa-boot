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

import org.mockito.AdditionalAnswers;
import org.mockito.MockSettings;
import org.mockito.Mockito;
import org.mockito.listeners.VerificationStartedEvent;
import org.mockito.listeners.VerificationStartedListener;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.core.ResolvableType;
import org.springframework.core.style.ToStringCreator;
import org.springframework.test.util.AopTestUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Proxy;

import static org.mockito.Mockito.mock;

/**
 * A complete definition that can be used to create a Mockito spy.
 *
 * @author pengym
 * @version SpyDefinition.java, v 0.1 2023年08月07日 19:42 pengym
 */
public class SpyDefinition extends Definition {

    private static final int MULTIPLIER = 31;

    private final boolean    proxyTargetAware;

    public SpyDefinition(ResolvableType resolvableType, String name, ResolvableType type,
                         String module, String field, MockReset reset, boolean proxyTargetAware,
                         QualifierDefinition qualifier) {
        super(resolvableType, name, type, module, field, reset, qualifier);
        this.proxyTargetAware = proxyTargetAware;
    }

    @SuppressWarnings("unchecked")
    public <T> T createSpy(Object instance) {
        if (mockInstance == null) {
            Assert.notNull(instance, "Instance must not be null");
            Assert.isInstanceOf(this.getMockType().resolve(), instance);
            if (Mockito.mockingDetails(instance).isSpy()) {
                return (T) instance;
            }
            MockSettings settings = MockReset.withSettings(getReset());
            if (this.proxyTargetAware) {
                settings
                    .verificationStartedListeners(new SpyDefinition.SpringAopBypassingVerificationStartedListener());
            }
            Class<?> toSpy;
            if (Proxy.isProxyClass(instance.getClass())) {
                settings.defaultAnswer(AdditionalAnswers.delegatesTo(instance));
                toSpy = this.getMockType().toClass();
            } else {
                settings.defaultAnswer(Mockito.CALLS_REAL_METHODS);
                settings.spiedInstance(instance);
                toSpy = instance.getClass();
            }
            mockInstance = mock(toSpy, settings);
        }
        return (T) mockInstance;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        SpyDefinition other = (SpyDefinition) obj;
        boolean result = super.equals(obj);
        result = result && this.proxyTargetAware == other.proxyTargetAware;
        return result;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = MULTIPLIER * result + Boolean.hashCode(this.proxyTargetAware);
        return result;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("mockType", this.getMockType())
            .append("name", this.getName()).append("type", this.getType())
            .append("module", this.getModule()).append("field", this.getField())
            .append("proxyTargetAware", this.proxyTargetAware).append("reset", getReset())
            .append("qualifier", getQualifier()).toString();
    }

    /**
     * A {@link VerificationStartedListener} that bypasses any proxy created by Spring AOP
     * when the verification of a spy starts.
     */
    private static final class SpringAopBypassingVerificationStartedListener implements
                                                                            VerificationStartedListener {

        @Override
        public void onVerificationStarted(VerificationStartedEvent event) {
            event.setMock(AopTestUtils.getUltimateTargetObject(event.getMock()));
        }

    }
}
