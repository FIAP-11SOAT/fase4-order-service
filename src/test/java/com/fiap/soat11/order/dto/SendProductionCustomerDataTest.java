package com.fiap.soat11.order.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class SendProductionCustomerDataTest {

    @Test
    void shouldCreateSendProductionCustomerData() {
        String id = "customer-123";
        String name = "John Doe";

        SendProductionCustomerData data = new SendProductionCustomerData(id, name);

        assertNotNull(data);
        assertEquals(id, data.id());
        assertEquals(name, data.name());
    }

    @Test
    void shouldHandleNullValues() {
        SendProductionCustomerData data = new SendProductionCustomerData(null, null);

        assertNotNull(data);
        assertEquals(null, data.id());
        assertEquals(null, data.name());
    }

    @Test
    void shouldHandleAnonymousCustomer() {
        SendProductionCustomerData data = new SendProductionCustomerData("anonymous", "anonymous");

        assertNotNull(data);
        assertEquals("anonymous", data.id());
        assertEquals("anonymous", data.name());
    }
}
