package in.tech_camp.chat_app.entity;

import lombok.Data;

@Data
public class UserEntity {
  private int id;
  private String nickname;
  private String email;
  private String password;
}


