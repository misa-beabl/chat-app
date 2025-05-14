package in.tech_camp.chat_app.system;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import in.tech_camp.chat_app.ChatAppApplication;
import in.tech_camp.chat_app.entity.MessageEntity;
import in.tech_camp.chat_app.entity.RoomEntity;
import in.tech_camp.chat_app.entity.RoomUsersEntity;
import in.tech_camp.chat_app.entity.UserEntity;
import in.tech_camp.chat_app.factories.MessageFormFactory;
import in.tech_camp.chat_app.factories.RoomFormFactory;
import in.tech_camp.chat_app.factories.UserFormFactory;
import in.tech_camp.chat_app.form.MessageForm;
import in.tech_camp.chat_app.form.RoomForm;
import in.tech_camp.chat_app.form.UserForm;
import in.tech_camp.chat_app.repository.MessageRepository;
import in.tech_camp.chat_app.repository.RoomRepository;
import in.tech_camp.chat_app.repository.RoomUsersRepository;
import in.tech_camp.chat_app.service.UserService;
import in.tech_camp.chat_app.support.LoginSupport;

@ActiveProfiles("test")
@SpringBootTest(classes = ChatAppApplication.class)
@AutoConfigureMockMvc
public class RoomIntegrationTest {
  private UserForm userForm1;
  private UserForm userForm2;
  private RoomForm roomForm;
  private MessageForm messageForm;

  private UserEntity userEntity1;
  private UserEntity userEntity2;
  private RoomEntity roomEntity;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserService userService;

  @Autowired
  private RoomRepository roomRepository;

  @Autowired
  private RoomUsersRepository roomUsersRepository;

  @Autowired
  private MessageRepository messageRepository;

  @BeforeEach
  public void setup() {
    userForm1 = UserFormFactory.createUser();
    userEntity1 = new UserEntity();
    userEntity1.setEmail(userForm1.getEmail());
    userEntity1.setNickname(userForm1.getNickname());
    userEntity1.setPassword(userForm1.getPassword());
    userService.createUserWithEncryptedPassword(userEntity1);

    userForm2 = UserFormFactory.createUser();
    userEntity2 = new UserEntity();
    userEntity2.setEmail(userForm2.getEmail());
    userEntity2.setNickname(userForm2.getNickname());
    userEntity2.setPassword(userForm2.getPassword());
    userService.createUserWithEncryptedPassword(userEntity2);

    roomForm = RoomFormFactory.createRoom();
    roomEntity = new RoomEntity();
    roomEntity.setName(roomForm.getName());
    roomRepository.insert(roomEntity);

    RoomUsersEntity roomUserEntity1 = new RoomUsersEntity();
    roomUserEntity1.setRoom(roomEntity);
    roomUserEntity1.setUser(userEntity1);
    roomUsersRepository.insert(roomUserEntity1);
    RoomUsersEntity roomUserEntity2 = new RoomUsersEntity();
    roomUserEntity2.setRoom(roomEntity);
    roomUserEntity2.setUser(userEntity2);
    roomUsersRepository.insert(roomUserEntity2);

    messageForm = MessageFormFactory.createMessage();
  }

  @Test
  public void チャットルームを削除すると関連するメッセージがすべて削除されている() throws Exception {
    // サインインする
    MockHttpSession session = LoginSupport.login(mockMvc, userForm1);

    // 作成したチャットルームへ遷移する
    mockMvc.perform(get("/rooms/{roomId}/messages", roomEntity.getId()).session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("messages/index"));

    // メッセージ情報を5つDBに追加する
    // messageForm = messageRepository.insert();
    
    for (int i = 0; i < 5; i++) {
      MessageEntity messageEntity = new MessageEntity();
      messageEntity.setContent(messageForm.getContent());
      messageEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
      messageEntity.setUser(userEntity1);
      messageEntity.setRoom(roomEntity);
      messageRepository.insert(messageEntity);
    }
    
    long initialCount = messageRepository.count();
    // チャットルームを削除するとトップページに遷移することを確認する
    mockMvc.perform(post("/rooms/{roomId}/delete", roomEntity.getId()).session(session).with(csrf()))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/"));

    // 作成した5つのメッセージが削除されていることを確認する
    long afterCount = messageRepository.count();
    assertEquals(initialCount - 5, afterCount);
  }
}
