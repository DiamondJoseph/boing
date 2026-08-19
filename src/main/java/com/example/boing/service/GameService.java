package com.example.boing.service;

import com.example.boing.domain.Game;
import com.example.boing.domain.generic.Persistable;
import com.example.boing.service.ExperienceService.NewExperience;
import com.example.boing.service.PersonService.NewPerson;
import com.example.boing.service.PersonService.PersonUpdate;
import com.example.boing.service.ResourceService.NewResource;
import com.example.boing.service.ResourceService.ResourceUpdate;
import com.example.boing.service.SkillService.NewSkill;
import com.example.boing.service.SkillService.SkillUpdate;
import com.example.boing.service.VampireService.NewVampire;
import com.example.boing.service.generic.MutableService;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GameService extends MutableService<Game, GameService.NewGame, GameService.GameUpdate> {

  @Autowired VampireService vService;
  @Autowired PersonService pService;
  @Autowired ResourceService rService;
  @Autowired SkillService sService;

  @Override
  protected Game create(NewGame game) {
    var vampire = vService.createAndSave(game.vampire);
    var people = game.people.stream().map(pService::createAndSave).toList();
    var resources = game.resources.stream().map(rService::createAndSave).toList();
    var skills = game.skills.stream().map(sService::createAndSave).toList();
    return new Game(vampire, people, resources, skills);
  }

  @Override
  protected Game update(Game game, GameUpdate update) {
    if (update.newPeople != null) {
      update.newPeople.stream().map(pService::createAndSave).forEach(game::addPerson);
    }
    if (update.peopleUpdates != null) {
      updateChildObjects(pService, game.getPeople(), update.peopleUpdates);
    }
    if (update.newResources != null) {
      update.newResources.stream().map(rService::createAndSave).forEach(game::addResource);
    }
    if (update.resourceUpdates != null) {
      updateChildObjects(rService, game.getResources(), update.resourceUpdates);
    }
    if (update.newSkills != null) {
      update.newSkills.stream().map(sService::createAndSave).forEach(game::addSkill);
    }
    if (update.skillUpdates != null) {
      updateChildObjects(sService, game.getSkills(), update.skillUpdates);
    }
    return game;
  }

  private <T extends Persistable, TNew, TUpdate> void updateChildObjects(
      MutableService<T, TNew, TUpdate> service,
      List<T> existing,
      List<AttachedId<TUpdate>> updates) {
    for (var entry : updates) {
      if (existing.stream().map(Persistable::getId).noneMatch(x -> entry.id == x)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Object is not part of this Game");
      }
      service.update(entry.id, entry.update);
    }
  }

  public record NewGame(
      @JsonProperty(required = true) NewVampire vampire,
      @JsonProperty(required = true) List<NewPerson> people,
      @JsonProperty(required = true) List<NewResource> resources,
      @JsonProperty(required = true) List<NewSkill> skills) {}
  ;

  record AttachedId<T>(
      @JsonProperty(required = true) long id, @JsonProperty(required = true) T update) {}
  ;

  public record GameUpdate(
      @JsonProperty(required = true) NewExperience newExperience,
      @JsonProperty(required = false) List<NewPerson> newPeople,
      @JsonProperty(required = false) List<AttachedId<PersonUpdate>> peopleUpdates,
      @JsonProperty(required = false) List<NewResource> newResources,
      @JsonProperty(required = false) List<AttachedId<ResourceUpdate>> resourceUpdates,
      @JsonProperty(required = false) List<NewSkill> newSkills,
      @JsonProperty(required = false) List<AttachedId<SkillUpdate>> skillUpdates) {}
}
