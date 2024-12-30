package com.example.carrie.utils.validations;

public class EmailValidator {

  public static boolean isValidEmail(String email) {
    final String REGEX_EMAIL = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    return email.matches(REGEX_EMAIL);
  }

}
