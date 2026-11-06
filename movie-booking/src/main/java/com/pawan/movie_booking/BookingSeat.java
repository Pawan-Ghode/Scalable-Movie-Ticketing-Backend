
package com.pawan.moviebooking;

import jakarta.persistence.*;

@Entity
@Table(
    name = "booking_seats",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_show_seat",
            columnNames = {"show_id", "seat_id"}
        )
    }
)
public class BookingSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    protected BookingSeat() {
    }

    public BookingSeat(Booking booking, Show show, Seat seat) {
        this.booking = booking;
        this.show = show;
        this.seat = seat;
    }

    public Integer getId() {
        return id;
    }

    public Booking getBooking() {
        return booking;
    }

    public Show getShow() {
        return show;
    }

    public Seat getSeat() {
        return seat;
    }
}
