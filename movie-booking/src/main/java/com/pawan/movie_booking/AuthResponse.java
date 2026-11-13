package com.pawan.moviebooking;

public record AuthResponse(
    String token,
    String tokenType
) {
}
