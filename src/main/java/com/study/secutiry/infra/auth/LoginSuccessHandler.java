package com.study.secutiry.infra.auth;

import com.study.secutiry.jpa.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        log.info("[로그인 성공 핸들러 호출] authentication={}", authentication);

        // 사용자 정보 업데이트
        String username = authentication.getName();
        String ip = getClientIp(request);
        log.debug("[로그인 사용자 정보] username={}, ip={}", username, ip);

        memberRepository
                .findByUsername(username)
                .ifPresent(member -> {
                    member.updateLoginInfo(ip);
                    memberRepository.save(member);
                });

        // 세션에 로그인 시간 저장
        HttpSession session = request.getSession();
        session.setAttribute("loginTime", LocalDateTime.now());
        log.debug("[세션 저장] loginTime={}", session.getAttribute("loginTime"));

        // 저장된 요청 확인 및 처리
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        if (savedRequest != null) {
            String redirectUrl = savedRequest.getRedirectUrl();
            // 에러 페이지로의 리다이렉트는 무시하고 기본 URL로 리다이렉트
            if (redirectUrl != null && redirectUrl.contains("/error")) {
                clearAuthenticationAttributes(request);
                getRedirectStrategy().sendRedirect(request, response, "/");
                return;
            }
        }



        // 기본 성공 처리 (리다이렉트 등)
        super.onAuthenticationSuccess(request, response, authentication);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

}
