package com.project.bookingmanager.dto;

public record AuthResponse(String token, String role, String fullName) {}