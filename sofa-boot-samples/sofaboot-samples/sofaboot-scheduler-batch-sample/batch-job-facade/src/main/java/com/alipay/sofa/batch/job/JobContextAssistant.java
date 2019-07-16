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
