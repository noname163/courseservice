package com.example.courseservice.data.dto.request;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class StudentNoteEdit {
    @NotNull
    private Long id;
    private String note;
}
