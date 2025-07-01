package com.softwares.controller;

import io.swagger.v3.oas.annotations.Operation;
// Usar el nombre completo para la anotación de Swagger en el código para evitar conflicto
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import com.softwares.exceptions.ProductException;
import com.softwares.exceptions.ReviewNotFoundException;
import com.softwares.exceptions.UserException;
import com.softwares.models.Product;
import com.softwares.models.Review;
import com.softwares.models.User;
import com.softwares.request.CreateReviewRequest;
import com.softwares.response.ApiResponse;
import com.softwares.service.ProductService;
import com.softwares.service.ReviewService;
import com.softwares.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;
    private final ProductService productService;


    /**
     * Obtiene todas las reseñas de un producto específico.
     * @param productId ID del producto.
     * @return Lista de reseñas del producto.
     */
    @Operation(summary = "Listar reseñas de producto", description = "Obtiene todas las reseñas asociadas a un producto.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reseñas obtenidas exitosamente")
    })
    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<List<Review>> getReviewsByProductId(
            @PathVariable Long productId) {
        List<Review> reviews = reviewService.getReviewsByProductId(productId);
        return ResponseEntity.ok(reviews);
    }


    /**
     * Crea una nueva reseña para un producto.
     * @param req Datos de la reseña.
     * @param productId ID del producto.
     * @param jwt Token JWT de autenticación.
     * @return Reseña creada.
     */
    @Operation(summary = "Crear reseña", description = "Crea una nueva reseña para un producto específico.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reseña creada exitosamente")
    })
    @PostMapping("/products/{productId}/reviews")
    public ResponseEntity<Review> writeReview(
            @RequestBody CreateReviewRequest req,
            @PathVariable Long productId,
            @RequestHeader("Authorization") String jwt) throws UserException, ProductException {
        User user = userService.findUserProfileByJwt(jwt);
        Product product = productService.findProductById(productId);
        Review review = reviewService.createReview(
                req, user, product
        );
        return ResponseEntity.ok(review);
    }


    /**
     * Actualiza una reseña existente.
     * @param req Datos actualizados de la reseña.
     * @param reviewId ID de la reseña a actualizar.
     * @param jwt Token JWT de autenticación.
     * @return Reseña actualizada.
     */
    @Operation(summary = "Actualizar reseña", description = "Actualiza una reseña existente por su ID.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reseña actualizada exitosamente")
    })
    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<Review> updateReview(
            @RequestBody CreateReviewRequest req,
            @PathVariable Long reviewId,
            @RequestHeader("Authorization") String jwt)
            throws UserException,
            ReviewNotFoundException, AuthenticationException {
        User user = userService.findUserProfileByJwt(jwt);
        Review review = reviewService.updateReview(
                reviewId,
                req.getReviewText(),
                req.getReviewRating(),
                user.getId()
        );
        return ResponseEntity.ok(review);
    }


    /**
     * Elimina una reseña por su ID.
     * @param reviewId ID de la reseña a eliminar.
     * @param jwt Token JWT de autenticación.
     * @return Respuesta de éxito.
     */
    @Operation(summary = "Eliminar reseña", description = "Elimina una reseña existente por su ID.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reseña eliminada exitosamente")
    })
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse> deleteReview(
            @PathVariable Long reviewId,
            @RequestHeader("Authorization") String jwt) throws UserException,
            ReviewNotFoundException, AuthenticationException {
        User user = userService.findUserProfileByJwt(jwt);
        reviewService.deleteReview(reviewId, user.getId());
        ApiResponse res = new ApiResponse();
        res.setMessage("Revisión eliminada exitosamente");
        res.setStatus(true);
        return ResponseEntity.ok(res);
    }
}

