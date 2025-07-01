package com.softwares.controller;

import com.softwares.exceptions.CategoryNotFoundException;
import com.softwares.exceptions.SellerException;
import com.softwares.exceptions.UserException;
import com.softwares.service.UserService;
import org.springframework.web.bind.annotation.*;

import com.softwares.exceptions.ProductException;
import com.softwares.models.Product;
import com.softwares.models.Seller;
import com.softwares.request.CreateProductRequest;
import com.softwares.service.ProductService;
import com.softwares.service.SellerService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/sellers/product")
@RequiredArgsConstructor
public class SellerProductController {

    private final ProductService productService;
    private final SellerService sellerService;
    private final UserService userService;



    @Operation(summary = "Obtener productos del vendedor", description = "Devuelve todos los productos asociados al vendedor autenticado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente")
    })
    @GetMapping()
    public ResponseEntity<List<Product>> getProductBySellerId(
            @RequestHeader("Authorization") String jwt) throws ProductException, SellerException {
        Seller seller=sellerService.getSellerProfile(jwt);
        List<Product> products = productService.getProductBySellerId(seller.getId());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }


    @Operation(summary = "Crear producto", description = "Crea un nuevo producto para el vendedor autenticado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Producto creado exitosamente")
    })
    @PostMapping()
    public ResponseEntity<Product> createProduct(
            @RequestBody CreateProductRequest request,
            @RequestHeader("Authorization")String jwt)
            throws UserException,
            ProductException, CategoryNotFoundException, SellerException {
        Seller seller=sellerService.getSellerProfile(jwt);
        Product product = productService.createProduct(request, seller);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }


    @Operation(summary = "Eliminar producto", description = "Elimina un producto por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        try {
            productService.deleteProduct(productId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ProductException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @Operation(summary = "Actualizar producto", description = "Actualiza los datos de un producto por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PatchMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId, @RequestBody Product product) {
        try {
            Product updatedProduct = productService.updateProduct(productId, product);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (ProductException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @Operation(summary = "Actualizar stock de producto", description = "Actualiza el stock de un producto por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PatchMapping("/{productId}/stock")
    public ResponseEntity<Product> updateProductStock(@PathVariable Long productId) {
        try {
            Product updatedProduct = productService.updateProductStock(productId);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (ProductException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
