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
import org.springframework.core.env.StandardEnvironment;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Tests for {@link SofaBootRpcPropertiesValidator}.
 *
 * @author huzijie
 */
public class SofaBootRpcPropertiesValidatorTests {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void nullPropertiesShouldBeValid() {
        SofaBootRpcPropertiesValidator propertiesValidator = new SofaBootRpcPropertiesValidator();
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        assertThat(propertiesValidator.isValid(null, context)).isTrue();
        verifyNoInteractions(context);
    }

    @Test
    void emptyPropertiesShouldBeValid() {
        Set<ConstraintViolation<SofaBootRpcProperties>> violations = validator
            .validate(newProperties());

        assertThat(violations).isEmpty();
    }

    @Test
    void boundaryValuesShouldBeValid() {
        SofaBootRpcProperties properties = newProperties();
        properties.setBoltPort("1024");
        properties.setVirtualPort("65535");
        properties.setBoltThreadPoolCoreSize("1");
        properties.setBoltThreadPoolMaxSize("1");
        properties.setBoltThreadPoolQueueSize("1");
        properties.setBoltAcceptsSize("1");
        properties.setConsumerRepeatedReferenceLimit("1");
        properties.setAftRegulationEffective("true");
        properties.setAftDegradeEffective("FALSE");
        properties.setRestTelnet("true");
        properties.setRestDaemon("false");
        properties.setAftTimeWindow("1");
        properties.setAftLeastWindowCount("1");
        properties.setAftLeastWindowExceptionRateMultiple("0.1");
        properties.setAftDegradeLeastWeight("100");
        properties.setAftDegradeMaxIpCount("1");
        properties.setAftWeightDegradeRate("1");
        properties.setAftWeightRecoverRate("0.1");

        Set<ConstraintViolation<SofaBootRpcProperties>> violations = validator.validate(properties);

        assertThat(violations).isEmpty();
    }

    @Test
    void invalidTextValuesShouldProduceFieldViolations() {
        SofaBootRpcProperties properties = newProperties();
        properties.setBoltPort("abc");
        properties.setBoltThreadPoolMaxSize("abc");
        properties.setAftRegulationEffective("enabled");
        properties.setRestDaemon("daemon");
        properties.setAftLeastWindowExceptionRateMultiple("NaN");
        properties.setAftWeightDegradeRate("fast");
        properties.setAftWeightRecoverRate("slow");

        Map<String, String> violations = messagesByField(validator.validate(properties));

        assertThat(violations)
            .containsEntry("boltPort", "Bolt port must be between 1024 and 65535")
            .containsEntry("boltThreadPoolMaxSize",
                "Bolt thread pool max size must be greater than 0")
            .containsEntry("aftRegulationEffective", "AFT regulation switch must be true or false")
            .containsEntry("restDaemon", "REST daemon switch must be true or false")
            .containsEntry("aftLeastWindowExceptionRateMultiple",
                "AFT least window exception rate multiple must be greater than 0")
            .containsEntry("aftWeightDegradeRate",
                "AFT weight degrade rate must be greater than 0 and less than or equal to 1")
            .containsEntry("aftWeightRecoverRate", "AFT weight recover rate must be greater than 0");
    }

    @Test
    void outOfRangeValuesShouldProduceFieldViolations() {
        SofaBootRpcProperties properties = newProperties();
        properties.setBoltPort("80");
        properties.setVirtualPort("65536");
        properties.setBoltThreadPoolCoreSize("0");
        properties.setConsumerRepeatedReferenceLimit("0");
        properties.setAftTimeWindow("0");
        properties.setAftDegradeLeastWeight("-1");
        properties.setAftWeightDegradeRate("0");
        properties.setAftWeightRecoverRate("0");

        Map<String, String> violations = messagesByField(validator.validate(properties));

        assertThat(violations)
            .containsEntry("boltPort", "Bolt port must be between 1024 and 65535")
            .containsEntry("virtualPort", "Virtual port must be between 1024 and 65535")
            .containsEntry("boltThreadPoolCoreSize",
                "Bolt thread pool core size must be greater than 0")
            .containsEntry("consumerRepeatedReferenceLimit",
                "Consumer repeated reference limit must be greater than 0")
            .containsEntry("aftTimeWindow", "AFT time window must be greater than 0")
            .containsEntry("aftDegradeLeastWeight",
                "AFT degrade least weight must be between 0 and 100")
            .containsEntry("aftWeightDegradeRate",
                "AFT weight degrade rate must be greater than 0 and less than or equal to 1")
            .containsEntry("aftWeightRecoverRate", "AFT weight recover rate must be greater than 0");
    }

    @Test
    void decimalValuesAboveMaxShouldBeRejected() {
        SofaBootRpcProperties properties = newProperties();
        properties.setAftWeightDegradeRate("1.1");

        Map<String, String> violations = messagesByField(validator.validate(properties));

        assertThat(violations).containsEntry("aftWeightDegradeRate",
            "AFT weight degrade rate must be greater than 0 and less than or equal to 1");
    }

    @Test
    void blankValuesShouldBeIgnored() {
        SofaBootRpcProperties properties = newProperties();
        properties.setBoltPort(" ");
        properties.setAftRegulationEffective(" ");
        properties.setAftWeightDegradeRate(" ");
        properties.setAftWeightRecoverRate(" ");

        Set<ConstraintViolation<SofaBootRpcProperties>> violations = validator.validate(properties);

        assertThat(violations).isEmpty();
    }

    @Test
    void invalidDecimalFormatShouldBeRejected() {
        SofaBootRpcProperties properties = newProperties();
        properties.setAftWeightDegradeRate(".");
        properties.setAftWeightRecoverRate(".");

        Map<String, String> violations = messagesByField(validator.validate(properties));

        assertThat(violations)
            .containsEntry("aftWeightDegradeRate",
                "AFT weight degrade rate must be greater than 0 and less than or equal to 1")
            .containsEntry("aftWeightRecoverRate", "AFT weight recover rate must be greater than 0");
    }

    @Test
    void inclusiveAndExclusiveDecimalBoundariesShouldBeHandled() throws Exception {
        SofaBootRpcPropertiesValidator propertiesValidator = new SofaBootRpcPropertiesValidator();
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class,
            RETURNS_DEEP_STUBS);

        assertThat(
            invokeValidateDecimalRange(propertiesValidator, context, "-0.1", BigDecimal.ZERO, true,
                BigDecimal.ONE, false)).isFalse();
        assertThat(
            invokeValidateDecimalRange(propertiesValidator, context, "0", BigDecimal.ZERO, true,
                BigDecimal.ONE, false)).isTrue();
        assertThat(
            invokeValidateDecimalRange(propertiesValidator, context, "1", BigDecimal.ZERO, true,
                BigDecimal.ONE, false)).isFalse();
    }

    private SofaBootRpcProperties newProperties() {
        SofaBootRpcProperties properties = new SofaBootRpcProperties();
        properties.setEnvironment(new StandardEnvironment());
        return properties;
    }

    private boolean invokeValidateDecimalRange(SofaBootRpcPropertiesValidator propertiesValidator,
                                               ConstraintValidatorContext context, String rawValue,
                                               BigDecimal min, boolean minInclusive,
                                               BigDecimal max, boolean maxInclusive)
                                                                                    throws Exception {
        Method method = SofaBootRpcPropertiesValidator.class.getDeclaredMethod(
            "validateDecimalRange", ConstraintValidatorContext.class, boolean.class, String.class,
            String.class, BigDecimal.class, boolean.class, BigDecimal.class, boolean.class,
            String.class);
        method.setAccessible(true);
        return (boolean) method.invoke(propertiesValidator, context, true, "field", rawValue, min,
            minInclusive, max, maxInclusive, "message");
    }

    private Map<String, String> messagesByField(
                                               Set<ConstraintViolation<SofaBootRpcProperties>> violations) {
        return violations.stream().collect(Collectors.toMap(
            violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage,
            (left, right) -> left));
    }
}
