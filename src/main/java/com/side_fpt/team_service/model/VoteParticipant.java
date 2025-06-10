package com.side_fpt.team_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Vote vote;

    @ManyToOne(fetch = FetchType.LAZY)
    private VoteOption voteOption;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    private LocalDateTime votedAt;
}
