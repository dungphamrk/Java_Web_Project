package ra.edu.controller.admin;

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
        prepareModel(model, page, size, keyword);
        model.addAttribute("position", new RecruitmentPositionDTO());
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
            prepareModel(model, page, size, keyword);
            model.addAttribute("position", positionDTO);
            if (positionDTO.getId() == 0) {
                model.addAttribute("openAddModal", true);
            } else {
                model.addAttribute("openEditModal", true);
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

    private void prepareModel(Model model, int page, int size, String keyword) {
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
    }

    @GetMapping("/edit/{id}")
    public String showEditModal(@PathVariable("id") int id,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "5") int size,
                                @RequestParam(required = false) String keyword,
                                Model model) {
        RecruitmentPositionDTO position = recruitmentPositionService.findById(id);
        prepareModel(model, page, size, keyword);
        model.addAttribute("position", position);
        model.addAttribute("openEditModal", true); // ✅ mở modal
        return "admin/recruitment-position";       // ✅ quay lại view chính
    }

}
