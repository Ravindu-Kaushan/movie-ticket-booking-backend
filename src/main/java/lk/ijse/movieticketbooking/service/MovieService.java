package lk.ijse.movieticketbooking.service;

import lk.ijse.movieticketbooking.dto.MovieDTO;

import java.util.List;

public interface MovieService {

    MovieDTO createMovie(MovieDTO movieDTO);

    List<MovieDTO> getAllMovies();

    MovieDTO getMovieById(Long id);

    MovieDTO updateMovie(Long id, MovieDTO movieDTO);

    void deleteMovie(Long id);
}