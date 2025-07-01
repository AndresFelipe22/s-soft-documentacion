package com.softwares.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import com.softwares.exceptions.UserException;
import com.softwares.models.Order;
import com.softwares.models.User;
import com.softwares.service.OrderService;
import com.softwares.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;


    /**
     * Crea una nueva orden para el usuario autenticado.
     * @param jwt Token JWT de autenticación en el header Authorization.
     * @return Orden creada.
     */
    @Operation(summary = "Crear orden", description = "Crea una nueva orden para el usuario autenticado.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Orden creada exitosamente")
    })
    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        Order order = orderService.createOrder(user);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }


    /**
     * Obtiene todas las órdenes del usuario autenticado.
     * @param jwt Token JWT de autenticación en el header Authorization.
     * @return Lista de órdenes del usuario.
     */
    @Operation(summary = "Listar órdenes del usuario", description = "Obtiene todas las órdenes asociadas al usuario autenticado.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Órdenes obtenidas exitosamente")
    })
    @GetMapping
    public ResponseEntity<List<Order>> getUserOrders(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        return ResponseEntity.ok(orderService.getOrdersByUser(user));
    }
}




