package app.pigrest.auth.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RegisterRequest {
    @NotBlank(message = "Username must not be blank")
    @Size(min = 4, max = 16, message = "Username must be between 4 and 16 characters")
    @Pattern(regexp = "^[a-z0-9_]+$", message = "Username must contain only lowercase letters, numbers, and underscores")
    private String username;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]+$",
        message = "Password must contain at least one letter, one number, and one special character from !@#$%^&*")
    private String password;

    @NotBlank(message = "Password confirmation must not be blank")
    private String passwordConfirm;

    @NotBlank(message = "Nickname must not be blank")
    @Size(min = 4, max = 12, message = "Nickname must be between 4 and 12 characters")
    private String nickname;

    @AssertTrue(message = "Password and confirmation must match")
    public boolean isPasswordMatching() {
        return password != null && password.equals(passwordConfirm);
    }
}
