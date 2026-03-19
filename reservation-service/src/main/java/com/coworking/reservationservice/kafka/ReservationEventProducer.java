package com.coworking.reservationservice.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ReservationEventProducer {

    private static final String TOPIC_MEMBER_SUSPENDED = "member-suspended";
    private static final String TOPIC_MEMBER_UNSUSPENDED = "member-unsuspended";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public ReservationEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMemberSuspended(Long memberId) {
        kafkaTemplate.send(TOPIC_MEMBER_SUSPENDED, memberId.toString());
    }

    public void sendMemberUnsuspended(Long memberId) {
        kafkaTemplate.send(TOPIC_MEMBER_UNSUSPENDED, memberId.toString());
    }
}
