package lk.ijse.movieticketbooking.service;

import lk.ijse.movieticketbooking.dto.BookingDTO;

import java.util.List;

public interface BookingService {

    BookingDTO createBooking(BookingDTO bookingDTO, String userEmail);

    BookingDTO getBookingById(Long id, String userEmail);

    List<BookingDTO> getMyBookings(String userEmail);

    List<BookingDTO> getAllBookings();

    BookingDTO cancelBooking(Long id, String userEmail);

    BookingDTO updateBookingStatus(Long id, String status);
}