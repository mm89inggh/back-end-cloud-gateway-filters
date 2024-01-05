package com.unir.gateway.decorator;

import com.unir.gateway.model.GatewayRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

import java.net.URI;

@Slf4j
public class GetRequestDecorator extends ServerHttpRequestDecorator {

    private final GatewayRequest gatewayRequest;


    public GetRequestDecorator(GatewayRequest gatewayRequest) {
        super(gatewayRequest.getExchange().getRequest());
        this.gatewayRequest = gatewayRequest;
    }

    @Override
    @NonNull
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    @NonNull
    public URI getURI() {
        return UriComponentsBuilder
                .fromUri(gatewayRequest.getExchange().getRequest().getURI())
                .queryParams(gatewayRequest.getQueryParams())
                .build()
                .toUri();
    }

    @Override
    @NonNull
    public HttpHeaders getHeaders() {
        return gatewayRequest.getHeaders();
    }

    @Override
    @NonNull
    public Flux<DataBuffer> getBody() {
        return Flux.empty();
    }
}
