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

package com.alibaba.cloud.seata.rest;

import java.io.IOException;

import com.alibaba.cloud.seata.feign.SeataFeignBuilderBeanPostProcessor;
import com.alibaba.cloud.seata.web.SeataHandlerInterceptor;
import io.micrometer.core.instrument.Counter;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.seata.core.context.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.util.StringUtils;

/**
 * @author xiaojing
 */
public class SeataRestTemplateInterceptor implements ClientHttpRequestInterceptor {


	@Override
	public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes,
			ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
		HttpRequestWrapper requestWrapper = new HttpRequestWrapper(httpRequest);

		String xid = RootContext.getXID();

		if (StringUtils.hasLength(xid)) {
			requestWrapper.getHeaders().add(RootContext.KEY_XID, xid);
		}


		return clientHttpRequestExecution.execute(requestWrapper, bytes);
	}

}
