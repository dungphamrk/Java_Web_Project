package ra.edu.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.edu.dto.CandidateDTO;
import ra.edu.entity.candidate.Candidate;
import ra.edu.repository.CandidateRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

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

    public void toggleUserStatus(int candidateId) {
        candidateRepository.toggleUserStatusByCandidateId(candidateId);
    }

    public CandidateDTO findById(int id) {
        Candidate candidate = candidateRepository.findById(id);
        if (candidate == null) {
            throw new RuntimeException("Candidate not found");
        }
        return modelMapper.map(candidate, CandidateDTO.class);
    }

    public void save(CandidateDTO candidateDTO) {
        boolean isDuplicate = candidateRepository.existsByEmail(candidateDTO.getEmail());
        if (isDuplicate && (candidateDTO.getId() == 0 || !candidateRepository.findById(candidateDTO.getId()).getEmail().equals(candidateDTO.getEmail()))) {
            throw new RuntimeException("Email đã tồn tại");
        }
        Candidate candidate = modelMapper.map(candidateDTO, Candidate.class);
        candidateRepository.save(candidate);
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