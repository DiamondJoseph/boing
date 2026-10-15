package com.example.boing.controller.generic;

import io.swagger.v3.oas.models.Operation;
import java.util.Map;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

/**
 * Names the operations of the generic controllers after the entity they serve, for example {@code
 * createGame}, {@code listPeople} and {@code updateMemory}. Without this, springdoc numbers them
 * ({@code create_7}), and adding a controller can renumber all of them.
 *
 * <p>Operations on other controllers keep the ID from their {@code @Operation} annotation.
 */
@Component
public class OperationIdCustomizer implements OperationCustomizer {

  private static final String CONTROLLER_SUFFIX = "Controller";

  /** Plurals that are not formed by adding "s". */
  private static final Map<String, String> IRREGULAR_PLURALS = Map.of("Person", "People");

  @Override
  public Operation customize(Operation operation, HandlerMethod handlerMethod) {
    var declaringClass = handlerMethod.getMethod().getDeclaringClass();
    if (declaringClass != ImmutableController.class && declaringClass != MutableController.class) {
      return operation;
    }
    var entity = entityName(handlerMethod.getBeanType());
    var operationId =
        switch (handlerMethod.getMethod().getName()) {
          case "create" -> "create" + entity;
          case "getAll" -> "list" + plural(entity);
          case "getById" -> "get" + entity;
          case "update" -> "update" + entity;
          case "delete" -> "delete" + entity;
          default ->
              throw new IllegalStateException(
                  "No operation ID for " + handlerMethod.getMethod().getName());
        };
    return operation.operationId(operationId);
  }

  static String entityName(Class<?> controller) {
    var name = controller.getSimpleName();
    if (!name.endsWith(CONTROLLER_SUFFIX)) {
      throw new IllegalStateException(controller + " should be named <Entity>Controller");
    }
    return name.substring(0, name.length() - CONTROLLER_SUFFIX.length());
  }

  static String plural(String entity) {
    if (IRREGULAR_PLURALS.containsKey(entity)) {
      return IRREGULAR_PLURALS.get(entity);
    }
    if (entity.endsWith("y")) {
      return entity.substring(0, entity.length() - 1) + "ies";
    }
    return entity + "s";
  }
}
