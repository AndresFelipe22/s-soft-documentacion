package com.softwares.controller;

import com.softwares.exceptions.CartItemException;
import com.softwares.exceptions.ProductException;
import com.softwares.exceptions.UserException;
import com.softwares.models.Cart;
import com.softwares.models.CartItem;
import com.softwares.models.Product;
import com.softwares.models.User;
import com.softwares.request.AddItemRequest;
import com.softwares.response.ApiResponse;
import com.softwares.service.CartItemService;
import com.softwares.service.CartService;
import com.softwares.service.ProductService;
import com.softwares.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
// Usar el nombre completo para la anotación de Swagger en el código para evitar conflicto
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;
    private final ProductService productService;
    private final CartItemService cartItemService;




    @Operation(summary = "Obtener carrito de usuario", description = "Devuelve el carrito del usuario autenticado.")
@ApiResponses(value = {
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Carrito obtenido exitosamente")
})
    @GetMapping
    public ResponseEntity<Cart> findUserCartHandler(@RequestHeader("Authorization") String jwt) throws UserException {
        User user=userService.findUserProfileByJwt(jwt);
        Cart cart=cartService.findUserCart(user);
        System.out.println("cart - "+cart.getUser().getEmail());
        return new ResponseEntity<Cart>(cart,HttpStatus.OK);
    }


    @Operation(summary = "Agregar producto al carrito", description = "Agrega un producto al carrito del usuario autenticado.")
@ApiResponses(value = {
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "Producto agregado exitosamente al carrito")
})
    @PutMapping("/add")
    public ResponseEntity<CartItem> addItemToCart(@RequestBody AddItemRequest req,
                                                  @RequestHeader("Authorization") String jwt) throws UserException, ProductException {
        User user=userService.findUserProfileByJwt(jwt);
        Product product=productService.findProductById(req.getProductId());
        CartItem item = cartService.addCartItem(user,
                product,
                req.getSize(),
                req.getQuantity());
        return new ResponseEntity<>(item,HttpStatus.ACCEPTED);
    }


    @Operation(summary = "Eliminar producto del carrito", description = "Elimina un producto del carrito del usuario autenticado.")
@ApiResponses(value = {
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "Producto eliminado exitosamente del carrito")
})
    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<ApiResponse>deleteCartItemHandler(
            @PathVariable Long cartItemId,
            @RequestHeader("Authorization")String jwt)
            throws CartItemException, UserException{
        User user=userService.findUserProfileByJwt(jwt);
        cartItemService.removeCartItem(user.getId(), cartItemId);
        ApiResponse res=new ApiResponse("Artículo eliminado del carrito",true);
        return new ResponseEntity<ApiResponse>(res,HttpStatus.ACCEPTED);
    }


    @Operation(summary = "Actualizar producto del carrito", description = "Actualiza la cantidad o detalles de un producto en el carrito.")
@ApiResponses(value = {
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "Producto actualizado exitosamente en el carrito")
})
    @PutMapping("/item/{cartItemId}")
    public ResponseEntity<CartItem>updateCartItemHandler(
            @PathVariable Long cartItemId,
            @RequestBody CartItem cartItem,
            @RequestHeader("Authorization")String jwt)
            throws CartItemException, UserException{
        User user=userService.findUserProfileByJwt(jwt);
        CartItem updatedCartItem = null;
        if(cartItem.getQuantity()>0){
            updatedCartItem=cartItemService.updateCartItem(user.getId(),
                    cartItemId, cartItem);
        }
        return new ResponseEntity<>(updatedCartItem,HttpStatus.ACCEPTED);
    }


}
