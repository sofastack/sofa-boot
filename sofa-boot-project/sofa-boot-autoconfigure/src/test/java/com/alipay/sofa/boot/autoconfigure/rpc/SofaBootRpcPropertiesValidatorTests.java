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
package com.alipay.sofa.boot.autoconfigure.rpc;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.env.StandardEnvironment;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Tests for {@link SofaBootRpcPropertiesValidator}.
 */
public class SofaBootRpcPropertiesValidatorTests {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldAcceptNullProperties() {
        SofaBootRpcPropertiesValidator propertiesValidator = new SofaBootRpcPropertiesValidator();
        ConstraintValidatorContext context = Mockito.mock(ConstraintValidatorContext.class);

        assertThat(propertiesValidator.isValid(null, context)).isTrue();
        verifyNoInteractions(context);
    }

    @Test
    void shouldAcceptValidProperties() {
        Set<ConstraintViolation<SofaBootRpcProperties>> violations = this.validator
            .validate(createValidProperties());

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldIgnoreBlankValues() {
        SofaBootRpcProperties properties = createValidProperties();
        properties.setBoltPort(" ");
        properties.setBoltThreadPoolCoreSize("");
        properties.setAftRegulationEffective(" ");
        properties.setAftWeightRecoverRate("");

        Set<ConstraintViolation<SofaBootRpcProperties>> violations = this.validator
            .validate(properties);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldRejectOutOfRangeAndInvalidBooleanValues() {
        SofaBootRpcProperties properties = createValidProperties();
        properties.setBoltPort("80");
        properties.setBoltThreadPoolCoreSize("0");
        properties.setAftRegulationEffective("maybe");
        properties.setAftLeastWindowExceptionRateMultiple("-1");
        properties.setAftDegradeLeastWeight("101");
        properties.setAftWeightDegradeRate("0");

        Set<String> propertyNames = propertyNames(this.validator.validate(properties));

        assertThat(propertyNames).contains("boltPort", "boltThreadPoolCoreSize",
            "aftRegulationEffective", "aftLeastWindowExceptionRateMultiple",
            "aftDegradeLeastWeight", "aftWeightDegradeRate");
    }

    @Test
    void shouldRejectNonNumericValues() {
        SofaBootRpcProperties properties = createValidProperties();
        properties.setH2cPort("port");
        properties.setDubboThreadPoolMaxSize("many");
        properties.setAftWeightRecoverRate("fast");

        Set<String> propertyNames = propertyNames(this.validator.validate(properties));

        assertThat(propertyNames).contains("h2cPort", "dubboThreadPoolMaxSize",
            "aftWeightRecoverRate");
    }

    private Set<String> propertyNames(Set<ConstraintViolation<SofaBootRpcProperties>> violations) {
        return violations.stream().map((violation) -> violation.getPropertyPath().toString())
            .collect(Collectors.toSet());
    }

    private SofaBootRpcProperties createValidProperties() {
        SofaBootRpcProperties properties = new SofaBootRpcProperties();
        properties.setEnvironment(new StandardEnvironment());
        properties.setBoltPort("12200");
        properties.setH2cPort("12201");
        properties.setRestPort("12202");
        properties.setDubboPort("12203");
        properties.setHttpPort("12204");
        properties.setTriplePort("12205");
        properties.setVirtualPort("12206");

        properties.setBoltThreadPoolCoreSize("10");
        properties.setBoltThreadPoolMaxSize("20");
        properties.setBoltThreadPoolQueueSize("100");
        properties.setBoltAcceptsSize("500");
        properties.setH2cThreadPoolCoreSize("10");
        properties.setH2cThreadPoolMaxSize("20");
        properties.setH2cThreadPoolQueueSize("100");
        properties.setH2cAcceptsSize("500");
        properties.setRestIoThreadSize("8");
        properties.setRestThreadPoolMaxSize("20");
        properties.setRestMaxRequestSize("1024");
        properties.setDubboIoThreadSize("8");
        properties.setDubboThreadPoolMaxSize("20");
        properties.setDubboAcceptsSize("500");
        properties.setHttpThreadPoolCoreSize("10");
        properties.setHttpThreadPoolMaxSize("20");
        properties.setHttpThreadPoolQueueSize("100");
        properties.setHttpAcceptsSize("500");
        properties.setTripleThreadPoolCoreSize("10");
        properties.setTripleThreadPoolMaxSize("20");
        properties.setTripleThreadPoolQueueSize("100");
        properties.setTripleAcceptsSize("500");
        properties.setConsumerRepeatedReferenceLimit("10");

        properties.setAftRegulationEffective("true");
        properties.setAftDegradeEffective("false");
        properties.setRestTelnet("false");
        properties.setRestDaemon("true");
        properties.setAftTimeWindow("10");
        properties.setAftLeastWindowCount("20");
        properties.setAftLeastWindowExceptionRateMultiple("1.5");
        properties.setAftDegradeLeastWeight("20");
        properties.setAftDegradeMaxIpCount("5");
        properties.setAftWeightDegradeRate("0.5");
        properties.setAftWeightRecoverRate("1.5");
        return properties;
    }
}
