package com.church.app.signup.utils;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Component
public class PasswordValidator {

    // 비밀번호 정규식:
    // - 최소 8자
    // - 영문자 1개 이상
    // - 숫자 1개 이상
    // - 특수문자(@$!%*#?&) 1개 이상
    private static final String PASSWORD_PATTERN =
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";

    // 정규식을 미리 컴파일 (성능 최적화)
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public boolean isValid(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return pattern.matcher(password).matches();
    }

    public void validate(String password) {
        if (!isValid(password)) {
            throw new IllegalArgumentException(
                    "비밀번호는 8자 이상이며, 영문자, 숫자, 특수문자(@$!%*#?&)를 포함해야 합니다."
            );
        }
    }
}
