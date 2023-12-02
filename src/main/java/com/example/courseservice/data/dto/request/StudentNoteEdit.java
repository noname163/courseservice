package com.example.courseservice.data.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class StudentNoteEdit {
    @NotNull
    @Size(min = 1)
    private Long id;
    private String note;
}
