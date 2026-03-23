package com.example.cbttest.Repository;

import com.example.cbttest.Entity.ChapterEntity;
import com.example.cbttest.Entity.QuestionEntity;
import com.example.cbttest.Entity.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {
    //문제와 관련된 쿼리
    //JPA를 이용한 쿼리
    //해당 챕터를 이용해서 문제를 조회
    List<QuestionEntity> findByChapter(ChapterEntity chapter);
    //챕터id로 문제르르 조회
    List<QuestionEntity> findByChapterId(Long chapterId);
    //챕터id와 문제 유형으로 조회
    List<QuestionEntity> findByChapterIdAndQuestionType(Long chapterId, Long id);

    //JPQ를 이용한 쿼리
    //교과목과 관련된 문제 조회
    @Query("""
        SELECT q FROM QuestionEntity q WHERE q.chapter.subject.id= :subjectId
        ORDER BY q.chapter.chapterNo, q.id
    """)

    List<QuestionEntity> findBySubjectId(@Param("subjectId") Long subjectId);
    //챕터와 관련된 문제조회
    @Query("""
        SELECT COUNT(q) FROM QuestionEntity q WHERE q.chapter.id = :chapterId""")
    long countByChapterId(@Param("chapterId") Long chapterId);
}
