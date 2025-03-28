package app.pigrest.auth.service;

import app.pigrest.auth.dto.request.LoginRequest;
import app.pigrest.auth.dto.request.RegisterRequest;
import app.pigrest.auth.model.Auth;
import app.pigrest.auth.repository.AuthRepository;
import app.pigrest.member.model.Member;
import app.pigrest.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final AuthRepository authRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Member create(RegisterRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Auth auth = Auth.of(request.getUsername(), encodedPassword);
        authRepository.save(auth);

        Member member = Member.of(request.getUsername(), auth);
        memberRepository.save(member);

        return member;
    }

    public void login(LoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
