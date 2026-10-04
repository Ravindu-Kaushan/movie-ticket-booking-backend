package lk.ijse.movieticketbooking.service.impl;

import lk.ijse.movieticketbooking.dto.BookingDTO;
import lk.ijse.movieticketbooking.entity.*;
import lk.ijse.movieticketbooking.exception.BadRequestException;
import lk.ijse.movieticketbooking.exception.ResourceNotFoundException;
import lk.ijse.movieticketbooking.repository.BookingRepository;
import lk.ijse.movieticketbooking.repository.ShowRepository;
import lk.ijse.movieticketbooking.repository.UserRepository;
import lk.ijse.movieticketbooking.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final ShowRepository showRepository;
    private final UserRepository userRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, ShowRepository showRepository,
                              UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.showRepository = showRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BookingDTO createBooking(BookingDTO dto, String userEmail) {
        log.info("Booking request received from user {} for show {}", userEmail, dto.getShowId());

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Show show = showRepository.findById(dto.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id " + dto.getShowId()));

        if (show.getStatus() == ShowStatus.CANCELLED) {
            log.warn("Booking failed, show {} is cancelled", show.getId());
            throw new BadRequestException("Cannot book a cancelled show");
        }
        if (show.getStatus() == ShowStatus.COMPLETED) {
            log.warn("Booking failed, show {} is completed", show.getId());
            throw new BadRequestException("Cannot book a completed show");
        }

        if (dto.getSeatNumbers() == null || dto.getSeatNumbers().isEmpty()) {
            log.warn("Booking failed, no seats selected by user {}", userEmail);
            throw new BadRequestException("At least one seat must be selected");
        }

        if (dto.getNumberOfTickets() == null || dto.getSeatNumbers().size() != dto.getNumberOfTickets()) {
            log.warn("Booking failed, tickets count does not match seat count for user {}", userEmail);
            throw new BadRequestException("Number of tickets must match the number of selected seats");
        }

        List<Booking> existingBookings = bookingRepository.findByShowId(show.getId());
        for (Booking existing : existingBookings) {
            if (existing.getStatus() == BookingStatus.CANCELLED) {
                continue;
            }
            for (String seat : dto.getSeatNumbers()) {
                if (existing.getSeatNumbers().contains(seat)) {
                    log.warn("Booking failed, seat {} already booked for show {}", seat, show.getId());
                    throw new BadRequestException("Seat " + seat + " is already booked for this show");
                }
            }
        }

        BigDecimal totalAmount = show.getTicketPrice().multiply(BigDecimal.valueOf(dto.getNumberOfTickets()));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShow(show);
        booking.setSeatNumbers(dto.getSeatNumbers());
        booking.setNumberOfTickets(dto.getNumberOfTickets());
        booking.setTotalAmount(totalAmount);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus(BookingStatus.PENDING);

        Booking saved = bookingRepository.save(booking);
        log.info("Booking created with id {} for user {}", saved.getId(), userEmail);
        return toDTO(saved);
    }

    @Override
    public BookingDTO getBookingById(Long id, String userEmail) {
        log.info("Get booking request received for booking {} from user {}", id, userEmail);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id " + id));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isOwner = booking.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            log.warn("View denied, user {} is not allowed to view booking {}", userEmail, id);
            throw new BadRequestException("You are not allowed to view this booking");
        }

        return toDTO(booking);
    }

    @Override
    public List<BookingDTO> getMyBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<BookingDTO> result = new ArrayList<>();
        for (Booking booking : bookingRepository.findByUserId(user.getId())) {
            result.add(toDTO(booking));
        }
        return result;
    }

    @Override
    public List<BookingDTO> getAllBookings() {
        List<BookingDTO> result = new ArrayList<>();
        for (Booking booking : bookingRepository.findAll()) {
            result.add(toDTO(booking));
        }
        return result;
    }

    @Override
    public BookingDTO cancelBooking(Long id, String userEmail) {
        log.info("Cancel request received for booking {} from user {}", id, userEmail);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id " + id));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isOwner = booking.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            log.warn("Cancel denied, user {} is not allowed to cancel booking {}", userEmail, id);
            throw new BadRequestException("You are not allowed to cancel this booking");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            log.warn("Cancel failed, booking {} is already cancelled", id);
            throw new BadRequestException("Booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking saved = bookingRepository.save(booking);
        log.info("Booking cancelled with id {} by user {}", saved.getId(), userEmail);
        return toDTO(saved);
    }

    @Override
    public BookingDTO updateBookingStatus(Long id, String status) {
        log.info("Update status request received for booking {} with status {}", id, status);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id " + id));

        BookingStatus newStatus;
        try {
            newStatus = BookingStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Update status failed, invalid status {} for booking {}", status, id);
            throw new BadRequestException("Invalid booking status: " + status);
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            log.warn("Update status failed, booking {} is already cancelled", id);
            throw new BadRequestException("A cancelled booking cannot be changed");
        }

        if (booking.getStatus() == newStatus) {
            log.warn("Update status failed, booking {} already has status {}", id, newStatus);
            throw new BadRequestException("Booking already has status " + newStatus);
        }

        booking.setStatus(newStatus);
        Booking saved = bookingRepository.save(booking);
        log.info("Booking {} status changed to {}", saved.getId(), newStatus);
        return toDTO(saved);
    }

    private BookingDTO toDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUser().getId());
        dto.setShowId(booking.getShow().getId());
        dto.setSeatNumbers(booking.getSeatNumbers());
        dto.setNumberOfTickets(booking.getNumberOfTickets());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setBookingDate(booking.getBookingDate());
        dto.setStatus(booking.getStatus().name());
        return dto;
    }
}