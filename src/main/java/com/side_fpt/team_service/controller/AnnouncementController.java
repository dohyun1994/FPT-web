package com.side_fpt.team_service.controller;

import com.side_fpt.team_service.dto.announce.AnnouncementRequestDTO;
import com.side_fpt.team_service.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
@RequiredArgsConstructor
@Controller
@RequestMapping("/announce")
public class AnnouncementController {
    private final AnnouncementService announcementService;
    @GetMapping("/announcement")
    public String AnnouncementPage() {
        System.out.println("Show Announcement Page");
        return "announcement/Announcement";
    }

    @GetMapping("/addAnnouncement")
    public String addAnnouncementPage() {
        System.out.println("Show add Announce Page");
        return "announcement/addAnnouncement";
    }

    @GetMapping("/detailAnnouncement")
    public String listAnnouncementPage() {
        System.out.println("Show list Announce Page");
        return "announcement/detailAnnouncement";
    }

    @PostMapping("/add")
    public String saveAnnouncement(@ModelAttribute AnnouncementRequestDTO dto) {
        try {
            announcementService.saveAnnouncement(dto);
        } catch (IOException e) {
            e.printStackTrace(); // 로그 찍고
            // 실패 시 에러 페이지 또는 다시 작성 폼으로 보낼 수 있음
            return "redirect:/announce/addAnnouncement";
        }

        // 성공 시 리스트 페이지로 리디렉트
        return "redirect:/announce/announcement";
    }

}
