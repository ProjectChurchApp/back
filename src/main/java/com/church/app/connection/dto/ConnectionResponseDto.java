package com.church.app.connection.dto;

import com.church.app.connection.entity.PastorConnection;

import java.time.LocalDateTime;

public record ConnectionResponseDto(
        Long id,
        String memberLoginID,
        String memberName,
        String pastorLoginID,
        String pastorName,
        String status,
        LocalDateTime requestedAt,
        LocalDateTime respondedAt
) {
    public ConnectionResponseDto(PastorConnection c) {
        this(
                c.getId(),
                c.getMember().getLoginID(),
                c.getMember().getName(),
                c.getPastor().getLoginID(),
                c.getPastor().getName(),
                c.getStatus().name(),
                c.getRequestedAt(),
                c.getRespondedAt()
        );
    }
}
