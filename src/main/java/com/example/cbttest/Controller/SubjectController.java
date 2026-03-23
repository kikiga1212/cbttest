package com.example.cbttest.Controller;

import com.example.cbttest.DTO.ChapterDTO;
import com.example.cbttest.DTO.SubjectDTO;
import com.example.cbttest.Service.ChapterService;
import com.example.cbttest.Service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/subjects") //그룹화해야 보안사용시 제어가 편하다.
public class SubjectController {
    private final SubjectService subjectService;
    private final ChapterService chapterService;
    /*
        html로 이동할 때는 return "templates에 있는 파일을 지정"
        Service는 이동할 때는 return "redirect:/맵핑명"->Controller내에
        Service에서 templates이동시 데이터값이 없거나 부족해서 오류가 발생할 확률이 높다.
     */
    /** 교과목 목록(전체조회, HTML로 이동) */
    @GetMapping
    public String listSubjects(Model model) {
        //1️⃣ HTML에 전달할 필요한 데이터를 조회
        //1) 조회후 추가적인 작업을 하고 model 저장시
            List<SubjectDTO> dto = subjectService.getAllSubjects();
            model.addAttribute("subjects", dto);
        //2) 위에 내용을 바로 다음행에서 사용할 때 한행으로 단순처리
            model.addAttribute("subjects", subjectService.getAllSubjects());

        // 2️⃣ 페이지로 이동
        return "subject/list";
    }

    /** 교과목 등록 폼(HTML로 이동) */
    @GetMapping("/new")
    public String createForm(Model model) {
        //1️⃣ 빈 DTO를 전달(유효성 검사, html obejct와 fields로 편하게 작업)
        // 삽입과 수정의 작업이 비슷하게 동작
        model.addAttribute("subject", new SubjectDTO());

        //2️⃣ 페이지로 이동(페이지 이동시 절대 앞에 /가 있으면 안된다.)
        //폴더의 파일명
        return "subject/create";
    }
    /** 교과목 등록 처리(Service) */
    @PostMapping
    public String createSubject(@ModelAttribute SubjectDTO dto) {
        //3️⃣ 예외를 이용해서 오류 메세지를 추가->try
        try {
            //1️⃣ 전달받은 dto를 service에 보내 데이터베이스에 저장
            SubjectDTO saved = subjectService.createSubject(dto);

            //2️⃣ 이동할 페이지를 처리하는 맵핑으로 이동(redirect는 반드시 앞에 / 표기)
            //http://주소를 구성
            return "redirect:/subjects/" + saved.getId(); //상세페이지로 이동
        } catch(Exception e) { //모든 오류종류를 처리
            //4️⃣ 등록을 실패했을 데 오류메세지를 가지고, 등록페이지로 이동
            return "redirect:/subjects/new?error="+e.getMessage();
        }
    }

    /** 교과목 상세보기(HTML로 이동) */
    @GetMapping("/{id}")
    public String viewSubject(@PathVariable Long id, Model model) {
        //1️⃣ id를 이용해서 대상을 조회
        SubjectDTO subject = subjectService.getSubject(id);

        //2️⃣ 조회한 내용과 추가전 내용을 전달
        // fixMe : 기존 챕터 목록과 신규 등록 챕터 정보
        model.addAttribute("subject", subject);
        model.addAttribute("chapters", subject.getChapters());
        model.addAttribute("newChapter", new ChapterDTO());

        //3️⃣ 상세보기 페이지로 이동
        return "subject/view";
    }
    /** 교과목 수정 폼(HTML로 이동) */
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        //1️⃣ 대상을 조회해서 결과를 저장
        model.addAttribute("subject", subjectService.getSubject(id));

        //2️⃣ 수정페이지로 이동
        return "subject/edit";
    }
    /** 교과목 수정 처리(Service) */
    @PostMapping("/edit/{id}")
    public String updateSubject(@PathVariable Long id,
                                @ModelAttribute SubjectDTO dto) {
        //3️⃣ 오류발생시 오류메세지를 가지고 수정페이지로 이동
        try {
            //1️⃣ 수정할 대상 id와 수정값을 가지고 수정처리
            subjectService.updateSubject(id, dto);

            //2️⃣ 상세페이지로 수정한 id를 가지고 이동
            return "redirect:/subjects/" + id; //상세페이지로 이동
        } catch(Exception e) {
            return "redirect:/subjects/edit/"+id+"?error="+e.getMessage();
        }
    }

    /** 교과목 삭제(Service) */
    @GetMapping("/delete/{id}")
    public String deleteSubject(@PathVariable Long id) {
        //1️⃣ 삭제할 id를 가지고 서비스에서 삭체 처리
        subjectService.deleteSubject(id);

        //2️⃣ 목록페이지로 이동
        return "redirect:/subjects";
    }

    //Todo : //챕터를 작업할 내용
    /** 챕터 등록*/
    @PostMapping("/{subjectId}/chapters")
    public String createChapter(@PathVariable Long subjectId, @ModelAttribute ChapterDTO dto) {
        try {
            chapterService.createChapter(subjectId, dto); //부모id와 자식의 데이터로 저장
            return "redirect:/subjects/"+subjectId; //교과목 상세페이지로 이동
        } catch(Exception e) {
            return "redirect:/subjects/"+subjectId+"?error="+e.getMessage();
        }
    }

    /**챕터 수정 페이지(교과목 정보, 챕터 수정할 데이터)*/
    @GetMapping("/{subjectId}/chapters/edit/{chapterId}")
    public String editChapterForm(@PathVariable Long subjectId, @PathVariable Long chapterId,
                                  Model model) {
        model.addAttribute("subject", subjectService.getSubject(subjectId)); //교과목
        model.addAttribute("chapter", chapterService.getChapter(chapterId)); //챕터

        return "subject/chapter-edit";
    }

    /**챕터 수정 처리*/
    @PostMapping("/{subjectId}/chapters/edit/{chapterId}")
    public String updateChaper(@PathVariable Long subjectId, @PathVariable Long chapterId,
                               @ModelAttribute ChapterDTO dto) {
        try {
            chapterService.updateChapter(chapterId, dto);
            return "redirect:/subjects/"+subjectId; //수정후 상세페이지로
        } catch(Exception e) {
            return "redirect:/subjects/"+subjectId+"?error="+e.getMessage();
        }
    }

    /**챕터 삭제*/
    @GetMapping("/{subjectId}/chapters/delete/{chapterId}")
    public String deleteChapter(@PathVariable Long subjectId, @PathVariable Long chapterId) {
        chapterService.deleteChapter(chapterId);
        return "redirect:/subjects/"+subjectId;
    }
    //Entity->DTO->Repository->Service 이후에 완성
}
//HTML작업시 유의 사항
//HTML에 메뉴가 없으면 브라우저에 주소를 수동으로 입력해서 각 페이지에 접근
//메뉴나 버튼으로 요청이 존재하면 해당 메뉴나 버튼으로 접근
//수정된 HTML은 반드시 F5(새로고침)로 적용한 후 작업