package ra.edu.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.edu.dto.TechnologyDTO;
import ra.edu.entity.technology.Status;
import ra.edu.entity.technology.Technology;
import ra.edu.repository.TechnologyRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TechnologyService {

    @Autowired
    private TechnologyRepository technologyRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<TechnologyDTO> findAllActive(int page, int size) {
        List<Technology> technologies = technologyRepository.findAllActive(page, size);
        return technologies.stream()
                .map(tech -> modelMapper.map(tech, TechnologyDTO.class))
                .collect(Collectors.toList());
    }

    public List<TechnologyDTO> findAll(int page, int size) {
        List<Technology> technologies = technologyRepository.findAll(page, size);
        return technologies.stream()
                .map(tech -> modelMapper.map(tech, TechnologyDTO.class))
                .collect(Collectors.toList());
    }

    public long getTotalActiveItems() {
        return technologyRepository.countActive();
    }

    public long getTotalItems() {
        return technologyRepository.countAll();
    }

    public TechnologyDTO findById(int id) {
        Technology technology = technologyRepository.findById(id);
        if (technology == null || technology.getStatus() == Status.INACTIVE) {
            throw new RuntimeException("Technology not found");
        }
        return modelMapper.map(technology, TechnologyDTO.class);
    }

    public void save(TechnologyDTO technologyDTO) {
        Technology technology = modelMapper.map(technologyDTO, Technology.class);
        if (technology.getId() != 0) {
            Technology existing = technologyRepository.findById(technology.getId());
            if (existing != null) {
                technology.setStatus(existing.getStatus());
            }
        }
        technologyRepository.save(technology);
    }

    public void delete(int id) {
        technologyRepository.updateStatus(id, Status.INACTIVE);
    }
}