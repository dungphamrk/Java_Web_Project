package ra.edu.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.edu.dto.CandidateDTO;
import ra.edu.entity.candidate.Candidate;
import ra.edu.entity.technology.Technology;
import ra.edu.repository.CandidateRepository;
import ra.edu.repository.TechnologyRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private TechnologyRepository technologyRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<CandidateDTO> findAll(int page, int size) {
        List<Candidate> candidates = candidateRepository.findAll(page, size);
        return candidates.stream()
                .map(candidate -> modelMapper.map(candidate, CandidateDTO.class))
                .collect(Collectors.toList());
    }

    public long getTotalItems() {
        return candidateRepository.countAll();
    }

    public CandidateDTO findByUsername(String username) {
        Candidate candidate = candidateRepository.findByUsername(username);
        if (candidate == null) {
            throw new RuntimeException("Không tìm thấy ứng viên");
        }
        CandidateDTO dto = modelMapper.map(candidate, CandidateDTO.class);
        dto.setTechnologies(
                candidate.getTechnologyList().stream()
                        .map(tech -> String.valueOf(tech.getId()))
                        .collect(Collectors.toList())
        );
        return dto;
    }

    public void toggleUserStatus(int candidateId) {
        candidateRepository.toggleUserStatusByCandidateId(candidateId);
    }

    public CandidateDTO findById(int id) {
        Candidate candidate = candidateRepository.findById(id);
        if (candidate == null) {
            throw new RuntimeException("Không tìm thấy ứng viên");
        }
        return modelMapper.map(candidate, CandidateDTO.class);
    }

    public void save(CandidateDTO candidateDTO) {
        boolean isDuplicate = candidateRepository.existsByEmail(candidateDTO.getEmail());
        if (isDuplicate && (candidateDTO.getId() == 0 ||
                !candidateRepository.findById(candidateDTO.getId()).getEmail().equals(candidateDTO.getEmail()))) {
            throw new RuntimeException("Email đã tồn tại");
        }
        Candidate candidate = modelMapper.map(candidateDTO, Candidate.class);
        candidateRepository.save(candidate);
    }

    public void updateProfile(CandidateDTO candidateDTO, String username) {
        // Tìm ứng viên hiện tại dựa trên username
        Candidate existingCandidate = candidateRepository.findByUsername(username);
        if (existingCandidate == null) {
            throw new RuntimeException("Không tìm thấy ứng viên");
        }

        // Kiểm tra email trùng lặp
        if (!existingCandidate.getEmail().equals(candidateDTO.getEmail()) &&
                candidateRepository.existsByEmail(candidateDTO.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        // Cập nhật các trường cơ bản
        existingCandidate.setName(candidateDTO.getName());
        existingCandidate.setEmail(candidateDTO.getEmail());
        existingCandidate.setPhone(candidateDTO.getPhone());
        existingCandidate.setDob(candidateDTO.getDob());
        existingCandidate.setExperience(candidateDTO.getExperience());
        existingCandidate.setGender(candidateDTO.getGender());
        existingCandidate.setDescription(candidateDTO.getDescription());

        // Xử lý danh sách công nghệ (nếu có)
        if (candidateDTO.getTechnologies() != null && !candidateDTO.getTechnologies().isEmpty()) {
            List<Integer> techIds = candidateDTO.getTechnologies().stream()
                    .map(Integer::parseInt)
                    .toList();
            Set<Technology> technologies = technologyRepository.findAllByIds(techIds);
            existingCandidate.setTechnologyList(technologies);
        }

        // Lưu lại vào database
        candidateRepository.save(existingCandidate);
    }

    public List<CandidateDTO> searchByName(String keyword, int page, int size) {
        List<Candidate> candidates = candidateRepository.searchByName(keyword, page, size);
        return candidates.stream()
                .map(candidate -> modelMapper.map(candidate, CandidateDTO.class))
                .collect(Collectors.toList());
    }

    public long countByName(String keyword) {
        return candidateRepository.countByName(keyword);
    }

    public void delete(int id) {
        candidateRepository.delete(id);
    }

    public List<CandidateDTO> filterCandidates(String keyword, String technology, String gender, String age, String experience, int page, int size) {
        return candidateRepository.filterCandidates(keyword, technology, gender, age, experience, page, size)
                .stream()
                .map(c -> modelMapper.map(c, CandidateDTO.class))
                .collect(Collectors.toList());
    }

    public long countFilteredCandidates(String keyword, String technology, String gender, String age, String experience) {
        return candidateRepository.countFilteredCandidates(keyword, technology, gender, age, experience);
    }
}