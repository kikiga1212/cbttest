package com.example.cbttest.DTO;

import com.example.cbttest.Entity.ExamAnswerEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ExamDTO {
    private Long id;
    private String examineeName;
    private LocalDateTime examDate;
    private String status;
    private Long subjectId;
    private String subjectName;
    private Integer totalScore;
    private Integer maxScore;
    private List<ExamAnswerDTO> answers;
}
