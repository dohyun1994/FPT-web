package com.side_fpt.team_service.dto.vote;

import com.side_fpt.team_service.model.VoteOption;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class VoteResponseDto {
    private Long id;
    private String title;
    private boolean allowMultiple;
    private boolean isAnonymous;
    private LocalDateTime endTime;
    private List<VoteOption> options;
}
