package com.softwares.controller;

import com.softwares.models.Cart;
import com.softwares.models.Coupon;
import com.softwares.models.User;
import com.softwares.service.CartService;
import com.softwares.service.CouponService;
import com.softwares.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class AdminCouponController {

    private final CouponService couponService;
    private final UserService userService;
    private final CartService cartService;

    @Operation(summary = "Aplicar o quitar cupón", description = "Aplica o elimina un cupón en el carrito del usuario según el parámetro 'apply'.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación realizada exitosamente")
    })
    @PostMapping("/apply")
    public ResponseEntity<Cart> applyCoupon(
            @RequestParam String apply,
            @RequestParam String code,
            @RequestParam double orderValue,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user=userService.findUserProfileByJwt(jwt);
        Cart cart;
        if(apply.equals("true")){
            cart = couponService.applyCoupon(code,orderValue,user);
        }
        else {
            cart = couponService.removeCoupon(code,user);
        }
        return ResponseEntity.ok(cart);
    }


    // Admin operations

    @Operation(summary = "Crear cupón", description = "Crea un nuevo cupón.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cupón creado exitosamente")
    })
    @PostMapping("/admin/create")
    public ResponseEntity<Coupon> createCoupon(@RequestBody Coupon coupon) {
        Coupon createdCoupon = couponService.createCoupon(coupon);
        return ResponseEntity.ok(createdCoupon);
    }

    @Operation(summary = "Eliminar cupón", description = "Elimina un cupón por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cupón eliminado exitosamente")
    })
    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<?> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.ok( "Cupón eliminado exitosamente");
    }

    @Operation(summary = "Listar cupones", description = "Devuelve todos los cupones existentes.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de cupones obtenida exitosamente")
    })
    @GetMapping("/admin/all")
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        List<Coupon> coupons = couponService.getAllCoupons();
        return ResponseEntity.ok(coupons);
    }
}

