package com.example.cbttest.DTO;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@ToString @Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChapterDTO {
    private Long id; //번호
    private Integer chapterNo; //챕터번호
    private String title; //제목
    private String description; //설명
    private LocalDateTime createdAt;
    private Long subjectId;  //교과목(부모테이블)의 id번호
    private String subjectName; //교과목 이름
    //문제 추가
    private Integer questionCount;//해당 챕터의 문항수
    private List<QuestionDTO> questions;//해당 챕터의 문제
}
//DTO에는 부모/자식의 Entity를 사용
//자식에서 부모 Entity를 사용하지 않을 때는 반드시 부모의 id값을 저장
