package in.tech_camp.chat_app.entity;

import java.util.List;

import lombok.Data;

@Data
public class RoomEntity {
  private int id;
  private String name;
  private List<RoomUsersEntity> roomUsers;
}
