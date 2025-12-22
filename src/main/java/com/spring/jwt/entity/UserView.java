package com.spring.jwt.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "userView")
@Data
public class UserView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userViewId;
    private LocalDate date;
    private Integer userId;
    private Integer dayCredit;
    private Integer contactId;

}

