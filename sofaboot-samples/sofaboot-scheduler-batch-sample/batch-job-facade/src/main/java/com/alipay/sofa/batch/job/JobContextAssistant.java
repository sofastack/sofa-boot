package com.alipay.sofa.batch.job;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

/**
 * Provide convenience method for developers using batch-job-facade.
 *
 * @author tzeyong
 * Date: 2019-04-18
 * Time: 15:18
 */
public class JobContextAssistant {
    private static TimeBasedGenerator GENERATOR = Generators.timeBasedGenerator();

    /**
     * Generate a time based UUID, where the newer uuid generated will be greater than any previously generated uuid
     *
     * @return a String for time based uuid
     */
    public static String generateJobRequestId() {
        String tempStr = GENERATOR.generate().toString();
        String[] parts = tempStr.split("-");
        StringBuilder sb = new StringBuilder();
        for (int i = (parts.length - 1); i >= 0; i--) {
            sb.append(parts[i]);
        }
        return sb.toString();
    }
}
