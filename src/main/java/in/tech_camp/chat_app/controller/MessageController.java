package in.tech_camp.chat_app.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import in.tech_camp.chat_app.ImageUrl;
import in.tech_camp.chat_app.custom_user.CustomUserDetail;
import in.tech_camp.chat_app.entity.MessageEntity;
import in.tech_camp.chat_app.entity.RoomEntity;
import in.tech_camp.chat_app.entity.RoomUsersEntity;
import in.tech_camp.chat_app.entity.UserEntity;
import in.tech_camp.chat_app.form.MessageForm;
import in.tech_camp.chat_app.repository.MessageRepository;
import in.tech_camp.chat_app.repository.RoomRepository;
import in.tech_camp.chat_app.repository.RoomUsersRepository;
import in.tech_camp.chat_app.repository.UserRepository;
import in.tech_camp.chat_app.validation.ValidationOrder;
import lombok.AllArgsConstructor;


@Controller
@AllArgsConstructor
public class MessageController {
  private final UserRepository userRepository;

  private final RoomUsersRepository roomUsersRepository;

  private final MessageRepository messageRepository;

  private final RoomRepository roomRepository;

  private final ImageUrl imageUrl;

  @GetMapping("/rooms/{roomId}/messages")
  public String showMessages(@PathVariable("roomId") Integer roomId, @AuthenticationPrincipal CustomUserDetail currentUser, Model model){
    UserEntity user = userRepository.findById(currentUser.getId());
    model.addAttribute("user", user);
    List<MessageEntity> message = messageRepository.showMessagesById(roomId);
    List<RoomUsersEntity> roomUserEntities = roomUsersRepository.showRoomsById(currentUser.getId());
    List<RoomEntity> roomList = roomUserEntities.stream()
        .map(RoomUsersEntity::getRoom)
        .collect(Collectors.toList());

    model.addAttribute("rooms", roomList);
    model.addAttribute("messageForm", new MessageForm());

    RoomEntity room = roomRepository.findRoomsById(roomId);
    model.addAttribute("room", room);

    model.addAttribute("message", message);
      return "messages/index";
  } 

  @PostMapping("/rooms/{roomId}/messages")
  public String saveMessages(@PathVariable("roomId") Integer roomId, @ModelAttribute("messageForm") @Validated(ValidationOrder.class) MessageForm messageForm, BindingResult result, @AuthenticationPrincipal CustomUserDetail currentUser, Model model) {
    if (result.hasErrors()) {
      return "redirect:/rooms/" + roomId + "/messages";
    }

    MessageEntity messageEntity = new MessageEntity();
    messageEntity.setContent(messageForm.getContent());

    MultipartFile imageFile = messageForm.getImage();
    if (imageFile != null && !imageFile.isEmpty()) {
      try {
        String uploadDir = imageUrl.getImageUrl();
        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "_" + imageFile.getOriginalFilename();
        Path imagePath = Paths.get(uploadDir, fileName);
        Files.copy(imageFile.getInputStream(), imagePath);
        messageEntity.setImage("/uploads/" + fileName);
      } catch (IOException e) {
        System.out.println("エラー：" + e);
        return "redirect:/rooms/" + roomId + "/messages";
      }
    }

    UserEntity user = userRepository.findById(currentUser.getId());
    RoomEntity room = roomRepository.findRoomsById(roomId);
    messageEntity.setUser(user);
    messageEntity.setRoom(room);

    try {
        messageRepository.insert(messageEntity);
      } catch (Exception e) {
        System.out.println("エラー：" + e);
      }
        return "redirect:/rooms/" + roomId + "/messages";
  }

  @PostMapping("/rooms/{roomId}/delete")
  public String deleteRoom(@PathVariable("roomId") Integer roomId) {
      messageRepository.deleteById(roomId);
      return "redirect:/";
  }
}