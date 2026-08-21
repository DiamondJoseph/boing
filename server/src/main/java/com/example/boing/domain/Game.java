package com.example.boing.domain;

import com.example.boing.domain.generic.Persistable;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Entity
public class Game extends Persistable {

  @OneToOne private Vampire vampire;
  @OneToMany private List<Person> people;
  @OneToMany private List<Resource> resources;
  @OneToMany private List<Skill> skills;
  @OneToMany private List<Memory> memories;
  @OneToMany private List<Diary> diaries;
  @Lob private Random random;

  public Game(Vampire vampire, List<Person> people, List<Resource> resources, List<Skill> skills) {
    if (skills.size() != 3) {
      throw new IllegalArgumentException("Should start with 3 skills");
    }
    if (resources.size() != 3) {
      throw new IllegalArgumentException("Should start with 3 resources");
    }
    if (people.size() < 3) {
      throw new IllegalArgumentException("Should start with at least 3 people");
    }
    this.vampire = vampire;
    this.people = people;
    this.resources = resources;
    this.skills = skills;
    this.memories = new ArrayList<>();
    this.random = new Random(System.currentTimeMillis());
  }

  @SuppressWarnings("unused")
  private Game() {}

  public Vampire getVampire() {
    return vampire;
  }

  public List<Person> getPeople() {
    return people;
  }

  public void addPerson(Person person) {
    this.people.add(person);
  }

  public List<Resource> getResources() {
    return resources;
  }

  public void addResource(Resource resource) {
    this.resources.add(resource);
  }

  public List<Skill> getSkills() {
    return skills;
  }

  public void addSkill(Skill skill) {
    this.skills.add(skill);
  }

  public List<Memory> getMemories() {
    return memories;
  }

  public void addMemory(Memory memory) {
    this.memories.add(memory);
  }

  public List<Diary> getDiaries() {
    return diaries;
  }

  public void addDiary(Diary diary) {
    this.diaries.add(diary);
  }
}
