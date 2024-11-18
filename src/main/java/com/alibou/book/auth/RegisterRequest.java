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
public class RegisterRequest {

    @NotEmpty(message = "Vorname darf nicht leer sein")
    @NotBlank(message = "Vorname darf nicht leer sein")
    private String firstname;

    @NotEmpty(message = "Nachname darf nicht leer sein")
    @NotBlank(message = "Nachname darf nicht leer sein")
    private String lastname;

    @Email
    @NotEmpty(message = "Email darf nicht leer sein")
    @NotBlank(message = "Email darf nicht leer sein")
    private String email;

    @Size(min = 6, message = "Mindestans 6 zeichen erforderlich!")
    @NotEmpty(message = "Passwort darf nicht leer sein")
    @NotBlank(message = "Passwort darf nicht leer sein")
    private String password;
}
