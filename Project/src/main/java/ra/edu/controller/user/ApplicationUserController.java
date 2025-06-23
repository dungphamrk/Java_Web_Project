package ra.edu.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.edu.dto.ApplicationDTO;
import ra.edu.entity.application.Progress;
import ra.edu.entity.application.RequestResult;
import ra.edu.service.ApplicationService;
import ra.edu.service.AuthService;

import javax.servlet.http.HttpServletRequest;
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
        if (username == null) {
            model.addAttribute("error", "Bạn cần đăng nhập để xem thông tin đơn tuyển dụng");
            return "/login";
        }
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

    @PostMapping("/approve")
    public String approveInterview(@RequestParam int applicationId,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "5") int size,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) String progress,
                                   RedirectAttributes redirectAttributes) {
        ApplicationDTO dto = applicationService.findById(applicationId);
        dto.setInterviewRequestResult(RequestResult.CONFIRM);
        dto.setProgress(Progress.INTERVIEWING);

        List<String> errors = applicationService.save(dto, ra.edu.validation.OnInterviewing.class);
        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("errors", errors);
            redirectAttributes.addFlashAttribute("openApproveModal", true);
            redirectAttributes.addFlashAttribute("applicationId", applicationId);
            redirectAttributes.addFlashAttribute("applicationDTO", dto);
            return "redirect:/user/application?page=" + page + "&size=" + size +
                    (keyword != null ? "&keyword=" + keyword : "") +
                    (progress != null ? "&progress=" + progress : "");
        }
        redirectAttributes.addFlashAttribute("success", "Xác nhận thời gian phỏng vấn thành công.");
        return "redirect:/user/application?page=" + page + "&size=" + size +
                (keyword != null ? "&keyword=" + keyword : "") +
                (progress != null ? "&progress=" + progress : "");
    }

    @PostMapping("/cancel")
    public String cancelApplication(@RequestParam int applicationId,
                                    @RequestParam String destroyReason,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "5") int size,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) String progress,
                                    RedirectAttributes redirectAttributes) {
        List<String> errors = applicationService.updateProgress(applicationId, Progress.CANCEL, destroyReason);
        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("errors", errors);
            redirectAttributes.addFlashAttribute("openCancelModal", true);
            redirectAttributes.addFlashAttribute("applicationId", applicationId);
            redirectAttributes.addFlashAttribute("destroyReason", destroyReason);
            return "redirect:/user/application?page=" + page + "&size=" + size +
                    (keyword != null ? "&keyword=" + keyword : "") +
                    (progress != null ? "&progress=" + progress : "");
        }
        redirectAttributes.addFlashAttribute("success", "Ứng tuyển đã được hủy thành công.");
        return "redirect:/user/application?page=" + page + "&size=" + size +
                (keyword != null ? "&keyword=" + keyword : "") +
                (progress != null ? "&progress=" + progress : "");
    }
}