package com.softwares.controller;


import java.util.Collection;
import java.util.List;

import com.softwares.config.JwtProvider;
import com.softwares.domain.USER_ROLE;
import com.softwares.dto.SoldItemDto;
import com.softwares.models.*;
import com.softwares.response.ApiResponse;
import com.softwares.service.*;
import com.softwares.service.impl.CustomeUserServiceImplementation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softwares.domain.AccountStatus;
import com.softwares.exceptions.SellerException;
import com.softwares.repository.VerificationCodeRepository;
import com.softwares.request.LoginRequest;
import com.softwares.response.AuthResponse;
import com.softwares.utils.OtpUtil;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Documentación Swagger/OpenAPI
import io.swagger.v3.oas.annotations.Operation;
// Usar el nombre completo para ApiResponse en las anotaciones para evitar conflicto
import io.swagger.v3.oas.annotations.responses.ApiResponses;



@RestController
@RequestMapping("/sellers")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;
    private final SellerReportService sellerReportService;
    private final EmailService emailService;
    private final VerificationCodeRepository verificationCodeRepository;
    private final VerificationService verificationService;
    private final JwtProvider jwtProvider;
    private final CustomeUserServiceImplementation customeUserServiceImplementation;
    private final OrderItemService orderItemService;



    /**
     * Envía un OTP de inicio de sesión al correo del vendedor.
     * @param req Objeto con el email del vendedor.
     * @return Respuesta de éxito.
     */
    @Operation(summary = "Enviar OTP de login", description = "Envía un OTP de inicio de sesión al correo del vendedor.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "OTP enviado correctamente")
    })
    @PostMapping("/sent/login-top")
    public ResponseEntity<ApiResponse> sentLoginOtp(@RequestBody VerificationCode req) throws MessagingException, SellerException {
        Seller seller = sellerService.getSellerByEmail(req.getEmail());

        String otp = OtpUtil.generateOTP();
        VerificationCode verificationCode = verificationService.createVerificationCode(otp, req.getEmail());

        String subject = "Software-S inicio con Otp";
        String text = "your login otp is - ";
        emailService.sendVerificationOtpEmail(req.getEmail(), verificationCode.getOtp(), subject, text);

        ApiResponse res = new ApiResponse();
        res.setMessage("otp sent");
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    /**
     * Verifica el OTP de login y retorna el JWT si es correcto.
     * @param req Objeto con email y OTP.
     * @return Respuesta de autenticación con JWT y rol.
     */
    @Operation(summary = "Verificar OTP de login", description = "Verifica el OTP de login y retorna el JWT si es correcto.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login exitoso")
    })
    @PostMapping("/verify/login-top")
    public ResponseEntity<AuthResponse> verifyLoginOtp(@RequestBody VerificationCode req) throws MessagingException, SellerException {
//        Seller savedSeller = sellerService.createSeller(seller);


        String otp = req.getOtp();
        String email = req.getEmail();
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(email);

        if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new SellerException("wrong otp...");
        }

        Authentication authentication = authenticate(req.getEmail());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);
        AuthResponse authResponse = new AuthResponse();

        authResponse.setMessage("Login Success");
        authResponse.setJwt(token);
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();


        String roleName = authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();


        authResponse.setRole(USER_ROLE.valueOf(roleName));

        return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);
    }

    private Authentication authenticate(String username) {
        UserDetails userDetails = customeUserServiceImplementation.loadUserByUsername("seller_" + username);

        System.out.println("sign in userDetails - " + userDetails);

        if (userDetails == null) {
            System.out.println("sign in userDetails - null " + userDetails);
            throw new BadCredentialsException("Invalid username or password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    /**
     * Verifica el email del vendedor usando el OTP recibido.
     * @param otp OTP recibido por email.
     * @return Vendedor verificado.
     */
    @Operation(summary = "Verificar email de vendedor", description = "Verifica el email del vendedor usando el OTP recibido.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Vendedor verificado correctamente")
    })
    @PatchMapping("/verify/{otp}")
    public ResponseEntity<Seller> verifySellerEmail(@PathVariable String otp) throws SellerException {


        VerificationCode verificationCode = verificationCodeRepository.findByOtp(otp);

        if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new SellerException("wrong otp...");
        }

        Seller seller = sellerService.verifyEmail(verificationCode.getEmail(), otp);

        return new ResponseEntity<>(seller, HttpStatus.OK);
    }


    /**
     * Crea un nuevo vendedor y envía un OTP de verificación por email.
     * @param seller Datos del vendedor.
     * @return Vendedor creado.
     */
    @Operation(summary = "Crear vendedor", description = "Crea un nuevo vendedor y envía un OTP de verificación por email.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Vendedor creado correctamente")
    })
    @PostMapping
    public ResponseEntity<Seller> createSeller(@RequestBody Seller seller) throws SellerException, MessagingException {
        Seller savedSeller = sellerService.createSeller(seller);

        String otp = OtpUtil.generateOTP();
        VerificationCode verificationCode = verificationService.createVerificationCode(otp, seller.getEmail());

        String subject = "Código de verificación de correo electrónico de Software - S";
        String text = "Bienvenido a Software - S, verifique su cuenta usando este enlace";
        String frontend_url = "http://localhost:3000/verify-seller/";
        emailService.sendVerificationOtpEmail(seller.getEmail(), verificationCode.getOtp(), subject, text + frontend_url);
        return new ResponseEntity<>(savedSeller, HttpStatus.CREATED);
    }

    /**
     * Obtiene un vendedor por su ID.
     * @param id ID del vendedor.
     * @return Vendedor encontrado.
     */
    @Operation(summary = "Obtener vendedor por ID", description = "Obtiene un vendedor por su ID.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Vendedor encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Seller> getSellerById(@PathVariable Long id) throws SellerException {
        Seller seller = sellerService.getSellerById(id);
        return new ResponseEntity<>(seller, HttpStatus.OK);
    }

    /**
     * Obtiene el perfil del vendedor autenticado.
     * @param jwt Token JWT de autenticación.
     * @return Perfil del vendedor.
     */
    @Operation(summary = "Obtener perfil de vendedor", description = "Obtiene el perfil del vendedor autenticado.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Perfil obtenido correctamente")
    })
    @GetMapping("/profile")
    public ResponseEntity<Seller> getSellerByJwt(
            @RequestHeader("Authorization") String jwt) throws SellerException {
        String email = jwtProvider.getEmailFromJwtToken(jwt);
        Seller seller = sellerService.getSellerByEmail(email);
        return new ResponseEntity<>(seller, HttpStatus.OK);
    }

    /**
     * Obtiene el reporte de ventas del vendedor autenticado.
     * @param jwt Token JWT de autenticación.
     * @return Reporte de ventas.
     */
    @Operation(summary = "Obtener reporte de vendedor", description = "Obtiene el reporte de ventas del vendedor autenticado.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reporte obtenido correctamente")
    })
    @GetMapping("/report")
    public ResponseEntity<SellerReport> getSellerReport(
            @RequestHeader("Authorization") String jwt) throws SellerException {
        String email = jwtProvider.getEmailFromJwtToken(jwt);
        Seller seller = sellerService.getSellerByEmail(email);
        SellerReport report = sellerReportService.getSellerReport(seller);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    /**
     * Lista todos los vendedores, opcionalmente filtrados por estado.
     * @param status Estado de la cuenta (opcional).
     * @return Lista de vendedores.
     */
    @Operation(summary = "Listar vendedores", description = "Lista todos los vendedores, opcionalmente filtrados por estado.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de vendedores obtenida correctamente")
    })
    @GetMapping
    public ResponseEntity<List<Seller>> getAllSellers(
            @RequestParam(required = false) AccountStatus status) {
        List<Seller> sellers = sellerService.getAllSellers(status);
        return ResponseEntity.ok(sellers);
    }

    /**
     * Actualiza el perfil del vendedor autenticado.
     * @param jwt Token JWT de autenticación.
     * @param seller Datos actualizados del vendedor.
     * @return Vendedor actualizado.
     */
    @Operation(summary = "Actualizar vendedor", description = "Actualiza el perfil del vendedor autenticado.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Vendedor actualizado correctamente")
    })
    @PatchMapping()
    public ResponseEntity<Seller> updateSeller(
            @RequestHeader("Authorization") String jwt, @RequestBody Seller seller) throws SellerException {

        Seller profile = sellerService.getSellerProfile(jwt);
        Seller updatedSeller = sellerService.updateSeller(profile.getId(), seller);
        return ResponseEntity.ok(updatedSeller);

    }

    /**
     * Elimina un vendedor por su ID.
     * @param id ID del vendedor.
     * @return Sin contenido si se elimina correctamente.
     */
    @Operation(summary = "Eliminar vendedor", description = "Elimina un vendedor por su ID.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Vendedor eliminado correctamente")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long id) throws SellerException {

        sellerService.deleteSeller(id);
        return ResponseEntity.noContent().build();

    }

    /**
     * Obtiene los productos vendidos por el vendedor autenticado.
     * @param jwt Token JWT de autenticación.
     * @return Lista de productos vendidos.
     */
    @Operation(summary = "Obtener productos vendidos", description = "Obtiene los productos vendidos por el vendedor autenticado.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de productos vendidos obtenida correctamente")
    })
    @GetMapping("/sold-items")
    public ResponseEntity<List<OrderItem>> getSoldItems(
            @RequestHeader("Authorization") String jwt) throws SellerException {

        String email = jwtProvider.getEmailFromJwtToken(jwt);
        Seller seller = sellerService.getSellerByEmail(email);
        List<OrderItem> soldItems = orderItemService.getSoldItemsBySellerId(seller.getId());

        return ResponseEntity.ok(soldItems);
    }

    /*@GetMapping("/sales")
    public ResponseEntity<List<SoldItemDto>> getSalesReport(@RequestHeader("Authorization") String jwt) throws SellerException {
        String email = jwtProvider.getEmailFromJwtToken(jwt);
        Seller seller = sellerService.getSellerByEmail(email);
        List<SoldItemDto> soldItems = sellerService.getSoldItemsBySellerId(seller.getId());
        return ResponseEntity.ok(soldItems);
    }*/



}
