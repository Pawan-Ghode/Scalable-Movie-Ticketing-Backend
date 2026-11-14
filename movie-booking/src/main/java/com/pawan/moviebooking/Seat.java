package com.pawan.moviebooking;

import jakarta.persistence.*;

@Entity
@Table(
    name = "seats",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_screen_seat",
            columnNames = {"screen_id", "seat_number"}
        )
    }
)
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    @Column(nullable = false)
    private String seatType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    protected Seat() {
    }

    public Seat(String seatNumber, String seatType, Screen screen) {
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.screen = screen;
    }

    public Integer getId() {
        return id;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public String getSeatType() {
        return seatType;
    }

    public Screen getScreen() {
        return screen;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }
}
