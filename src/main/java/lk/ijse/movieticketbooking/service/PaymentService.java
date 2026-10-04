package lk.ijse.movieticketbooking.service;

import lk.ijse.movieticketbooking.dto.PaymentRequestDTO;
import lk.ijse.movieticketbooking.dto.PaymentResponseDTO;
import java.util.List;

public interface PaymentService {

    PaymentResponseDTO createPayment(PaymentRequestDTO request, String email);

    PaymentResponseDTO processPayment(Long paymentId, boolean success, String email);

    PaymentResponseDTO getPaymentById(Long paymentId, String email, boolean isAdmin);

    String getPaymentStatus(Long paymentId, String email, boolean isAdmin);

    List<PaymentResponseDTO> getAllPayments();
}