package lk.ijse.movieticketbooking.controller;

import jakarta.validation.Valid;
import lk.ijse.movieticketbooking.dto.PaymentRequestDTO;
import lk.ijse.movieticketbooking.dto.PaymentResponseDTO;
import lk.ijse.movieticketbooking.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> createPayment(@Valid @RequestBody PaymentRequestDTO request,
                                                            Authentication authentication) {
        String email = authentication.getName();
        PaymentResponseDTO response = paymentService.createPayment(request, email);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/process")
    public ResponseEntity<PaymentResponseDTO> processPayment(@PathVariable Long id,
                                                             @RequestParam boolean success,
                                                             Authentication authentication) {
        String email = authentication.getName();
        PaymentResponseDTO response = paymentService.processPayment(id, success, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable Long id,
                                                             Authentication authentication) {
        String email = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().toString().contains("ROLE_ADMIN");
        PaymentResponseDTO response = paymentService.getPaymentById(id, email, isAdmin);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<String> getPaymentStatus(@PathVariable Long id,
                                                   Authentication authentication) {
        String email = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().toString().contains("ROLE_ADMIN");
        String status = paymentService.getPaymentStatus(id, email, isAdmin);
        return ResponseEntity.ok(status);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponseDTO>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
}