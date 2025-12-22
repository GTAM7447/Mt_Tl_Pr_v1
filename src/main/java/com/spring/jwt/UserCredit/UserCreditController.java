package com.spring.jwt.UserCredit;

import com.spring.jwt.dto.ResponseDto;
import com.spring.jwt.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-credit")
@RequiredArgsConstructor
public class UserCreditController {

    private final UserCreditService service;

    @PostMapping
    public ResponseEntity<ResponseDto<?>> create(@RequestBody UserCreditDTO dto) {
        try {
            return ResponseEntity.ok(ResponseDto.success("Created", service.create(dto)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error("Failed", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<?>> getById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(ResponseDto.success("Success", service.getById(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error("Failed", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ResponseDto<?>> getAll() {
        return ResponseEntity.ok(ResponseDto.success("Success", service.getAll()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<?>> update(@PathVariable Integer id, @RequestBody UserCreditDTO dto) {
        try {
            return ResponseEntity.ok(ResponseDto.success("Updated", service.update(id, dto)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error("Failed", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<?>> delete(@PathVariable Integer id) {
        try {
            service.delete(id);
            return ResponseEntity.ok(ResponseDto.success("Deleted", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDto.error("Failed", e.getMessage()));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDto<UserCreditDTO>> getByUserId(@PathVariable Integer userId) {

        try {
            UserCreditDTO credit = service.getByUserId(userId);
            return ResponseEntity.ok(ResponseDto.success("User credit fetched successfully", credit));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseDto.error("No credit data found", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDto.error("Error fetching user credit", ex.getMessage()));
        }
    }
}
