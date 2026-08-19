package com.church.app.connection.dto;

import com.church.app.signup.entity.User;

public record MemberSummaryDto(String loginID, String name) {
    public MemberSummaryDto(User user) {
        this(user.getLoginID(), user.getName());
    }
}
