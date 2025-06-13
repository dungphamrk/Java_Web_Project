package ra.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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
                                   Model model) {
        List<TechnologyDTO> technologies = technologyService.findAllActive(page, size);
        long totalItems = technologyService.getTotalItems();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        model.addAttribute("technologies", technologies);
        model.addAttribute("technology", new TechnologyDTO()); // dùng cho modal thêm
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("pageSize", size);
        return "/admin/technology"; // Trang chính có modal
    }

    @PostMapping("/save")
    public String saveTechnology(@Valid @ModelAttribute("technology") TechnologyDTO technologyDTO,
                                 BindingResult result,
                                 Model model) {
        if (result.hasErrors()) {
            List<TechnologyDTO> technologies = technologyService.findAll(0, 5);
            long totalItems = technologyService.getTotalItems();
            int totalPages = (int) Math.ceil((double) totalItems / 5);

            model.addAttribute("technologies", technologies);
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalItems", totalItems);
            model.addAttribute("pageSize", 5);
            return "redirect:/admin/technology";
        }

        technologyService.save(technologyDTO);
        return "redirect:/admin/technology";
    }

    @GetMapping("/find/{id}")
    @ResponseBody
    public TechnologyDTO findTechnologyById(@PathVariable("id") int id) {
        return technologyService.findById(id); // Dùng để nạp dữ liệu lên modal sửa (AJAX)
    }

    @GetMapping("/delete/{id}")
    public String deleteTechnology(@PathVariable("id") int id) {
        technologyService.delete(id);
        return "redirect:/admin/technology";
    }
}
