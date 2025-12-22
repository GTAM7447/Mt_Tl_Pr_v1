package com.spring.jwt.UserView;


import com.spring.jwt.ContactDetails.dto.ContactDetailsResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserViewWithContactDTO {

    private Integer userViewId;
    private String date;

    private Integer viewerUserId;
    private Integer contactUserId;

    private Integer dayCredit;

    // Full contact details here
    private ContactDetailsResponse contactDetails;
}
