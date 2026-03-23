package com.example.cbttest.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDTO {
    private Long id;
    private String content;
    private String questionType;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String answer;
    private String explanation;
    private Integer point;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long chapterId;//자식은 부모의 id값을 반드시 기억
    private String chapterTitle;
    private String subjectId;//교과목id
    private String subjectName;
}
