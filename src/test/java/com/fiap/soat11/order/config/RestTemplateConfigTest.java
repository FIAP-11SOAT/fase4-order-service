package com.fiap.soat11.order.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class RestTemplateConfigTest {

    @Test
    void testRestTemplateBeanCreation() {
        RestTemplateConfig config = new RestTemplateConfig();
        RestTemplate restTemplate = config.restTemplate();
        
        assertNotNull(restTemplate, "RestTemplate should not be null");
        assertInstanceOf(RestTemplate.class, restTemplate, "Should be instance of RestTemplate");
    }
}
