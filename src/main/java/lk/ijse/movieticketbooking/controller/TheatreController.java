package lk.ijse.movieticketbooking.controller;

import jakarta.validation.Valid;
import lk.ijse.movieticketbooking.dto.TheatreDTO;
import lk.ijse.movieticketbooking.service.TheatreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theatres")
public class TheatreController {

    private final TheatreService theatreService;

    public TheatreController(TheatreService theatreService) {
        this.theatreService = theatreService;
    }

    @PostMapping
    public ResponseEntity<TheatreDTO> createTheatre(@Valid @RequestBody TheatreDTO theatreDTO) {
        return new ResponseEntity<>(theatreService.createTheatre(theatreDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TheatreDTO>> getAllTheatres() {
        return ResponseEntity.ok(theatreService.getAllTheatres());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TheatreDTO> getTheatreById(@PathVariable Long id) {
        return ResponseEntity.ok(theatreService.getTheatreById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TheatreDTO> updateTheatre(@PathVariable Long id, @Valid @RequestBody TheatreDTO theatreDTO) {
        return ResponseEntity.ok(theatreService.updateTheatre(id, theatreDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheatre(@PathVariable Long id) {
        theatreService.deleteTheatre(id);
        return ResponseEntity.noContent().build();
    }
}