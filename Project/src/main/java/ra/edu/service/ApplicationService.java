package ra.edu.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.edu.dto.ApplicationDTO;
import ra.edu.entity.application.Application;
import ra.edu.entity.application.Progress;
import ra.edu.entity.recruitmentPosition.RecruitmentPosition;
import ra.edu.repository.ApplicationRepository;
import ra.edu.repository.CandidateRepository;
import ra.edu.repository.RecruitmentPositionRepository;
import ra.edu.repository.user.UserRepository;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private RecruitmentPositionRepository recruitmentPositionRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private Validator validator;

    @Autowired
    private CandidateRepository candidateRepository;

    public List<ApplicationDTO> findAll(int page, int size) {
        List<Application> applications = applicationRepository.findAll(page, size);
        return applications.stream()
                .map(application -> modelMapper.map(application, ApplicationDTO.class))
                .collect(Collectors.toList());
    }

    public List<String> updateInterview(int id, String interviewLink, LocalDateTime interviewTime) {
        List<String> errors = new ArrayList<>();
        if (!applicationRepository.restrictFinalStateUpdates(id)) {
            errors.add("Không thể thao tác với ứng tuyển này.");
            return errors;
        }
        Application application = applicationRepository.findById(id);
        if (application == null) {
            errors.add("Ứng tuyển không tồn tại.");
            return errors;
        }

        // Tạo DTO để validate
        ApplicationDTO dto = modelMapper.map(application, ApplicationDTO.class);
        dto.setInterviewLink(interviewLink);
        dto.setInterviewTime(interviewTime);
        dto.setProgress(Progress.HANDLING);
        dto.setInterviewRequestDate(LocalDateTime.now());
        // Validate với nhóm OnInterviewing
        Set<ConstraintViolation<ApplicationDTO>> violations = validator.validate(dto, ra.edu.validation.OnInterviewing.class);
        if (!violations.isEmpty()) {
            return violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toList());
        }
        application.setInterviewRequestDate(LocalDateTime.now());
        application.setInterviewLink(interviewLink);
        application.setInterviewTime(interviewTime);
        application.setProgress(Progress.HANDLING);
        application.setUpdateAt(LocalDateTime.now());
        if (!applicationRepository.save(application)) {
            errors.add("Không thể lưu thông tin phỏng vấn.");
            return errors;
        }
        return errors; // Trả về danh sách rỗng nếu không có lỗi
    }

    public long getTotalItems() {
        return applicationRepository.countAll();
    }

    public ApplicationDTO findById(int id) {
        Application application = applicationRepository.findById(id);
        if (application == null || application.getProgress() == Progress.CANCEL) {
            throw new RuntimeException("Ứng tuyển không tồn tại.");
        }
        return modelMapper.map(application, ApplicationDTO.class);
    }

    public List<String> save(ApplicationDTO applicationDTO, Class<?> validationGroup) {
        List<String> errors = new ArrayList<>();
        // Validate DTO dựa trên nhóm validation
        Set<ConstraintViolation<ApplicationDTO>> violations = validator.validate(applicationDTO, validationGroup);
        if (!violations.isEmpty()) {
            return violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toList());
        }
        Application application = modelMapper.map(applicationDTO, Application.class);

        // Giữ các trường hiện có khi cập nhật
        if (application.getId() != 0) {
            if (!applicationRepository.restrictFinalStateUpdates(application.getId())) {
                errors.add("Không thể thao tác với đơn ứng tuyển này.");
                return errors;
            }
            Application existing = applicationRepository.findById(application.getId());
            if (existing == null) {
                errors.add("Ứng tuyển không tồn tại.");
                return errors;
            }
            application.setCreateAt(existing.getCreateAt());
            application.setUpdateAt(existing.getUpdateAt());
            application.setProgress(existing.getProgress());
        }

        if (!applicationRepository.save(application)) {
            errors.add("Không thể lưu đơn ứng tuyển.");
            return errors;
        }
        return errors; // Trả về danh sách rỗng nếu không có lỗi
    }

    public List<ApplicationDTO> filterApplications(String keyword, String progress, int page, int size) {
        return applicationRepository.filterApplications(keyword, progress, page, size)
                .stream()
                .map(a -> modelMapper.map(a, ApplicationDTO.class))
                .collect(Collectors.toList());
    }

    public long getTotalFilteredItems(String keyword, String progress) {
        return applicationRepository.countFilteredApplications(keyword, progress);
    }

    public List<String> updateProgress(int id, Progress progress, String destroyReason) {
        List<String> errors = new ArrayList<>();
        if (!applicationRepository.restrictFinalStateUpdates(id)) {
            errors.add("Không thể thao tác với ứng tuyển này.");
            return errors;
        }

        Application application = applicationRepository.findById(id);
        if (application == null) {
            errors.add("Ứng tuyển không tồn tại.");
            return errors;
        }
        ApplicationDTO dto = modelMapper.map(application, ApplicationDTO.class);
        dto.setProgress(progress);
        if (progress == Progress.REJECT || progress == Progress.CANCEL) {
            dto.setDestroyReason(destroyReason);
            dto.setDestroyAt(LocalDateTime.now());
        }

        Class<?> validationGroup = switch (progress) {
            case PENDING -> ra.edu.validation.OnPending.class;
            case HANDLING -> ra.edu.validation.OnHandling.class;
            case INTERVIEWING -> ra.edu.validation.OnInterviewing.class;
            case DONE -> ra.edu.validation.OnDone.class;
            case REJECT -> ra.edu.validation.OnReject.class;
            case CANCEL -> ra.edu.validation.OnCancel.class;
        };

        Set<ConstraintViolation<ApplicationDTO>> violations = validator.validate(dto, validationGroup);
        if (!violations.isEmpty()) {
            return violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toList());
        }

        if (!applicationRepository.updateProgress(id, progress, destroyReason)) {
            errors.add("Không thể cập nhật tiến trình.");
            return errors;
        }
        return errors; // Trả về danh sách rỗng nếu không có lỗi
    }

    public List<String> createApplication(int positionId, String username, String cvUrl) {
        List<String> errors = new ArrayList<>();
        Application app = new Application();
        app.setCvUrl(cvUrl);
        app.setRecruitmentPosition(recruitmentPositionRepository.findById(positionId));
        app.setCandidate(candidateRepository.findByUsername(username));
        app.setProgress(Progress.PENDING);
        if (!applicationRepository.save(app)) {
            errors.add("Không thể tạo ứng tuyển.");
            return errors;
        }
        return errors;
    }

    public List<ApplicationDTO> findAllByUser(String username, String keyword, String progress, int page, int size) {
        return applicationRepository.findByUserAndFilter(username, keyword, progress, page, size)
                .stream()
                .map(a -> modelMapper.map(a, ApplicationDTO.class))
                .collect(Collectors.toList());
    }

    public long getTotalByUser(String username, String keyword, String progress) {
        return applicationRepository.countByUserAndFilter(username, keyword, progress);
    }
}