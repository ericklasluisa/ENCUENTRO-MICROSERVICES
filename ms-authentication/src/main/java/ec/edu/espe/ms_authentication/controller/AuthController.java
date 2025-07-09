package ec.edu.espe.ms_authentication.controller;

import ec.edu.espe.ms_authentication.dto.LoginDto;
import ec.edu.espe.ms_authentication.dto.RegisterDto;
import ec.edu.espe.ms_authentication.dto.RespuestaDto;
import ec.edu.espe.ms_authentication.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<RespuestaDto> login(@RequestBody LoginDto loginDto) {

        return ResponseEntity.ok(authService.login(loginDto));
    }

    @PostMapping("/register")
    public ResponseEntity<RespuestaDto> register(@RequestBody RegisterDto registerDto) {

        return ResponseEntity.ok(authService.register(registerDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RespuestaDto> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        return ResponseEntity.ok(authService.refreshToken(authHeader));
    }
}
