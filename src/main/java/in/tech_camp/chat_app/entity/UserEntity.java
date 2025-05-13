package in.tech_camp.chat_app.entity;

import java.util.List;

import lombok.Data;

@Data
public class UserEntity {
  private int id;
  private String nickname;
  private String email;
  private String password;
  private List<RoomUsersEntity> roomUsers;
  private List<MessageEntity> messages;
}


