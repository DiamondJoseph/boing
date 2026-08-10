package com.example.boing.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;

@Entity
public class Vampire {

  @Id @GeneratedValue private Long id;
  private String name;
  private String intialDescription;
  @OneToMany private List<Skill> skills;
  @OneToMany private List<Resource> resources;

  public Vampire(
      String name,
      String intialDescription,
      List<Character> characters,
      List<Skill> skills,
      List<Resource> resources) {
    this.name = name;
    this.intialDescription = intialDescription;
    if (skills.size() != 3) {
      throw new IllegalArgumentException("Vampire should start with 3 skills");
    }
    this.skills = skills;
    if (resources.size() != 3) {
      throw new IllegalArgumentException("Vampire should start with 3 resources");
    }
    this.resources = resources;
  }

  @SuppressWarnings("unused")
  private Vampire() {
    // Default constructor for DB deserilisation
  }

  public String getName() {
    return name;
  }

  public String getIntialDescription() {
    return intialDescription;
  }

  public List<Skill> getSkills() {
    return skills;
  }

  public void addSkill(Skill skill) {
    skills.add(skill);
  }

  public List<Resource> getResources() {
    return resources;
  }

  public void addResource(Resource resource) {
    resources.add(resource);
  }
}
