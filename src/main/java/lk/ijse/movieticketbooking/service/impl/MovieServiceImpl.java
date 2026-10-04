package lk.ijse.movieticketbooking.service.impl;

import lk.ijse.movieticketbooking.dto.MovieDTO;
import lk.ijse.movieticketbooking.entity.Movie;
import lk.ijse.movieticketbooking.entity.MovieStatus;
import lk.ijse.movieticketbooking.exception.ResourceNotFoundException;
import lk.ijse.movieticketbooking.repository.MovieRepository;
import lk.ijse.movieticketbooking.service.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private static final Logger log = LoggerFactory.getLogger(MovieServiceImpl.class);

    private final MovieRepository movieRepository;

    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public MovieDTO createMovie(MovieDTO movieDTO) {
        Movie movie = new Movie();
        copyToEntity(movieDTO, movie);
        Movie saved = movieRepository.save(movie);
        log.info("Movie created with id {}", saved.getId());
        return toDTO(saved);
    }

    @Override
    public List<MovieDTO> getAllMovies() {
        List<MovieDTO> result = new ArrayList<>();
        for (Movie movie : movieRepository.findAll()) {
            result.add(toDTO(movie));
        }
        return result;
    }

    @Override
    public MovieDTO getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id " + id));
        return toDTO(movie);
    }

    @Override
    public MovieDTO updateMovie(Long id, MovieDTO movieDTO) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id " + id));
        copyToEntity(movieDTO, movie);
        Movie saved = movieRepository.save(movie);
        log.info("Movie updated with id {}", saved.getId());
        return toDTO(saved);
    }

    @Override
    public void deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id " + id));
        movieRepository.delete(movie);
        log.info("Movie deleted with id {}", id);
    }

    private void copyToEntity(MovieDTO dto, Movie movie) {
        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setDuration(dto.getDuration());
        movie.setLanguage(dto.getLanguage());
        movie.setGenre(dto.getGenre());
        movie.setReleaseDate(dto.getReleaseDate());

        if (dto.getStatus() == null) {
            movie.setStatus(MovieStatus.UPCOMING);
        } else {
            movie.setStatus(MovieStatus.valueOf(dto.getStatus().toUpperCase()));
        }
    }

    private MovieDTO toDTO(Movie movie) {
        MovieDTO dto = new MovieDTO();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setDescription(movie.getDescription());
        dto.setDuration(movie.getDuration());
        dto.setLanguage(movie.getLanguage());
        dto.setGenre(movie.getGenre());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setStatus(movie.getStatus().name());
        return dto;
    }
}