package com.side_fpt.team_service.service;

import com.side_fpt.team_service.dto.announce.AnnouncementRequestDTO;
import com.side_fpt.team_service.model.Announcement;
import com.side_fpt.team_service.repository.AnnouncementRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/*****************************************
 Developer Name : Soobcong
 Created On : 25. 5. 14.
 Project Name : team-service
 *****************************************/

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    // 파일 저장 경로
    private final String uploadDir = "D:/uploads/";

    public void saveAnnouncement(AnnouncementRequestDTO dto) throws IOException {

        String savedFilePath = null;

        // 1. 파일이 있다면 저장
        if (dto.getFile() != null && !dto.getFile().isEmpty()) {
            String originalFilename = dto.getFile().getOriginalFilename();
            String fileName = UUID.randomUUID() + "_" + originalFilename;

            Path path = Paths.get(uploadDir + fileName);
            Files.createDirectories(path.getParent()); // 폴더 없으면 생성
            Files.copy(dto.getFile().getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            savedFilePath = fileName;
        }

        // 2. Entity 생성 및 저장
        Announcement announcement = Announcement.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .placeName(dto.getPlaceName())
                .placeAddress(dto.getPlaceAddress())
                .placex(dto.getPlacex())
                .placey(dto.getPlacey())
                .createdBy(dto.getCreatedBy())  // 로그인 사용자 정보라면 나중에 Security에서 가져올 수도 있음
                .filePath(savedFilePath)
                .build();

        announcementRepository.save(announcement);
    }
}
