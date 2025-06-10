package com.side_fpt.team_service.repository;

import com.side_fpt.team_service.model.VoteParticipant;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoteParticipantRepository extends JpaRepository<VoteParticipant, Long> {

    boolean existsByVoteIdAndAccountId(Long voteId, Long accountId);

    @Query("SELECT vp.account.name FROM VoteParticipant vp WHERE vp.voteOption.id = :optionId")
    List<String> findParticipantNamesByOptionId(@Param("optionId") Long optionId);

    @Query("SELECT COUNT(DISTINCT vp.account.id) FROM VoteParticipant vp WHERE vp.vote.id = :voteId")
    Long countDistinctAccountIdsByVoteId(@Param("voteId") Long voteId);

    @Modifying
    @Transactional
    @Query("DELETE FROM VoteParticipant vp WHERE vp.vote.id = :voteId")
    void deleteByVoteId(@Param("voteId") Long voteId);
}
