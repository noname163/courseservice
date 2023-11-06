package com.example.courseservice.services.levelservice.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.courseservice.data.dto.response.LevelResponse;
import com.example.courseservice.data.entities.Level;
import com.example.courseservice.data.repositories.LevelRepository;
import com.example.courseservice.exceptions.BadRequestException;
import com.example.courseservice.services.levelservice.LevelService;

@Service
public class LevelServiceImpl implements LevelService {
    @Autowired
    private LevelRepository levelRepository;

    @Override
    public void createLevel(String name) {
        if (name == null || name.equals("")) {
            throw new BadRequestException("Level name cannot blank");
        }
        levelRepository.save(Level.builder().name(name).build());
    }

    @Override
    public List<LevelResponse> getListLevel() {
        List<Level> levels = levelRepository.findAll();
        if(levels.isEmpty()){
            return List.of();
        }
        List<LevelResponse> levelResponses = new ArrayList<>();
        for (Level level : levels) {
            levelResponses.add(LevelResponse
                    .builder()
                    .id(level.getId())
                    .name(level.getName())
                    .build());
        }
        return levelResponses;
    }

    
}
