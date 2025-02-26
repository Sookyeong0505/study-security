package com.study.secutiry.member.dto;

import com.study.secutiry.jpa.entity.AccountStatus;
import com.study.secutiry.jpa.entity.MemberEntity;
import com.study.secutiry.jpa.entity.Role;

import java.time.LocalDateTime;

public class MemberResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private AccountStatus status;
    private LocalDateTime createdAt;

    public MemberResponse(MemberEntity entity) {
        this.id = entity.getId();
        this.username = entity.getUsername();
        this.email = entity.getEmail();
        this.role = entity.getRole();
        this.status = entity.getStatus();
        this.createdAt = entity.getCreatedAt();
    }
}
