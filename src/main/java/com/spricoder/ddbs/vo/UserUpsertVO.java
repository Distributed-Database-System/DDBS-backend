package com.spricoder.ddbs.vo;

import com.spricoder.ddbs.data.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserUpsertVO {

    private String uid;
    private String name;
    private String gender;
    private String email;
    private String phone;
    private String dept;
    private String grade;
    private String language;
    private String region;
    private String role;
}