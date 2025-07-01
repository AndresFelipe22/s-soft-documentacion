package com.softwares.controller;

import com.softwares.exceptions.SellerException;
import com.softwares.exceptions.UserException;
import com.softwares.models.VerificationCode;
import com.softwares.request.SignupRequest;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softwares.domain.USER_ROLE;
import com.softwares.repository.UserRepository;
import com.softwares.request.LoginRequest;
import com.softwares.response.ApiResponse;
import com.softwares.response.AuthResponse;
import com.softwares.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    /**
     * Envía un OTP de login o registro al correo del usuario.
     * @param req Objeto con el email del usuario.
     * @return Respuesta de éxito.
     */
    @Operation(summary = "Enviar OTP de login/registro", description = "Envía un OTP de login o registro al correo del usuario.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "OTP enviado correctamente")
    })
    @PostMapping("/sent/login-signup-otp")
    public ResponseEntity<ApiResponse> sentLoginOtp(
            @RequestBody VerificationCode req) throws MessagingException, UserException {

        authService.sentLoginOtp(req.getEmail());

        ApiResponse res = new ApiResponse();
        res.setMessage("otp sent");
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    /**
     * Registra un nuevo usuario y retorna el JWT.
     * @param req Datos de registro del usuario.
     * @return Respuesta de autenticación con JWT y rol.
     */
    @Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario y retorna el JWT.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario registrado correctamente")
    })
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(
            @Valid
            @RequestBody SignupRequest req)
            throws SellerException {


        String token = authService.createUser(req);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Registro exitoso");
        authResponse.setRole(USER_ROLE.ROLE_CUSTOMER);

        return new ResponseEntity<>(authResponse, HttpStatus.OK);

    }

    /**
     * Inicia sesión de usuario y retorna el JWT.
     * @param loginRequest Datos de inicio de sesión.
     * @return Respuesta de autenticación con JWT y rol.
     */
    @Operation(summary = "Iniciar sesión", description = "Inicia sesión de usuario y retorna el JWT.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso")
    })
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody LoginRequest loginRequest) throws SellerException {

        AuthResponse authResponse = authService.signin(loginRequest);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }




}