package com.example.courseservice.services.levelservice;

import java.util.List;

import com.example.courseservice.data.dto.response.LevelResponse;

public interface LevelService {
    public void createLevel(String name);
    public List<LevelResponse> getListLevel();
}
