package lk.ijse.movieticketbooking.controller;

import jakarta.validation.Valid;
import lk.ijse.movieticketbooking.dto.ShowDTO;
import lk.ijse.movieticketbooking.service.ShowService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shows")
public class ShowController {

    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    @PostMapping
    public ResponseEntity<ShowDTO> createShow(@Valid @RequestBody ShowDTO showDTO) {
        return new ResponseEntity<>(showService.createShow(showDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ShowDTO>> getAllShows() {
        return ResponseEntity.ok(showService.getAllShows());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowDTO> getShowById(@PathVariable Long id) {
        return ResponseEntity.ok(showService.getShowById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShowDTO> updateShow(@PathVariable Long id, @Valid @RequestBody ShowDTO showDTO) {
        return ResponseEntity.ok(showService.updateShow(id, showDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShow(@PathVariable Long id) {
        showService.deleteShow(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ShowDTO>> getShowsByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(showService.getShowsByMovie(movieId));
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<List<String>> getBookedSeats(@PathVariable Long id) {
        return ResponseEntity.ok(showService.getBookedSeats(id));
    }
}