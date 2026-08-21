package com.example.boing.repository;

import com.example.boing.domain.Vampire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VampireRepository extends JpaRepository<Vampire, Long> {}
