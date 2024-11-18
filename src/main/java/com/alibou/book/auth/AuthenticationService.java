package com.alibou.book.auth;

import com.alibou.book.email.EmailService;
import com.alibou.book.role.RoleRepository;
import com.alibou.book.security.JwtService;
import com.alibou.book.user.User;
import com.alibou.book.user.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final EmailService emailService;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequest request) throws Exception {

        try {
            var userRole = roleRepo.findByName("USER")
                    .orElseThrow(() -> new Exception("Role: USER wurde nicht initialisiert"));

            var user = User.builder()
                    .firstname(request.getFirstname())
                    .lastname(request.getLastname())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .accountLocked(false)
                    .enabled(true)
                    .roles(List.of(userRole))
                    .build();

            userRepo.save(user);
            String subject = "Willkomen bei der Application!";
            String body = String.format(
                    "Hallo %s,\n\nVielen Dank für Ihre Registrierung bei der Webseite.\n\nBeste Grüße,\nDas Team",
                    user.getFirstname());
            emailService.sendEMail(user.getEmail(), subject, body);

        } catch (Exception e) {
            throw new Exception("Fehler beim Registrieren des Users");
        }

    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        try {
            var auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(auth);
            var claims = new HashMap<String, Object>();
            var user = (User) auth.getPrincipal();
            claims.put("fullname", user.getFullname());
            var jwtToken = jwtService.generateToken(claims, user);

            return AuthenticationResponse.builder()
                    .success(true)
                    .token(jwtToken)
                    .build();
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Ungültige Anmeldedaten");
        }

    }
}
