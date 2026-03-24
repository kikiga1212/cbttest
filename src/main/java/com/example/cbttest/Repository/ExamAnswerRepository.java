package com.example.cbttest.Repository;

import com.example.cbttest.Entity.ExamAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamAnswerRepository extends JpaRepository<ExamAnswerEntity, Long> {
    //시험정보로 조회
    List<ExamAnswerEntity> findByExamId(Long examId);
    //시험정보와 문제 조회
    Optional<ExamAnswerEntity> findByExamIdAndQuestionId(Long examId, Long questionId);
}
