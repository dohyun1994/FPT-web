package com.side_fpt.team_service.dto.schedule;

import com.side_fpt.team_service.model.Schedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {

    private Long id;
    private String title;
    private LocalDate date;


    public ScheduleDTO(Schedule schedule) {
        this.id = schedule.getId();
        this.title = schedule.getTitle();
        this.date = schedule.getDate();
    }

}
