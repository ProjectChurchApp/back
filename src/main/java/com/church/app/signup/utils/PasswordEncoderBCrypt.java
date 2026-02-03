package com.church.app.signup.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderBCrypt {
    private final BCryptPasswordEncoder encoder;
    public PasswordEncoderBCrypt(BCryptPasswordEncoder encoder) {
        this.encoder = encoder;
    }

    /**
     * 비밀번호 암호화
     */
    public String encode(CharSequence rawPassword) throws PasswordEncodeException {
        try {
            return encoder.encode(rawPassword);
        } catch (Exception e) {
            throw new PasswordEncodeException("비밀번호 암호화 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 비밀번호 일치 여부 확인
     */
    public boolean matches(CharSequence rawPassword, String encodedPassword) throws PasswordEncodeException {
        try {
            return encoder.matches(rawPassword, encodedPassword);
        } catch (Exception e) {
            throw new PasswordEncodeException("비밀번호 검증 중 오류가 발생했습니다.", e);
        }
    }
}
