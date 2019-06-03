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

package com.alipay.sofa.rpc.boot.swagger;

import com.alipay.sofa.rpc.boot.runtime.binding.RpcBindingType;
import com.alipay.sofa.runtime.SofaFramework;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.oas.integration.GenericOpenApiContext;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.OpenApiContextLocator;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.models.OpenAPI;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author khotyn
 */
public class SwaggerServiceImpl implements SwaggerService {
    private OpenAPI     openapi;
    private Set<String> restfulServices;

    @Override
    public String openapi() {
        if (openapi == null) {
            synchronized (this) {
                if (openapi == null) {
                    openapi = buildOpenApi();
                }
            }
        } else {
            if (!getAllRestfulService().equals(restfulServices)) {
                synchronized (this) {
                    if (!getAllRestfulService().equals(restfulServices)) {
                        openapi = updateOpenApi();
                    }
                }
            }
        }

        return Json.pretty(openapi);

    }

    private OpenAPI updateOpenApi() {
        OpenApiContext openApiContext = OpenApiContextLocator.getInstance().getOpenApiContext(
            OpenApiContext.OPENAPI_CONTEXT_ID_DEFAULT);
        if (openApiContext instanceof GenericOpenApiContext) {
            restfulServices = getAllRestfulService();
            SwaggerConfiguration oasConfig = new SwaggerConfiguration().resourceClasses(restfulServices);
            ((GenericOpenApiContext) openApiContext).getOpenApiScanner().setConfiguration(oasConfig);
            try {
                ((GenericOpenApiContext) openApiContext).setCacheTTL(0);
                return openApiContext.read();
            } finally {
                ((GenericOpenApiContext) openApiContext).setCacheTTL(-1);
            }
        } else {
            return null;
        }
    }

    private OpenAPI buildOpenApi() {
        try {
            restfulServices = getAllRestfulService();
            SwaggerConfiguration oasConfig = new SwaggerConfiguration().resourceClasses(restfulServices);

            OpenApiContext oac = new JaxrsOpenApiContextBuilder()
                .openApiConfiguration(oasConfig)
                .buildContext(true);
            return oac.read();
        } catch (OpenApiConfigurationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private Set<String> getAllRestfulService() {
        return SofaFramework.getRuntimeSet().stream()
                .map(srm -> srm.getComponentManager().getComponentInfosByType(ServiceComponent.SERVICE_COMPONENT_TYPE))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet())
                .stream()
                .filter(ci -> {
                    ServiceComponent sc = (ServiceComponent) ci;
                    return sc.getService().getBinding(RpcBindingType.REST_BINDING_TYPE) != null;
                })
                .map(sc -> ((ServiceComponent) sc).getService().getInterfaceType())
                .map(Class::getName).collect(Collectors.toSet());
    }
}
