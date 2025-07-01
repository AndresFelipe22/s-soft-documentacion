
package com.softwares.controller;

import com.softwares.models.Home;
import com.softwares.models.HomeCategory;
import com.softwares.service.HomeCategoryService;
import com.softwares.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CustomerController {
    private final HomeCategoryService homeCategoryService;
    private final HomeService homeService;


    @Operation(summary = "Obtener datos de la página principal", description = "Devuelve los datos de la página principal para el cliente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Datos obtenidos exitosamente")
    })
    @GetMapping("/home-page")
    public ResponseEntity<Home> getHomePageData() {
        // Home homePageData = homeService.getHomePageData();
        // return new ResponseEntity<>(homePageData, HttpStatus.ACCEPTED);
        return null;
    }

    @Operation(summary = "Crear categorías de inicio", description = "Crea y retorna las categorías de la página principal.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Categorías creadas exitosamente")
    })
    @PostMapping("/home/categories")
    public ResponseEntity<Home> createHomeCategories(
            @RequestBody List<HomeCategory> homeCategories
    ) {
        List<HomeCategory> categories = homeCategoryService.createCategories(homeCategories);
        Home home=homeService.creatHomePageData(categories);
        return new ResponseEntity<>(home, HttpStatus.ACCEPTED);
    }
}

