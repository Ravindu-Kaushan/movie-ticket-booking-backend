package lk.ijse.movieticketbooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDTO {

    private Long id;

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    private BigDecimal amount;

    private LocalDateTime paymentDate;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private String status;

    public PaymentDTO() {
    }

    public PaymentDTO(Long id, Long bookingId, BigDecimal amount, LocalDateTime paymentDate, String paymentMethod, String status) {
        this.id = id;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}