package ra.edu.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.edu.dto.ApplicationDTO;
import ra.edu.entity.application.Progress;
import ra.edu.entity.application.RequestResult;
import ra.edu.service.ApplicationService;
import ra.edu.service.AuthService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("admin/application")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private AuthService authService;
    @GetMapping("")
    public String listApplications(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "5") int size,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) String progress,
                                   Model model, HttpServletRequest request) {
        prepareModel(model, page, size, keyword, progress);
        model.addAttribute("application", new ApplicationDTO());
        return "/admin/application";
    }

    @GetMapping("/edit/{id}")
    public String showEditModal(@PathVariable("id") int id,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "5") int size,
                                @RequestParam(required = false) String keyword,
                                @RequestParam(required = false) String progress,
                                Model model) {
        ApplicationDTO application = applicationService.findById(id);
        prepareModel(model, page, size, keyword, progress);
        model.addAttribute("application", application);
        model.addAttribute("openEditModal", true);
        return "/admin/application";
    }

    @PostMapping("/update-interview")
    public String updateInterview(@ModelAttribute @Valid ApplicationDTO applicationDTO,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "5") int size,
                                  @RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) String progress,
                                  RedirectAttributes redirectAttributes) {
        List<String> errors = applicationService.updateInterview(
                applicationDTO.getId(),
                applicationDTO.getInterviewLink(),
                applicationDTO.getInterviewTime()
        );
        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("errors", errors);
            redirectAttributes.addFlashAttribute("openInterviewModal", true);
            redirectAttributes.addFlashAttribute("applicationId", applicationDTO.getId());
            redirectAttributes.addFlashAttribute("applicationDTO", applicationDTO); // Giữ giá trị form
            return "redirect:/admin/application?page=" + page + "&size=" + size +
                    (keyword != null ? "&keyword=" + keyword : "") +
                    (progress != null ? "&progress=" + progress : "");
        }
        redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin phỏng vấn thành công.");
        return "redirect:/admin/application?page=" + page + "&size=" + size +
                (keyword != null ? "&keyword=" + keyword : "") +
                (progress != null ? "&progress=" + progress : "");
    }

    private void prepareModel(Model model, int page, int size, String keyword, String progress) {
        List<ApplicationDTO> applications;
        long totalItems;

        if ((keyword != null && !keyword.isEmpty()) || (progress != null && !progress.isEmpty())) {
            applications = applicationService.filterApplications(keyword, progress, page, size);
            totalItems = applicationService.getTotalFilteredItems(keyword, progress);
        } else {
            applications = applicationService.findAll(page, size);
            totalItems = applicationService.getTotalItems();
        }

        int totalPages = (int) Math.ceil((double) totalItems / size);

        model.addAttribute("applications", applications);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("pageSize", size);
        model.addAttribute("progressOptions", Progress.values());
    }

    @PostMapping("/destroy")
    public String destroyApplication(@RequestParam int applicationId,
                                     @RequestParam String destroyReason,
                                     RedirectAttributes redirectAttributes) {
        List<String> errors = applicationService.updateProgress(applicationId, Progress.REJECT, destroyReason);
        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("errors", errors);
            redirectAttributes.addFlashAttribute("openDestroyModal", true);
            redirectAttributes.addFlashAttribute("applicationId", applicationId);
            redirectAttributes.addFlashAttribute("destroyReason", destroyReason); // Giữ giá trị lý do
            return "redirect:/admin/application";
        }
        redirectAttributes.addFlashAttribute("success", "Ứng tuyển đã bị từ chối thành công.");
        return "redirect:/admin/application";
    }

    @PostMapping("/approve")
    public String approveInterview(@RequestParam int applicationId,
                                   @RequestParam String result,
                                   @RequestParam String resultNote,
                                   RedirectAttributes redirectAttributes) {
        ApplicationDTO dto = applicationService.findById(applicationId);
        dto.setInterviewRequestResult(RequestResult.valueOf(result.toUpperCase()));
        dto.setInterviewResultNote(resultNote);
        dto.setInterviewResult(result);
        dto.setProgress(Progress.DONE);

        List<String> errors = applicationService.save(dto, ra.edu.validation.OnDone.class);
        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("errors", errors);
            redirectAttributes.addFlashAttribute("openApproveModal", true);
            redirectAttributes.addFlashAttribute("applicationId", applicationId);
            redirectAttributes.addFlashAttribute("applicationDTO", dto); // Giữ giá trị form
            return "redirect:/admin/application";
        }
        redirectAttributes.addFlashAttribute("success", "Kết quả phỏng vấn đã được cập nhật.");
        return "redirect:/admin/application";
    }
}