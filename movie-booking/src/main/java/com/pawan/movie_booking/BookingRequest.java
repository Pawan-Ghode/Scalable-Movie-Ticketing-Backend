package com.pawan.moviebooking;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record BookingRequest(

        @NotNull
        Integer showId,

        @NotEmpty
        List<@NotNull Integer> seatIds

) {
}

