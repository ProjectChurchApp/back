package com.church.app.Security.login.dto;

public record LoginResponse (boolean success, String message, String username, String token){}
