package com.example.boing.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;

@Entity
public class Vampire {

  @Id @GeneratedValue private Long id;
  @Column private String name;
  @Column private String intialDescription;
  @OneToMany private List<Person> people;
  @OneToMany private List<Resource> resources;
  @OneToMany private List<Skill> skills;

  public Vampire(
      String name,
      String intialDescription,
      List<Person> people,
      List<Skill> skills,
      List<Resource> resources) {
    if (skills.size() != 3) {
      throw new IllegalArgumentException("Vampire should start with 3 skills");
    }
    if (resources.size() != 3) {
      throw new IllegalArgumentException("Vampire should start with 3 resources");
    }
    if (people.size() < 3) {
      throw new IllegalArgumentException("Vampire should start with at least 3 people");
    }
    this.name = name;
    this.intialDescription = intialDescription;
    this.skills = skills;
    this.resources = resources;
    this.people = people;
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
