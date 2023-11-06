package com.example.courseservice.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.courseservice.data.dto.response.LevelResponse;
import com.example.courseservice.services.levelservice.LevelService;

@RestController
@RequestMapping("/api/levels")
public class LevelController {

    @Autowired
    private LevelService levelService;

    @GetMapping()
    public ResponseEntity<List<LevelResponse>> getLevels(){
        return ResponseEntity.status(HttpStatus.OK).body(levelService.getListLevel());
    }
}
