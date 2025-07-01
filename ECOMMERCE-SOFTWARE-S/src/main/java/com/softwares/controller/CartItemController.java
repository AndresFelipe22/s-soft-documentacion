
// Controlador para operaciones sobre los items individuales del carrito.
// Aquí se pueden definir endpoints para actualizar, eliminar o consultar items específicos del carrito.
package com.softwares.controller;

import com.softwares.service.CartItemService;
import com.softwares.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart_items")

/**
 * Controlador REST para gestionar los items individuales del carrito de compras.
 * Define endpoints relacionados con la manipulación de items en el carrito.
 */
public class CartItemController {


    // Servicio para la lógica de negocio de los items del carrito
    private CartItemService cartItemService;
    // Servicio para la gestión de usuarios
    private UserService userService;


    /**
     * Constructor con inyección de dependencias para los servicios necesarios.
     * @param cartItemService Servicio de items del carrito
     * @param userService Servicio de usuarios
     */
    public CartItemController(CartItemService cartItemService, UserService userService) {
        this.cartItemService = cartItemService;
        this.userService = userService;
    }


}

