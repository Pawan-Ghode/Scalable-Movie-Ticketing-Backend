package com.pawan.moviebooking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import java.time.LocalDateTime;
import org.springframework.security.test.context.support.WithMockUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "test@example.com", roles = {"USER"})
class BookingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingSeatRepository bookingSeatRepository;

    private Integer showId;
    private Integer seat1Id;
    private Integer seat2Id;
    private Integer otherScreenSeatId;

    @BeforeEach
    void setUp() {

        // Clean existing test data in foreign-key dependency order
        bookingSeatRepository.deleteAll();
        bookingRepository.deleteAll();
        seatRepository.deleteAll();
        showRepository.deleteAll();
        screenRepository.deleteAll();
        theaterRepository.deleteAll();
        movieRepository.deleteAll();

        // Create Movie
        Movie movie = movieRepository.save(
                new Movie(null, "Integration Test Movie", 250)
        );

        // Create Theater
        Theater theater = theaterRepository.save(
                new Theater("Integration Test Theater", "Nagpur")
        );

        // Create Screen 1
        Screen screen1 = screenRepository.save(
                new Screen("Screen 1", theater)
        );

        // Create Screen 2
        Screen screen2 = screenRepository.save(
                new Screen("Screen 2", theater)
        );

        // Create Show on Screen 1
        Show show = showRepository.save(
                new Show(
                        movie,
                        screen1,
                        LocalDateTime.now().plusDays(1),
                        250
                )
        );

        showId = show.getId();

        // Create seats on Screen 1
        Seat seat1 = seatRepository.save(
                new Seat("A1", "PREMIUM", screen1)
        );

        Seat seat2 = seatRepository.save(
                new Seat("A2", "PREMIUM", screen1)
        );

        // Create a seat on Screen 2
        Seat otherScreenSeat = seatRepository.save(
                new Seat("B1", "REGULAR", screen2)
        );

        seat1Id = seat1.getId();
        seat2Id = seat2.getId();
        otherScreenSeatId = otherScreenSeat.getId();
    }

    // Test 1: Successful booking
   
@Test
void shouldCreateBookingSuccessfully() throws Exception {

    // 1. Send booking request and verify HTTP response
    mockMvc.perform(post("/api/bookings")
    .with(user("test@example.com").roles("USER"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {
                        "showId": %d,
                        "seatIds": [%d, %d]
                    }
                    """.formatted(showId, seat1Id, seat2Id)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.bookingId").isNumber())
            .andExpect(jsonPath("$.showId").value(showId))
            .andExpect(jsonPath("$.seatNumbers[0]").value("A1"))
            .andExpect(jsonPath("$.seatNumbers[1]").value("A2"))
            .andExpect(jsonPath("$.totalAmount").value(500))
            .andExpect(jsonPath("$.status").value("CONFIRMED"))
            .andExpect(jsonPath("$.createdAt").isNotEmpty());

    // 2. Verify actual database records
    assertEquals(1, bookingRepository.count());
    assertEquals(2, bookingSeatRepository.count());

    // 3. Verify saved booking details
    Booking savedBooking = bookingRepository.findAll().get(0);

    assertEquals(showId, savedBooking.getShow().getId());
    assertEquals(500, savedBooking.getTotalAmount());
    assertEquals("CONFIRMED", savedBooking.getStatus());
}


    // Test 2: Show does not exist
    @Test
    void shouldReturn404WhenShowDoesNotExist() throws Exception {

        mockMvc.perform(post("/api/bookings")
        .with(user("test@example.com").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "showId": 999999,
                            "seatIds": [%d]
                        }
                        """.formatted(seat1Id)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        assertEquals(0, bookingRepository.count());
        assertEquals(0, bookingSeatRepository.count());
    }

    // Test 3: Duplicate seat IDs
    @Test
    void shouldRejectDuplicateSeatIds() throws Exception {

        mockMvc.perform(post("/api/bookings")
        .with(user("test@example.com").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "showId": %d,
                            "seatIds": [%d, %d]
                        }
                        """.formatted(showId, seat1Id, seat1Id)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        assertEquals(0, bookingRepository.count());
        assertEquals(0, bookingSeatRepository.count());
    }

    // Test 4: Seat does not exist
    @Test
    void shouldRejectNonExistingSeat() throws Exception {

        mockMvc.perform(post("/api/bookings")
        .with(user("test@example.com").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "showId": %d,
                            "seatIds": [999999]
                        }
                        """.formatted(showId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        assertEquals(0, bookingRepository.count());
        assertEquals(0, bookingSeatRepository.count());
    }

    // Test 5: Seat belongs to another screen
    @Test
    void shouldRejectSeatFromAnotherScreen() throws Exception {

        mockMvc.perform(post("/api/bookings")
        .with(user("test@example.com").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "showId": %d,
                            "seatIds": [%d, %d]
                        }
                        """.formatted(showId, seat1Id, otherScreenSeatId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        assertEquals(0, bookingRepository.count());
        assertEquals(0, bookingSeatRepository.count());
    }

    // Test 6: Seat is already booked
    @Test
    void shouldRejectAlreadyBookedSeat() throws Exception {

        // First request should succeed
        mockMvc.perform(post("/api/bookings")
        .with(user("test@example.com").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "showId": %d,
                            "seatIds": [%d]
                        }
                        """.formatted(showId, seat1Id)))
                .andExpect(status().isCreated());

        // Second request for the same seat should fail
        mockMvc.perform(post("/api/bookings")
        .with(user("test@example.com").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "showId": %d,
                            "seatIds": [%d]
                        }
                        """.formatted(showId, seat1Id)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("One or more seats are already booked"));

        // Failed booking must not create additional records
        assertEquals(1, bookingRepository.count());
        assertEquals(1, bookingSeatRepository.count());
    }

    // Test 7: Empty seat list
    @Test
    void shouldRejectEmptySeatList() throws Exception {

        mockMvc.perform(post("/api/bookings")
        .with(user("test@example.com").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "showId": %d,
                            "seatIds": []
                        }
                        """.formatted(showId)))
                .andExpect(status().isBadRequest());

        assertEquals(0, bookingRepository.count());
        assertEquals(0, bookingSeatRepository.count());
    }

    @Test
void shouldRejectMissingShowId() throws Exception {

    mockMvc.perform(post("/api/bookings")
    .with(user("test@example.com").roles("USER"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {
                        "seatIds": [1]
                    }
                    """))
            .andExpect(status().isBadRequest());

    assertEquals(0, bookingRepository.count());
    assertEquals(0, bookingSeatRepository.count());
 }

 @Test
void shouldRejectNullSeatId() throws Exception {

    mockMvc.perform(post("/api/bookings")
    .with(user("test@example.com").roles("USER"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {
                        "showId": %d,
                        "seatIds": [null]
                    }
                    """.formatted(showId)))
            .andExpect(status().isBadRequest());

    assertEquals(0, bookingRepository.count());
    assertEquals(0, bookingSeatRepository.count());
 }

 @Test
void shouldRejectMalformedJson() throws Exception {

    mockMvc.perform(post("/api/bookings")
    .with(user("test@example.com").roles("USER"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {
                        "showId": 1,
                        "seatIds": [
                    """))
            .andExpect(status().isBadRequest());

    assertEquals(0, bookingRepository.count());
    assertEquals(0, bookingSeatRepository.count());
 }
}