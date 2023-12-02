package com.example.courseservice.data.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.example.courseservice.data.constants.Validation;

import lombok.Data;

@Data
public class StudentNoteRequest {
    private String note;
    @Pattern(regexp = Validation.TIME_REGEX, message = "Time must in format HH:mm:ss")
    private String duration;
    @NotNull
    @Size(min = 1)
    private Long videoId;
}
