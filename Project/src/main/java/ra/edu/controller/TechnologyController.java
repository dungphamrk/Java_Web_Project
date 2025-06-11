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
@RequestMapping("/technology")
public class TechnologyController {

    @Autowired
    private TechnologyService technologyService;

    @GetMapping
    public String listTechnologies(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "5") int size,
                                   Model model) {
        List<TechnologyDTO> technologies = technologyService.findAll(page, size);
        long totalItems = technologyService.getTotalItems();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        model.addAttribute("technologies", technologies);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("pageSize", size);
        return "technology/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("technology", new TechnologyDTO());
        return "technology/form";
    }

    @PostMapping("/add")
    public String addTechnology(@Valid @ModelAttribute("technology") TechnologyDTO technologyDTO,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "technology/form";
        }
        technologyService.save(technologyDTO);
        return "redirect:/technology";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") int id, Model model) {
        TechnologyDTO technology = technologyService.findById(id);
        model.addAttribute("technology", technology);
        return "technology/form";
    }

    @PostMapping("/edit/{id}")
    public String updateTechnology(@PathVariable("id") int id,
                                   @Valid @ModelAttribute("technology") TechnologyDTO technologyDTO,
                                   BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "technology/form";
        }
        technologyDTO.setId(id);
        technologyService.save(technologyDTO);
        return "redirect:/technology";
    }

    @GetMapping("/delete/{id}")
    public String deleteTechnology(@PathVariable("id") int id) {
        technologyService.delete(id);
        return "redirect:/technology";
    }
}