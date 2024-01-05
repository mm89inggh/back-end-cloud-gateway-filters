package com.unir.gateway.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GatewayRequest {

    private HttpMethod targetMethod;
    private LinkedMultiValueMap<String, String> queryParams;
    private Object body;

    @JsonIgnore
    private ServerWebExchange exchange;

    @JsonIgnore
    private HttpHeaders headers;
}
