package app.pigrest.member.dto.response;

import app.pigrest.auth.model.CustomUser;
import app.pigrest.member.model.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;
import java.util.logging.Logger;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class GetMemberResponse {
    private String username;
    private String nickname;
    private boolean isCurrentUser;
    private String description;
    private String profileImage;
    private String defaultImage;
    private int followersCount; // FIXME: 팔로우/팔로워 기능 추가 후 변경
    private int followingCount;

//    List<Board> boards; // FIXME: Board 기능 추가 후 변경

    public static GetMemberResponse of(Member member, CustomUser currentUser) {
        boolean isCurrentUser = Optional.ofNullable(currentUser)
                .map(customUser -> customUser.getUsername().equals(member.getAuth().getUsername()))
                .orElse(false);

        return GetMemberResponse.builder()
                .username(member.getAuth().getUsername())
                .nickname(member.getNickname())
                .isCurrentUser(isCurrentUser)
                .description(member.getDescription())
                .profileImage(member.getProfileImageUrl())
                .defaultImage("")
                .followersCount(0)
                .followingCount(0) // FIXME: 팔로우/팔로워 기능 추가후 변경
                .build();
    }
}
