package app.pigrest.member.service;

import app.pigrest.global.common.ApiStatusCode;
import app.pigrest.global.exception.ResourceNotFoundException;
import app.pigrest.member.dto.request.UpdateMemberRequest;
import app.pigrest.member.domain.Member;
import app.pigrest.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Member getMember(String username) {
        return memberRepository.findByAuthUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(ApiStatusCode.USER_NOT_FOUND, "User not found"));
    }

    @Transactional
    public void updateProfile(String username, UpdateMemberRequest request) {
        Member member = memberRepository.findByAuthUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(ApiStatusCode.USER_NOT_FOUND, "User not found"));
        member.updateProfile(request.getNickname(), request.getDescription());
    }

    @Transactional
    public void updateProfileImage(String username, String profileImageUrl) {
        Member member = memberRepository.findByAuthUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(ApiStatusCode.USER_NOT_FOUND, "User not found"));
        member.updateProfileImageUrl(profileImageUrl);
    }
}
