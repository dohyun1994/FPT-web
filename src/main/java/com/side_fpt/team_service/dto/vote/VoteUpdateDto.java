package com.side_fpt.team_service.dto.vote;

import lombok.Getter;

@Getter
public class VoteUpdateDto {
    private String title;
    private boolean allowMultiple;
    private boolean isAnonymous;
}
