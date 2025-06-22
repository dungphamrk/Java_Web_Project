package ra.edu.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.edu.dto.TechnologyDTO;
import ra.edu.service.TechnologyService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("admin/technology")
public class TechnologyController {

    @Autowired
    private TechnologyService technologyService;

    @GetMapping("")
    public String listTechnologies(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "5") int size,
                                   @RequestParam(required = false) String keyword,
                                   Model model) {
        List<TechnologyDTO> technologies;
        long totalItems;

        if (keyword != null && !keyword.isEmpty()) {
            technologies = technologyService.searchByName(keyword, page, size);
            totalItems = technologyService.countByName(keyword);
            model.addAttribute("keyword", keyword); // giữ lại từ khóa tìm
        } else {
            technologies = technologyService.findAllActive(page, size);
            totalItems = technologyService.getTotalActiveItems();
        }

        int totalPages = (int) Math.ceil((double) totalItems / size);
        model.addAttribute("technologies", technologies);
        model.addAttribute("technology", new TechnologyDTO());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("pageSize", size);

        return "/admin/technology";
    }


    @PostMapping("/save")
    public String saveTechnology(@Valid @ModelAttribute("technology") TechnologyDTO technologyDTO,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "redirect:/admin/technology";
        }

        try {
            technologyService.save(technologyDTO);
            return "redirect:/admin/technology";
        } catch (RuntimeException e) {
            List<TechnologyDTO> technologies = technologyService.findAllActive(0, 5);
            long totalItems = technologyService.getTotalActiveItems();
            int totalPages = (int) Math.ceil((double) totalItems / 5);

            model.addAttribute("technologies", technologies);
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalItems", totalItems);
            model.addAttribute("pageSize", 5);
            model.addAttribute("openAddModal", true);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

            return "redirect:/admin/technology";
        }
    }

    @GetMapping("/find/{id}")
    @ResponseBody
    public TechnologyDTO findTechnologyById(@PathVariable("id") int id) {
        return technologyService.findById(id);
    }

    @GetMapping("/delete/{id}")
    public String deleteTechnology(@PathVariable("id") int id) {
        technologyService.delete(id);
        return "redirect:/admin/technology";
    }
}
