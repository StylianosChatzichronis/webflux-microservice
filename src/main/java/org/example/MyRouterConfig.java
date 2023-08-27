package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;


@Configuration
public class MyRouterConfig {


    @Value("${app.base}")
    private String baseUrl;

    @Value("${app.post}")
    private String post;

    @Value("${app.get}")
    private String get;

    @Bean
    public WebClient webClient() {
        return WebClient.create();
    }

    @Bean
    public RouterFunction<ServerResponse> myRouterFunction() {
        return RouterFunctions.route()
                .nest(POST("/post"), builder -> builder
                        .POST("", this::handlePostRequest)
                )
                .nest(GET("/get"), builder -> builder
                        .GET("", this::handleGetRequest)
                )
                .build();
    }

    private Mono<ServerResponse> handlePostRequest(ServerRequest request) {
        WebClient client = webClient();
        return client.post()
                .uri(baseUrl + post)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request.bodyToMono(Obj1.class), Obj1.class)
                .exchange()
                .flatMap(response -> response.bodyToMono(String.class))
                .flatMap(responseString -> {
                    // Parse the response JSON to extract the "data" field
                    String dataField = "";

                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = null;
                    try {
                        jsonNode = objectMapper.readTree(responseString);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    JsonNode dataNode = jsonNode.get("data");
                    if (dataNode != null) {
                        dataField = dataNode.textValue();
                    }
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .syncBody(dataField);
                });
    }

    private Mono<ServerResponse> handleGetRequest(ServerRequest request) {
        String parameterValue = request.queryParam("param").orElse("default_value");
        // You can use the parameterValue in your logic
        return ok().contentType(MediaType.APPLICATION_JSON).bodyValue("GET Request with parameter: " + parameterValue);
    }
}
