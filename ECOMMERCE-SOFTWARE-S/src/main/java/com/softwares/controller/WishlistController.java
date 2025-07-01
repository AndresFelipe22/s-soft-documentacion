package com.softwares.controller;

import com.softwares.exceptions.ProductException;
import com.softwares.exceptions.UserException;
import com.softwares.exceptions.WishlistNotFoundException;
import com.softwares.models.Product;
import com.softwares.models.User;
import com.softwares.models.Wishlist;
import com.softwares.service.ProductService;
import com.softwares.service.UserService;
import com.softwares.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;
    private final ProductService productService;
    private final UserService userService;



    @Operation(summary = "Crear wishlist", description = "Crea una nueva wishlist para el usuario.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Wishlist creada exitosamente")
    })
    @PostMapping("/create")
    public ResponseEntity<Wishlist> createWishlist(@RequestBody User user) {
        Wishlist wishlist = wishlistService.createWishlist(user);
        return ResponseEntity.ok(wishlist);
    }


    @Operation(summary = "Obtener wishlist de usuario", description = "Devuelve la wishlist del usuario autenticado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Wishlist obtenida exitosamente")
    })
    @GetMapping()
    public ResponseEntity<Wishlist> getWishlistByUserId(
            @RequestHeader("Authorization") String jwt) throws UserException {
        User user = userService.findUserProfileByJwt(jwt);
        Wishlist wishlist = wishlistService.getWishlistByUserId(user);
        return ResponseEntity.ok(wishlist);
    }


    @Operation(summary = "Agregar producto a wishlist", description = "Agrega un producto a la wishlist del usuario autenticado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto agregado exitosamente a la wishlist")
    })
    @PostMapping("/add-product/{productId}")
    public ResponseEntity<Wishlist> addProductToWishlist(
            @PathVariable Long productId,
            @RequestHeader("Authorization") String jwt) throws WishlistNotFoundException, ProductException, UserException {
        Product product = productService.findProductById(productId);
        User user=userService.findUserProfileByJwt(jwt);
        Wishlist updatedWishlist = wishlistService.addProductToWishlist(
                user,
                product
        );
        return ResponseEntity.ok(updatedWishlist);
    }

}
