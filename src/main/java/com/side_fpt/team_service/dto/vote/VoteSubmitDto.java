package com.side_fpt.team_service.dto.vote;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VoteSubmitDto {
    private Long voteId;
    private List<Long> optionIds;
}
