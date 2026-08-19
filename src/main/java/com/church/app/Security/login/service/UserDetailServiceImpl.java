package com.church.app.Security.login.service;

import com.church.app.signup.entity.AccountStatus;
import com.church.app.signup.entity.Role;
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
                    //
                    return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + loginID);
                });

        boolean pendingPastor = user.getRole() == Role.PASTOR && user.getAccountStatus() != AccountStatus.ACTIVE;
        String authority = pendingPastor ? "ROLE_PENDING_PASTOR" : "ROLE_" + user.getRole().name();

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(authority));

        return new org.springframework.security.core.userdetails.User(user.getLoginID(), user.getPassword(), authorities);
    }
}
