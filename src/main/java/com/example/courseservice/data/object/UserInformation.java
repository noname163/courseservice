package com.example.courseservice.data.object;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInformation {
    private Long id;
    private String email;
    private String role;
    private String fullname;
}
