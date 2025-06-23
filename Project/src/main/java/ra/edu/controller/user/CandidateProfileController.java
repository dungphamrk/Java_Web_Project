package ra.edu.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ra.edu.dto.CandidateDTO;
import ra.edu.dto.PasswordDTO;
import ra.edu.entity.candidate.Gender;
import ra.edu.entity.user.User;
import ra.edu.service.CandidateService;
import ra.edu.service.AuthService;
import ra.edu.service.TechnologyService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.beans.PropertyEditorSupport;

@Controller
@RequestMapping("/user")
public class CandidateProfileController {

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private AuthService authService;

    @Autowired
    private TechnologyService technologyService;

    // Hiển thị trang profile
    @GetMapping("/profile")
    public String showProfile(HttpServletRequest request, Model model) {
        String username = extractUsernameFromCookies(request);

        if (username == null) {
            return "redirect:/login";
        }

        CandidateDTO user = candidateService.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("technologies", technologyService.findAllActiveWithoutLanding());
        model.addAttribute("passwordDTO", new PasswordDTO());
        return "user/profile";
    }

    // Xử lý cập nhật mật khẩu
    @PostMapping("/profile/change-password")
    public String changePassword(@Valid @ModelAttribute("passwordDTO") PasswordDTO passwordDTO,
                                 BindingResult result,
                                 HttpServletRequest request,
                                 HttpServletResponse response,
                                 Model model) {
        String username = extractUsernameFromCookies(request);

        if (username == null) {
            return "redirect:/login";
        }

        CandidateDTO user = candidateService.findByUsername(username);

        model.addAttribute("user", user);
        model.addAttribute("technologies", technologyService.findAllActiveWithoutLanding());
        model.addAttribute("passwordDTO", passwordDTO);

        if (result.hasErrors()) {
            model.addAttribute("openPasswordModal", true);
            model.addAttribute("error", "Vui lòng kiểm tra lại thông tin.");
            return "user/profile";
        }

        try {
            String currentUsername = authService.getCurrentUsername(request);
            User userEntity = authService.getUserById(user.getId());
            if (userEntity != null && authService.login(currentUsername, passwordDTO.getCurrentPassword(), response) != null) { // Truyền response
                if (passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
                    userEntity.setPassword(passwordDTO.getNewPassword());
                    model.addAttribute("success", "Mật khẩu đã được thay đổi thành công.");
                } else {
                    model.addAttribute("openPasswordModal", true);
                    model.addAttribute("error", "Mật khẩu mới và xác nhận mật khẩu không khớp.");
                }
            } else {
                model.addAttribute("openPasswordModal", true);
                model.addAttribute("error", "Mật khẩu hiện tại không đúng.");
            }
        } catch (RuntimeException e) {
            model.addAttribute("openPasswordModal", true);
            model.addAttribute("error", e.getMessage());
        }

        return "user/profile";
    }
    // Cập nhật thông tin người dùng
    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute("user") CandidateDTO userDTO,
                                BindingResult result,
                                HttpServletRequest request,
                                Model model) {
        String username = extractUsernameFromCookies(request);
        if (username == null) {
            return "redirect:/login";
        }

        model.addAttribute("technologies", technologyService.findAllActiveWithoutLanding());
        model.addAttribute("passwordDTO", new PasswordDTO());

        if (result.hasErrors()) {
            result.getAllErrors().forEach(e -> System.out.println(e.toString()));
            model.addAttribute("user", userDTO);
            model.addAttribute("openEditModal", true);
            return "user/profile";
        }

        try {
            candidateService.updateProfile(userDTO, username);
            model.addAttribute("user", candidateService.findByUsername(username));
            model.addAttribute("success", "Thông tin của bạn đã được cập nhật thành công.");
        } catch (RuntimeException e) {
            model.addAttribute("user", userDTO);
            model.addAttribute("openEditModal", true);
            model.addAttribute("error", e.getMessage());
        }

        return "user/profile";
    }

    // Helper: Lấy username từ cookie
    private String extractUsernameFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("username".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}