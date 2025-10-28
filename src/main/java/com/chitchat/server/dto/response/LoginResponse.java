package com.chitchat.server.dto.response;

import com.chitchat.server.entity.Role;
import com.chitchat.server.enums.Gender;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

@Data
public class LoginResponse {
     @JsonProperty("access_token")
     private String access_token;
     private UserLogin user;

     @Data
     @AllArgsConstructor
     @NoArgsConstructor
     public static class UserLogin {

          private String id;
          private String email;
          private String username;
          private String avatarUrl;
          private String firstName;
          private String lastName;
          private String location;
          private String bio;
          private String job;
          private Set<Role> authorities;
          private String phone;

          private LocalDate dob;
          private Instant createdAt;
          private Instant updatedAt;
          
          private Gender gender;

          private int friendNum;
          private Long mutualFriendsNum;
          
          public UserLogin(String id, String email, String username, Set<Role> authorities) {
               this.id = id;
               this.email = email;
               this.username = username;
               this.authorities = authorities;
          }
          
          public UserLogin(String id, String email, String username, String avatarUrl, String firstName, String lastName,
                String location, String bio, Set<Role> authorities) {
            this.id = id;
            this.email = email;
            this.username = username;
            this.avatarUrl = avatarUrl;
            this.firstName = firstName;
            this.lastName = lastName;
            this.location = location;
            this.bio = bio;
            this.authorities = authorities;
          }
     }

     @Data
     @AllArgsConstructor
     @NoArgsConstructor
     public static class UserGetAccount {
          private UserLogin user;
     }

     @Data
     @AllArgsConstructor
     @NoArgsConstructor
     public static class UserInsideToken {
          private String id;
          private String email;
          private String username;
          private String location;
     }
}
