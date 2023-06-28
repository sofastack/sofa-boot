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
package com.alipay.sofa.runtime.filter;

import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link JvmFilterHolder}.
 *
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * @author huzijie
 * Created on 2020/8/18
 */
@ExtendWith(MockitoExtension.class)
public class JvmFilterHolderTests {

    @Mock
    private SofaRuntimeContext sofaRuntimeContext;

    @Test
    public void sortJvmFilter() {
        JvmFilterHolder jvmFilterHolder = new JvmFilterHolder();
        JvmFilter jvmFilter1 = new OrderedJvmFilter(10);
        JvmFilter jvmFilter2 = new OrderedJvmFilter(10);
        jvmFilterHolder.addJvmFilter(jvmFilter1);
        jvmFilterHolder.addJvmFilter(jvmFilter2);
        List<JvmFilter> filters = jvmFilterHolder.getJvmFilters();
        assertThat(filters.get(0)).isEqualTo(jvmFilter1);
        assertThat(filters.get(1)).isEqualTo(jvmFilter2);

        JvmFilter jvmFilter3 = new OrderedJvmFilter(0);
        jvmFilterHolder.addJvmFilter(jvmFilter3);
        filters = jvmFilterHolder.getJvmFilters();
        assertThat(filters.get(0)).isEqualTo(jvmFilter3);
    }

    @Test
    public void invokeJvmFilter() {
        JvmFilterHolder jvmFilterHolder = new JvmFilterHolder();
        NormalJvmFilter jvmFilter1 = new NormalJvmFilter();
        NormalJvmFilter jvmFilter2 = new NormalJvmFilter();
        jvmFilterHolder.addJvmFilter(jvmFilter1);
        jvmFilterHolder.addJvmFilter(jvmFilter2);

        assertThat(jvmFilter1.isBeforeInvoked()).isFalse();
        assertThat(jvmFilter2.isBeforeInvoked()).isFalse();
        assertThat(jvmFilter1.isAfterInvoked()).isFalse();
        assertThat(jvmFilter2.isAfterInvoked()).isFalse();

        JvmFilterContext jvmFilterContext = new JvmFilterContext();
        jvmFilterContext.setSofaRuntimeContext(sofaRuntimeContext);
        Mockito.doReturn(jvmFilterHolder).when(sofaRuntimeContext).getJvmFilterHolder();
        JvmFilterHolder.beforeInvoking(jvmFilterContext);
        JvmFilterHolder.afterInvoking(jvmFilterContext);

        assertThat(jvmFilter1.isBeforeInvoked()).isTrue();
        assertThat(jvmFilter2.isBeforeInvoked()).isTrue();
        assertThat(jvmFilter1.isAfterInvoked()).isTrue();
        assertThat(jvmFilter2.isAfterInvoked()).isTrue();
    }

    @Test
    public void interruptJvmFilter() {
        JvmFilterHolder jvmFilterHolder = new JvmFilterHolder();
        NormalJvmFilter jvmFilter1 = new NormalJvmFilter();
        InterruptedJvmFilter jvmFilter2 = new InterruptedJvmFilter(-1);
        InterruptedJvmFilter jvmFilter3 = new InterruptedJvmFilter(1);
        jvmFilterHolder.addJvmFilter(jvmFilter1);
        jvmFilterHolder.addJvmFilter(jvmFilter2);
        jvmFilterHolder.addJvmFilter(jvmFilter3);

        assertThat(jvmFilter1.isBeforeInvoked()).isFalse();
        assertThat(jvmFilter1.isAfterInvoked()).isFalse();

        JvmFilterContext jvmFilterContext = new JvmFilterContext();
        jvmFilterContext.setSofaRuntimeContext(sofaRuntimeContext);
        Mockito.doReturn(jvmFilterHolder).when(sofaRuntimeContext).getJvmFilterHolder();
        JvmFilterHolder.beforeInvoking(jvmFilterContext);
        JvmFilterHolder.afterInvoking(jvmFilterContext);

        assertThat(jvmFilter1.isBeforeInvoked()).isFalse();
        assertThat(jvmFilter1.isAfterInvoked()).isFalse();

    }

    static class NormalJvmFilter implements JvmFilter {

        private boolean beforeInvoked;

        private boolean afterInvoked;

        @Override
        public boolean before(JvmFilterContext context) {
            beforeInvoked = true;
            return true;
        }

        @Override
        public boolean after(JvmFilterContext context) {
            afterInvoked = true;
            return true;
        }

        @Override
        public int getOrder() {
            return 0;
        }

        public boolean isBeforeInvoked() {
            return beforeInvoked;
        }

        public boolean isAfterInvoked() {
            return afterInvoked;
        }
    }

    static class OrderedJvmFilter implements JvmFilter {

        private final int order;

        public OrderedJvmFilter(int order) {
            this.order = order;
        }

        @Override
        public boolean before(JvmFilterContext context) {
            return true;
        }

        @Override
        public boolean after(JvmFilterContext context) {
            return true;
        }

        @Override
        public int getOrder() {
            return this.order;
        }
    }

    static class InterruptedJvmFilter implements JvmFilter {

        private final int order;

        public InterruptedJvmFilter(int order) {
            this.order = order;
        }

        @Override
        public boolean before(JvmFilterContext context) {
            return false;
        }

        @Override
        public boolean after(JvmFilterContext context) {
            return false;
        }

        @Override
        public int getOrder() {
            return this.order;
        }
    }
}
