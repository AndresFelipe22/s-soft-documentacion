package com.softwares.controller;

import com.softwares.domain.AccountStatus;
import com.softwares.exceptions.SellerException;
import com.softwares.models.HomeCategory;
import com.softwares.models.Seller;
import com.softwares.service.HomeCategoryService;
import com.softwares.service.SellerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SellerService sellerService;
    private final HomeCategoryService homeCategoryService;


    /**
     * Actualiza el estado de la cuenta de un vendedor.
     * @param id ID del vendedor.
     * @param status Nuevo estado de la cuenta.
     * @return Vendedor actualizado.
     */
    @Operation(summary = "Actualizar estado de vendedor", description = "Actualiza el estado de la cuenta de un vendedor.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Estado actualizado correctamente")
    })
    @PatchMapping("/seller/{id}/status/{status}")
    public ResponseEntity<Seller> updateSellerStatus(
            @PathVariable Long id,
            @PathVariable AccountStatus status) throws SellerException {

        Seller updatedSeller = sellerService.updateSellerAccountStatus(id,status);
        return ResponseEntity.ok(updatedSeller);

    }

    /**
     * Obtiene todas las categorías principales de la página de inicio.
     * @return Lista de categorías.
     */
    @Operation(summary = "Listar categorías principales", description = "Obtiene todas las categorías principales de la página de inicio.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Categorías obtenidas correctamente")
    })
    @GetMapping("/home-category")
    public ResponseEntity<List<HomeCategory>> getHomeCategory(
    ) throws Exception {

        List<HomeCategory> categories=homeCategoryService.getAllCategories();
        return ResponseEntity.ok(categories);

    }

    /**
     * Actualiza una categoría principal por su ID.
     * @param id ID de la categoría.
     * @param homeCategory Datos actualizados de la categoría.
     * @return Categoría actualizada.
     */
    @Operation(summary = "Actualizar categoría principal", description = "Actualiza una categoría principal por su ID.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Categoría actualizada correctamente")
    })
    @PatchMapping("/home-category/{id}")
    public ResponseEntity<HomeCategory> updateHomeCategory(
            @PathVariable Long id,
            @RequestBody HomeCategory homeCategory) throws Exception {

        HomeCategory updatedCategory=homeCategoryService.updateCategory(homeCategory,id);
        return ResponseEntity.ok(updatedCategory);

    }
}

