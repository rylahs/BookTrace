package org.booktrace.member.endpoint.controller;

import lombok.Data;

@Data
public class SignUpForm {
    private String email;
    private String password;
    private String nickname;
}
