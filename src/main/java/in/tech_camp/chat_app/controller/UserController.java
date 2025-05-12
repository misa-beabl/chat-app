package in.tech_camp.chat_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import in.tech_camp.chat_app.entity.UserEntity;
import in.tech_camp.chat_app.form.LoginForm;
import in.tech_camp.chat_app.form.UserEditForm;
import in.tech_camp.chat_app.form.UserForm;
import in.tech_camp.chat_app.repository.UserRepository;
import in.tech_camp.chat_app.service.UserService;
import lombok.AllArgsConstructor;






@Controller
@AllArgsConstructor
public class UserController {

private final UserRepository userRepository;

private final UserService userService;

  @GetMapping("/users/sign_up")
  public String showSignUp(Model model) {
    model.addAttribute("userForm", new UserForm());
    return "users/signUp";
  }

  @PostMapping("/user")
  public String createUser(@ModelAttribute("userForm") UserForm userForm, Model model) {
      UserEntity userEntity = new UserEntity();
      userEntity.setNickname(userForm.getNickname());
      userEntity.setEmail(userForm.getEmail());
      userEntity.setPassword(userForm.getPassword());

      try {
      userService.createUserWithEncryptedPassword(userEntity);
    } catch (Exception e) {
      System.out.println("エラー：" + e);
      model.addAttribute("userForm", userForm);
      return "users/signUp";
    }
      
      return "redirect:/";
  }

  @GetMapping("/users/login")
  public String showLogin(Model model) {
    model.addAttribute("loginForm", new LoginForm());
    return "users/login";
  }
  
   @GetMapping("/login")
  public String showLoginWithError(@RequestParam(value = "error", required = false) String error, @ModelAttribute("loginForm") LoginForm loginForm, Model model) {
    if (error != null) {
      model.addAttribute("loginError", "メールアドレスかパスワードが間違っています。");
    }
    return "users/login";
  }

  @GetMapping("/users/{userId}/edit")
  public String editUserForm(@PathVariable("userId") Integer userId, Model model) {
      UserEntity user = userRepository.findById(userId);

      System.out.println(userRepository.findById(userId));

      UserEditForm editForm = new UserEditForm();
      editForm.setId(user.getId());
      editForm.setName(user.getNickname());
      editForm.setEmail(user.getEmail());

      model.addAttribute("user", editForm);
      return "users/edit";
  }

  @PostMapping("/users/{userId}")
  public String updateUser(@ModelAttribute("user") UserEditForm userEditForm, @PathVariable("userId") Integer userId, Model model) {

      UserEntity user = userRepository.findById(userId);
      user.setNickname(userEditForm.getName());
      user.setEmail(userEditForm.getEmail());

      try {
        userRepository.updateUser(user);
      } catch (Exception e) {
        System.out.println("エラー：" + e);
        model.addAttribute("user", userEditForm);
        return "users/edit";
      }      
      
      return "redirect:/";
  }
  
}