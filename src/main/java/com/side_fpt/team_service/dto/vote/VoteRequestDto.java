package com.side_fpt.team_service.dto.vote;

import com.side_fpt.team_service.model.Account;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class VoteRequestDto {
    private String title;
    private boolean allowMultiple;
    private boolean isAnonymous;
    private LocalDateTime endTime;
    private List<String> options;
    private Account creator;
    private List<String> optionImageUrls;
}
