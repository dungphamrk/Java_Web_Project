package ra.edu.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ra.edu.dto.RecruitmentPositionDTO;
import ra.edu.service.RecruitmentPositionService;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private RecruitmentPositionService recruitmentPositionService;

    @GetMapping("home")
    public String home(Model model) {
        List<RecruitmentPositionDTO> positions = recruitmentPositionService.findAllActive(0, 6);
        model.addAttribute("positions", positions);
        return "user/home";
    }
}