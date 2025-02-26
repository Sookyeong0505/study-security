package com.study.secutiry.infra.auth;

import com.study.secutiry.jpa.MemberRepository;
import com.study.secutiry.jpa.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.debug("[CustomUserDetailsService#loadUserByUsername 호출] username={}", username);

        MemberEntity member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        log.debug("[조회된 사용자] member={}", member);

        return new User(
                member.getUsername(),
                member.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(member.getRole().getKey()))
        );
    }
}
