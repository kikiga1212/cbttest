package com.example.cbttest.Service;

import com.example.cbttest.DTO.ExamDTO;
import com.example.cbttest.Entity.ExamAnswerEntity;
import com.example.cbttest.Entity.ExamEntity;
import com.example.cbttest.Entity.QuestionEntity;
import com.example.cbttest.Entity.SubjectEntity;
import com.example.cbttest.Repository.ExamAnswerRepository;
import com.example.cbttest.Repository.ExamRepository;
import com.example.cbttest.Repository.QuestionRepository;
import com.example.cbttest.Repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ExamService {
    private final ExamRepository examRepository;
    private final ExamAnswerRepository examAnswerRepository;
    private final SubjectRepository subjectRepository;
    private final QuestionRepository questionRepository;

    //평가시작 : 응시과목과 응시자명을 가지고
    public ExamDTO startExam(Long subjectId, String examineeName){
        SubjectEntity subject = subjectRepository.findById(subjectId)
                .orElseThrow(()-> new IllegalArgumentException("교과목이 존재하지 않습니다."));

        ExamEntity exam = ExamEntity.builder()
                .examineeName(examineeName)
                .subject(subject)
                .status("IN_PROGRESS")
                .build();

        ExamEntity saved = examRepository.save(exam);
        return toDTO(saved);
    }

    //답안제출(평가 진행중 답안 저장
    public void submitAnswer(Long examId, Map<Long, String> answerMap){
        ExamEntity exam = examRepository.findById(examId)
                .orElseThrow(()-> new IllegalArgumentException("평가가 존재하지 않습니다. "));
        for(Map.Entry<Long, String> entry : answerMap.entrySet()){
            Long questionId = entry.getKey();
            String submittedAnswer = entry.getValue();

            QuestionEntity question = questionRepository.findById(questionId)
                    .orElseThrow(()->new IllegalArgumentException("문제가 존재하지 않습니다."));

            //기존 답이 존재하면 업데이트
            ExamAnswerEntity answerEntity = examAnswerRepository.findByExamIdAndQuestionId(examId, questionId)
                    .orElseGet(()-> ExamAnswerEntity.builder()
                            .exam(exam)
                            .question(question)
                            .build());

            answerEntity.setSubmittedAnswer(submittedAnswer);
            //4지선다는 자동채점
            if("MULTIPLE_CHOICE".equals(question.getQuestionType())){
                boolean correct = question.getAnswer().equals(submittedAnswer);
                answerEntity.setIsCorrect(correct);
                answerEntity.setEarnedPoint(correct ? question.getPoint() : 0);
            }else {//필답형/주관식
                answerEntity.setIsCorrect(null);
                answerEntity.setEarnedPoint(null);
            }
            examAnswerRepository.save(answerEntity);
        }
        exam.setStatus("SUBMITTED");
        examRepository.save(exam);
    }

    //수동 채점 저장
    public void gradeManual(Long examId, Map<Long, Integer> scoreMap, Map<Long, String> commentMap){
        ExamEntity exam = examRepository.findById(examId)
                .orElseThrow(()->new IllegalArgumentException("평가가 존재하지 않습니다. "));

        List<ExamAnswerEntity> answers = examAnswerRepository.findByExamId(examId);
        for(ExamAnswerEntity answer : answers){
            Long qid = answer.getQuestion().getId();
            if(scoreMap.containsKey(qid)){
                int score = scoreMap.get(qid);
                int maxPoint = answer.getQuestion().getPoint();

                answer.setEarnedPoint(score);
                answer.setIsCorrect(score >= maxPoint);
                if(commentMap.containsKey(qid)){
                    answer.setGradingComment(commentMap.get(qid));
                }
                examAnswerRepository.save(answer);
            }
        }
    }

    //변환
    private ExamDTO toDTO(ExamEntity entity){
        int maxScore = 0;

        if("SUBMITTED".equals(entity.getStatus()) || "GRADED".equals(entity.getStatus())){
            maxScore = examAnswerRepository.findByExamId(entity.getId()).stream()
                    .mapToInt(a->a.getQuestion().getPoint()).sum();
        }

        return ExamDTO.builder()
                .id(entity.getId())
                .examineeName(entity.getExamineeName())
                .examDate(entity.getExamDate())
                .subjectId(entity.getSubject().getId())
                .subjectName(entity.getSubject().getName())
                .totalScore(entity.getTotalScore())
                .maxScore(entity.getMaxScore() !=null ? entity.getMaxScore() : maxScore)
                .status(entity.getStatus())
                .maxScore(maxScore)
                .build();
    }


}
