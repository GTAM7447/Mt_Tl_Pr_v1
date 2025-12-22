package com.spring.jwt.UserView;

import com.spring.jwt.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-views")
public class UserViewController {

//    private final UserViewService userViewService;


//    @GetMapping("/{userId}")
//    public ResponseEntity<ResponseDto<List<UserViewWithContactDTO>>> getViewsByUserId(@PathVariable Integer userId) {
//
//        List<UserViewWithContactDTO> result = userViewService.getByUserId(userId);
//        return ResponseEntity.ok(
//                ResponseDto.success("User views fetched successfully", result)
//        );
//    }
}
