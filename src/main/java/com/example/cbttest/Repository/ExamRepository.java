package com.example.cbttest.Repository;

import com.example.cbttest.Entity.ExamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<ExamEntity, Long> {
    //교과목을 조회해서 응시 날짜를 내림차순 정렬
    List<ExamEntity> findBySubjectIdOrderByExamDateDesc(Long sugjectId);
    //응시자명으로 조회해서 응시날짜를 내림차순 정렬
    List<ExamEntity> findByExamineeNameContainingOrderByExamDateDesc(String examineeName);
    //모든 응시정보를 날짜로 내림차순 정렬
    List<ExamEntity> findAllByOrderByExamDateDesc();
    //응시상태로 조회
    List<ExamEntity> findByStatus(String status);
}
