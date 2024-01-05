package com.unir.gateway.filter;

import com.unir.gateway.decorator.RequestDecoratorFactory;
import com.unir.gateway.utils.RequestBodyExtractor;
import com.unir.gateway.model.GatewayRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MutationFilter extends AbstractGatewayFilterFactory<Object> {

    private final RequestBodyExtractor requestBodyExtractor;
    private final RequestDecoratorFactory requestDecoratorFactory;

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            if (exchange.getRequest().getHeaders().getContentType() == null) {
                return chain.filter(exchange);
            } else {
                return DataBufferUtils.join(exchange.getRequest().getBody())
                        .flatMap(dataBuffer -> {
                            GatewayRequest request = requestBodyExtractor.getRequest(exchange, dataBuffer);
                            ServerHttpRequest mutatedRequest = requestDecoratorFactory.getDecorator(request);
                            log.info("Proxying request: {} {}", mutatedRequest.getMethod(), mutatedRequest.getURI());
                            return chain.filter(exchange.mutate().request(mutatedRequest).build());
                        });
            }
        };
    }
}