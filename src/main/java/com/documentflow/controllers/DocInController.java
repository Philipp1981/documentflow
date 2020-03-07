package com.documentflow.controllers;

import com.documentflow.entities.*;
import com.documentflow.entities.dto.DocInDto;
import com.documentflow.services.*;
import com.documentflow.utils.DocInFilter;
import com.documentflow.utils.DocInUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@RestController
@RequestMapping("/docs/in")
public class DocInController {

    private DocInService docInService;
    private DocTypeService docTypeService;
    private DepartmentService departmentService;
    private DocInUtils docInUtils;
    private StateService stateService;

    @Autowired
    public DocInController(DocInService docInService, DocTypeService docTypeService,
                           DepartmentService departmentService, DocInUtils docInUtils,
                           StateService stateService) {
        this.docInService = docInService;
        this.docTypeService = docTypeService;
        this.departmentService = departmentService;
        this.docInUtils = docInUtils;
        this.stateService = stateService;
    }

    @GetMapping()
    public String showIn(
            Model model,
            HttpServletRequest request,

            @RequestParam(value = "currentPage", required = false) Integer currentPage) {
        if (currentPage == null || currentPage < 1) {
            currentPage = 1;
        }
        model.addAttribute("currentPage", currentPage);
        DocInFilter filter = new DocInFilter(request);
        model.addAttribute("filter", filter.getFiltersStr());
//        Page<DocInDto> page = docInService.findAll(PageRequest.of(currentPage-1,20, Sort.Direction.ASC, "regDate")).map(d -> docInUtils.convertToDTO(d));
        Page<DocInDto> page = docInService.findAllByPagingAndFiltering(filter.getSpecification(), PageRequest.of(currentPage-1,20, Sort.Direction.ASC, "regDate"))
                .map(d -> docInUtils.convertToDTO(d));
        model.addAttribute("docs", page);
        model.addAttribute("states", stateService.findAllStates());
        model.addAttribute("docTypes", docTypeService.findAllDocTypes());
        model.addAttribute("departments", departmentService.findAllDepartments());
        return "docIn";
    }

    @RequestMapping("/card/{id}")
    public DocInDto getCard(@PathVariable("id") Long id, Principal principal) {
        return docInUtils.getDocIn(id, principal.getName());
    }

    @PostMapping("/card")
    public String regEditDoc(@ModelAttribute(name = "doc") DocInDto docInDto) {
        DocIn docIn = docInUtils.convertFromDTO(docInDto);
        if (docIn.getId() == null) {
            docIn.setRegNumber(docInUtils.getRegNumber());
        }
        docInService.save(docIn);
        return "redirect:/docs/in";
    }

    @PostMapping("/del")
    public String delete(@ModelAttribute(name = "doc") DocInDto docInDto) {
        docInService.deleteById(docInDto.getId());
        return "redirect:/docs/in";
    }
}
