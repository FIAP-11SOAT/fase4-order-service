package com.fiap.soat11.order.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class SendProductionItemDataTest {

    @Test
    void shouldCreateSendProductionItemData() {
        String name = "Product 1";
        Integer quantity = 5;

        SendProductionItemData data = new SendProductionItemData(name, quantity);

        assertNotNull(data);
        assertEquals(name, data.name());
        assertEquals(quantity, data.quantity());
    }

    @Test
    void shouldHandleNullValues() {
        SendProductionItemData data = new SendProductionItemData(null, null);

        assertNotNull(data);
        assertEquals(null, data.name());
        assertEquals(null, data.quantity());
    }

    @Test
    void shouldHandleZeroQuantity() {
        SendProductionItemData data = new SendProductionItemData("Product", 0);

        assertNotNull(data);
        assertEquals("Product", data.name());
        assertEquals(0, data.quantity());
    }
}
