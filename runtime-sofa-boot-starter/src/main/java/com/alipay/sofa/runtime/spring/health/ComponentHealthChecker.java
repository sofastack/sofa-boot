/**
 * Copyright Notice: This software is developed by Ant Small and Micro Financial Services Group Co., Ltd. This software and all the relevant information, including but not limited to any signs, images, photographs, animations, text, interface design,
 *  audios and videos, and printed materials, are protected by copyright laws and other intellectual property laws and treaties.
 *  The use of this software shall abide by the laws and regulations as well as Software Installation License Agreement/Software Use Agreement updated from time to time.
 *   Without authorization from Ant Small and Micro Financial Services Group Co., Ltd., no one may conduct the following actions:
 *
 *   1) reproduce, spread, present, set up a mirror of, upload, download this software;
 *
 *   2) reverse engineer, decompile the source code of this software or try to find the source code in any other ways;
 *
 *   3) modify, translate and adapt this software, or develop derivative products, works, and services based on this software;
 *
 *   4) distribute, lease, rent, sub-license, demise or transfer any rights in relation to this software, or authorize the reproduction of this software on other’s computers.
 */
package com.alipay.sofa.runtime.spring.health;

import com.alipay.boot.sofarpc.configuration.Slite2Configuration;
import com.alipay.sofa.healthcheck.core.DefaultHealthChecker;
import com.alipay.sofa.runtime.spi.SofaFrameworkHolder;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.health.HealthResult;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

/**
 * @author xuanbei 18/3/17
 */
@Component
public class ComponentHealthChecker extends DefaultHealthChecker {

    @Override
    public Health isHealthy() {
        boolean allPassed = true;
        Health.Builder builder = new Health.Builder();
        for (ComponentInfo componentInfo : SofaFrameworkHolder.getSofaFramework()
            .getSofaRuntimeContext(Slite2Configuration.getAppName()).getComponentManager().getComponents()) {
            HealthResult healthy = componentInfo.isHealthy();
            if (healthy.isHealthy()) {
                builder.withDetail(healthy.getHealthName(), "passed");
            } else {
                builder.withDetail(healthy.getHealthName(), healthy.getHealthReport());
                allPassed = false;
            }
        }

        if (allPassed) {
            return builder.status(Status.UP).build();
        } else {
            return builder.status(Status.DOWN).build();
        }
    }

    @Override
    public String getComponentName() {
        return "RUNTIME-COMPONENT";
    }
}
