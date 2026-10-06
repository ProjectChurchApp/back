package com.church.app.admin.dto;

import com.church.app.signup.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 관리자 화면에 보여줄 가입자 요약.
 * 비밀번호는 BCrypt 단방향 해시로 저장되어 원문을 복원할 수 없고,
 * 복원할 수 있더라도 노출해서는 안 되므로 포함하지 않는다.
 */
@Getter
public class UserSummaryDto {

    private final Integer userId;
    private final String loginID;
    private final String name;
    private final String role;
    private final String accountStatus;
    private final LocalDateTime createdAt;

    public UserSummaryDto(User user) {
        this.userId = user.getUserId();
        this.loginID = user.getLoginID();
        this.name = user.getName();
        this.role = user.getRole().getLabel();
        this.accountStatus = user.getAccountStatus().name();
        this.createdAt = user.getCreatedat();
    }
}
