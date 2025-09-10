package app.pigrest.common;

import app.pigrest.auth.domain.Auth;
import app.pigrest.content.domain.Draft;
import app.pigrest.content.domain.Image;
import app.pigrest.member.domain.Member;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class TestDataFactory {
    public static Member createMember() {
        return createMember("test_piggy");
    }

    public static Member createMember(String username) {
        Auth auth = Auth.of(username, "encodedPassword");
        return Member.of("piggy", auth);
    }

    public static Image createImage() {
        return Image.of("test", "https://s3.amazaon..");
    }

    public static Draft createDraft(Member member) {
        Image image = createImage();
        return Draft.createFromImage(member, image);
    }

    public static Draft createDraftFromCache(UUID id, Member member, Image image) {
        Instant now = Instant.now();
        return Draft.restoreFromCache(
                id,
                "캐시된 제목",
                "캐시된 내용",
                member,
                image,
                now.plus(7, ChronoUnit.DAYS),
                now,
                now
        );
    }
}
