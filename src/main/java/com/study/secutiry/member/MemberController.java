package com.study.secutiry.member;

import com.study.secutiry.member.dto.MemberResponse;
import com.study.secutiry.member.dto.MemberSignupRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/signup")
    public String signupForm(Model model, @ModelAttribute("member") MemberSignupRequest request) {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("member") MemberSignupRequest request,
                         BindingResult result) {

        if (result.hasErrors()) {
            return "signup";
        }

        try {
            MemberResponse response = memberService.signup(request);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            result.rejectValue("username", "error.username", e.getMessage());
            return "signup";
        }
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }
}