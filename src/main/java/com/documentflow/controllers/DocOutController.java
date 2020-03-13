package com.documentflow.controllers;


import com.documentflow.entities.DTO.DocOutDTO;
import com.documentflow.entities.DocOut;
import com.documentflow.entities.State;
import com.documentflow.entities.User;
import com.documentflow.services.*;
import com.documentflow.utils.DocOutFilter;
import com.documentflow.utils.DocOutUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDate;


@Controller
@RequestMapping("/docs/out")
public class DocOutController {

    private DocOutService docOutService;
    private DocInService docInService;
    private DocTypeService docTypeService;
    private UserServiceImpl userService;
    private DocOutUtils docOutUtils;
    private StateService stateService;
    private ContragentServiceImpl contragentService;
    private TaskService taskService;

    @Autowired
    public void setDocOutService(DocOutService docOutService, DocTypeService docTypeService, UserServiceImpl userService,
                                 DocOutUtils docOutUtils, StateService stateService, ContragentServiceImpl contragentService,
                                 TaskService taskService) {
        this.docOutService = docOutService;
        this.docTypeService = docTypeService;
        this.userService = userService;
        this.docOutUtils = docOutUtils;
        this.stateService = stateService;
        this.contragentService=contragentService;
        this.taskService=taskService;
    }

    @GetMapping()
    public String showAllDocOut(Model model, HttpServletRequest request, @RequestParam(value = "currentPage", required = false) Integer currentPage) {
        if (currentPage == null || currentPage < 1) {
            currentPage = 1;
        }
        model.addAttribute("currentPage", currentPage);
//        Page<DocOut> pageOut = docOutService.findAll(PageRequest.of(currentPage - 1, 20, Sort.Direction.DESC, "createDate"));
//        Page<DocOutDTO> pageDTOs = pageOut.map(d -> new DocOutDTO(d));
//        pageDTOs.stream().map(d -> model.addAttribute(d));
  //      model.addAttribute("docsOut", pageDTOs);

  //      DocOutDTO docOut = new DocOutDTO();


        DocOutFilter filter = new DocOutFilter(request);
        model.addAttribute("filter", filter.getFiltersString());
        Page<DocOutDTO> page = docOutService.findAllByPagingAndFiltering(filter.getSpecification(), PageRequest.of(currentPage-1,15, Sort.Direction.DESC, "createDate"))
                .map(d -> docOutUtils.convertFromDocOut(d));
        model.addAttribute("docs", page);
        model.addAttribute("createDate", LocalDate.now());
        model.addAttribute("creator", userService.getAllUsers());
        model.addAttribute("signer", userService.getAllUsers());
        model.addAttribute("states", stateService.findAllStates());
        model.addAttribute("tasks", taskService.findAll(Pageable.unpaged()));
        model.addAttribute("docTypes", docTypeService.findAllDocTypes());
        model.addAttribute("docOutAddress", docTypeService.findAllDocTypes());
//        model.addAttribute("appendix", docOutService.findOneById(1L).getAppendix());
        return "doc_out";

    }

    @RequestMapping(value = "/newcard")
    public ModelAndView createDoc() {
        ModelAndView result = new ModelAndView("doc_out");

        result.addObject("creators", userService.getAllUsers());
        result.addObject("signers", userService.getAllUsers());
        result.addObject("states", stateService.findAllStates());
        result.addObject("tasks", taskService.findAll(Pageable.unpaged()));
        result.addObject("docTypess", docTypeService.findAllDocTypes());
        result.addObject("docOutAddress", docTypeService.findAllDocTypes());
        result.addObject("docOut", new DocOutDTO());
        return result;
    }

    @RequestMapping(value = "/card/new", method = RequestMethod.POST)
    public String createDocNew(@ModelAttribute DocOutDTO docOutDTO) {
  //      docOutDTO.setRegDate(null);
        DocOut docOut = docOutUtils.convertFromDocOutDTO(docOutDTO);
        docOut.setNumber("б/н");

        docOutService.save(docOut);
        return "redirect:/docs/out";
    }

    @PostMapping("/card")
    public String regEditDoc(@ModelAttribute(name = "docOut") DocOutDTO docOutDTO) {
      if (docOutDTO.getNumber()!=null || !docOutDTO.getNumber().equals("б/н"))   {
          return "redirect:/docs/out";
      } else {
          DocOut docOut = docOutUtils.convertFromDocOutDTO(docOutDTO);
//        if (docOut.getId() == null) {
//            docOutDTO.setRegNumber(docOutUtils.getRegNumber());
//        }
          docOutService.save(docOut);
          return "redirect:/docs/out";
      }
    }

    @ResponseBody
    @RequestMapping("/card/{id}")
    public DocOutDTO getCard(@PathVariable("id") Long id) {
        return docOutUtils.getDocOutDTO(id);
    }

    @PostMapping("/delete")
    public String deleteDoc(@ModelAttribute(name = "docOutDTO") DocOutDTO docOutDTO) {
       DocOut docOut = docOutService.findOneById(docOutDTO.getId());
 //      if(docOut.getIsGenerated()==true) return "redirect:/docs/out";
        if(docOut.getState()!=stateService.getStateById(1)
                || docOut.getState()!=stateService.getStateById(3)
                || docOut.getState()!=stateService.getStateById(4)
                || docOut.getState()!=stateService.getStateById(8)
                || docOut.getState()!=stateService.getStateById(9)) {

            docOut.setState(stateService.getStateById(4));
            docOutService.save(docOut);
        }
            return "redirect:/docs/out";
    }
}