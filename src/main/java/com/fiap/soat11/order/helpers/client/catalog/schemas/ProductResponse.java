package com.fiap.soat11.order.helpers.client.catalog.schemas;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ProductResponse(
        UUID id,

        String name,

        String description,

        BigDecimal price,

        @JsonProperty("image_url")
        String imageUrl,

        @JsonProperty("preparation_time")
        Integer preparationTime,

        @JsonProperty("category_id")
        UUID categoryId,

        @JsonProperty("created_at")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX")
        OffsetDateTime createdAt,

        @JsonProperty("updated_at")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX")
        OffsetDateTime updatedAt
) {}
