package com.example.cbttest.Service;

import com.example.cbttest.DTO.ChapterDTO;
import com.example.cbttest.Entity.ChapterEntity;
import com.example.cbttest.Entity.SubjectEntity;
import com.example.cbttest.Repository.ChapterRepository;
import com.example.cbttest.Repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.Subject;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChapterService {
    private final ModelMapper modelMapper;
    private final ChapterRepository chapterRepository;
    private final SubjectRepository subjectRepository; //자식테이블은 반드시 부모테이블의 Repository가 필요

    /**삽입*/
    public ChapterDTO createChapter(Long subjectId, ChapterDTO dto) {
        SubjectEntity subject = subjectRepository.findById(subjectId)
                .orElseThrow(()->new IllegalArgumentException("교과목이 존재하지 않습니다."));//부모테이블의 데이터 존재여부

        ChapterEntity entity = modelMapper.map(dto, ChapterEntity.class);//1.저장데이터 Entity
        entity.setSubject(subject);

        ChapterEntity entity2 = ChapterEntity.builder()//2. 저장데이터 Builder
                .chapterNo(dto.getChapterNo())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .subject(subject)
                .build();

        return  toDTO(chapterRepository.save(entity));
    }

    /**수정*/
    public ChapterDTO updateChapter(Long id, ChapterDTO dto) {
        //자식정보를 읽으면 subject에 자동으로 부모정보가 저장된다.
        ChapterEntity entity = chapterRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("챕터가 존재하지 않습니다."));

        entity.setChapterNo(dto.getChapterNo());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());

        return toDTO(chapterRepository.save(entity));
    }

    /**삭제*/
    public void deleteChapter(Long id) {
        if(!chapterRepository.existsById(id)) {
            throw new IllegalArgumentException("챕터가 존재하지 않습니다.");
        }
        chapterRepository.deleteById(id);
    }

    /**전체조회*/
    @Transactional(readOnly=true)
    public List<ChapterDTO> getChaptersBySubject(Long subjectId) {
        return chapterRepository.findBySubjectIdOrderByChapterNoAsc(subjectId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**개별조회*/
    @Transactional(readOnly = true)
    public ChapterDTO getChapter(Long id) {
        ChapterEntity entity = chapterRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("챕터가 존재하지 않습니다."));

        return toDTO(entity);
    }

    private ChapterDTO toDTO(ChapterEntity entity) {
        return ChapterDTO.builder()
                .id(entity.getId())
                .chapterNo(entity.getChapterNo())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .subjectId(entity.getSubject().getId()) //교과목 번호(부모테이블)
                .subjectName(entity.getSubject().getName()) //교과목제목
                .questionCount(entity.getQuestions() != null ? entity.getQuestions().size() : 0)
                .build();
    }
}
