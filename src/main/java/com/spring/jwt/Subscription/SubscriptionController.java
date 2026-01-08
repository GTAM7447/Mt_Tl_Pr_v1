package com.spring.jwt.Subscription;

import com.spring.jwt.dto.ResponseDto;
import com.spring.jwt.aspect.Loggable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService service;

    @PostMapping
    @Loggable(action = "CREATE_SUBSCRIPTION_PLAN")
    public ResponseDto<SubscriptionDTO> create(@RequestBody SubscriptionDTO dto) {
        return ResponseDto.success("Subscription created", service.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseDto<SubscriptionDTO> getById(@PathVariable Integer id) {
        return ResponseDto.success("Subscription fetched", service.getById(id));
    }

    @GetMapping
    public ResponseDto<List<SubscriptionDTO>> getAll() {
        return ResponseDto.success("Subscription list", service.getAll());
    }

    @PatchMapping("/{id}")
    public ResponseDto<SubscriptionDTO> update(@PathVariable Integer id, @RequestBody SubscriptionDTO dto) {
        return ResponseDto.success("Subscription updated", service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseDto<?> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseDto.success("Subscription deleted", null);
    }

    @PostMapping("/purchase/{subscriptionId}")
    @Loggable(action = "PURCHASE_SUBSCRIPTION")
    public ResponseDto<?> purchaseSubscription(
            @PathVariable Integer subscriptionId,
            @RequestParam Integer userId) {
        service.purchaseSubscription(userId, subscriptionId);
        return ResponseDto.success("Subscription purchased and profile activated", null);
    }
}
