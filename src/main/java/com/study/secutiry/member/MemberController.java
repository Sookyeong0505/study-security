package com.study.secutiry.member;

import com.study.secutiry.member.dto.LoginRequest;
import com.study.secutiry.member.dto.MemberResponse;
import com.study.secutiry.member.dto.MemberSignupRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/signup")
    public String signupForm(@ModelAttribute("member") MemberSignupRequest request) {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("member") MemberSignupRequest request,
                         BindingResult result) {
        if (result.hasErrors()) {
            log.info("[회원가입 요청 에러 발생] errors={}", result);
            return "signup";
        }

        try {
            MemberResponse response = memberService.signup(request);
            log.info("[회원가입 성공] member={}", response);
            return "redirect:/login?registered=true";
        } catch (IllegalArgumentException e) {
            result.rejectValue("username", "error.username", e.getMessage());
            log.info("[회원가입 처리 실패] errors={}", result);
            return "signup";
        }
    }

    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String message,
                            @RequestParam(required = false) boolean registered,
                            @ModelAttribute("loginRequest") LoginRequest loginRequest,
                            Model model) {
        log.info("[로그인 요청] error={}, message={}, registered={}", error, message, registered);
        if (error != null) {
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", message != null ? message : "아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        if (registered) {
            model.addAttribute("registered", true);
            model.addAttribute("registeredMessage", "회원가입이 완료되었습니다. 로그인해주세요.");
        }
        return "login";
    }


}