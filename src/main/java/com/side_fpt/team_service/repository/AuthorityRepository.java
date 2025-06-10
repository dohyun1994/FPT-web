package com.side_fpt.team_service.repository;

import com.side_fpt.team_service.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long>{
    
}
