package lk.ijse.movieticketbooking.repository;

import lk.ijse.movieticketbooking.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}