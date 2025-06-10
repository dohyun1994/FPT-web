package com.side_fpt.team_service.controller.api;

import com.side_fpt.team_service.dto.vote.VoteRequestDto;
import com.side_fpt.team_service.dto.vote.VoteUpdateDto;
import com.side_fpt.team_service.model.Vote;
import com.side_fpt.team_service.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/votes")
public class VoteRestController {

    private final VoteService voteService;

    @GetMapping
    public ResponseEntity<List<Vote>> getAllVotes() {
        return ResponseEntity.ok(voteService.getAllVotes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vote> getVote(@PathVariable Long id) {
        Vote vote = voteService.getVoteById(id);
        return vote != null ? ResponseEntity.ok(vote) : ResponseEntity.notFound().build();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Vote> createVote(@ModelAttribute VoteRequestDto dto, Principal principal) {
        Vote saved = voteService.createVote(
                dto.getTitle(),
                dto.isAllowMultiple(),
                dto.isAnonymous(),
                dto.getEndTime(),
                dto.getOptions(),
                dto.getOptionImageUrls(),
                principal.getName()
        );
        return ResponseEntity.ok(saved);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateVote(@PathVariable Long id, @RequestBody VoteUpdateDto dto) {
        Vote vote = voteService.getVoteById(id);
        if (vote == null) return ResponseEntity.notFound().build();

        vote.setTitle(dto.getTitle());
        vote.setAllowMultiple(dto.isAllowMultiple());
        vote.setAnonymous(dto.isAnonymous());
        return ResponseEntity.ok(vote);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVote(@PathVariable Long id, Principal principal) {
        Vote vote = voteService.getVoteById(id);
        if (vote == null) return ResponseEntity.notFound().build();

        try {
            voteService.deleteVote(id, principal.getName());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("삭제 중 오류가 발생했습니다.");
        }
    }
}
