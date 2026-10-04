package lk.ijse.movieticketbooking.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BookingDTO {

    private Long id;

    private Long userId;

    @NotNull(message = "Show ID is required")
    private Long showId;

    @NotEmpty(message = "At least one seat must be selected")
    private List<String> seatNumbers;

    @NotNull(message = "Number of tickets is required")
    private Integer numberOfTickets;

    private BigDecimal totalAmount;

    private LocalDateTime bookingDate;

    private String status;

    public BookingDTO() {
    }

    public BookingDTO(Long id, Long userId, Long showId, List<String> seatNumbers, Integer numberOfTickets, BigDecimal totalAmount, LocalDateTime bookingDate, String status) {
        this.id = id;
        this.userId = userId;
        this.showId = showId;
        this.seatNumbers = seatNumbers;
        this.numberOfTickets = numberOfTickets;
        this.totalAmount = totalAmount;
        this.bookingDate = bookingDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getShowId() {
        return showId;
    }

    public void setShowId(Long showId) {
        this.showId = showId;
    }

    public List<String> getSeatNumbers() {
        return seatNumbers;
    }

    public void setSeatNumbers(List<String> seatNumbers) {
        this.seatNumbers = seatNumbers;
    }

    public Integer getNumberOfTickets() {
        return numberOfTickets;
    }

    public void setNumberOfTickets(Integer numberOfTickets) {
        this.numberOfTickets = numberOfTickets;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}