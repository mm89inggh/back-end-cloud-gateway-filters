package com.unir.gateway.filter;

import com.unir.gateway.decorator.RequestDecoratorFactory;
import com.unir.gateway.model.GatewayRequest;
import com.unir.gateway.utils.RequestBodyExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * This class is a custom filter for the Spring Cloud Gateway. It is responsible for translating incoming requests.
 * It uses the RequestBodyExtractor to extract the body of the request and the RequestDecoratorFactory to create a decorator for the request.
 * The decorator is used to modify the request before it is forwarded to the downstream service.
 * If the incoming request does not have a content type, it is forwarded without any modifications.
 * Otherwise, the body of the request is joined into a single DataBuffer, and the request is mutated using the decorator before being forwarded.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RequestTranslationFilter implements GlobalFilter {

    private final RequestBodyExtractor requestBodyExtractor;
    private final RequestDecoratorFactory requestDecoratorFactory;

    /**
     * This method is the main filter method for the Spring Cloud Gateway.
     * It checks if the incoming request has a content type. If it does not, the request is forwarded without any modifications.
     * If the request does have a content type, the body of the request is joined into a single DataBuffer.
     * Then, the request is mutated using the decorator before being forwarded.
     *
     * @param exchange the current server web exchange
     * @param chain the gateway filter chain
     * @return a Mono<Void> that indicates when request handling is complete
     */
    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

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
    }
}
