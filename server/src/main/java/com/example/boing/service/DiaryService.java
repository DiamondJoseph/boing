package com.example.boing.service;

import com.example.boing.domain.Diary;
import com.example.boing.service.generic.MutableService;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiaryService
    extends MutableService<Diary, DiaryService.NewDiary, DiaryService.DiaryUpdate> {

  @Autowired MemoryService mService;

  @Override
  protected Diary create(NewDiary diary) {
    var memories = diary.initialMemories.stream().map(mService::getOrThrow).toList();
    return new Diary(diary.description, memories);
  }

  @Override
  protected Diary update(Diary diary, DiaryUpdate update) {
    if (update.lost != null) {
      diary.setLost(update.lost);
    }
    if (update.addedMemories != null) {
      var memories = update.addedMemories.stream().map(mService::getOrThrow).toList();
      for (var memory : memories) {
        diary.addMemory(memory);
      }
    }
    return diary;
  }

  public record NewDiary(
      @JsonProperty(required = true) String description,
      @JsonProperty(required = true) List<Long> initialMemories) {}

  public record DiaryUpdate(@JsonProperty Boolean lost, @JsonProperty List<Long> addedMemories) {}
}
