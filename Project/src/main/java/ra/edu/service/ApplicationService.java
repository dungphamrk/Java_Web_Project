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

import java.time.LocalDateTime;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
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
        List<ApplicationDTO> applicationDTO = applications.stream()
                .map(application -> modelMapper.map(application, ApplicationDTO.class))
                .collect(Collectors.toList());

        return applicationDTO;
    }

    public void updateInterview(int id, String interviewLink, LocalDateTime interviewTime) {
        Application application = applicationRepository.findById(id);
        application.setInterviewLink(interviewLink);
        application.setInterviewTime(interviewTime);
        application.setProgress(Progress.HANDLING);
        application.setUpdateAt(LocalDateTime.now());
        applicationRepository.save(application);
    }

    public long getTotalItems() {
        return applicationRepository.countAll();
    }

    public ApplicationDTO findById(int id) {
        Application application = applicationRepository.findById(id);
        if (application == null || application.getProgress() == Progress.CANCEL) {
            throw new RuntimeException("Application not found");
        }
        return modelMapper.map(application, ApplicationDTO.class);
    }

    public void save(ApplicationDTO applicationDTO, Class<?> validationGroup) {
        // Validate DTO based on progress group
        Set<ConstraintViolation<ApplicationDTO>> violations = validator.validate(applicationDTO, validationGroup);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("; "));
            throw new RuntimeException(errorMessage);
        }
        Application application = modelMapper.map(applicationDTO, Application.class);

        // Preserve existing fields for updates
        if (application.getId() != 0) {
            Application existing = applicationRepository.findById(application.getId());
            if (existing == null) {
                throw new RuntimeException("Application not found");
            }
            application.setCreateAt(existing.getCreateAt());
            application.setUpdateAt(existing.getUpdateAt());
            application.setProgress(existing.getProgress());
        }

        applicationRepository.save(application);
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

    public void updateProgress(int id, Progress progress, String destroyReason) {
        Application application = applicationRepository.findById(id);
        if (application == null) {
            throw new RuntimeException("Application not found");
        }

        // Validate based on progress group
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
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("; "));
            throw new RuntimeException(errorMessage);
        }

        applicationRepository.updateProgress(id, progress, destroyReason);
    }


    public void createApplication(int positionId, String username, String cvUrl) {
        Application app = new Application();
        app.setCvUrl(cvUrl);
        app.setRecruitmentPosition(recruitmentPositionRepository.findById(positionId));
        app.setCandidate(candidateRepository.findByUsername(username));
        app.setProgress(Progress.PENDING);
        applicationRepository.save(app);
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