package app.pigrest.member.service;

import app.pigrest.common.ApiStatusCode;
import app.pigrest.exception.ResourceNotFoundException;
import app.pigrest.member.model.Member;
import app.pigrest.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Member getMember(String username) {
        return memberRepository.findByAuthUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(ApiStatusCode.USER_NOT_FOUND, "User not found"));
    }
}
