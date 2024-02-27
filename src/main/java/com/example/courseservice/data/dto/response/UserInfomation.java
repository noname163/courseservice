package com.example.courseservice.data.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserInfomation {
    private Long userId;
    private String username;
    private String userAvatar;
}
