package com.example.courseservice.data.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.example.courseservice.data.constants.Validation;

import lombok.Data;
import lombok.NonNull;

@Data
public class StudentNoteRequest {
    private String note;
    @Pattern(regexp = Validation.TIME_REGEX, message = "Time must in format HH:mm:ss")
    private String duration;
    private Long videoId;
}
