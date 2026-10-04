package lk.ijse.movieticketbooking.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "theatres")
public class Theatre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String theatreName;

    private String location;

    private Integer capacity;

    @Enumerated(EnumType.STRING)
    private TheatreStatus status;

    @OneToMany(mappedBy = "theatre", cascade = CascadeType.ALL)
    private List<Show> shows;

    public Theatre() {
    }

    public Theatre(Long id, String theatreName, String location, Integer capacity, TheatreStatus status) {
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

    public TheatreStatus getStatus() {
        return status;
    }

    public void setStatus(TheatreStatus status) {
        this.status = status;
    }

    public List<Show> getShows() {
        return shows;
    }

    public void setShows(List<Show> shows) {
        this.shows = shows;
    }
}