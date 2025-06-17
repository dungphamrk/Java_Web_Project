package ra.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.edu.dto.RecruitmentPositionDTO;
import ra.edu.service.RecruitmentPositionService;
import ra.edu.service.TechnologyService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("admin/recruitment-position")
public class RecruitmentPositionController {

    @Autowired
    private RecruitmentPositionService recruitmentPositionService;

    @Autowired
    private TechnologyService technologyService;

    @GetMapping("")
    public String listRecruitmentPositions(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "5") int size,
                                           @RequestParam(required = false) String keyword,
                                           Model model) {

        List<RecruitmentPositionDTO> positions;
        long totalItems;

        if (keyword != null && !keyword.isEmpty()) {
            positions = recruitmentPositionService.searchByName(keyword, page, size);
            totalItems = recruitmentPositionService.countByName(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            positions = recruitmentPositionService.findAllActive(page, size);
            totalItems = recruitmentPositionService.getTotalActiveItems();
        }

        int totalPages = (int) Math.ceil((double) totalItems / size);

        model.addAttribute("positions", positions);
        model.addAttribute("position", new RecruitmentPositionDTO());
        model.addAttribute("technologies", technologyService.findAllActiveWithoutLanding());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("pageSize", size);

        return "/admin/recruitment-position";
    }

    @PostMapping("/save")
    public String saveRecruitmentPosition(@Valid @ModelAttribute("position") RecruitmentPositionDTO positionDTO,
                                          BindingResult result,
                                          Model model,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "5") int size,
                                          @RequestParam(required = false) String keyword,
                                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            List<RecruitmentPositionDTO> positions;
            long totalItems;

            if (keyword != null && !keyword.isEmpty()) {
                positions = recruitmentPositionService.searchByName(keyword, page, size);
                totalItems = recruitmentPositionService.countByName(keyword);
                model.addAttribute("keyword", keyword);
            } else {
                positions = recruitmentPositionService.findAllActive(page, size);
                totalItems = recruitmentPositionService.getTotalActiveItems();
            }

            int totalPages = (int) Math.ceil((double) totalItems / size);

            model.addAttribute("positions", positions);
            model.addAttribute("technologies", technologyService.findAllActiveWithoutLanding());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalItems", totalItems);
            model.addAttribute("pageSize", size);

            model.addAttribute("position", positionDTO);
            if (positionDTO.getId() == 0) {
                model.addAttribute("openAddModal", true); // mở modal thêm
            } else {
                model.addAttribute("openEditModal", true); // mở modal sửa
            }

            return "admin/recruitment-position";
        }

        try {
            recruitmentPositionService.save(positionDTO);
            return "redirect:/admin/recruitment-position";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/recruitment-position";
        }
    }



    @GetMapping("/find/{id}")
    @ResponseBody
    public RecruitmentPositionDTO findRecruitmentPositionById(@PathVariable("id") int id) {
        return recruitmentPositionService.findById(id);
    }

    @GetMapping("/delete/{id}")
    public String deleteRecruitmentPosition(@PathVariable("id") int id) {
        recruitmentPositionService.delete(id);
        return "redirect:/admin/recruitment-position";
    }
}
