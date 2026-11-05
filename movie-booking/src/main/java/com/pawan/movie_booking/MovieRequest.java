package com.pawan.moviebooking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record MovieRequest(
        @NotBlank String title,
        @PositiveOrZero int price
) {}
