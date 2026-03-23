package com.example.cbttest.Service;

import com.example.cbttest.DTO.ChapterDTO;
import com.example.cbttest.DTO.SubjectDTO;
import com.example.cbttest.Entity.SubjectEntity;
import com.example.cbttest.Repository.ChapterRepository;
import com.example.cbttest.Repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 교과목 작업에 필요한 로직
 */
@Service
@RequiredArgsConstructor
@Transactional //삽입, 수정, 삭제 적용, 작업전 백업해서 작업이 실패했을 때 작업전으로 복원
public class SubjectService {
    private final ModelMapper modelMapper;
    private final SubjectRepository subjectRepository;
    private final ChapterRepository chapterRepository; //자식테이블
    //todo: 챕터 정보

    /**
     * 교과목 등록
     */
    //개별 메소드에 적용(삽입,수정,삭제에) @Transactional
    public SubjectDTO createSubject(SubjectDTO dto) {
        //1️⃣ 변환방법
        //1) modelMapper 이용했을 때
        SubjectEntity entity = modelMapper.map(dto, SubjectEntity.class);

        //2) 수동으로 DTO를 Entity 변환할 때
        SubjectEntity entity2 = SubjectEntity.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();

        //2️⃣ 저장
        SubjectEntity saved = subjectRepository.save(entity);
        //2️⃣ 결과전달
        return toDTO(saved);
    }

    /**
     * 교과목 수정
     */
    public SubjectDTO updateSubject(Long id, SubjectDTO dto) {
        //1️⃣ 중복데이터 확인(유효성 검사)
        SubjectEntity entity = subjectRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("교과목이 존재하지 않습니다."));
        //2️⃣ 조회한 데이터(Entity)에 변경된 데이터(DTO)를 적용
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());

        //3️⃣ 저장 후 전달
        return toDTO(subjectRepository.save(entity));
    }

    /**
     * 교과목 삭제
     */
    public  void deleteSubject(Long id) {
        //1️⃣ 삭제 대상이 존재여부를 확인
        if(!subjectRepository.existsById(id)) { //삭제할 ID가 존재하지 않으면
            throw new IllegalArgumentException("교과목이 존재하지 않습니다.");
        }
        //2️⃣ 삭제처리
        subjectRepository.deleteById(id);
    }

    /**
     * 교과목의 전체 조회
     */
    @Transactional(readOnly = true) //클래스에 @Transactional을 적용했으면 조회는 제외
    public List<SubjectDTO> getAllSubjects() {
        //1️⃣ 전체조회해서 DTO로 변환해서 전달
        return subjectRepository.findAll().stream() //stream()은 for 또는 forEach와 동일한 반복
                .map(this::toDTO) //map은 stream값을 하나씩 저장, 개별 each(개별저장될 변수 : list 변수)
                .collect(Collectors.toList()); //collect는 map을 다시 저장(리스트 형식)
    }

    /**
     * 교과목의 개별 조회(상세보기, 수정)
     */
    @Transactional(readOnly = true)
    public SubjectDTO getSubject(Long id) {
        // 1️⃣ 조회(유효성 검사)
        SubjectEntity entity = subjectRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("교과목이 존재하지 않습니다."));

        // 2️⃣ 변환
        SubjectDTO dto = toDTO(entity);
        //todo: 교과목에 해당하는 챕터를 수집
        List<ChapterDTO> chapters = chapterRepository.findBySubjectIdOrderByChapterNoAsc(id)
                .stream().map(c->ChapterDTO.builder()
                        .id(c.getId())
                        .chapterNo(c.getChapterNo())
                        .title(c.getTitle())
                        .description(c.getDescription())
                        .createdAt(c.getCreatedAt())
                        .subjectId(entity.getId())
                        .subjectName(entity.getName())
                        .build()).collect(Collectors.toList());
        dto.setChapters(chapters);
        // 3️⃣ 전달
        return dto;
    }

    //todo: 해야할 내용을 기재
    //fixMe : 수정할 내용을 기재

    //변환(Entity를 DTO로 변환)
    private SubjectDTO toDTO(SubjectEntity entity) {
        //SubjectDTO subjectDTO  = modelMapper.map(entity, SubjectDTO.class);
        //return subjectDTO;

        return SubjectDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .chapterCount(entity.getChapters() != null ? entity.getChapters().size() : 0)
                .build();
    }
}
