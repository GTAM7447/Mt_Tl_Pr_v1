package com.spring.jwt.Subscription;

import com.spring.jwt.entity.Enums.Status;
import com.spring.jwt.entity.Subscription;
import com.spring.jwt.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository repo;

    @Override
    public SubscriptionDTO create(SubscriptionDTO dto) {

        Subscription entity = SubscriptionMapper.toEntity(dto);
        entity.setCreatedDate(LocalDate.now());
        if (entity.getStatus() == null) {
            entity.setStatus(Status.ACTIVE);
        }
        Subscription saved = repo.save(entity);
        return SubscriptionMapper.toDTO(saved);
    }

    @Override
    public SubscriptionDTO getById(Integer id) {
        Subscription sub = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found: " + id));
        return SubscriptionMapper.toDTO(sub);
    }

    @Override
    public List<SubscriptionDTO> getAll() {
        return repo.findAll()
                .stream()
                .map(SubscriptionMapper::toDTO)
                .toList();
    }

    @Override
    public SubscriptionDTO update(Integer id, SubscriptionDTO dto) {
        Subscription existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found: " + id));

        // Update only non-null fields
        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getCredit() != null) existing.setCredit(dto.getCredit());
        if (dto.getCreatedDate() != null) existing.setCreatedDate(dto.getCreatedDate());
        if (dto.getDayLimit() != null) existing.setDayLimit(dto.getDayLimit());
        if (dto.getTimePeriodMonths() != null) existing.setTimePeriodMonths(dto.getTimePeriodMonths());
        if (dto.getStatus() != null) {
            existing.setStatus(Enum.valueOf(com.spring.jwt.entity.Enums.Status.class, dto.getStatus()));
        }

        Subscription saved = repo.save(existing);
        return SubscriptionMapper.toDTO(saved);
    }

    @Override
    public void delete(Integer id) {
        Subscription sub = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found: " + id));
        repo.delete(sub);
    }
}
