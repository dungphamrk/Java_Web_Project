package ra.edu.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ra.edu.service.AuthService;

import javax.servlet.http.HttpServletRequest;

@Controller
public class DashBoardController {

    @Autowired
    AuthService authService;
    @GetMapping("admin")
    public String admin(@RequestParam(value = "username", required = false) String username, HttpServletRequest request) {
        String role = authService.getCurrentUserRole(request);
        if (role != null) {
            if (role.equals("CANDIDATE")) {
                return "redirect:/home";
            } else if (role.equals("ADMIN")) {
                return "admin/dashboard";
            }
        }
        // Xử lý thêm nếu cần dựa trên username
        return "admin/dashboard";
    }
}
