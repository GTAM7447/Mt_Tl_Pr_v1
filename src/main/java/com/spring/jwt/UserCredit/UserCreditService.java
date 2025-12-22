package com.spring.jwt.UserCredit;

import java.util.List;

public interface UserCreditService {

    UserCreditDTO create(UserCreditDTO dto);

    UserCreditDTO getById(Integer id);

    List<UserCreditDTO> getAll();

    UserCreditDTO update(Integer id, UserCreditDTO dto);

    void delete(Integer id);
     UserCreditDTO getByUserId(Integer userId);
}
