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
package com.alipay.sofa.boot.logging.filter;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

import com.alipay.sofa.common.log.spi.Log4j2FilterGenerator;

/**
 * @author qilong.zql
 * @since 1.0.15
 */
public class DefaultLog4j2FilterGenerator implements Log4j2FilterGenerator {

    public static final Filter FILTER = new AbstractFilter() {

                                          @Override
                                          public Result filter(LogEvent event) {
                                              return Result.DENY;
                                          }

                                          @Override
                                          public Result filter(Logger logger, Level level,
                                                               Marker marker, Message msg,
                                                               Throwable t) {
                                              return Result.DENY;
                                          }

                                          @Override
                                          public Result filter(Logger logger, Level level,
                                                               Marker marker, Object msg,
                                                               Throwable t) {
                                              return Result.DENY;
                                          }

                                          @Override
                                          public Result filter(Logger logger, Level level,
                                                               Marker marker, String msg,
                                                               Object... params) {
                                              return Result.DENY;
                                          }

                                      };

    @Override
    public Filter[] generatorFilters() {
        return new Filter[] { FILTER };
    }
}