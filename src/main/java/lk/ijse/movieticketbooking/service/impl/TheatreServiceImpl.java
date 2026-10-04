package lk.ijse.movieticketbooking.service.impl;

import lk.ijse.movieticketbooking.dto.TheatreDTO;
import lk.ijse.movieticketbooking.entity.Theatre;
import lk.ijse.movieticketbooking.entity.TheatreStatus;
import lk.ijse.movieticketbooking.exception.ResourceNotFoundException;
import lk.ijse.movieticketbooking.repository.TheatreRepository;
import lk.ijse.movieticketbooking.service.TheatreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TheatreServiceImpl implements TheatreService {

    private static final Logger log = LoggerFactory.getLogger(TheatreServiceImpl.class);

    private final TheatreRepository theatreRepository;

    public TheatreServiceImpl(TheatreRepository theatreRepository) {
        this.theatreRepository = theatreRepository;
    }

    @Override
    public TheatreDTO createTheatre(TheatreDTO theatreDTO) {
        log.info("Create theatre request received for theatre name {}", theatreDTO.getTheatreName());

        Theatre theatre = new Theatre();
        copyToEntity(theatreDTO, theatre);
        Theatre saved = theatreRepository.save(theatre);
        log.info("Theatre created with id {}", saved.getId());
        return toDTO(saved);
    }

    @Override
    public List<TheatreDTO> getAllTheatres() {
        List<TheatreDTO> result = new ArrayList<>();
        for (Theatre theatre : theatreRepository.findAll()) {
            result.add(toDTO(theatre));
        }
        return result;
    }

    @Override
    public TheatreDTO getTheatreById(Long id) {
        Theatre theatre = theatreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id " + id));
        return toDTO(theatre);
    }

    @Override
    public TheatreDTO updateTheatre(Long id, TheatreDTO theatreDTO) {
        log.info("Update theatre request received for theatre id {}", id);

        Theatre theatre = theatreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id " + id));
        copyToEntity(theatreDTO, theatre);
        Theatre saved = theatreRepository.save(theatre);
        log.info("Theatre updated with id {}", saved.getId());
        return toDTO(saved);
    }

    @Override
    public void deleteTheatre(Long id) {
        log.info("Delete theatre request received for theatre id {}", id);

        Theatre theatre = theatreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id " + id));
        theatreRepository.delete(theatre);
        log.info("Theatre deleted with id {}", id);
    }

    private void copyToEntity(TheatreDTO dto, Theatre theatre) {
        theatre.setTheatreName(dto.getTheatreName());
        theatre.setLocation(dto.getLocation());
        theatre.setCapacity(dto.getCapacity());

        if (dto.getStatus() == null) {
            theatre.setStatus(TheatreStatus.ACTIVE);
        } else {
            theatre.setStatus(TheatreStatus.valueOf(dto.getStatus().toUpperCase()));
        }
    }

    private TheatreDTO toDTO(Theatre theatre) {
        TheatreDTO dto = new TheatreDTO();
        dto.setId(theatre.getId());
        dto.setTheatreName(theatre.getTheatreName());
        dto.setLocation(theatre.getLocation());
        dto.setCapacity(theatre.getCapacity());
        dto.setStatus(theatre.getStatus().name());
        return dto;
    }
}