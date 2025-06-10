package com.side_fpt.team_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private boolean allowMultiple;
    private boolean isAnonymous;
    private LocalDateTime endTime;

    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<VoteOption> options;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private Account creator;

    @Builder.Default
    @Column(nullable = false)
    private boolean ended = false;

    public int getTotalParticipants() {
        return options == null ? 0 : options.stream().mapToInt(VoteOption::getVoteCount).sum();
    }

    public boolean isClosed() {
        return ended || endTime.isBefore(LocalDateTime.now());
    }

}
