package com.church.app.Security.login.dto;

public record TokenResponse(boolean success, String message, String accessToken) {}