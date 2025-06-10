package com.side_fpt.team_service.dto.announce;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/*****************************************
 Developer Name : Soobcong
 Created On : 25. 5. 14.
 Project Name : team-service
 *****************************************/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementRequestDTO {
    private String title;
    private String content;
    private String placeName;
    private String placeAddress;
    private Double placex;  //경도
    private Double placey;  //위도
    private String createdBy;
    private MultipartFile file;
}
