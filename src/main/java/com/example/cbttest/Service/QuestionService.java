package com.example.cbttest.Service;

import com.example.cbttest.DTO.QuestionDTO;
import com.example.cbttest.Entity.ChapterEntity;
import com.example.cbttest.Entity.QuestionEntity;
import com.example.cbttest.Repository.ChapterRepository;
import com.example.cbttest.Repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionService {
    private final QuestionRepository questionRepository;//문제
    private final ModelMapper modelMapper;
    private final ChapterRepository chapterRepository;//챕터(부모)

    //삽입(부모 챕터번호와 문항을 받아서 저장)
    public QuestionDTO createQuestion(Long chapterId, QuestionDTO dto){
        //부모 정볼르 조회(유효성)
        ChapterEntity chapter = chapterRepository.findById(chapterId)
                .orElseThrow(()-> new IllegalArgumentException("챕터가 존재하지 않습니다."));
        QuestionEntity entity = QuestionEntity.builder()
                .content(dto.getContent())          //1:1로 값을 지정
                .questionType(dto.getQuestionType())//필요한 데이터만 적용해서 사용하므로
                .option1(dto.getOption1())          //오류발생률이 적다.
                .option2(dto.getOption2())
                .option3(dto.getOption3())
                .option4(dto.getOption4())
                .answer(dto.getAnswer())
                .explanation(dto.getExplanation())
                .point(dto.getPoint() !=null ? dto.getPoint() : 1)
                .chapter(chapter)
                .build();

        //modelmapper를 이용하면 DTO를 쉽게 Entity변환이 가능한데, 필요한 변수에 의해서 오류가 발생할 확률은 높다.
        QuestionEntity entity1 = modelMapper.map(dto, QuestionEntity.class);
        entity1.setChapter(chapter);

        //modelmapper로 변환해서 전달시 추가적인 내용을 작업하기가 힘들다.
        //return modelMapper.map(questionRepository.save(entity),QuestionDTO.class);
        return toDTO(questionRepository.save(entity));
    }

    //수정
    //수정할 대상의 id와 수정할 내용(chapterid 존재)을 가지고 수정
    public QuestionDTO updateQuestion(Long id, QuestionDTO dto){
        //유효성 검사, 수정할 데이터를 조회
        QuestionEntity entity = questionRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("문제가 존재하지 않습니다."));

        //수정할 부분만 적용(가능하면 데이터베이스 내용에서 부분수정으로 작업하면 오류가 발생하지 않는다.
        entity.setContent(dto.getContent());
        entity.setQuestionType(dto.getQuestionType());
        entity.setOption1(dto.getOption1());
        entity.setOption2(dto.getOption2());
        entity.setOption3(dto.getOption3());
        entity.setOption4(dto.getOption4());
        entity.setAnswer(dto.getAnswer());
        entity.setExplanation(dto.getExplanation());
        //숫자데이터는 유효성검사후 저장(오류가 가장 많이 발생하는 데이터형 숫자형)
        if(dto.getPoint() != null) entity.setPoint(dto.getPoint());

        return toDTO(questionRepository.save(entity));
    }

    //삭제
    public void deleteQuestion(Long id){
        if(!questionRepository.existsById(id)) {
            throw new IllegalArgumentException("문제가 존재하지 않습니다.");
        }
        questionRepository.deleteById(id);
        //배열로 전달된 여러개의 값을 동시에 저장 questionRepository.saveAll()
        //DTO[] dto = {dto1, dto2, dto3};
        //배열로 전달된 여러개의 갑을 동시에 삭제 questionRepository.deleteAllById();
        //Long[] ids = {3, 6, 8 ,9};
    }

    //@Transactional 이 클래스에 선언시 조회에 @Transactional(readOnly=true) 적용시킨다.
    //메소드에 적용시 삽입, 수정, 삭제에 @Transactional 적용시킨다.
    //일괄처리(Transactional) 전에 Commit(백업)후 작업하다가 오류가 발생이 되면 Rollback(전상태 복원)

    //전체조회(챕터별)
    @Transactional(readOnly=true)
    public List<QuestionDTO> getQuestionByChapter(Long chapterId){
        return questionRepository.findByChapterId(chapterId)
                .stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    //전체조회(교과목별)
    @Transactional(readOnly=true)
    public List<QuestionDTO> getQuestionBySubject(Long subjectId){
        return questionRepository.findBySubjectId(subjectId)
                .stream().map(this::toDTO)
                .collect(Collectors.toList());
    }


    //개별조회
    @Transactional(readOnly=true)
    public QuestionDTO getQuestion(Long id){
        QuestionEntity entity = questionRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("문제가 존재하지 않습니다."));

        return toDTO(entity);
    }

    /**변환 메소드(modelmapper 미사용시)*/
    private QuestionDTO toDTO(QuestionEntity entity){
        return QuestionDTO.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .questionType(entity.getQuestionType())
                .option1(entity.getOption1())
                .option2(entity.getOption2())
                .option3(entity.getOption3())
                .option4(entity.getOption4())
                .answer(entity.getAnswer())
                .explanation(entity.getExplanation())
                .point(entity.getPoint())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .chapterId(entity.getChapter().getId())//entity에 존재하지 않는 값
                .chapterTitle(entity.getChapter().getTitle())
                .subjectId(entity.getChapter().getSubject().getId())
                .subjectName(entity.getChapter().getSubject().getName())
                .build();

    }



}
