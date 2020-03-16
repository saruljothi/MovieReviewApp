package com.mobiquity.movieReviewApp.domain.accountmanagement.controller;

import com.mobiquity.movieReviewApp.domain.accountmanagement.model.PasswordReset;
import com.mobiquity.movieReviewApp.domain.accountmanagement.model.PasswordUpdate;
import com.mobiquity.movieReviewApp.domain.accountmanagement.model.ResponseMovieApp;
import com.mobiquity.movieReviewApp.domain.accountmanagement.service.PasswordManagementService;
import java.util.Collections;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class PasswordController {

  private PasswordManagementService passwordManagementService;

  public PasswordController(PasswordManagementService passwordManagementService) {
    this.passwordManagementService = passwordManagementService;
  }

  @GetMapping("/forgotPassword/reset")
  public ResponseEntity<Object> forgotPassword(@RequestParam String emailId) {
    return new ResponseEntity<>(
        new ResponseMovieApp(
            Collections.singletonList(passwordManagementService.forgotPasswordLink(emailId))),
        HttpStatus.OK);
  }

  @PutMapping("/forgotPassword/newPassword")
  public ResponseEntity<Object> setNewPassword(@RequestBody PasswordReset passwordAndToken) {
    return new ResponseEntity<>(
        new ResponseMovieApp(
            Collections.singletonList(
                passwordManagementService.updateForgottenPasswordWithNewPassword(passwordAndToken))
        ), HttpStatus.OK);
  }

  @PutMapping("/changePassword")
  public ResponseEntity<Object> updatePassword(
      @RequestBody PasswordUpdate passwordChangeAndAccount) {
    return new ResponseEntity<>(
        new ResponseMovieApp(
            Collections
                .singletonList(passwordManagementService.updatePassword(passwordChangeAndAccount))
        ), HttpStatus.OK);
  }
}
