package ra.edu.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.edu.dto.RegistrationDTO;
import ra.edu.dto.UserDTO;
import ra.edu.dto.CandidateDTO;
import ra.edu.entity.candidate.Candidate;
import ra.edu.entity.user.User;
import ra.edu.repository.candidate.CandidateRepositoryImp;
import ra.edu.repository.user.UserRepositoryImp;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepositoryImp userRepositoryImp;

    @Autowired
    private CandidateRepositoryImp candidateRepositoryImp;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private Validator validator;

    @Transactional
    public List<String> register(RegistrationDTO registrationDTO) {
        List<String> errors = new ArrayList<>();

        // Validate UserDTO
        Set<ConstraintViolation<UserDTO>> userViolations = validator.validate(registrationDTO.getUserDTO());
        userViolations.forEach(v -> errors.add(v.getMessage()));

        // Validate CandidateDTO
        Set<ConstraintViolation<CandidateDTO>> candidateViolations = validator.validate(registrationDTO.getCandidateDTO());
        candidateViolations.forEach(v -> errors.add(v.getMessage()));

        // Check for existing username
        User existingUser = userRepositoryImp.findByUsername(registrationDTO.getUserDTO().getUsername());
        if (existingUser != null) {
            errors.add("Username already exists");
        }

        if (!errors.isEmpty()) {
            return errors;
        }

        User user = modelMapper.map(registrationDTO.getUserDTO(), User.class);
        userRepositoryImp.save(user);

        Candidate candidate = modelMapper.map(registrationDTO.getCandidateDTO(), Candidate.class);
        candidate.setId(user.getId()); // one-to-one
        candidateRepositoryImp.save(candidate);

        return null;
    }

    @Transactional
    public User login(String username, String password, HttpServletResponse response) {
        User user = userRepositoryImp.findByUsernameAndPassword(username, password);
        if (user != null) {
            // Lưu username và role vào cookie
            Cookie usernameCookie = new Cookie("username", user.getUsername());
            Cookie roleCookie = new Cookie("role", user.getRole().toString());
            usernameCookie.setPath("/");
            roleCookie.setPath("/");
            usernameCookie.setMaxAge(24 * 60 * 60); // 1 ngày
            roleCookie.setMaxAge(24 * 60 * 60);

            response.addCookie(usernameCookie);
            response.addCookie(roleCookie);
        }
        return user;
    }

    public void logout(HttpServletResponse response) {
        Cookie usernameCookie = new Cookie("username", null);
        Cookie roleCookie = new Cookie("role", null);
        usernameCookie.setMaxAge(0);
        roleCookie.setMaxAge(0);
        usernameCookie.setPath("/");
        roleCookie.setPath("/");

        response.addCookie(usernameCookie);
        response.addCookie(roleCookie);
    }

    public String getCurrentUsername(HttpServletRequest request) {
        for (Cookie cookie : request.getCookies()) {
            if ("username".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public String getCurrentUserRole(HttpServletRequest request) {
        for (Cookie cookie : request.getCookies()) {
            if ("role".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    @Transactional
    public User getUserById(int id) {
        return userRepositoryImp.getUserById(id);
    }

    @Transactional
    public int updateUserStatus(int id, String status) {
        return userRepositoryImp.updateStatus(id, status);
    }
}
