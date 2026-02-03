package com.church.app.signup.dto;

import java.sql.Date;

public record SignupRequest(String loginID, String password, String role, String name) {
}
