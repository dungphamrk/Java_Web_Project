package ra.edu.controller.user;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.edu.dto.RecruitmentPositionDTO;
import ra.edu.service.ApplicationService;
import ra.edu.service.AuthService;
import ra.edu.service.RecruitmentPositionService;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
public class RecruitmentDetailController {

    @Autowired
    RecruitmentPositionService recruitmentPositionService;

    @Autowired
    ApplicationService applicationService;

    @Autowired
    Cloudinary cloudinary;

    @Autowired
    AuthService authService;

    @GetMapping("/recruitment/{id}")
    public String viewRecruitment(@PathVariable int id, Model model) {
        RecruitmentPositionDTO position = recruitmentPositionService.findById(id);
        List<RecruitmentPositionDTO> allPositions = recruitmentPositionService.findAll(0,6);

        model.addAttribute("position", position);
        model.addAttribute("positions", allPositions);
        return "user/recuitment-details";
    }
    @PostMapping("/apply")
    public String apply(@RequestParam("positionId") int positionId,
                        @RequestParam("file") MultipartFile file,
                        RedirectAttributes redirectAttributes,
                        HttpServletRequest request) {
        try {
            String username = authService.getCurrentUsername(request);
            if (username == null) {
                redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để ứng tuyển.");
                return "redirect:/recruitment/" + positionId;
            }

            // Upload file lên Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
            String cvUrl = (String) uploadResult.get("secure_url");

            // Gọi service để tạo application
            applicationService.createApplication(positionId, username, cvUrl);
            redirectAttributes.addFlashAttribute("success", "Application submitted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi upload CV: " + e.getMessage());
        }
        return "redirect:/recruitment/" + positionId;
    }
}
