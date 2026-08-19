package com.church.app.connection.repository;

import com.church.app.connection.entity.PastorConnection;
import com.church.app.signup.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PastorConnectionRepository extends JpaRepository<PastorConnection, Long> {

    boolean existsByMemberAndStatus(User member, PastorConnection.Status status);

    List<PastorConnection> findAllByPastorAndStatus(User pastor, PastorConnection.Status status);

    Optional<PastorConnection> findTopByMemberOrderByRequestedAtDesc(User member);
}
