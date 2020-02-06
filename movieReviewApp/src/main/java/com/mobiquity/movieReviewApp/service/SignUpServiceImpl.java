package com.mobiquity.movieReviewApp.service;

import com.mobiquity.movieReviewApp.model.ResetPassword;
import com.mobiquity.movieReviewApp.model.UserProfile;
import com.mobiquity.movieReviewApp.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class SignUpServiceImpl implements SignUpService {

  private static final int EXPIRATION = 60 * 24 * 60 * 1000;
  private static final int FIXEDRATE = 60 * 60 * 24 * 7 * 1000;
  private JavaMailSender javaMailSender;

  private UserRepository userRepository;
  private Claims claim;

  public SignUpServiceImpl(UserRepository userRepository, JavaMailSender javaMailSender) {
    this.userRepository = userRepository;
    this.javaMailSender = javaMailSender;
  }

  @Override
  public String saveUser(UserProfile userProfile) {

    userProfile.setPassword(BCrypt.hashpw(userProfile.getPassword(), BCrypt.gensalt()));
    UserProfile user = userRepository.save(userProfile);
    sendActivationLink(user.getEmailId(), user.getUserId());

    return "Activate your link";
  }

  @Transactional
  @Override
  public String resetPassword(ResetPassword resetPassword) {
    String password = userRepository.findPasswordByEmailId(resetPassword.getEmailId());

    if (BCrypt.checkpw(resetPassword.getOldPassword(), password)) {
      updateHashedPassword(resetPassword);
      return "Password Updated";
    } else {
      return "OldPassword is Not Matching";
    }
  }

  private void updateHashedPassword(ResetPassword resetPassword) {
    String hashedPassword = BCrypt.hashpw(resetPassword.getNewPassword(), BCrypt.gensalt());
    System.out.println(hashedPassword);
    userRepository
        .updatePassword(resetPassword.getEmailId(), hashedPassword);
  }

  @Override
  public String passwordActivationLink(String emailId) {
    sendActivationLink(emailId, 0);
    return "Password Reset link sent to your email";
  }

  @Override
  @Transactional
  public String UpdatePassword(ResetPassword resetPassword) {
    updateHashedPassword(resetPassword);
    return "New Password is Updated";
  }

  @Transactional
  @Override
  public String registerAccount(String token) {
    try {
      claim = retrieveDataFromClaim(token);
      long id = Long.parseLong(claim.getSubject().split(" ")[1]);
      userRepository.updateStatus(id);
      return "You are Registered Successfully";
    } catch (ExpiredJwtException e) {
      /*userRepository
          .deleteByUserIdAndStatus(Long.parseLong(claim.getSubject().split(" ")[1]), false);*/
      return "Your activation link got expired";
    }

  }

  private Claims retrieveDataFromClaim(String token) {
    return Jwts.parser().setSigningKey(System.getenv("secret")).parseClaimsJws(token).getBody();
  }


  private void sendActivationLink(String emailId, long userId) {
    SimpleMailMessage email = new SimpleMailMessage();
    email.setTo(emailId);
    email.setSubject("activation link");
    email.setText(
        "activation link valid for 24 hrs" + "\n" + " http://localhost:8080/v1/activatelink?token="
            + generateJwtToken(emailId, userId));
    javaMailSender.send(email);
  }


  private String generateJwtToken(String emailId, long userId) {
    Map<String, Object> claims = new HashMap<>();
    return Jwts.builder().setClaims(claims)
        .setSubject(emailId + " " + userId)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION)).signWith(
            SignatureAlgorithm.HS512, System.getenv("secret")).compact();
  }


  @Scheduled(fixedRate = FIXEDRATE)
  @Transactional
  public void setScheduler() {
    userRepository.deleteByCreatedOnAndStatus(LocalDateTime.now().minusDays(1));
  }

}
