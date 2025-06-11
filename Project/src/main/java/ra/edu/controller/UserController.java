package ra.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ra.edu.dto.RegistrationDTO;
import ra.edu.dto.UserDTO;
import ra.edu.dto.CandidateDTO;
import ra.edu.entity.user.User;
import ra.edu.service.UserService;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLogin(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute UserDTO userDTO, Model model, HttpSession session, HttpServletResponse response) {
        User user = userService.login(userDTO.getUsername(), userDTO.getPassword(), response);
        if (user != null) {
            session.setAttribute("user", user);
            return "redirect:/home"; // Adjust redirect as needed
        }
        model.addAttribute("error", "Invalid username or password");
        return "login";
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setUserDTO(new UserDTO());
        registrationDTO.setCandidateDTO(new CandidateDTO());
        model.addAttribute("registrationDTO", registrationDTO);
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegistrationDTO registrationDTO, Model model) {
        List<String> errors = userService.register(registrationDTO);
        if (errors == null) {
            return "redirect:/login";
        }
        model.addAttribute("errors", errors);
        return "register";
    }
}