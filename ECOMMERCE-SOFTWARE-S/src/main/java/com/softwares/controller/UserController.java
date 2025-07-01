package com.softwares.controller;


import com.softwares.exceptions.UserException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import com.softwares.models.User;
import com.softwares.service.UserService;
import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService=userService;
    }

    @Operation(summary = "Obtener perfil de usuario", description = "Devuelve el perfil del usuario autenticado a partir del JWT.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Perfil de usuario obtenido exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado o token inválido")
    })
    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfileHandler(
            @RequestHeader("Authorization") String jwt) throws UserException {
        System.out.println("/api/users/profile");
        User user=userService.findUserProfileByJwt(jwt);
        return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
    }


}
