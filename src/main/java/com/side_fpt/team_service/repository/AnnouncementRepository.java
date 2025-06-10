package com.side_fpt.team_service.repository;

import com.side_fpt.team_service.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*****************************************
 Developer Name : Soobcong
 Created On : 25. 5. 14.
 Project Name : team-service
 *****************************************/

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
}