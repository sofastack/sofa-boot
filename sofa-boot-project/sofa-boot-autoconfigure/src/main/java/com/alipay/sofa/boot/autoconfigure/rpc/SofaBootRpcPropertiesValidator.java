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
            "Bolt 端口必须在 1024 到 65535 之间");
        valid = validatePort(context, valid, "h2cPort", value.getH2cPort(),
            "H2C 端口必须在 1024 到 65535 之间");
        valid = validatePort(context, valid, "restPort", value.getRestPort(),
            "REST 端口必须在 1024 到 65535 之间");
        valid = validatePort(context, valid, "dubboPort", value.getDubboPort(),
            "Dubbo 端口必须在 1024 到 65535 之间");
        valid = validatePort(context, valid, "httpPort", value.getHttpPort(),
            "HTTP 端口必须在 1024 到 65535 之间");
        valid = validatePort(context, valid, "triplePort", value.getTriplePort(),
            "Triple 端口必须在 1024 到 65535 之间");
        valid = validatePort(context, valid, "virtualPort", value.getVirtualPort(),
            "虚拟发布端口必须在 1024 到 65535 之间");

        valid = validatePositiveInteger(context, valid, "boltThreadPoolCoreSize",
            value.getBoltThreadPoolCoreSize(), "Bolt 线程池核心线程数必须大于 0");
        valid = validatePositiveInteger(context, valid, "boltThreadPoolMaxSize",
            value.getBoltThreadPoolMaxSize(), "Bolt 线程池最大线程数必须大于 0");
        valid = validatePositiveInteger(context, valid, "boltThreadPoolQueueSize",
            value.getBoltThreadPoolQueueSize(), "Bolt 线程池队列长度必须大于 0");
        valid = validatePositiveInteger(context, valid, "boltAcceptsSize",
            value.getBoltAcceptsSize(), "Bolt 允许建立的连接数必须大于 0");
        valid = validatePositiveInteger(context, valid, "h2cThreadPoolCoreSize",
            value.getH2cThreadPoolCoreSize(), "H2C 线程池核心线程数必须大于 0");
        valid = validatePositiveInteger(context, valid, "h2cThreadPoolMaxSize",
            value.getH2cThreadPoolMaxSize(), "H2C 线程池最大线程数必须大于 0");
        valid = validatePositiveInteger(context, valid, "h2cThreadPoolQueueSize",
            value.getH2cThreadPoolQueueSize(), "H2C 线程池队列长度必须大于 0");
        valid = validatePositiveInteger(context, valid, "h2cAcceptsSize",
            value.getH2cAcceptsSize(), "H2C 允许建立的连接数必须大于 0");
        valid = validatePositiveInteger(context, valid, "restIoThreadSize",
            value.getRestIoThreadSize(), "REST IO 线程数必须大于 0");
        valid = validatePositiveInteger(context, valid, "restThreadPoolMaxSize",
            value.getRestThreadPoolMaxSize(), "REST 线程池最大线程数必须大于 0");
        valid = validatePositiveInteger(context, valid, "restMaxRequestSize",
            value.getRestMaxRequestSize(), "REST 最大请求体大小必须大于 0");
        valid = validatePositiveInteger(context, valid, "dubboIoThreadSize",
            value.getDubboIoThreadSize(), "Dubbo IO 线程数必须大于 0");
        valid = validatePositiveInteger(context, valid, "dubboThreadPoolMaxSize",
            value.getDubboThreadPoolMaxSize(), "Dubbo 线程池最大线程数必须大于 0");
        valid = validatePositiveInteger(context, valid, "dubboAcceptsSize",
            value.getDubboAcceptsSize(), "Dubbo 允许建立的连接数必须大于 0");
        valid = validatePositiveInteger(context, valid, "httpThreadPoolCoreSize",
            value.getHttpThreadPoolCoreSize(), "HTTP 线程池核心线程数必须大于 0");
        valid = validatePositiveInteger(context, valid, "httpThreadPoolMaxSize",
            value.getHttpThreadPoolMaxSize(), "HTTP 线程池最大线程数必须大于 0");
        valid = validatePositiveInteger(context, valid, "httpThreadPoolQueueSize",
            value.getHttpThreadPoolQueueSize(), "HTTP 线程池队列长度必须大于 0");
        valid = validatePositiveInteger(context, valid, "httpAcceptsSize",
            value.getHttpAcceptsSize(), "HTTP 允许建立的连接数必须大于 0");
        valid = validatePositiveInteger(context, valid, "tripleThreadPoolCoreSize",
            value.getTripleThreadPoolCoreSize(), "Triple 线程池核心线程数必须大于 0");
        valid = validatePositiveInteger(context, valid, "tripleThreadPoolMaxSize",
            value.getTripleThreadPoolMaxSize(), "Triple 线程池最大线程数必须大于 0");
        valid = validatePositiveInteger(context, valid, "tripleThreadPoolQueueSize",
            value.getTripleThreadPoolQueueSize(), "Triple 线程池队列长度必须大于 0");
        valid = validatePositiveInteger(context, valid, "tripleAcceptsSize",
            value.getTripleAcceptsSize(), "Triple 允许建立的连接数必须大于 0");
        valid = validatePositiveInteger(context, valid, "consumerRepeatedReferenceLimit",
            value.getConsumerRepeatedReferenceLimit(), "重复引用限制必须大于 0");

        valid = validateBooleanString(context, valid, "aftRegulationEffective",
            value.getAftRegulationEffective(), "AFT 单机故障剔除开关必须为 true 或 false");
        valid = validateBooleanString(context, valid, "aftDegradeEffective",
            value.getAftDegradeEffective(), "AFT 降级开关必须为 true 或 false");
        valid = validateBooleanString(context, valid, "restTelnet", value.getRestTelnet(),
            "REST telnet 开关必须为 true 或 false");
        valid = validateBooleanString(context, valid, "restDaemon", value.getRestDaemon(),
            "REST daemon 开关必须为 true 或 false");

        valid = validatePositiveInteger(context, valid, "aftTimeWindow", value.getAftTimeWindow(),
            "AFT 时间窗口必须大于 0");
        valid = validatePositiveInteger(context, valid, "aftLeastWindowCount",
            value.getAftLeastWindowCount(), "AFT 最小调用次数必须大于 0");
        valid = validatePositiveDecimal(context, valid, "aftLeastWindowExceptionRateMultiple",
            value.getAftLeastWindowExceptionRateMultiple(), "AFT 最小异常率倍数必须大于 0");
        valid = validateRange(context, valid, "aftDegradeLeastWeight",
            value.getAftDegradeLeastWeight(), 0, 100, "降级最小权重必须在 0 到 100 之间");
        valid = validatePositiveInteger(context, valid, "aftDegradeMaxIpCount",
            value.getAftDegradeMaxIpCount(), "AFT 最大降级 IP 数必须大于 0");
        valid = validateDecimalRange(context, valid, "aftWeightDegradeRate",
            value.getAftWeightDegradeRate(), BigDecimal.ZERO, false, BigDecimal.ONE, true,
            "降级速率必须大于 0 且不能大于 1");
        valid = validatePositiveDecimal(context, valid, "aftWeightRecoverRate",
            value.getAftWeightRecoverRate(), "恢复速率必须大于 0");

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
