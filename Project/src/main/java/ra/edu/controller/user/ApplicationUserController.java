package ra.edu.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ra.edu.dto.ApplicationDTO;
import ra.edu.entity.application.Progress;
import ra.edu.service.ApplicationService;
import ra.edu.service.AuthService;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user/application")
public class ApplicationUserController {

    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private AuthService authService;

    @GetMapping("")
    public String listUserApplications(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "5") int size,
                                       @RequestParam(required = false) String keyword,
                                       @RequestParam(required = false) String progress,
                                       HttpServletRequest request,
                                       Model model) {

        String username = authService.getCurrentUsername(request);

        // Lấy danh sách đơn ứng tuyển theo user
        List<ApplicationDTO> applications = applicationService.findAllByUser(username, keyword, progress, page, size);
        long totalItems = applicationService.getTotalByUser(username, keyword, progress);

        int totalPages = (int) Math.ceil((double) totalItems / size);

        model.addAttribute("applications", applications);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("pageSize", size);
        model.addAttribute("progressOptions", Progress.values());
        model.addAttribute("keyword", keyword);
        model.addAttribute("progress", progress);

        return "/user/application";
    }
}
