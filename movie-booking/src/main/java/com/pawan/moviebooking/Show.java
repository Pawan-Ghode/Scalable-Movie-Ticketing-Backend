
package com.pawan.moviebooking;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "movie_shows")
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private Integer price;

    protected Show() {
    }

    public Show(Movie movie, Screen screen,
                LocalDateTime startTime, Integer price) {
        this.movie = movie;
        this.screen = screen;
        this.startTime = startTime;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public Movie getMovie() {
        return movie;
    }

    public Screen getScreen() {
        return screen;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Integer getPrice() {
        return price;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}

