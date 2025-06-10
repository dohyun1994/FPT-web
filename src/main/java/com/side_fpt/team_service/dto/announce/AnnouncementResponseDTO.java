package com.side_fpt.team_service.dto.announce;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/*****************************************
 Developer Name : Soobcong
 Created On : 25. 5. 14.
 Project Name : team-service
 *****************************************/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementResponseDTO {

    private Long id;
    private String title;
    private String content;

    private String placeName;
    private String placeAddress;

    private Double placex;
    private Double placey;

    private String createdBy;
    private String filePath;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 생성자, Getter
}

