package com.church.app.admin.dto;

import com.church.app.signup.entity.User;

import java.time.LocalDateTime;

public record PastorRequestDto(int userId, String loginID, String name, LocalDateTime requestedAt) {
    public PastorRequestDto(User user) {
        this(user.getUserId(), user.getLoginID(), user.getName(), user.getCreatedat());
    }
}
