package ra.edu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashBoardController {
    @GetMapping("admin")
    public String admin(){
        return "admin/dashboard";
    }
}
