package com.example.courseservice.data.dto.request;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.example.courseservice.data.object.Subject;
import com.example.courseservice.data.object.Topic;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseRequest {
    private String description;
    @NotNull
    @NotBlank
    @Size(min = 10, message = "Name cannot less than 10 characters")
    private String name;
    @NotNull
    private Double price;
    @NotNull
    private Subject subject;
    @NotNull
    private long levelId;
    @NotEmpty
    @NotNull
    private List<Topic> topic;
}
