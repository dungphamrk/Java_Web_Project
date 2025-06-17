package ra.edu.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.edu.dto.RecruitmentPositionDTO;
import ra.edu.entity.recruitmentPosition.RecruitmentPosition;
import ra.edu.entity.recruitmentPosition.Status;
import ra.edu.entity.technology.Technology;
import ra.edu.repository.RecruitmentPositionRepository;
import ra.edu.repository.TechnologyRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecruitmentPositionService {

    @Autowired
    private RecruitmentPositionRepository recruitmentPositionRepository;

    @Autowired
    private TechnologyRepository technologyRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<RecruitmentPositionDTO> findAllActive(int page, int size) {
        List<RecruitmentPosition> positions = recruitmentPositionRepository.findAllActive(page, size);
        return positions.stream()
                .map(position -> modelMapper.map(position, RecruitmentPositionDTO.class))
                .collect(Collectors.toList());
    }

    public List<RecruitmentPositionDTO> findAll(int page, int size) {
        List<RecruitmentPosition> positions = recruitmentPositionRepository.findAll(page, size);
        return positions.stream()
                .map(position -> modelMapper.map(position, RecruitmentPositionDTO.class))
                .collect(Collectors.toList());
    }

    public long getTotalActiveItems() {
        return recruitmentPositionRepository.countActive();
    }

    public long getTotalItems() {
        return recruitmentPositionRepository.countAll();
    }

    public RecruitmentPositionDTO findById(int id) {
        RecruitmentPosition position = recruitmentPositionRepository.findById(id);
        if (position == null || position.getStatus() == Status.INACTIVE) {
            throw new RuntimeException("Recruitment position not found");
        }
        return modelMapper.map(position, RecruitmentPositionDTO.class);
    }

    public void save(RecruitmentPositionDTO positionDTO) {
        // Nếu là thêm mới
        if (positionDTO.getId() == 0) {
            if (recruitmentPositionRepository.existsByName(positionDTO.getName())) {
                throw new RuntimeException("Tên vị trí tuyển dụng đã tồn tại");
            }
        } else {
            // Là cập nhật
            RecruitmentPosition existing = recruitmentPositionRepository.findById(positionDTO.getId());
            if (existing == null) {
                throw new RuntimeException("Vị trí không tồn tại");
            }

            // Nếu tên bị đổi và trùng với tên vị trí khác
            if (!existing.getName().equals(positionDTO.getName()) &&
                    recruitmentPositionRepository.existsByName(positionDTO.getName())) {
                throw new RuntimeException("Tên vị trí tuyển dụng đã tồn tại");
            }
        }

        // Lúc này có thể map và lưu
        RecruitmentPosition position = modelMapper.map(positionDTO, RecruitmentPosition.class);

        // Map công nghệ
        if (positionDTO.getTechnologies() != null && !positionDTO.getTechnologies().isEmpty()) {
            List<Integer> techIds = positionDTO.getTechnologies().stream()
                    .map(Integer::parseInt)
                    .toList();
            List<Technology> technologyList = technologyRepository.findAllByIds(techIds);
            position.setTechnologyList(technologyList);
        } else {
            position.setTechnologyList(new ArrayList<>());
        }

        if (position.getId() != 0) {
            RecruitmentPosition existing = recruitmentPositionRepository.findById(position.getId());
            if (existing != null) {
                position.setStatus(existing.getStatus());
                position.setCreatedDate(existing.getCreatedDate());
            }
        }

        recruitmentPositionRepository.save(position);
    }


    private void mapTechnologies(RecruitmentPositionDTO dto, RecruitmentPosition entity) {
        if (dto.getTechnologies() != null && !dto.getTechnologies().isEmpty()) {
            List<Integer> techIds = dto.getTechnologies().stream()
                    .map(Integer::parseInt)
                    .toList();
            List<Technology> technologyList = technologyRepository.findAllByIds(techIds);
            entity.setTechnologyList(technologyList);
        } else {
            entity.setTechnologyList(new ArrayList<>());
        }
    }

    public List<RecruitmentPositionDTO> searchByName(String keyword, int page, int size) {
        List<RecruitmentPosition> positions = recruitmentPositionRepository.searchByName(keyword, page, size);
        return positions.stream()
                .map(position -> modelMapper.map(position, RecruitmentPositionDTO.class))
                .collect(Collectors.toList());
    }

    public long countByName(String keyword) {
        return recruitmentPositionRepository.countByName(keyword);
    }

    public void delete(int id) {
        recruitmentPositionRepository.updateStatus(id, Status.INACTIVE);
    }
}