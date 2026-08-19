package com.church.app.connection.dto;

import com.church.app.signup.entity.User;

public record PastorOptionDto(String loginID, String name) {
    public PastorOptionDto(User user) {
        this(user.getLoginID(), user.getName());
    }
}
