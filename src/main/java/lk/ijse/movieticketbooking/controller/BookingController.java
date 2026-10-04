package lk.ijse.movieticketbooking.controller;

import jakarta.validation.Valid;
import lk.ijse.movieticketbooking.dto.BookingDTO;
import lk.ijse.movieticketbooking.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(@Valid @RequestBody BookingDTO bookingDTO,
                                                    Authentication authentication) {
        String email = authentication.getName();
        return new ResponseEntity<>(bookingService.createBooking(bookingDTO, email), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(bookingService.getBookingById(id, email));
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingDTO>> getMyBookings(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(bookingService.getMyBookings(email));
    }

    @GetMapping
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingDTO> cancelBooking(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(bookingService.cancelBooking(id, email));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<BookingDTO> updateBookingStatus(@PathVariable Long id,
                                                          @RequestParam String status) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, status));
    }
}