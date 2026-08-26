package com.example.boing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BoingApplication {

  public static void main(String[] args) {
    if (args.length == 1 && args[0].equals("--version")) {
      System.out.println(BoingApplication.class.getPackage().getImplementationVersion());
      return;
    }
    SpringApplication.run(BoingApplication.class, args);
  }
}
