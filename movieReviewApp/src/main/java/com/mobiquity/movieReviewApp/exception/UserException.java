package com.mobiquity.movieReviewApp.exception;

import java.sql.SQLException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter

public class UserException extends RuntimeException {

  public UserException(String message) {
   super(message);
  }

}
