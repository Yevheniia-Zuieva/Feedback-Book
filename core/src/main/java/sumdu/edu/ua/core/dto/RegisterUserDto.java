package sumdu.edu.ua.core.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserDto {

    @Email(message = "Некоректний email")
    @NotBlank(message = "Email обов'язковий")
    private String email;

    @Size(min = 6, message = "Пароль має бути не менше 6 символів")
    private String password;

    private String nickname;
}