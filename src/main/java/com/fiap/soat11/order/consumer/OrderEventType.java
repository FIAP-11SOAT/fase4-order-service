package com.fiap.soat11.order.consumer;

public enum OrderEventType {
    PAYMENT_CREATED("payment-created-event"),
    PAYMENT_COMPLETED("payment-completed-event"),
    PAYMENT_FAILED("payment-failed-event"),
    PRODUCTION_STARTED("production-started-event"),
    PRODUCTION_COMPLETED("production-completed-event"),
    PRODUCTION_DELIVERED("production-delivered-event");

    private final String eventName;

    OrderEventType(String eventName) {
        this.eventName = eventName;
    }

    public String getEventName() {
        return eventName;
    }

    public static OrderEventType fromEventName(String eventName) {
        for (OrderEventType type : OrderEventType.values()) {
            if (type.eventName.equals(eventName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown event name: " + eventName);
    }
}
