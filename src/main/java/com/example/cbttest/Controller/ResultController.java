package com.example.cbttest.Controller;

import com.example.cbttest.DTO.ExamDTO;
import com.example.cbttest.Service.ExamService;
import com.example.cbttest.Service.SubjectService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/result")
public class ResultController {
    private final ExamService examService;
    private final SubjectService subjectService;

    //평가 결과 목록
    @GetMapping
    public String listResults(@RequestParam(required = false) Long subjectId,
                              @RequestParam(required = false) String status, Model model){
        if(subjectId != null){//교과목 id가 존재하면
            model.addAttribute("exams", examService.getExamsBySubject(subjectId));
            model.addAttribute("selectedSubjected",subjectId);
        } else if (status != null) {//교과목id는 없고, 상태가 있으면
            model.addAttribute("exams", examService.getExamByStatus(status));
            model.addAttribute("selectedStatus",status);
        }else{//교과목과 상태가 존재하지 않으면
            model.addAttribute("exams", examService.getAllExams());
        }
        model.addAttribute("subjects", subjectService.getAllSubjects());

        return "result/list";
    }
    //평가 결과 상세
    @GetMapping("/{examId}")
    public String viewResult(@PathVariable Long examId, Model model){
        ExamDTO exam = examService.getExamResult(examId);
        model.addAttribute("exam", exam);

        //수동 채점 필요여부(4지선다가 아닌 문제가 있거나, 채점이 아닌 문제가 있으면 true)
        boolean needsGrading = exam.getAnswers() !=null && exam.getAnswers().stream()
                .anyMatch(a-> !"MULTIPLE_CHOICE".equals(a.getQuestionType())
                        && a.getEarnedPoint() ==null);

        model.addAttribute("needsGrading", needsGrading);
        return "result/view";
    }

    //수정 채점 폼
    @GetMapping("/grade/{examId}")
    public String gradeForm(@PathVariable Long examId, Model model){
        ExamDTO exam = examService.getExamResult(examId);
        model.addAttribute("exam", exam);

        return "result/grade";//채점페이지로
    }

    //수동 채점 처리
    @PostMapping("/grade/{examId}")
    public String gradeExam(@PathVariable Long examId, HttpServletRequest request){
        Map<Long, Integer> scoreMap = new HashMap<>();//점수
        Map<Long, String> commentMap = new HashMap<>();//총평

        Map<String, String[]> params = request.getParameterMap();
        for(Map.Entry<String, String[]> entry : params.entrySet()){//엔트리 형태로 불러온다.
            if(entry.getKey().startsWith("score_")) {//스코어로 시작하는 키를 찾아서
                Long questionId = Long.parseLong(entry.getKey().replace("score_", ""));
                String val = entry.getKey().length()>0 ? entry.getValue()[0]:"0";
                try{
                    scoreMap.put(questionId, Integer.parseInt(val.trim()));
                }catch(NumberFormatException e){// 숫자가 아니면 오류처리
                    scoreMap.put(questionId, 0);
                }
            }//if end
            if(entry.getKey().startsWith("comment_")){
                Long questionId = Long.parseLong(entry.getKey().replace("comment_", ""));
                commentMap.put(questionId, entry.getValue().length>0 ? entry.getValue()[0] :"");
            }//if end

        }//for end

        examService.gradeManual(examId, scoreMap, commentMap);
        return "redirect:/result/"+examId;
    }

}
