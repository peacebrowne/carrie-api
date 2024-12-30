package com.example.carrie.utils.validations;

public class UUIDValidator {

  public static boolean isValidUUID(String uuid) {
    String uuidRegex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    return uuid.matches(uuidRegex);
  }
}
