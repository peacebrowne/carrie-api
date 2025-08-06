package com.example.carrie.utils.validations;


public class UUIDValidator {

    private static String uuid;
    
    /**
   * @param uuid - The ID to be validated
   * @return BOOLEAN
   */
    public static boolean isValidUUID(String uuid) {
        UUIDValidator.uuid = uuid;
        String uuidRegex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    return uuid.matches(uuidRegex);
  }
}
