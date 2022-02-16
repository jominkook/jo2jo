package com.metanet.jo2jo.controller;

import com.metanet.jo2jo.domain.curriculum.CurriculumDto;
import com.metanet.jo2jo.domain.department.DepartmentDto;
import com.metanet.jo2jo.service.CurriculumRegisterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CurriculumController {
    private final CurriculumRegisterService curriculumRegisterService;

    //커리큘럼 등록
    @GetMapping("/curriculum/new")
    String curriculumRegisterForm(HttpSession session, Model model){
//        if (session.getAttribute("user").equals("admin")) {
//
//        }
        //최하위 부서 리스트
        List<DepartmentDto> deptList = curriculumRegisterService.findLowestDepartment();
        model.addAttribute("deptList", deptList);

        return "curriculum/curriculum-register";
    }


    @PostMapping("/curriculum/new")
    String curriculumRegister(@ModelAttribute CurriculumDto curriculum,
                              @RequestParam("daterange") String daterange,
                              HttpSession session, Model model, @Valid CurriculumDto curriculumDto,
                              BindingResult bindingResult)  throws IOException {
//        if (session.getAttribute("user").equals("admin")) {
//
//        }

        //daterange -> startdate, enddate
        String[] dateArr = daterange.split(" - ");
        curriculum.setStartdate(dateArr[0]);
        curriculum.setEnddate(dateArr[1]);

        //currcost 전처리
        Long newCurrcost = curriculum.getCurrcost() * 10000L;

        //deptrange 전처리
        String newDeptrange = curriculum.getDeptrange().replace(',' ,' ');  //','을 ' '으로 치환

        //educos 전처리
        List<String> educosList = new ArrayList<>(){{
            add(curriculum.getEducos1());
            add(curriculum.getEducos2());
            add(curriculum.getEducos3());
            add(curriculum.getEducos4());
            add(curriculum.getEducos5());
        }} ;
        educosList.removeAll(Arrays.asList("",null));
        int temp = 5-educosList.size();
        for(int i=0; i<temp; i++){
            educosList.add("");
        }

        curriculum.setEducos1(educosList.get(0));
        curriculum.setEducos2(educosList.get(1));
        curriculum.setEducos3(educosList.get(2));
        curriculum.setEducos4(educosList.get(3));
        curriculum.setEducos5(educosList.get(4));
        curriculum.setCurrcost(newCurrcost);
        curriculum.setDeptrange(newDeptrange);

        CurriculumDto newCurriculum = curriculumRegisterService.saveCurriculum(curriculum);
        Long newCostotalcnt = curriculumRegisterService.registerCurriculumCostotalcnt(newCurriculum);


        return "redirect:/curriculums";
    }
}
