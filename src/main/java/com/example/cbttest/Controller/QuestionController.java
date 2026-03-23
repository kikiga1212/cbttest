package com.example.cbttest.Controller;

import com.example.cbttest.DTO.QuestionDTO;
import com.example.cbttest.Service.ChapterService;
import com.example.cbttest.Service.QuestionService;
import com.example.cbttest.Service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/questions")
public class QuestionController {
    private final QuestionService questionService;
    private final SubjectService subjectService;
    private final ChapterService chapterService;

    //교과목 조회를 윟안 교과목 조회
    @GetMapping
    public String listSubjects(Model model) {
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "question/subjects";
    }

    //챕터 조회를 위한 챕터조회
    @GetMapping("/subject/{subjectId}")
    public String listChapters(@PathVariable Long subjectId, Model model) {
        model.addAttribute("subject", subjectService.getSubject(subjectId));
        model.addAttribute("chapters", chapterService.getChaptersBySubject(subjectId));

        return "question/chapters";
    }

    //챕터별 문제 목록
    @GetMapping("/chapter/{chapterId}")
    public String listQuestions(@PathVariable Long chapterId, Model model) {
        model.addAttribute("chapter", chapterService.getChapter(chapterId));
        model.addAttribute("questions", questionService.getQuestionByChapter(chapterId));

        return "question/list";
    }

    //상세보기(개별조회)
    // 삽입 폼 이동
    @GetMapping("/new/{chapterId}")
    public String createForm(@PathVariable Long chapterId, Model model) {
        QuestionDTO dto = new QuestionDTO();
        dto.setChapterId(chapterId);
        dto.setPoint(1);

        model.addAttribute("question", dto);
        model.addAttribute("chapter", chapterService.getChapter(chapterId));
        return "question/create";
    }

    //삽입처리
    @PostMapping("/new/{chapterId}")
    public String createQuestion(@PathVariable Long chapterId, @ModelAttribute QuestionDTO dto) {
        try{
            questionService.createQuestion(chapterId, dto);
            return "redirect:/questions/chapter/"+chapterId;
        } catch (Exception e) {
            return "redirect:/questions/new/"+chapterId+"?error="+e.getMessage();
        }

    }


    //수정폼 이동
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        QuestionDTO question = questionService.getQuestion(id);

        model.addAttribute("question", question);
        model.addAttribute("chapter", chapterService.getChapter(question.getChapterId()));

        return "question/edit";
    }

    //수정처리
    @PostMapping("/edit/{id}")
    public String updateQuestion(@PathVariable Long id, @ModelAttribute QuestionDTO dto) {
        try {
            QuestionDTO updated = questionService.updateQuestion(id, dto);
            return "redirect:/questions/chapter/"+dto.getChapterId();
        } catch (Exception e) {
            return "redirect:/questions/edit/"+id+"?error="+e.getMessage();
        }
    }

    //삭제처리
    @GetMapping("/delete/{id}")
    public String deleteQuestion(@PathVariable Long id) {
        QuestionDTO question = questionService.getQuestion(id);
        Long chapterId = question.getChapterId();
        questionService.deleteQuestion(id);

        return "redirect:/questions/chapter/"+chapterId;


    }



}
