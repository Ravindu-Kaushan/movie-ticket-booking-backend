package lk.ijse.movieticketbooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TheatreDTO {

    private Long id;

    @NotBlank(message = "Theatre name is required")
    private String theatreName;

    private String location;

    @NotNull(message = "Capacity is required")
    private Integer capacity;

    private String status;

    public TheatreDTO() {
    }

    public TheatreDTO(Long id, String theatreName, String location, Integer capacity, String status) {
        this.id = id;
        this.theatreName = theatreName;
        this.location = location;
        this.capacity = capacity;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTheatreName() {
        return theatreName;
    }

    public void setTheatreName(String theatreName) {
        this.theatreName = theatreName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}