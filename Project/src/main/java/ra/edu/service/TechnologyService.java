package ra.edu.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.edu.dto.TechnologyDTO;
import ra.edu.entity.technology.Status;
import ra.edu.entity.technology.Technology;
import ra.edu.repository.TechnologyRepository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TechnologyService {

    @Autowired
    private TechnologyRepository technologyRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<TechnologyDTO> findAllActive(int page, int size) {
        List<Technology> technologies = technologyRepository.findAllActive(page, size);
        return technologies.stream()
                .map(tech -> modelMapper.map(tech, TechnologyDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TechnologyDTO> findAllActiveWithoutLanding() {
        List<Technology> technologies = technologyRepository.findAllActiveWithoutLanding();
        return technologies.stream()
                .map(tech -> modelMapper.map(tech, TechnologyDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TechnologyDTO> findAllByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Technology> technologies = technologyRepository.findAllByIds(ids);
        return technologies.stream()
                .map(tech -> modelMapper.map(tech, TechnologyDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TechnologyDTO> findAll(int page, int size) {
        List<Technology> technologies = technologyRepository.findAll(page, size);
        return technologies.stream()
                .map(tech -> modelMapper.map(tech, TechnologyDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getTotalActiveItems() {
        return technologyRepository.countActive();
    }

    @Transactional(readOnly = true)
    public long getTotalItems() {
        return technologyRepository.countAll();
    }

    @Transactional(readOnly = true)
    public TechnologyDTO findById(int id) {
        Technology technology = technologyRepository.findById(id);
        if (technology == null || technology.getStatus() == Status.INACTIVE) {
            throw new RuntimeException("Technology not found");
        }
        return modelMapper.map(technology, TechnologyDTO.class);
    }

    @Transactional
    public void save(TechnologyDTO technologyDTO) {
        boolean isDuplicate = technologyRepository.existsByName(technologyDTO.getName());
        if (isDuplicate) {
            throw new RuntimeException("Tên công nghệ đã tồn tại");
        }
        Technology technology = modelMapper.map(technologyDTO, Technology.class);
        if (technology.getId() == 0) {
            technology.setStatus(Status.ACTIVE); // Đặt status mặc định
        } else {
            Technology existing = technologyRepository.findById(technology.getId());
            if (existing != null) {
                technology.setStatus(existing.getStatus());
            }
        }
        technologyRepository.save(technology);
    }

    @Transactional(readOnly = true)
    public List<TechnologyDTO> searchByName(String keyword, int page, int size) {
        List<Technology> technologies = technologyRepository.searchByName(keyword, page, size);
        return technologies.stream()
                .map(tech -> modelMapper.map(tech, TechnologyDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long countByName(String keyword) {
        return technologyRepository.countByName(keyword);
    }

    @Transactional
    public void delete(int id) {
        technologyRepository.updateStatus(id, Status.INACTIVE);
    }
}