package com.side_fpt.team_service.repository;

import com.side_fpt.team_service.model.VoteOption;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM VoteOption vo WHERE vo.vote.id = :voteId")
    void deleteByVoteId(@Param("voteId") Long voteId);
}
