package in.tech_camp.chat_app.entity;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class MessageEntity {
  private int id;
  private String content;
  private String image;
  private Timestamp createdAt;
  private UserEntity user;
  private RoomEntity room;
}
