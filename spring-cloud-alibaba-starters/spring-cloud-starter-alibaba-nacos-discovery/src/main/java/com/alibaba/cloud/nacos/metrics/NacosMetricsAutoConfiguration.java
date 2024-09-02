/*
 * Copyright 2013-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.cloud.nacos.metrics;

import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.metrics.aop.ReactiveBeanPostProcessor;
import com.alibaba.cloud.nacos.metrics.aop.RestBeanPostProcessor;
import com.alibaba.cloud.nacos.metrics.aop.interceptor.OpenFeignInterceptor;
import com.alibaba.cloud.nacos.metrics.aop.interceptor.ReactiveInterceptor;
import com.alibaba.cloud.nacos.metrics.aop.interceptor.RestTemplateInterceptor;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration(proxyBeanMethods = false)
@ConditionalOnDiscoveryEnabled
@ConditionalOnNacosDiscoveryEnabled
public class NacosMetricsAutoConfiguration {

	@Bean
	InitializingBean forcePrometheusPostProcessor(BeanPostProcessor meterRegistryPostProcessor, PrometheusMeterRegistry registry) {
		return () -> meterRegistryPostProcessor.postProcessAfterInitialization(registry, "");
	}

	@ConditionalOnClass(WebClient.class)
	@ConditionalOnBean(WebClient.Builder.class)
	protected static class NacosMetricsReactiveConfiguration {

		@Bean
		@ConditionalOnMissingBean
		public ReactiveInterceptor reactiveInterceptor() {
			return new ReactiveInterceptor();
		}

		@Bean
		public ReactiveBeanPostProcessor reactiveBeanPostProcessor() {
			return new ReactiveBeanPostProcessor();
		}
	}

	@ConditionalOnClass(RestTemplate.class)
	@ConditionalOnBean(RestTemplate.class)
	protected static class NacosMetricsRestConfiguration {

		@Bean
		@ConditionalOnMissingBean
		public RestTemplateInterceptor restTemplateInterceptor() {
			return new RestTemplateInterceptor();
		}

		@Bean
		public RestBeanPostProcessor restBeanPostProcessor() {
			return new RestBeanPostProcessor();
		}
	}

	@ConditionalOnClass(RestTemplate.class)
	@ConditionalOnBean(RestTemplate.class)
	protected static class NacosMetricsOpenFeignConfiguration {

		@Bean
		@ConditionalOnMissingBean
		public OpenFeignInterceptor openFeignInterceptor() {
			return new OpenFeignInterceptor();
		}

	}

}
