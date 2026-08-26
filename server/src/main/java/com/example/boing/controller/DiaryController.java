package com.example.boing.controller;

import com.example.boing.controller.generic.MutableController;
import com.example.boing.domain.Diary;
import com.example.boing.service.DiaryService;
import com.example.boing.service.DiaryService.DiaryUpdate;
import com.example.boing.service.DiaryService.NewDiary;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/diary")
public class DiaryController
    extends MutableController<DiaryService, Diary, NewDiary, DiaryUpdate> {}
