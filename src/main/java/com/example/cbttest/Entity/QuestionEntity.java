package com.example.cbttest.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name="question")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class QuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//번호

    @Column(columnDefinition = "TEXT",nullable = false)
    private String content;//문제내용

    @Column(length = 20, nullable = false)
    private String questionType;//문제유형(4지선다, 단답형, 주관식)

    @Column(columnDefinition = "TEXT")//4지선다 보기
    private String option1;
    @Column(columnDefinition = "TEXT")//4지선다 보기
    private String option2;
    @Column(columnDefinition = "TEXT")//4지선다 보기
    private String option3;
    @Column(columnDefinition = "TEXT")//4지선다 보기
    private String option4;

    @Column(columnDefinition = "TEXT")
    private String answer;//정답(4지선다 정답, 단답형, 주관식)

    @Column(columnDefinition = "TEXT")
    private String explanation;//답만 해설
    @Column(nullable=false)
    @Builder.Default
    private Integer point = 1;//배점

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;//생성날짜
    @LastModifiedDate
    private LocalDateTime updatedAt;//최종수정날짜

    //챕터와 연관관계
    //하나의 챕터에는 여러개의 문제가 존재할 수  있다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="chapter_id", nullable = false)
    private ChapterEntity chapter;

}
