package com.example.cbttest.Entity;

import jakarta.persistence.*;
import lombok.*;

//답안 엔티티
@Entity
@Table(name="exam_answer")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamAnswerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)//하나의 문항(부모)에는 여러학생(자식)이 답이 존재가 가능
    @JoinColumn(name="exam_id", nullable = false)
    private ExamEntity exam;//응시

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="question_id", nullable = false)
    private QuestionEntity question;//문제

    @Column(columnDefinition = "TEXT")
    private String submittedAnswer;//제출한 답안
    private Boolean isCorrect;//정답여부(4지선다 자동채점, 필답형/주관식은 수동)
    private Integer earnedPoint;//획득 점수

    @Column(columnDefinition = "TEXT")
    private String gradingComment;//총평
}
