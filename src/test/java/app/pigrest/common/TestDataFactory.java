package app.pigrest.common;

import app.pigrest.auth.domain.Auth;
import app.pigrest.content.domain.Draft;
import app.pigrest.content.domain.Image;
import app.pigrest.member.domain.Member;

public class TestDataFactory {
    public static Member createMember() {
        Auth auth = Auth.of("test_piggy", "encodedPassword");
        return Member.of("piggy", auth);
    }

    public static Image createImage() {
        return Image.of("test");
    }

    public static Draft createDraft(Member member) {
        Image image = createImage();
        return Draft.createFromImage(member, image);
    }
}
