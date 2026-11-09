package com.pawan.moviebooking;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final SeatRepository seatRepository;
    private final ShowRepository showRepository;

    public BookingService(
            BookingRepository bookingRepository,
            BookingSeatRepository bookingSeatRepository,
            SeatRepository seatRepository,
            ShowRepository showRepository) {

        this.bookingRepository = bookingRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.seatRepository = seatRepository;
        this.showRepository = showRepository;
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {

        // 1. Validate show
       Show show = showRepository.findByIdForUpdate(request.showId())
        .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Show not found"));

        // 2. Reject duplicate seat IDs
        List<Integer> seatIds = request.seatIds();

        Set<Integer> uniqueSeatIds = new HashSet<>(seatIds);

        if (uniqueSeatIds.size() != seatIds.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Duplicate seat IDs are not allowed"
            );
        }

        // 3. Fetch seats
        List<Seat> seats = seatRepository.findAllByIdIn(seatIds);

        if (seats.size() != seatIds.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "One or more seats do not exist"
            );
        }

        // 4. Validate seats belong to show's screen
        for (Seat seat : seats) {

            if (!seat.getScreen().getId()
                    .equals(show.getScreen().getId())) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Selected seat does not belong to this show's screen"
                );
            }
        }

        // 5. Check already booked seats
        for (Integer seatId : seatIds) {

            if (bookingSeatRepository.existsByShow_IdAndSeat_Id(
                    show.getId(), seatId)) {

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "One or more seats are already booked"
                );
            }
        }

        // 6. Calculate total amount
        int totalAmount = Math.multiplyExact(
                show.getPrice(),
                seats.size()
        );

        // 7. Create booking
        Booking booking = new Booking(
                show,
                totalAmount,
                "CONFIRMED"
        );

        Booking savedBooking = bookingRepository.save(booking);

        // 8. Create booking-seat records
        List<BookingSeat> bookingSeats = seats.stream()
                .map(seat -> new BookingSeat(
                        savedBooking,
                        show,
                        seat
                ))
                .toList();

        bookingSeatRepository.saveAll(bookingSeats);

        // 9. Prepare response
        List<String> seatNumbers = seats.stream()
                .map(Seat::getSeatNumber)
                .toList();

        return new BookingResponse(
                savedBooking.getId(),
                show.getId(),
                seatNumbers,
                totalAmount,
                savedBooking.getStatus(),
                savedBooking.getCreatedAt()
        );
    }
}

