package ra.edu.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.edu.dto.RegistrationDTO;
import ra.edu.dto.TechnologyDTO;
import ra.edu.dto.UserDTO;
import ra.edu.dto.CandidateDTO;
import ra.edu.entity.user.User;

import ra.edu.repository.UserRepository;
import ra.edu.service.AuthService;
import ra.edu.service.PasswordResetService;
import ra.edu.service.TechnologyService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.List;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private TechnologyService technologyService;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/reset-password")
    @Transactional
    public String resetPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng với email này.");
            return "redirect:/reset";
        }

        String newPassword = passwordResetService.generateRandomPassword(8);
        user.setPassword(newPassword);

        boolean sent = passwordResetService.sendNewPasswordEmail(email, newPassword);
        if (sent) {
            redirectAttributes.addFlashAttribute("message", "Mật khẩu mới đã được gửi tới email của bạn.");
            userRepository.save(user);
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "Gửi email thất bại. Vui lòng thử lại sau.");
            return "redirect:/reset";
        }
    }
    @GetMapping("/reset")
    public String showResetPasswordForm(Model model) {
        return "reset";
    }

    @GetMapping("/login")
    public String showLogin(Model model, HttpServletRequest request) {
        String role = authService.getCurrentUserRole(request);
        if (role != null) {
            if (role.equals("CANDIDATE")) {
                return "redirect:/home";
            } else if (role.equals("ADMIN")) {
                return "redirect:/admin";
            }
        }
        model.addAttribute("userDTO", new UserDTO());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute UserDTO userDTO, Model model, HttpSession session, HttpServletResponse response) {
        User user = authService.login(userDTO.getUsername(), userDTO.getPassword(), response);
        if (user != null) {
            session.setAttribute("user", user);
            return "redirect:/home";
        }
        model.addAttribute("error", "Invalid username or password");
        return "login";
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setUserDTO(new UserDTO());
        registrationDTO.setCandidateDTO(new CandidateDTO());
        List<TechnologyDTO> technologies= technologyService.findAllActiveWithoutLanding();
        model.addAttribute("registrationDTO", registrationDTO);
        model.addAttribute("technologies", technologies);
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegistrationDTO registrationDTO, Model model) {
        List<String> errors = authService.register(registrationDTO);
        if (errors == null) {
            return "redirect:/login";
        }
        model.addAttribute("errors", errors);
        return "register";
    }
    @PostMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response) {
        session.invalidate();
        authService.logout(response);
        return "redirect:/login";
    }
}