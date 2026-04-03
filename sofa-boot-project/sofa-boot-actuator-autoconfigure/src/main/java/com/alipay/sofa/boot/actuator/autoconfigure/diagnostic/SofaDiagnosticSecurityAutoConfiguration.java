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
package com.alipay.sofa.boot.actuator.autoconfigure.diagnostic;

import com.alipay.sofa.boot.actuator.diagnostic.SofaDiagnosticEndpoint;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for securing the
 * {@link SofaDiagnosticEndpoint sofa-diagnostic} actuator endpoint.
 *
 * <p>Requires HTTP Basic authentication by default ({@code sensitive=true}).
 * Set {@code management.endpoint.sofa-diagnostic.sensitive=false} to allow public access.
 * Backs off when the application defines its own {@link SecurityFilterChain}.
 *
 * @author xiaosiyuan
 * @version SofaDiagnosticSecurityAutoConfiguration.java, v 0.1 2026年04月03日 xiaosiyuan Exp $
 */
@AutoConfiguration(before = { SecurityAutoConfiguration.class,
                             UserDetailsServiceAutoConfiguration.class,
                             ManagementWebSecurityAutoConfiguration.class })
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(SofaDiagnosticSecurityProperties.class)
public class SofaDiagnosticSecurityAutoConfiguration {

    /**
     * Security filter chain for the {@code sofa-diagnostic} actuator endpoint only.
     */
    @Bean
    public SecurityFilterChain sofaDiagnosticSecurityFilterChain(HttpSecurity http,
                                                                 SofaDiagnosticSecurityProperties properties)
                                                                                                       throws Exception {
        http.securityMatcher(EndpointRequest.to(SofaDiagnosticEndpoint.class))
            .authorizeHttpRequests(auth -> {
                if (properties.isSensitive()) {
                    auth.anyRequest().authenticated();
                } else {
                    auth.anyRequest().permitAll();
                }
            })
            // Programmatic REST endpoint; CSRF disabled.
            .csrf(csrf -> csrf.disable());
        if (properties.isSensitive()) {
            http.httpBasic(Customizer.withDefaults());
        }
        return http.build();
    }
}
