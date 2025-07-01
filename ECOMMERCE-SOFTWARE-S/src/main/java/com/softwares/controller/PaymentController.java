package com.softwares.controller;

import com.paypal.api.payments.*;
import com.paypal.base.rest.PayPalRESTException;
import com.softwares.service.impl.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Map;

@RestController
@RequestMapping("/api/paypal")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    /**
     * Inicia el proceso de pago con PayPal y devuelve la URL de aprobación.
     * @param amount Monto a pagar.
     * @return URL de redirección para aprobar el pago en PayPal.
     */
    @Operation(summary = "Crear pago PayPal", description = "Inicia el proceso de pago y devuelve la URL de aprobación de PayPal.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "URL de aprobación generada correctamente"),
        @ApiResponse(responseCode = "500", description = "Error al generar el pago o enlace de aprobación")
    })
    @GetMapping("/pay")
    public ResponseEntity<?> createPayment(@RequestParam Double amount) {
        try {
            Payment payment = paymentService.createPayment(
                    amount,
                    "USD",
                    "paypal",
                    "sale",
                    "Compra desde el carrito",
                    "http://localhost:3000/paypal/cancel",
                    "http://localhost:3000/paypal/success"
            );
            for (Links link : payment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    return ResponseEntity.ok(Map.of("redirect_url", link.getHref()));
                }
            }
            return ResponseEntity.status(500).body("No se encontró el enlace de aprobación");
        } catch (PayPalRESTException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }


    /**
     * Ejecuta el pago después de la aprobación en PayPal.
     * @param paymentId ID del pago generado por PayPal.
     * @param payerId ID del pagador proporcionado por PayPal.
     * @return Detalles del pago realizado.
     */
    @Operation(summary = "Ejecutar pago PayPal", description = "Ejecuta el pago tras la aprobación del usuario en PayPal.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pago ejecutado correctamente"),
        @ApiResponse(responseCode = "500", description = "Error al ejecutar el pago")
    })
    @GetMapping("/success")
    public ResponseEntity<?> executePayment(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId) {
        try {
            Payment payment = paymentService.executePayment(paymentId, payerId);
            return ResponseEntity.ok(payment);
        } catch (PayPalRESTException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }


    /**
     * Cancela el proceso de pago.
     * @return Mensaje de cancelación.
     */
    @Operation(summary = "Cancelar pago PayPal", description = "Cancela el proceso de pago y devuelve un mensaje de cancelación.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pago cancelado por el usuario")
    })
    @GetMapping("/cancel")
    public ResponseEntity<?> cancelPayment() {
        return ResponseEntity.ok("El pago fue cancelado por el usuario");
    }
}
