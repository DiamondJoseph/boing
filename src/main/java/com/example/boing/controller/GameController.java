package com.example.boing.controller;

import com.example.boing.controller.generic.MutableController;
import com.example.boing.domain.Game;
import com.example.boing.service.GameService;
import com.example.boing.service.GameService.GameUpdate;
import com.example.boing.service.GameService.NewGame;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game")
public class GameController extends MutableController<GameService, Game, NewGame, GameUpdate> {}
