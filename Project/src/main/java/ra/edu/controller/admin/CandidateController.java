package ra.edu.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ra.edu.dto.CandidateDTO;
import ra.edu.service.CandidateService;
import ra.edu.service.TechnologyService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("admin/candidate")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;
    @Autowired
    private TechnologyService technologyService;

    @GetMapping("")
    public String listCandidates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String technology,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String age,
            @RequestParam(required = false) String experience,
            Model model
    ) {
        List<CandidateDTO> candidates = candidateService.filterCandidates(keyword, technology, gender, age, experience, page, size);
        long totalItems = candidateService.countFilteredCandidates(keyword, technology, gender, age, experience);

        model.addAttribute("candidates", candidates);
        model.addAttribute("candidate", new CandidateDTO());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (int) Math.ceil((double) totalItems / size));
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("pageSize", size);
        model.addAttribute("technologies", technologyService.findAllActiveWithoutLanding());

        model.addAttribute("keyword", keyword);
        model.addAttribute("technology", technology);
        model.addAttribute("gender", gender);
        model.addAttribute("age", age);
        model.addAttribute("experience", experience);

        return "/admin/candidate";
    }

    @PostMapping("/save")
    public String saveCandidate(@Valid @ModelAttribute("candidate") CandidateDTO candidateDTO,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            List<CandidateDTO> candidates = candidateService.findAll(0, 5);
            long totalItems = candidateService.getTotalItems();
            int totalPages = (int) Math.ceil((double) totalItems / 5);

            model.addAttribute("candidates", candidates);
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalItems", totalItems);
            model.addAttribute("pageSize", 5);
            model.addAttribute("openAddModal", true);
            return "/admin/candidate";
        }

        try {
            candidateService.save(candidateDTO);
            return "redirect:/admin/candidate";
        } catch (RuntimeException e) {
            List<CandidateDTO> candidates = candidateService.findAll(0, 5);
            long totalItems = candidateService.getTotalItems();
            int totalPages = (int) Math.ceil((double) totalItems / 5);

            model.addAttribute("candidates", candidates);
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalItems", totalItems);
            model.addAttribute("pageSize", 5);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("openAddModal", true);
            return "/admin/candidate";
        }
    }

    @GetMapping("/find/{id}")
    @ResponseBody
    public CandidateDTO findCandidateById(@PathVariable("id") int id) {
        return candidateService.findById(id);
    }

    @GetMapping("/lockAccount/{id}")
    public String deleteCandidate(@PathVariable("id") int id) {
        candidateService.toggleUserStatus(id);
        return "redirect:/admin/candidate";
    }
}