package lk.ijse.movieticketbooking.service;

import lk.ijse.movieticketbooking.dto.ShowDTO;

import java.util.List;

public interface ShowService {

    ShowDTO createShow(ShowDTO showDTO);

    List<ShowDTO> getAllShows();

    ShowDTO getShowById(Long id);

    ShowDTO updateShow(Long id, ShowDTO showDTO);

    void deleteShow(Long id);

    List<ShowDTO> getShowsByMovie(Long movieId);

    List<String> getBookedSeats(Long showId);
}