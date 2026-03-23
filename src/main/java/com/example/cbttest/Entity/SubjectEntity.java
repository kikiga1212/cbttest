package com.example.cbttest.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 교과목 테이블(엔티티)
 */
@Entity
@Table(name = "subject")
@Getter @Setter
@Builder   //@ToString는 연관관계 작업시 무한루프 현상, 예외적용
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class SubjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //일련번호

    @Column(length =100, nullable = false)
    private String name; //교과목명

    @Column(columnDefinition = "TEXT") //String은 255자, "TEXT"는 65535자
    private String description; //설명

    @Column(nullable=false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt; //생성날짜

    //부모를 참조하는 자식테이블의 정보 및 연관관계의 제약조건
    //하나의 교과목(1)은 여러 챕터(N)와 연관
    //교과목이 없으면 챕터는 존재할 수가 없다. 교과목(부모), 챕터(자식)
    //교과목은 챕터가 없어도 존재할 수 있다.
    //mappedBy 자식테이블에 선언된 부모 Entity 변수명
    //orphanRemoval true-부모삭제시 연관된 자식 데이터도 함께 삭제
    //              false-자식이 존재시 삭제 불가능(부모삭제시 자식 데이터는 존재)
    @OneToMany(mappedBy = "subject", cascade=CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChapterEntity> chapters = new ArrayList<>();
}
