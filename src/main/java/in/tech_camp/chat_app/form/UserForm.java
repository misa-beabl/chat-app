package in.tech_camp.chat_app.form;

import lombok.Data;

@Data
public class UserForm {
  private String nickname;
  private String email;
  private String password;
  private String passwordConfirmation;
}
