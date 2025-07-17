package ra.edu.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ra.edu.dto.RecruitmentPositionDTO;
import ra.edu.service.RecruitmentPositionService;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private RecruitmentPositionService recruitmentPositionService;

    @GetMapping("home")
    public String home(Model model,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "6") int size) {
        List<RecruitmentPositionDTO> positions = recruitmentPositionService.findAllActive(page, size);
        long totalItems = recruitmentPositionService.getTotalActiveItems();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        model.addAttribute("positions", positions);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("pageSize", size);

        return "user/home";
    }
}