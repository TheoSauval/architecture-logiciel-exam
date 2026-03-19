package com.coworking.memberservice.kafka;

import com.coworking.memberservice.model.Member;
import com.coworking.memberservice.repository.MemberRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MemberEventConsumer {

    private final MemberRepository memberRepository;

    public MemberEventConsumer(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @KafkaListener(topics = "member-suspended", groupId = "member-service-group")
    public void onMemberSuspended(String memberId) {
        memberRepository.findById(Long.parseLong(memberId)).ifPresent(member -> {
            member.setSuspended(true);
            memberRepository.save(member);
        });
    }

    @KafkaListener(topics = "member-unsuspended", groupId = "member-service-group")
    public void onMemberUnsuspended(String memberId) {
        memberRepository.findById(Long.parseLong(memberId)).ifPresent(member -> {
            member.setSuspended(false);
            memberRepository.save(member);
        });
    }
}
