package com.example.courseservice.data.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LevelResponse {
    private long id;
    private String name;
}
