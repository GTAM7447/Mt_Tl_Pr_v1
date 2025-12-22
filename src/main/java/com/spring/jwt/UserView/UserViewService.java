package com.spring.jwt.UserView;

import java.util.List;

public interface UserViewService {
    List<UserViewWithContactDTO> getByUserId(Integer userId);
}
