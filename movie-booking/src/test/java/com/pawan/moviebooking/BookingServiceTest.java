package com.pawan.moviebooking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingSeatRepository bookingSeatRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private ShowRepository showRepository;

    @InjectMocks
    private BookingService bookingService;

    private Show show;
    private Screen showScreen;
    private Seat seat;

   @BeforeEach
void setUp() {
    show = org.mockito.Mockito.mock(Show.class);
    showScreen = org.mockito.Mockito.mock(Screen.class);
    seat = org.mockito.Mockito.mock(Seat.class);

    lenient().when(show.getId()).thenReturn(2);
    lenient().when(show.getPrice()).thenReturn(250);
    lenient().when(show.getScreen()).thenReturn(showScreen);
    lenient().when(showScreen.getId()).thenReturn(1);

    lenient().when(seat.getScreen()).thenReturn(showScreen);
    lenient().when(seat.getSeatNumber()).thenReturn("A5");
}
    @Test
    void createBooking_shouldCreateBookingSuccessfully() {

        when(showRepository.findByIdForUpdate(2))
                .thenReturn(Optional.of(show));

        when(seatRepository.findAllByIdIn(List.of(6)))
                .thenReturn(List.of(seat));

        when(bookingSeatRepository.existsByShow_IdAndSeat_Id(2, 6))
                .thenReturn(false);

        Booking savedBooking = org.mockito.Mockito.mock(Booking.class);

        LocalDateTime createdAt = LocalDateTime.of(2026, 11, 10, 14, 0);

        when(savedBooking.getId()).thenReturn(10);
        when(savedBooking.getStatus()).thenReturn("CONFIRMED");
        when(savedBooking.getCreatedAt()).thenReturn(createdAt);

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(savedBooking);

        BookingResponse response = bookingService.createBooking(
                new BookingRequest(2, List.of(6))
        );

        assertEquals(10, response.bookingId());
        assertEquals(2, response.showId());
        assertEquals(List.of("A5"), response.seatNumbers());
        assertEquals(250, response.totalAmount());
        assertEquals("CONFIRMED", response.status());
        assertEquals(createdAt, response.createdAt());

        verify(bookingRepository).save(any(Booking.class));
        verify(bookingSeatRepository).saveAll(anyList());
    }

    @Test
    void createBooking_shouldThrow404_whenShowDoesNotExist() {

        when(showRepository.findByIdForUpdate(999))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> bookingService.createBooking(
                        new BookingRequest(999, List.of(6))
                )
        );

        assertEquals(404, exception.getStatusCode().value());
        assertEquals("Show not found", exception.getReason());
    }

    @Test
    void createBooking_shouldThrow400_whenDuplicateSeatIdsAreProvided() {

        when(showRepository.findByIdForUpdate(2))
                .thenReturn(Optional.of(show));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> bookingService.createBooking(
                        new BookingRequest(2, List.of(6, 6))
                )
        );

        assertEquals(400, exception.getStatusCode().value());
        assertEquals(
                "Duplicate seat IDs are not allowed",
                exception.getReason()
        );
    }

    @Test
    void createBooking_shouldThrow400_whenSeatDoesNotExist() {

        when(showRepository.findByIdForUpdate(2))
                .thenReturn(Optional.of(show));

        when(seatRepository.findAllByIdIn(List.of(999)))
                .thenReturn(List.of());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> bookingService.createBooking(
                        new BookingRequest(2, List.of(999))
                )
        );

        assertEquals(400, exception.getStatusCode().value());
        assertEquals(
                "One or more seats do not exist",
                exception.getReason()
        );
    }

    @Test
    void createBooking_shouldThrow400_whenSeatBelongsToDifferentScreen() {

        when(showRepository.findByIdForUpdate(2))
                .thenReturn(Optional.of(show));

        Screen differentScreen = org.mockito.Mockito.mock(Screen.class);
        when(differentScreen.getId()).thenReturn(2);
        when(seat.getScreen()).thenReturn(differentScreen);

        when(seatRepository.findAllByIdIn(List.of(5)))
                .thenReturn(List.of(seat));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> bookingService.createBooking(
                        new BookingRequest(2, List.of(5))
                )
        );

        assertEquals(400, exception.getStatusCode().value());
        assertEquals(
                "Selected seat does not belong to this show's screen",
                exception.getReason()
        );
    }

    @Test
    void createBooking_shouldThrow409_whenSeatIsAlreadyBooked() {

        when(showRepository.findByIdForUpdate(2))
                .thenReturn(Optional.of(show));

        when(seatRepository.findAllByIdIn(List.of(6)))
                .thenReturn(List.of(seat));

        when(bookingSeatRepository.existsByShow_IdAndSeat_Id(2, 6))
                .thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> bookingService.createBooking(
                        new BookingRequest(2, List.of(6))
                )
        );

        assertEquals(409, exception.getStatusCode().value());
        assertEquals(
                "One or more seats are already booked",
                exception.getReason()
        );
    }
}
