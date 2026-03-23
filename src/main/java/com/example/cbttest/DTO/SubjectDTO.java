package com.example.cbttest.DTO;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @ToString @Builder
@AllArgsConstructor @NoArgsConstructor
public class SubjectDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;

    //교과목 작업시 해당 자식에 챕터 정보
    private Integer chapterCount;
    private List<ChapterDTO> chapters;
}
//Entity->DTO->Repository->Service에 추가항목을 작업