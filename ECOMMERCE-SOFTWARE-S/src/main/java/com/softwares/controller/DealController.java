package com.softwares.controller;

import com.softwares.models.Deal;
import com.softwares.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.softwares.service.DealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/deals")
public class DealController {
    private final DealService dealService;


    /**
     * Crea una nueva oferta (deal).
     * @param deals Objeto Deal a crear.
     * @return Deal creado.
     */
    @Operation(summary = "Crear oferta", description = "Crea una nueva oferta (deal).")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "Oferta creada exitosamente")
    })
    @PostMapping
    public ResponseEntity<Deal> createDeals(
            @RequestBody Deal deals
    ){
        Deal createdDeals=dealService.createDeal(deals);
        return new ResponseEntity<>(createdDeals, HttpStatus.ACCEPTED);
    }


    /**
     * Obtiene todas las ofertas (deals).
     * @return Lista de deals.
     */
    @Operation(summary = "Listar ofertas", description = "Obtiene todas las ofertas registradas.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "Ofertas obtenidas exitosamente")
    })
    @GetMapping
    public ResponseEntity<List<Deal>> getDeals(){
        List<Deal> deals=dealService.getDeals();
        return new ResponseEntity<>(deals, HttpStatus.ACCEPTED);
    }


    /**
     * Actualiza una oferta existente.
     * @param id ID de la oferta a actualizar.
     * @param deal Objeto Deal con los nuevos datos.
     * @return Deal actualizado.
     */
    @Operation(summary = "Actualizar oferta", description = "Actualiza una oferta existente por ID.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Oferta actualizada exitosamente")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Deal> updateDeal(
            @PathVariable Long id,
            @RequestBody Deal deal) throws Exception {
        Deal updatedDeal=dealService.updateDeal(deal,id);
        return ResponseEntity.ok(updatedDeal);
    }


    /**
     * Elimina una oferta por ID.
     * @param id ID de la oferta a eliminar.
     * @return Respuesta de éxito.
     */
    @Operation(summary = "Eliminar oferta", description = "Elimina una oferta existente por ID.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "Oferta eliminada exitosamente")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteDeals(
            @PathVariable Long id
    ) throws Exception {
        dealService.deleteDeal(id);
        ApiResponse apiResponse=new ApiResponse();
        apiResponse.setMessage("Oferta eliminada");
        return new ResponseEntity<>(apiResponse, HttpStatus.ACCEPTED);
    }



}

