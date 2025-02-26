package com.study.secutiry.member;

import com.study.secutiry.jpa.MemberRepository;
import com.study.secutiry.jpa.entity.MemberEntity;
import com.study.secutiry.member.dto.MemberResponse;
import com.study.secutiry.member.dto.MemberSignupRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberResponse signup(MemberSignupRequest request) {
        log.debug("[MemberService#signup] request={}", request);

        // 중복 검사
        if (memberRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        if (memberRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 패스워드 암호화
        String encodedPassword = passwordEncoder.encode(request.password());

        // 회원 생성
        MemberEntity member = MemberEntity.builder()
                .username(request.username())
                .password(encodedPassword)
                .email(request.email())
                .build();

        MemberEntity savedMember = memberRepository.save(member);
        log.debug("[MemberService#signup] savedMember={}", savedMember);

        return new MemberResponse(savedMember);
    }
}
