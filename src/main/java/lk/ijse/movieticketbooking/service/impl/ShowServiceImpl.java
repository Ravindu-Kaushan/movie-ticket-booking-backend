package lk.ijse.movieticketbooking.service.impl;

import lk.ijse.movieticketbooking.dto.ShowDTO;
import lk.ijse.movieticketbooking.entity.*;
import lk.ijse.movieticketbooking.exception.BadRequestException;
import lk.ijse.movieticketbooking.exception.ResourceNotFoundException;
import lk.ijse.movieticketbooking.repository.BookingRepository;
import lk.ijse.movieticketbooking.repository.MovieRepository;
import lk.ijse.movieticketbooking.repository.ShowRepository;
import lk.ijse.movieticketbooking.repository.TheatreRepository;
import lk.ijse.movieticketbooking.service.ShowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShowServiceImpl implements ShowService {

    private static final Logger log = LoggerFactory.getLogger(ShowServiceImpl.class);

    private final ShowRepository showRepository;
    private final MovieRepository movieRepository;
    private final TheatreRepository theatreRepository;
    private final BookingRepository bookingRepository;

    public ShowServiceImpl(ShowRepository showRepository, MovieRepository movieRepository,
                           TheatreRepository theatreRepository, BookingRepository bookingRepository) {
        this.showRepository = showRepository;
        this.movieRepository = movieRepository;
        this.theatreRepository = theatreRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public ShowDTO createShow(ShowDTO dto) {
        log.info("Create show request received for movie id {} and theatre id {}", dto.getMovieId(), dto.getTheatreId());

        Movie movie = movieRepository.findById(dto.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id " + dto.getMovieId()));

        if (movie.getStatus() == MovieStatus.ENDED) {
            log.warn("Show creation failed: movie id {} has ENDED", movie.getId());
            throw new BadRequestException("Cannot create a show for a movie that has ENDED");
        }

        Theatre theatre = theatreRepository.findById(dto.getTheatreId())
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id " + dto.getTheatreId()));

        if (theatre.getStatus() != TheatreStatus.ACTIVE) {
            log.warn("Show creation failed: theatre id {} is not ACTIVE", theatre.getId());
            throw new BadRequestException("Cannot create a show for an inactive theatre");
        }

        for (Show existing : showRepository.findAll()) {
            if (existing.getTheatre().getId().equals(theatre.getId())
                    && existing.getShowDate().equals(dto.getShowDate())
                    && existing.getShowTime().equals(dto.getShowTime())
                    && existing.getStatus() != ShowStatus.CANCELLED) {
                log.warn("Show creation failed: theatre id {} already has a show on {} at {}",
                        theatre.getId(), dto.getShowDate(), dto.getShowTime());
                throw new BadRequestException("This theatre already has a show at the same date and time");
            }
        }

        Show show = new Show();
        show.setMovie(movie);
        show.setTheatre(theatre);
        show.setShowDate(dto.getShowDate());
        show.setShowTime(dto.getShowTime());
        show.setTicketPrice(dto.getTicketPrice());
        show.setStatus(dto.getStatus() == null ? ShowStatus.SCHEDULED : ShowStatus.valueOf(dto.getStatus().toUpperCase()));

        Show saved = showRepository.save(show);
        log.info("Show created with id {}", saved.getId());
        return toDTO(saved);
    }

    @Override
    public List<ShowDTO> getAllShows() {
        List<ShowDTO> result = new ArrayList<>();
        for (Show show : showRepository.findAll()) {
            result.add(toDTO(show));
        }
        return result;
    }

    @Override
    public ShowDTO getShowById(Long id) {
        Show show = showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id " + id));
        return toDTO(show);
    }

    @Override
    public ShowDTO updateShow(Long id, ShowDTO dto) {
        log.info("Update show request received for show id {}", id);

        Show show = showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id " + id));

        if (dto.getMovieId() != null) {
            Movie movie = movieRepository.findById(dto.getMovieId())
                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id " + dto.getMovieId()));
            show.setMovie(movie);
        }

        if (dto.getTheatreId() != null) {
            Theatre theatre = theatreRepository.findById(dto.getTheatreId())
                    .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id " + dto.getTheatreId()));
            show.setTheatre(theatre);
        }

        if (dto.getShowDate() != null) show.setShowDate(dto.getShowDate());
        if (dto.getShowTime() != null) show.setShowTime(dto.getShowTime());
        if (dto.getTicketPrice() != null) show.setTicketPrice(dto.getTicketPrice());
        if (dto.getStatus() != null) show.setStatus(ShowStatus.valueOf(dto.getStatus().toUpperCase()));

        Show saved = showRepository.save(show);
        log.info("Show updated with id {}", saved.getId());
        return toDTO(saved);
    }

    @Override
    public void deleteShow(Long id) {
        log.info("Delete show request received for show id {}", id);

        Show show = showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id " + id));
        showRepository.delete(show);
        log.info("Show deleted with id {}", id);
    }

    @Override
    public List<ShowDTO> getShowsByMovie(Long movieId) {
        List<ShowDTO> result = new ArrayList<>();
        for (Show show : showRepository.findByMovieId(movieId)) {
            result.add(toDTO(show));
        }
        return result;
    }

    @Override
    public List<String> getBookedSeats(Long showId) {
        log.info("Booked seats request received for show id {}", showId);

        showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id " + showId));

        List<String> bookedSeats = new ArrayList<>();
        for (Booking booking : bookingRepository.findByShowId(showId)) {
            if (booking.getStatus() != BookingStatus.CANCELLED) {
                for (String seat : booking.getSeatNumbers()) {
                    bookedSeats.add(seat);
                }
            }
        }
        return bookedSeats;
    }

    private ShowDTO toDTO(Show show) {
        ShowDTO dto = new ShowDTO();
        dto.setId(show.getId());
        dto.setMovieId(show.getMovie().getId());
        dto.setTheatreId(show.getTheatre().getId());
        dto.setShowDate(show.getShowDate());
        dto.setShowTime(show.getShowTime());
        dto.setTicketPrice(show.getTicketPrice());
        dto.setStatus(show.getStatus().name());
        return dto;
    }
}