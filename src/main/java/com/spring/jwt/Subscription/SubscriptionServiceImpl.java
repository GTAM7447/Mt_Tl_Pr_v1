package com.spring.jwt.Subscription;

import com.spring.jwt.entity.Enums.Status;
import com.spring.jwt.entity.Subscription;
import com.spring.jwt.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
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
    @Transactional
    public SubscriptionDTO update(Integer id, SubscriptionDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Subscription DTO cannot be null");
        }

        Subscription existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found: " + id));

        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getCredit() != null) {
            if (dto.getCredit() < 0) {
                throw new IllegalArgumentException("Credit cannot be negative");
            }
            existing.setCredit(dto.getCredit());
        }
        if (dto.getCreatedDate() != null) existing.setCreatedDate(dto.getCreatedDate());
        if (dto.getDayLimit() != null) {
            if (dto.getDayLimit() < 0) {
                throw new IllegalArgumentException("Day limit cannot be negative");
            }
            existing.setDayLimit(dto.getDayLimit());
        }
        if (dto.getTimePeriodMonths() != null) {
            if (dto.getTimePeriodMonths() <= 0) {
                throw new IllegalArgumentException("Time period must be positive");
            }
            existing.setTimePeriodMonths(dto.getTimePeriodMonths());
        }
        if (dto.getStatus() != null) {
            try {
                existing.setStatus(Enum.valueOf(com.spring.jwt.entity.Enums.Status.class, dto.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status value: " + dto.getStatus());
            }
        }

        Subscription saved = repo.save(existing);
        return SubscriptionMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Subscription sub = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found: " + id));
        repo.delete(sub);
    }
}
