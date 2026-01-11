package com.manikanda.common;

public class KafkaConfigProperties {

    public static final String PAYMENT_EVENT_TOPIC = "payment-events";
    public static final String PAYMENT_EVENT_GROUP = "payment-event-group";
    public static final String UPDATE_PAYMENT_READ_REPO_EVENT_TOPIC = "update-seat-read-repo";


    public static final String SEAT_RESERVED_TOPIC = "seat-reserved-topic";
    public static final String SEAT_RESERVED_CMD_TOPIC = "seat-reserved-commands";
    public static final String SEAT_EVENT_GROUP = "seat-event-group";
    public static final String UPDATE_SEAT_READ_REPO_EVENT_TOPIC = "update-seat-read-repo";


    public static final String MOVIE_BOOKING_EVENTS_TOPIC = "movie-booking-events";
    public static final String MOVIE_BOOKING_GROUP = "movie-booking-group";
    public static final String UPDATE_READ_REPO_EVENT_TOPIC = "update-read-repo";


    public static final String ORCHESTRATOR_CONSUMER_GROUP = "orchestrator-event-group";

}
