package com.softwares.controller;

import com.softwares.service.SellerService;
import com.softwares.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softwares.exceptions.ProductException;
import com.softwares.models.Product;
import com.softwares.service.ProductService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {


    private final ProductService productService;

    private final UserService userService;

    private final SellerService sellerService;



    @GetMapping("/{productId}")

    @Operation(summary = "Obtener producto por ID", description = "Devuelve un producto según su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto encontrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Product> getProductById(@PathVariable Long productId) throws ProductException {
        Product product = productService.findProductById(productId);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @Operation(summary = "Buscar productos", description = "Busca productos por nombre o descripción. Si no se envía query, retorna todos los productos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de productos encontrada")
    })
    public ResponseEntity<List<Product>> searchProduct(@RequestParam(required = false) String query) {
        List<Product> products = productService.searchProduct(query);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "Listar productos con filtros", description = "Obtiene una página de productos filtrando por diferentes parámetros.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Página de productos obtenida exitosamente")
    })
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Integer minDiscount,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String stock,
            @RequestParam(defaultValue = "0") Integer pageNumber) {
        System.out.println("color p -------- "+pageNumber);
        return new ResponseEntity<>(
                productService.getAllProduct(category,brand,
                        color, size, minPrice,
                        maxPrice, minDiscount, sort,
                        stock, pageNumber), HttpStatus.OK);
    }
}
