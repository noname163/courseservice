package com.example.courseservice.services.levelservice;

import java.util.List;

import com.example.courseservice.data.dto.response.LevelResponse;
import com.example.courseservice.data.entities.Level;

public interface LevelService {
    public void createLevel(String name);
    public List<LevelResponse> getListLevel();
    public Level getLevel(Long id);
    public List<Level> getListLevel(List<Long> ids);
}
