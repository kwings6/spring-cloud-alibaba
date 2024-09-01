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

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceInstance;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.metrics.manager.MetricsManager;
import com.alibaba.cloud.nacos.metrics.registry.RpcStepMeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.cumulative.CumulativeCounter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.core.instrument.step.StepCounter;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;
import io.micrometer.core.instrument.MeterRegistry;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ReactiveInterceptor implements ExchangeFilterFunction {
    @Autowired
    private RpcStepMeterRegistry rpcStepMeterRegistry;
    private static final Logger log = LoggerFactory.getLogger(ReactiveInterceptor.class);

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {

        log.info("traffic exec interceptor...");

        // request filed.
        HttpMethod method = request.method();
        URI url = request.url();
        BodyInserter<?, ? super ClientHttpRequest> body = request.body();
        Map<String, Object> attributes = request.attributes();
        log.info("request filed: method:{}, url:{}, body:{}, attributes:{}", method, url, body, attributes);

        return next.exchange(request)
                .doOnSuccess(response -> {
                    // 这里可以对响应进行拦截和处理
                    // 例如：打印响应状态码、响应头等
                    Counter qpsCounterRes = Counter.builder("spring-cloud.rpc.reactive.qps.response")
                            .description("Spring Cloud Alibaba QPS metrics when use Reactive RPC Call.")
                            .baseUnit(TimeUnit.SECONDS.name())
                            .tag("sca.reactive.rpc.method", method.name())
                            .tag("sca.reactive.rpc.url", url.toString())
//                            .tag("sca.reactive.rpc.body", body.toString())
//                            .tag("sca.reactive.rpc.attributes", attributes.toString())
                            .tag("sca.reactibe.status", response.statusCode().toString())
                            .register(rpcStepMeterRegistry);
                    qpsCounterRes.increment();
                    // 如果需要进一步处理响应体，可以在这里进行
                    // 注意：响应体需要被消费，否则可能会导致连接泄漏
                    response.bodyToMono(String.class)
                            .doOnNext(System.out::println)
                            .subscribe();
                })
                .doOnError(error -> {
                    // 这里可以对发生的错误进行处理
                   log.warn("Error occurred: " + error.getMessage());
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
