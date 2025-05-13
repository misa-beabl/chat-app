package in.tech_camp.chat_app.entity;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class MessageEntity {
  private int id;
  private Long content;
  private Long image;
  private Timestamp createdAt;
  private UserEntity user;
  private RoomEntity room;
}
