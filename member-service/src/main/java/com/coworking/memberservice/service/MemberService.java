package com.coworking.memberservice.service;

import com.coworking.memberservice.model.Member;
import com.coworking.memberservice.model.SubscriptionType;
import com.coworking.memberservice.repository.MemberRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    private static final String TOPIC_MEMBER_DELETED = "member-deleted";

    private final MemberRepository memberRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public MemberService(MemberRepository memberRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.memberRepository = memberRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Membre introuvable : " + id));
    }

    public Member createMember(Member member) {
        member.setSuspended(false);
        member.setMaxConcurrentBookings(resolveMaxBookings(member.getSubscriptionType()));
        return memberRepository.save(member);
    }

    public Member updateMember(Long id, Member updated) {
        Member member = getMemberById(id);
        member.setFullName(updated.getFullName());
        member.setEmail(updated.getEmail());
        if (updated.getSubscriptionType() != null) {
            member.setSubscriptionType(updated.getSubscriptionType());
            member.setMaxConcurrentBookings(resolveMaxBookings(updated.getSubscriptionType()));
        }
        return memberRepository.save(member);
    }

    public void deleteMember(Long id) {
        getMemberById(id);
        kafkaTemplate.send(TOPIC_MEMBER_DELETED, id.toString());
        memberRepository.deleteById(id);
    }

    public Member updateSuspended(Long id, boolean suspended) {
        Member member = getMemberById(id);
        member.setSuspended(suspended);
        return memberRepository.save(member);
    }

    private int resolveMaxBookings(SubscriptionType type) {
        return switch (type) {
            case BASIC -> 2;
            case PRO -> 5;
            case ENTERPRISE -> 10;
        };
    }
}
