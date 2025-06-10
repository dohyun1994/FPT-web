package com.side_fpt.team_service.repository;

import com.side_fpt.team_service.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query("SELECT DISTINCT v FROM Vote v LEFT JOIN FETCH v.options ORDER BY v.id DESC")
    List<Vote> findAllWithOptions();

    @Query("SELECT v FROM Vote v LEFT JOIN FETCH v.options WHERE v.id = :voteId")
    Vote getVoteByIdWithOptions(@Param("voteId") Long voteId);

}
