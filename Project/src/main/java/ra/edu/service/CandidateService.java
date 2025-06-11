package ra.edu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.edu.dto.CandidateDTO;
import ra.edu.entity.candidate.Candidate;
import ra.edu.repository.candidate.CandidateRepositoryImp;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepositoryImp candidateRepositoryImp;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public boolean changePassword(Integer userId, String oldPassword, String newPassword, String phone) {
        return candidateRepositoryImp.changePassword(userId, oldPassword, newPassword, phone) > 0;
    }


    @Transactional(readOnly = true)
    public List<CandidateDTO> searchByName(String name, int pageNumber, int pageSize) {
        List<Candidate> candidates = candidateRepositoryImp.searchByName(name, pageNumber, pageSize);
        return candidates.stream()
                .map(candidate -> modelMapper.map(candidate, CandidateDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CandidateDTO> filterByExperience(int experience, int pageNumber, int pageSize) {
        List<Candidate> candidates = candidateRepositoryImp.filterByExperience(experience, pageNumber, pageSize);
        return candidates.stream()
                .map(candidate -> modelMapper.map(candidate, CandidateDTO.class))
                .collect(Collectors.toList());
    }

//    @Transactional(readOnly = true)
//    public List<CandidateDTO> filterByAge(int age, int pageNumber, int pageSize) {
//        List<Candidate> candidates = candidateRepositoryImp.filterByAge(age, pageNumber, pageSize);
//        return candidates.stream()
//                .map(candidate -> modelMapper.map(candidate, CandidateDTO.class))
//                .collect(Collectors.toList());
//    }

    @Transactional(readOnly = true)
    public List<CandidateDTO> filterByGender(String gender, int pageNumber, int pageSize) {
        List<Candidate> candidates = candidateRepositoryImp.filterByGender(gender, pageNumber, pageSize);
        return candidates.stream()
                .map(candidate -> modelMapper.map(candidate, CandidateDTO.class))
                .collect(Collectors.toList());
    }

//    @Transactional(readOnly = true)
//    public List<CandidateDTO> filterByTechnology(String technology, int pageNumber, int pageSize) {
//        List<Candidate> candidates = candidateRepositoryImp.filterByTechnology(technology, pageNumber, pageSize);
//        return candidates.stream()
//                .map(candidate -> modelMapper.map(candidate, CandidateDTO.class))
//                .collect(Collectors.toList());
//    }

    @Transactional
    public boolean resetPassword(int userId, String newPassword) {
        return candidateRepositoryImp.resetPassword(userId, newPassword) > 0;
    }

    @Transactional(readOnly = true)
    public CandidateDTO getCandidateById(int userId) {
        Candidate candidate = candidateRepositoryImp.getCandidateById(userId);
        return candidate != null ? modelMapper.map(candidate, CandidateDTO.class) : null;
    }

    @Transactional(readOnly = true)
    public int getTotalCandidatesCount() {
        return candidateRepositoryImp.getTotalCandidatesCount();
    }

    @Transactional(readOnly = true)
    public int getTotalCandidatesByName(String name) {
        return candidateRepositoryImp.getTotalCandidatesByName(name);
    }

    @Transactional(readOnly = true)
    public int getTotalCandidatesByExperience(int experience) {
        return candidateRepositoryImp.getTotalCandidatesByExperience(experience);
    }

//    @Transactional(readOnly = true)
//    public int getTotalCandidatesByAge(int age) {
//        return candidateRepositoryImp.getTotalCandidatesByAge(age);
//    }

    @Transactional(readOnly = true)
    public int getTotalCandidatesByGender(String gender) {
        return candidateRepositoryImp.getTotalCandidatesByGender(gender);
    }

//    @Transactional(readOnly = true)
//    public int getTotalCandidatesByTechnology(String technology) {
//        return candidateRepositoryImp.getTotalCandidatesByTechnology(technology);
//    }
}