package com.example.cbttest.DTO;

import lombok.*;

//응답
@Getter @Setter
@ToString @Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamAnswerDTO {
    private Long id;
    private Long examId;
    private Long questionId;
    private String questionContent;
    private String questionType;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String correctAnswer;
    private String submittedAnswer;
    private Boolean isCorrect;
    private Integer earnedPoint;
    private Integer maxPoint;
    private String gradingComment;
}
