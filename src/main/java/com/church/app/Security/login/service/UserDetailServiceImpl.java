package com.church.app.Security.login.service;

import com.church.app.signup.entity.User;
import com.church.app.signup.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String loginID) throws UsernameNotFoundException {

        User user = userRepository.findByLoginID(loginID)
                .orElseThrow(() -> {
                    System.out.println("사용자를 찾을 수 없음: " + loginID);
                    return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + loginID);
                });

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));

        return new org.springframework.security.core.userdetails.User(user.getLoginID(), user.getPassword(), authorities);
    }
}
