package com.softwares.controller;

import com.softwares.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.softwares.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;


    /**
     * Endpoint principal de bienvenida para el sistema de e-commerce multivendedor.
     * @return Mensaje de bienvenida.
     */
    @Operation(summary = "Bienvenida", description = "Devuelve un mensaje de bienvenida para el sistema de comercio electrónico multivendedor.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "Respuesta exitosa de bienvenida")
    })
    @GetMapping
    public ResponseEntity<ApiResponse> home(){
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Comercio electrónico multivendedor");
        return new ResponseEntity<>(apiResponse, HttpStatus.ACCEPTED);
    }




}