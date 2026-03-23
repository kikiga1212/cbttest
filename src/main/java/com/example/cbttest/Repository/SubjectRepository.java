package com.example.cbttest.Repository;

import com.example.cbttest.Entity.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<SubjectEntity, Long> {
    //삽입, 수정, 삭제, 조회
    /**
     * 교과목의 존재여부
     * 동일한 교과목이 존재할 수 없어서, 저장전에 존재여부를 판별할 필요가 있다.
     * find,search~검색, is~맞는지 여부판별, exists~존재여부, update~수정
     */
    boolean existsByName(String name); //존재하면 true, 존재하지 않으면 false
    /**
     * 교과목으로 검색
     * 검색할 단어가 이름에 포함되어 있으면 조회
     * between 범위(~에서 ~까지)-날짜범위
     */
    List<SubjectEntity> findByNameContaining(String name);

}
