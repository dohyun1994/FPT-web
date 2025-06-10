package com.side_fpt.team_service.service;

import com.side_fpt.team_service.model.Account;
import com.side_fpt.team_service.model.Vote;
import com.side_fpt.team_service.model.VoteOption;
import com.side_fpt.team_service.model.VoteParticipant;
import com.side_fpt.team_service.repository.AccountRepository;
import com.side_fpt.team_service.repository.VoteOptionRepository;
import com.side_fpt.team_service.repository.VoteParticipantRepository;
import com.side_fpt.team_service.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteService {
    private final AccountRepository accountRepository;
    private final VoteRepository voteRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final VoteParticipantRepository voteParticipantRepository;

    @Transactional
    public Vote createVote(String title, boolean allowMultiple, boolean isAnonymous, LocalDateTime endTime,
                           List<String> options, List<String> imageUrls, String creatorEmail) {
        Account creator = accountRepository.findOneByEmailIgnoreCase(creatorEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        Vote vote = Vote.builder()
                .title(title)
                .allowMultiple(allowMultiple)
                .isAnonymous(isAnonymous)
                .endTime(endTime)
                .creator(creator)
                .ended(false)
                .build();

        voteRepository.save(vote);

        for (int i = 0; i < options.size(); i++) {
            String text = options.get(i);
            String imageUrl = (imageUrls != null && imageUrls.size() > i) ? imageUrls.get(i) : null;

            VoteOption option = VoteOption.builder()
                    .optionText(text)
                    .imageUrl(imageUrl)
                    .voteCount(0)
                    .vote(vote)
                    .build();

            voteOptionRepository.save(option);
        }

        return vote;
    }

    @Transactional(readOnly = true)
    public List<Vote> getAllVotes() {
        return voteRepository.findAllWithOptions(); // 강제 join fetch
    }

    @Transactional(readOnly = true)
    public Vote getVoteById(Long id) {
        return voteRepository.findById(id).orElse(null);
    }

    @Transactional
    public void deleteVote(Long voteId, String email) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 투표가 존재하지 않습니다."));

        // 투표 작성자만 삭제 가능하도록 검사
        if (!vote.getCreator().getEmail().equals(email)) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }

        // 1. 참여자 삭제
        voteParticipantRepository.deleteByVoteId(voteId);

        // 2. 옵션 삭제
        voteOptionRepository.deleteByVoteId(voteId);

        // 3. 투표 자체 삭제
        voteRepository.deleteById(voteId);

        log.info("투표 ID {} 가 삭제되었습니다.", voteId);
    }

    public void submitVote(Long voteId, List<Long> optionIds) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new RuntimeException("Vote not found"));

        for (VoteOption option : vote.getOptions()) {
            if (optionIds.contains(option.getId())) {
                option.setVoteCount(option.getVoteCount() + 1);
            }
        }

        voteRepository.save(vote); // cascade 설정 시 옵션도 같이 저장됨
    }

    public boolean hasAlreadyVoted(Long voteId, Long accountId) {
        return voteParticipantRepository.existsByVoteIdAndAccountId(voteId, accountId);
    }

    public void recordVote(Long voteId, Long accountId, List<Long> optionIds) {
        Vote vote = voteRepository.findById(voteId).orElseThrow();
        Account account = accountRepository.findById(accountId).orElseThrow();

        for (Long optionId : optionIds) {
            VoteOption option = voteOptionRepository.findById(optionId)
                    .orElseThrow(() -> new IllegalArgumentException("선택한 옵션이 존재하지 않습니다."));

            voteParticipantRepository.save(VoteParticipant.builder()
                    .vote(vote)
                    .voteOption(option)
                    .account(account)
                    .votedAt(LocalDateTime.now())
                    .build());
        }
    }

    @Transactional
    public void castVote(Long voteId, List<Long> optionIds) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 투표가 존재하지 않습니다."));

        for (Long optionId : optionIds) {
            VoteOption option = vote.getOptions().stream()
                    .filter(o -> o.getId().equals(optionId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("해당 옵션이 존재하지 않습니다."));

            option.setVoteCount(option.getVoteCount() + 1);
        }

        voteRepository.save(vote);
    }

    @Transactional
    public void endVote(Long voteId, String requesterEmail) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new IllegalArgumentException("투표를 찾을 수 없습니다."));

        if (!vote.getCreator().getEmail().equalsIgnoreCase(requesterEmail)) {
            throw new AccessDeniedException("투표 생성자만 종료할 수 있습니다.");
        }

        vote.setEnded(true); // 종료 처리
        voteRepository.save(vote);
    }

}
