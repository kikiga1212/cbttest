package com.example.cbttest.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="exam")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ExamEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String examineeName;//응시자

    @Column(nullable = false,updatable = false)
    @CreatedDate
    private LocalDateTime examDate;//응시일
    @Column(length = 20, nullable = false)
    @Builder.Default
    private String status ="IN_PROGRESS";//상태(IN_PROGRESS, SUBMITTED, GRADED)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private SubjectEntity subject;//응시교과목

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ExamAnswerEntity> answers = new ArrayList<>();//응답(답안)

    private Integer totalScore;//최종점수
    private Integer maxScore;//최대점수
}
//부모 OneToMany(ManyToOne)<-> 자식 ManyToOne(OneToMany)양쪽에 연관관계가 존재하면 : 양방향
//자식 또는 부모에만 연관관계가 존재하면 : 단방향