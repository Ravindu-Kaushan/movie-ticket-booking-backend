package lk.ijse.movieticketbooking.service.impl;

import lk.ijse.movieticketbooking.dto.PaymentRequestDTO;
import lk.ijse.movieticketbooking.dto.PaymentResponseDTO;
import lk.ijse.movieticketbooking.entity.Booking;
import lk.ijse.movieticketbooking.entity.BookingStatus;
import lk.ijse.movieticketbooking.entity.Payment;
import lk.ijse.movieticketbooking.entity.PaymentStatus;
import lk.ijse.movieticketbooking.exception.BadRequestException;
import lk.ijse.movieticketbooking.exception.ResourceNotFoundException;
import lk.ijse.movieticketbooking.repository.BookingRepository;
import lk.ijse.movieticketbooking.repository.PaymentRepository;
import lk.ijse.movieticketbooking.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository, BookingRepository bookingRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public PaymentResponseDTO createPayment(PaymentRequestDTO request, String email) {
        log.info("Create payment request received from user {} for booking {}", email, request.getBookingId());

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getEmail().equals(email)) {
            log.warn("Create payment denied, booking {} does not belong to user {}", booking.getId(), email);
            throw new BadRequestException("This is not your booking");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            log.warn("Create payment failed, booking {} is cancelled", booking.getId());
            throw new BadRequestException("Cancelled booking cannot be paid");
        }

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            log.warn("Create payment failed, booking {} is already confirmed", booking.getId());
            throw new BadRequestException("Booking is already confirmed");
        }

        if (paymentRepository.findByBookingId(booking.getId()).isPresent()) {
            log.warn("Create payment failed, payment already exists for booking {}", booking.getId());
            throw new BadRequestException("Payment already exists for this booking");
        }

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(PaymentStatus.PENDING);

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment {} created for booking {}", savedPayment.getId(), booking.getId());

        return toDTO(savedPayment);
    }

    @Override
    public PaymentResponseDTO processPayment(Long paymentId, boolean success, String email) {
        log.info("Process payment request received for payment {} from user {}", paymentId, email);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        Booking booking = payment.getBooking();

        if (!booking.getUser().getEmail().equals(email)) {
            log.warn("Process payment denied, payment {} does not belong to user {}", paymentId, email);
            throw new BadRequestException("This is not your payment");
        }

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            log.warn("Process payment failed, payment {} is already completed", paymentId);
            throw new BadRequestException("Payment is already completed");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            log.warn("Process payment failed, booking {} is cancelled", booking.getId());
            throw new BadRequestException("Cancelled booking cannot be paid");
        }

        if (success) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setPaymentDate(LocalDateTime.now());
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
            log.info("Payment {} completed, booking {} confirmed", paymentId, booking.getId());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            log.warn("Payment {} failed for booking {}", paymentId, booking.getId());
        }

        Payment savedPayment = paymentRepository.save(payment);
        return toDTO(savedPayment);
    }

    @Override
    public PaymentResponseDTO getPaymentById(Long paymentId, String email, boolean isAdmin) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (!isAdmin) {
            if (!payment.getBooking().getUser().getEmail().equals(email)) {
                log.warn("View payment denied, payment {} does not belong to user {}", paymentId, email);
                throw new BadRequestException("This is not your payment");
            }
        }

        return toDTO(payment);
    }

    @Override
    public String getPaymentStatus(Long paymentId, String email, boolean isAdmin) {
        PaymentResponseDTO dto = getPaymentById(paymentId, email, isAdmin);
        return dto.getStatus().toString();
    }

    @Override
    public List<PaymentResponseDTO> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        List<PaymentResponseDTO> result = new ArrayList<>();

        for (Payment payment : payments) {
            result.add(toDTO(payment));
        }

        return result;
    }

    private PaymentResponseDTO toDTO(Payment payment) {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setId(payment.getId());
        dto.setBookingId(payment.getBooking().getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setStatus(payment.getStatus());
        return dto;
    }
}