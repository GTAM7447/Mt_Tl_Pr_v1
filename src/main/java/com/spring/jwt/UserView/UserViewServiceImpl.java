//package com.spring.jwt.UserView;
//
//import com.spring.jwt.ContactDetails.ContactDetailsDTO;
//import com.spring.jwt.ContactDetails.ContactDetailsMapper;
//import com.spring.jwt.ContactDetails.ContactDetailsRepository;
//import com.spring.jwt.ContactDetails.dto.ContactDetailsResponse;
//import com.spring.jwt.entity.UserView;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class UserViewServiceImpl implements UserViewService {
//
//    private final UserViewRepository repo;
//    private final ContactDetailsRepository contactRepo;
//
//
//    @Override
//    public List<UserViewWithContactDTO> getByUserId(Integer userId) {
//
//        List<UserView> views = repo.findByUserIdOrderByDateDesc(userId);
//
//        return views.stream().map(view -> {
//
//            ContactDetailsResponse contactDTO = contactRepo.findByUser_Id(view.getContactId())
//                    .map(new ContactDetailsMapper::toResponse)
//                    .orElse(null);
//
//            return UserViewMapper.toWithContactDTO(view, contactDTO);
//
//        }).toList();
//    }
//
//}
