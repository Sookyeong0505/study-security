package com.study.secutiry.infra.auth;

import com.study.secutiry.jpa.MemberRepository;
import com.study.secutiry.jpa.entity.AccountStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final MemberRepository memberRepository;
    private static final int MAX_ATTEMPTS = 5;

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        log.info("[로그인 실패 핸들러 호출] exception={}", exception.getMessage());

        String username = request.getParameter("username");
        AtomicReference<String> errorMessage = new AtomicReference<>("아이디 또는 비밀번호가 일치하지 않습니다.");

        // 실패 원인에 따른 메시지 설정
        if (exception instanceof BadCredentialsException) {
            // 로그인 실패 횟수 증가
            memberRepository.findByUsername(username).ifPresent(member -> {
                member.incrementLoginAttempts();

                // 최대 시도 횟수 초과 시 계정 잠금 처리
                if (member.getLoginAttempts() >= MAX_ATTEMPTS) {
                    member.setStatus(AccountStatus.SUSPENDED);
                    errorMessage.set("계정이 잠겼습니다. 관리자에게 문의하세요.");
                }
                memberRepository.save(member);
            });
        } else if (exception instanceof LockedException) {
            errorMessage.set("계정이 잠겼습니다. 관리자에게 문의하세요.");
        } else if (exception instanceof DisabledException) {
            errorMessage.set("비활성화된 계정입니다.");
        } else if (exception instanceof CredentialsExpiredException) {
            errorMessage.set("비밀번호 유효기간이 만료되었습니다. 비밀번호를 변경해주세요.");
        }

        // 실패 URL에 에러 메시지 추가
        setDefaultFailureUrl("/login?error=true&message=" + errorMessage);
        super.onAuthenticationFailure(request, response, exception);
    }

}
