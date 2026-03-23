package com.example.cbttest.Repository;

import com.example.cbttest.Entity.ChapterEntity;
import com.example.cbttest.Entity.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChapterRepository
        extends JpaRepository<ChapterEntity, Long> {
    //삽입,수정,삭제, 조회(교과목과 상관없이 모든 데이터를 조회)

    //해당 교과목에 속하는 챕터를 조회(교과목 제목으로 조회를 해서, 챕터번호를 오름차순)
    List<ChapterEntity> findBySubjectOrderByChapterNoAsc(SubjectEntity subject);
    //교과목의 id에 속하는 챕터를 조회
    //SubjectEntity suject=>Subject,  그안에 id, subject, descrition=>첫글자 대문자로
    //Entity변수+Entity내의 변수=>SubjectId (subjectEntity에 id를 가지고)
    List<ChapterEntity> findBySubjectIdOrderByChapterNoAsc(Long sujectId);

    //해당 교과목에 챕터가 존재하는지 여부
    boolean existsBySubjectAndChapterNo(SubjectEntity subject, Integer chapterNo);
}
