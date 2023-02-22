package com.alipay.sofa.boot.util;

import com.alipay.sofa.common.log.CommonLoggingConfigurations;
import com.alipay.sofa.common.log.Constants;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Util to enable console log for specific loggers.
 *
 * @author huzijie
 * @version LogOutPutUtils.java, v 0.1 2023年02月22日 5:53 PM huzijie Exp $
 */
public class LogOutPutUtils {

    public static void openOutPutForLoggers(String ... loggers) {
        openOutPutForLoggers(Arrays.asList(loggers));
    }

    public static void openOutPutForLoggers(Class<?> ... classes) {
        openOutPutForLoggers(Arrays.stream(classes).map(Class::getName).collect(Collectors.toList()));
    }

    public static void openOutPutForLoggers(Collection<String> loggers) {
        CommonLoggingConfigurations.loadExternalConfiguration(Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_SWITCH, "true");
        CommonLoggingConfigurations.addAllConsoleLogger(new HashSet<>(loggers));
    }
}
