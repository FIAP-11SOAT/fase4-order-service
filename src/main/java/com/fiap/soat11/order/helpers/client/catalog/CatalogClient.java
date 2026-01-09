package com.fiap.soat11.order.helpers.client.catalog;

import java.util.List;
import java.util.UUID;

import com.fiap.soat11.order.helpers.client.catalog.schemas.ProductResponse;

public interface CatalogClient {
    List<ProductResponse> getProducts();
    List<ProductResponse> getProductsByIds(List<UUID> productIds);
}
