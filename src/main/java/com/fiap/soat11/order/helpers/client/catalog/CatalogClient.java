package com.fiap.soat11.order.helpers.client.catalog;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fiap.soat11.order.helpers.client.catalog.schemas.ProductResponse;

@Component
public class CatalogClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public CatalogClient(
        RestTemplate restTemplate, 
        @Value("${fase4.order.service.client.catalog.url}") String baseUrl
    ) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public List<ProductResponse> getProducts() {
        String url = baseUrl + "/products";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<ProductResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<ProductResponse>>() {
                });

        return response.getBody();
    }


    public List<ProductResponse> getProductsByIds(List<UUID> productIds) {
        List<ProductResponse> allProducts = getProducts();
        return allProducts.stream()
                .filter(product -> productIds.contains(product.id()))
                .toList();
    }
}
