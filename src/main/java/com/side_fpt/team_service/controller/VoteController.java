package com.side_fpt.team_service.controller;

import com.side_fpt.team_service.dto.vote.VoteSubmitDto;
import com.side_fpt.team_service.model.Account;
import com.side_fpt.team_service.model.Vote;
import com.side_fpt.team_service.model.VoteOption;
import com.side_fpt.team_service.repository.VoteParticipantRepository;
import com.side_fpt.team_service.service.AccountService;
import com.side_fpt.team_service.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/vote")
public class VoteController {

    private final VoteService voteService;
    private final AccountService accountService;
    private final VoteParticipantRepository voteParticipantRepository;

    @GetMapping("/voteBoard")
    public String voteBoardPage(Model model) {
        List<Vote> votes = voteService.getAllVotes();
        System.out.println("===== votes size: " + votes.size());

        // voteId별 참여자 수 저장할 Map
        Map<Long, Integer> participantCountMap = new HashMap<>();

        for (Vote vote : votes) {
            int count = voteParticipantRepository
                    .countDistinctAccountIdsByVoteId(vote.getId())
                    .intValue();
            participantCountMap.put(vote.getId(), count);
        }

        model.addAttribute("votes", votes);
        model.addAttribute("participantCountMap", participantCountMap);
        return "voteBoard";
    }

    @GetMapping("/addVote")
    public String addVotePage() {
        return "addVote";
    }

    @PostMapping("/addVote")
    public String submitVote(
            @RequestParam String voteTitle,
            @RequestParam(required = false) List<String> options,
            @RequestParam(required = false) List<String> optionImageUrls,  // 이미지 URL 리스트 추가
            @RequestParam(required = false, defaultValue = "false") boolean allowMultiple,
            @RequestParam(required = false, defaultValue = "false") boolean isAnonymous,
            @RequestParam("endTime") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endTime,
            Principal principal
    ) {
        System.out.println("principal : " + principal);
        System.out.println("options : " + options);
        System.out.println("imageUrls : " + optionImageUrls);
        voteService.createVote(voteTitle, allowMultiple, isAnonymous, endTime, options, optionImageUrls, principal.getName());
        return "redirect:/vote/voteBoard";
    }


    @GetMapping("/voteDetail")
    public String voteDetailPage(@RequestParam("voteId") Long voteId, Model model, Principal principal) {
        Vote vote = voteService.getVoteById(voteId);
        String email = principal.getName().trim();

        boolean isCreator = vote.getCreator() != null && vote.getCreator().getEmail().trim().equalsIgnoreCase(email);
        boolean isClosed = vote.isEnded() || vote.getEndTime().isBefore(LocalDateTime.now());

        // 참여자 수
        int totalParticipants = voteParticipantRepository.countDistinctAccountIdsByVoteId(vote.getId()).intValue();

        // 최다득표 수
        int maxVotes = vote.getOptions().stream()
                .mapToInt(VoteOption::getVoteCount)
                .max()
                .orElse(0);

        // 로그인한 사용자의 Account
        Account user = accountService.getByEmail(email);
        boolean hasVoted = voteService.hasAlreadyVoted(voteId, user.getId());

        // 공개 투표이면서 이미 참여했을 경우 각 옵션에 투표자 이름 추가
        // TODO: 이미 참여한 경우에 대한 조건 제거 필요. 컨트롤러, 레파지토리 
        if (!vote.isAnonymous()) {
            for (VoteOption option : vote.getOptions()) {
                List<String> names = voteParticipantRepository.findParticipantNamesByOptionId(option.getId());
                System.out.println("[debugging] name : " + names);
                System.out.println("[debugging] option.imageUrl: " + option.getImageUrl());
                option.setVoterNames(names);
            }
        }

        System.out.println("[debugging] vote : " + vote.toString());
        System.out.println("[debugging] isCreator : " + isCreator);
        System.out.println("[debugging] isClosed : " + isClosed);
        System.out.println("[debugging] hasVoted : " + hasVoted);
        System.out.println("[debugging] totalParticipants : " + totalParticipants);
        System.out.println("[debugging] maxVotes : " + maxVotes);

        model.addAttribute("vote", vote);
        model.addAttribute("isCreator", isCreator);
        model.addAttribute("isClosed", isClosed);
        model.addAttribute("hasVoted", hasVoted);
        model.addAttribute("totalParticipants", totalParticipants);
        model.addAttribute("maxVotes", maxVotes);

        return "voteDetail";
    }

    @PostMapping("/submitVote")
    public ResponseEntity<?> submitVote(@RequestBody VoteSubmitDto dto, Principal principal) {
        try {
            // 사용자 정보 가져오기
            Account account = accountService.findOneByEmail(principal.getName())
                    .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

            // 중복 투표 방지
            if (voteService.hasAlreadyVoted(dto.getVoteId(), account.getId())) {
                return ResponseEntity.badRequest().body("이미 투표에 참여하셨습니다.");
            }
            // 투표 반영 (옵션 투표 수 증가 등)
            voteService.castVote(dto.getVoteId(), dto.getOptionIds());
            // 투표 기록 저장
            voteService.recordVote(dto.getVoteId(), account.getId(), dto.getOptionIds());

            return ResponseEntity.ok("투표 완료");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
        }
    }

    @PostMapping("/endVote")
    public ResponseEntity<String> endVote(@RequestParam Long voteId, Principal principal) {
        voteService.endVote(voteId, principal.getName());
        return ResponseEntity.ok("투표가 종료되었습니다.");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteVote(@RequestParam Long voteId, Principal principal) {
        try {
            voteService.deleteVote(voteId, principal.getName());
            return ResponseEntity.ok("삭제 성공");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("삭제 실패");
        }
    }

}

