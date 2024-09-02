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
package com.alibaba.cloud.nacos.metrics.aop.interceptor;


import io.micrometer.core.instrument.Counter;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;

import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.concurrent.TimeUnit;

public class ReactiveInterceptor implements ExchangeFilterFunction {

	@Autowired
	private PrometheusMeterRegistry prometheusMeterRegistry;

	@Override
	public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {

		HttpMethod method = request.method();
		URI url = request.url();

		return next.exchange(request)
				.doOnSuccess(response -> {
					Counter qpsCounter = Counter.builder("spring-cloud.rpc.reactive.qps")
							.description("Spring Cloud Alibaba QPS metrics when use Reactive RPC Call.")
							.baseUnit(TimeUnit.SECONDS.name())
							.tag("sca.reactive.rpc.method", method.name())
							.tag("sca.reactive.rpc", "url: " + url
									+ "  method: " + method.name()
									+ "  status: " + response.statusCode())
							.register(prometheusMeterRegistry);

					qpsCounter.increment();

					response.bodyToMono(String.class)
							.doOnNext(System.out::println)
							.subscribe();
				})
				.doOnError(error -> {
				});

	}

	@Override
	public ExchangeFilterFunction andThen(ExchangeFilterFunction afterFilter) {
		return ExchangeFilterFunction.super.andThen(afterFilter);
	}

	@Override
	public ExchangeFunction apply(ExchangeFunction exchange) {
		return ExchangeFilterFunction.super.apply(exchange);
	}
}
