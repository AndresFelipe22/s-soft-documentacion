package com.softwares.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softwares.service.impl.PaypalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/paypal")
@RequiredArgsConstructor
public class PaypalController {

    private final PaypalService paypalService;


    /**
     * Crea un pago PayPal y devuelve la URL de aprobación.
     * @param jwt Token JWT de autenticación en el header Authorization.
     * @return URL de aprobación de PayPal.
     */
    @Operation(summary = "Crear pago PayPal", description = "Crea un pago PayPal y devuelve la URL de aprobación para el usuario.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "URL de aprobación generada correctamente"),
        @ApiResponse(responseCode = "500", description = "Error al crear el pago")
    })
    @PostMapping("/create-payment")
    public ResponseEntity<String> createPayment(@RequestHeader("Authorization") String jwt) {
        try {
            String approvalUrl = paypalService.createPayment(jwt);
            return ResponseEntity.ok(approvalUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear pago: " + e.getMessage());
        }
    }
}
