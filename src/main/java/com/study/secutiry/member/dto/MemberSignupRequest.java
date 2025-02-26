package com.study.secutiry.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberSignupRequest(
        @NotBlank(message = "아이디는 필수 입력값입니다")
        @Size(min = 4, max = 20, message = "아이디는 4~20자 사이여야 합니다")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "아이디는 영문, 숫자, 언더스코어만 가능합니다")
        String username,

        @NotBlank(message = "비밀번호는 필수 입력값입니다")
        @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이여야 합니다")
        String password,

        @NotBlank(message = "이메일은 필수 입력값입니다")
        @Email(message = "유효한 이메일 형식이 아닙니다")
        String email
        ) {
}
