package com.example.cbttest.Controller;

import com.example.cbttest.DTO.ExamDTO;
import com.example.cbttest.DTO.QuestionDTO;
import com.example.cbttest.Service.ExamService;
import com.example.cbttest.Service.QuestionService;
import com.example.cbttest.Service.SubjectService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/exam")
public class ExamController {
    private final ExamService examService;
    private final SubjectService subjectService;
    private final QuestionService questionService;

    //평가시작 - 교과목 선택
    @GetMapping
    public String selectSubject(Model model){
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "exam/select";
    }

    //응시자 정보 입력 폼
    @GetMapping("/start/{subjectId}")
    public String startForm(@PathVariable Long subjectId, Model model){
        model.addAttribute("subject", subjectService.getSubject(subjectId));
        return "exam/start";
    }

    //평가시작
    @PostMapping("/start/{subjectId}")
    public String startExam(@PathVariable Long subjectId, @RequestParam String examineeName){
        ExamDTO exam = examService.startExam(subjectId, examineeName);
        return "redirect:/exam/take/"+exam.getId();
    }

    //평가 진행 화면
    @GetMapping("/take/{examId}")
    public String takeExam(@PathVariable Long examId, Model model){
        ExamDTO exam = examService.getExamResult(examId);
        List<QuestionDTO> questions = questionService.getQuestionBySubject(exam.getSubjectId());
        model.addAttribute("exam", exam);
        model.addAttribute("questions", questions);
        return "exam/take";
    }

    //답안 제출 처리
    @PostMapping("/submit/{examId}")
    public String submitExam(@PathVariable Long examId, HttpServletRequest request){
        //동일한 변수명으로 여러값을 저장할때는 배열, 서로 다른 변수명과 값을 배열로 저장할때는 Map
        Map<Long, String> answerMap = new HashMap<>();
        Map<String, String[]> params = request.getParameterMap();
        for(Map.Entry<String, String[]> entry: params.entrySet()){
            if(entry.getKey().startsWith("answer")){
                //replace 치환 answer_ 구분하기 위한 임시문자열->사용ㅇ시에는 answer_ 제거
                Long questionId = Long.parseLong(entry.getKey().replace("answer", ""));
                String value = entry.getValue().length>0?entry.getValue()[0]:"";
                answerMap.put(questionId, value);//map에 값을 저장할때 (키, 값)=> put(추가)
            }
        }
        examService.submitAnswer(examId, answerMap);
        return "redirect:/result/"+examId;
    }

}
