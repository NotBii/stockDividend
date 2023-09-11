package com.zerobase.stock.web;

import com.zerobase.stock.model.Auth;
import com.zerobase.stock.security.TokenProvider;
import com.zerobase.stock.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

public class AuthController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    //가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Auth.SingUp request) {
        var result = this.memberService.register(request);
        return ResponseEntity.ok(result);
    }

    //로그인
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Auth.SignIn request) {
        var member = this.memberService.authenticate(request);
        var token = this.tokenProvider.generateToken(member.getUsername(), member.getRoles());
        log.info("user login -> " + request.getUsername());
        return ResponseEntity.ok(token);
    }

//    @PostMapping("/signout")
//    public ResponseEntity<?> signout(@)
}
