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

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * Validator for {@link SofaBootRpcProperties}.
 *
 * @author huzijie
 */
public class SofaBootRpcPropertiesValidator
                                           implements
                                           ConstraintValidator<ValidSofaBootRpcProperties, SofaBootRpcProperties> {

    @Override
    public boolean isValid(SofaBootRpcProperties value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        boolean valid = true;

        valid = validatePort(context, valid, "boltPort", value.getBoltPort(),
            "Bolt port must be between 1024 and 65535");
        valid = validatePort(context, valid, "h2cPort", value.getH2cPort(),
            "H2C port must be between 1024 and 65535");
        valid = validatePort(context, valid, "restPort", value.getRestPort(),
            "REST port must be between 1024 and 65535");
        valid = validatePort(context, valid, "dubboPort", value.getDubboPort(),
            "Dubbo port must be between 1024 and 65535");
        valid = validatePort(context, valid, "httpPort", value.getHttpPort(),
            "HTTP port must be between 1024 and 65535");
        valid = validatePort(context, valid, "triplePort", value.getTriplePort(),
            "Triple port must be between 1024 and 65535");
        valid = validatePort(context, valid, "virtualPort", value.getVirtualPort(),
            "Virtual port must be between 1024 and 65535");

        valid = validatePositiveInteger(context, valid, "boltThreadPoolCoreSize",
            value.getBoltThreadPoolCoreSize(), "Bolt thread pool core size must be greater than 0");
        valid = validatePositiveInteger(context, valid, "boltThreadPoolMaxSize",
            value.getBoltThreadPoolMaxSize(), "Bolt thread pool max size must be greater than 0");
        valid = validatePositiveInteger(context, valid, "boltThreadPoolQueueSize",
            value.getBoltThreadPoolQueueSize(),
            "Bolt thread pool queue size must be greater than 0");
        valid = validatePositiveInteger(context, valid, "boltAcceptsSize",
            value.getBoltAcceptsSize(), "Bolt accepts size must be greater than 0");
        valid = validatePositiveInteger(context, valid, "h2cThreadPoolCoreSize",
            value.getH2cThreadPoolCoreSize(), "H2C thread pool core size must be greater than 0");
        valid = validatePositiveInteger(context, valid, "h2cThreadPoolMaxSize",
            value.getH2cThreadPoolMaxSize(), "H2C thread pool max size must be greater than 0");
        valid = validatePositiveInteger(context, valid, "h2cThreadPoolQueueSize",
            value.getH2cThreadPoolQueueSize(), "H2C thread pool queue size must be greater than 0");
        valid = validatePositiveInteger(context, valid, "h2cAcceptsSize",
            value.getH2cAcceptsSize(), "H2C accepts size must be greater than 0");
        valid = validatePositiveInteger(context, valid, "restIoThreadSize",
            value.getRestIoThreadSize(), "REST IO thread size must be greater than 0");
        valid = validatePositiveInteger(context, valid, "restThreadPoolMaxSize",
            value.getRestThreadPoolMaxSize(), "REST thread pool max size must be greater than 0");
        valid = validatePositiveInteger(context, valid, "restMaxRequestSize",
            value.getRestMaxRequestSize(), "REST max request size must be greater than 0");
        valid = validatePositiveInteger(context, valid, "dubboIoThreadSize",
            value.getDubboIoThreadSize(), "Dubbo IO thread size must be greater than 0");
        valid = validatePositiveInteger(context, valid, "dubboThreadPoolMaxSize",
            value.getDubboThreadPoolMaxSize(), "Dubbo thread pool max size must be greater than 0");
        valid = validatePositiveInteger(context, valid, "dubboAcceptsSize",
            value.getDubboAcceptsSize(), "Dubbo accepts size must be greater than 0");
        valid = validatePositiveInteger(context, valid, "httpThreadPoolCoreSize",
            value.getHttpThreadPoolCoreSize(), "HTTP thread pool core size must be greater than 0");
        valid = validatePositiveInteger(context, valid, "httpThreadPoolMaxSize",
            value.getHttpThreadPoolMaxSize(), "HTTP thread pool max size must be greater than 0");
        valid = validatePositiveInteger(context, valid, "httpThreadPoolQueueSize",
            value.getHttpThreadPoolQueueSize(),
            "HTTP thread pool queue size must be greater than 0");
        valid = validatePositiveInteger(context, valid, "httpAcceptsSize",
            value.getHttpAcceptsSize(), "HTTP accepts size must be greater than 0");
        valid = validatePositiveInteger(context, valid, "tripleThreadPoolCoreSize",
            value.getTripleThreadPoolCoreSize(),
            "Triple thread pool core size must be greater than 0");
        valid = validatePositiveInteger(context, valid, "tripleThreadPoolMaxSize",
            value.getTripleThreadPoolMaxSize(),
            "Triple thread pool max size must be greater than 0");
        valid = validatePositiveInteger(context, valid, "tripleThreadPoolQueueSize",
            value.getTripleThreadPoolQueueSize(),
            "Triple thread pool queue size must be greater than 0");
        valid = validatePositiveInteger(context, valid, "tripleAcceptsSize",
            value.getTripleAcceptsSize(), "Triple accepts size must be greater than 0");
        valid = validatePositiveInteger(context, valid, "consumerRepeatedReferenceLimit",
            value.getConsumerRepeatedReferenceLimit(),
            "Consumer repeated reference limit must be greater than 0");

        valid = validateBooleanString(context, valid, "aftRegulationEffective",
            value.getAftRegulationEffective(), "AFT regulation switch must be true or false");
        valid = validateBooleanString(context, valid, "aftDegradeEffective",
            value.getAftDegradeEffective(), "AFT degrade switch must be true or false");
        valid = validateBooleanString(context, valid, "restTelnet", value.getRestTelnet(),
            "REST telnet switch must be true or false");
        valid = validateBooleanString(context, valid, "restDaemon", value.getRestDaemon(),
            "REST daemon switch must be true or false");

        valid = validatePositiveInteger(context, valid, "aftTimeWindow", value.getAftTimeWindow(),
            "AFT time window must be greater than 0");
        valid = validatePositiveInteger(context, valid, "aftLeastWindowCount",
            value.getAftLeastWindowCount(), "AFT least window count must be greater than 0");
        valid = validatePositiveDecimal(context, valid, "aftLeastWindowExceptionRateMultiple",
            value.getAftLeastWindowExceptionRateMultiple(),
            "AFT least window exception rate multiple must be greater than 0");
        valid = validateRange(context, valid, "aftDegradeLeastWeight",
            value.getAftDegradeLeastWeight(), 0, 100,
            "AFT degrade least weight must be between 0 and 100");
        valid = validatePositiveInteger(context, valid, "aftDegradeMaxIpCount",
            value.getAftDegradeMaxIpCount(), "AFT degrade max IP count must be greater than 0");
        valid = validateDecimalRange(context, valid, "aftWeightDegradeRate",
            value.getAftWeightDegradeRate(), BigDecimal.ZERO, false, BigDecimal.ONE, true,
            "AFT weight degrade rate must be greater than 0 and less than or equal to 1");
        valid = validatePositiveDecimal(context, valid, "aftWeightRecoverRate",
            value.getAftWeightRecoverRate(), "AFT weight recover rate must be greater than 0");

        return valid;
    }

    private boolean validatePort(ConstraintValidatorContext context, boolean valid,
                                 String fieldName, String rawValue, String message) {
        return validateRange(context, valid, fieldName, rawValue, 1024, 65535, message);
    }

    private boolean validatePositiveInteger(ConstraintValidatorContext context, boolean valid,
                                            String fieldName, String rawValue, String message) {
        if (!StringUtils.hasText(rawValue)) {
            return valid;
        }
        Integer parsedValue = parseInteger(rawValue);
        if (parsedValue == null || parsedValue <= 0) {
            addViolation(context, fieldName, message);
            return false;
        }
        return valid;
    }

    private boolean validateRange(ConstraintValidatorContext context, boolean valid,
                                  String fieldName, String rawValue, int min, int max,
                                  String message) {
        if (!StringUtils.hasText(rawValue)) {
            return valid;
        }
        Integer parsedValue = parseInteger(rawValue);
        if (parsedValue == null || parsedValue < min || parsedValue > max) {
            addViolation(context, fieldName, message);
            return false;
        }
        return valid;
    }

    private boolean validatePositiveDecimal(ConstraintValidatorContext context, boolean valid,
                                            String fieldName, String rawValue, String message) {
        if (!StringUtils.hasText(rawValue)) {
            return valid;
        }
        BigDecimal parsedValue = parseDecimal(rawValue);
        if (parsedValue == null || parsedValue.compareTo(BigDecimal.ZERO) <= 0) {
            addViolation(context, fieldName, message);
            return false;
        }
        return valid;
    }

    private boolean validateDecimalRange(ConstraintValidatorContext context, boolean valid,
                                         String fieldName, String rawValue, BigDecimal min,
                                         boolean minInclusive, BigDecimal max,
                                         boolean maxInclusive, String message) {
        if (!StringUtils.hasText(rawValue)) {
            return valid;
        }
        BigDecimal parsedValue = parseDecimal(rawValue);
        if (parsedValue == null) {
            addViolation(context, fieldName, message);
            return false;
        }

        boolean underMin = minInclusive ? parsedValue.compareTo(min) < 0 : parsedValue
            .compareTo(min) <= 0;
        boolean overMax = maxInclusive ? parsedValue.compareTo(max) > 0 : parsedValue
            .compareTo(max) >= 0;
        if (underMin || overMax) {
            addViolation(context, fieldName, message);
            return false;
        }
        return valid;
    }

    private boolean validateBooleanString(ConstraintValidatorContext context, boolean valid,
                                          String fieldName, String rawValue, String message) {
        if (!StringUtils.hasText(rawValue)) {
            return valid;
        }
        if (!"true".equalsIgnoreCase(rawValue) && !"false".equalsIgnoreCase(rawValue)) {
            addViolation(context, fieldName, message);
            return false;
        }
        return valid;
    }

    private Integer parseInteger(String rawValue) {
        try {
            return Integer.valueOf(rawValue);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private BigDecimal parseDecimal(String rawValue) {
        try {
            return new BigDecimal(rawValue);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void addViolation(ConstraintValidatorContext context, String fieldName, String message) {
        context.buildConstraintViolationWithTemplate(message).addPropertyNode(fieldName)
            .addConstraintViolation();
    }
}
