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
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RestTemplateInterceptor implements ClientHttpRequestInterceptor{

    @Autowired
    private PrometheusMeterRegistry prometheusMeterRegistry;

    private Counter qpsCounter;
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        ClientHttpResponse response = execution.execute(request, body);


         qpsCounter = Counter.builder("spring-cloud.rpc.restTemplate.qps")
                .description("Spring Cloud Alibaba QPS metrics when use resTemplate RPC Call.")
                .baseUnit(TimeUnit.SECONDS.name())
                .tag("sca.resTemplate.rpc", "url: " + request.getURI()
                        + "  method: " + request.getMethod()
                        + "  status: " + response.getStatusCode())
                .register(prometheusMeterRegistry);


        qpsCounter.increment();

        return response;

    }

}
