package com.alibou.book.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationRequest {

    @Email(message = "Email muss gültig sein")
    @NotEmpty(message = "Vorname darf nicht leer sein")
    @NotBlank(message = "Vorname darf nicht leer sein")
    private String email;

    @Size(min=6, message="Mindestans 6 zeichen erforderlich!")
    @NotEmpty(message = "Vorname darf nicht leer sein")
    @NotBlank(message = "Vorname darf nicht leer sein")
    private String password;

}
