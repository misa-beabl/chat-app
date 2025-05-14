package in.tech_camp.chat_app.form;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BindingResult;

import in.tech_camp.chat_app.factories.UserFormFactory;
import in.tech_camp.chat_app.validation.ValidationPriority1;
import in.tech_camp.chat_app.validation.ValidationPriority2;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;


@ActiveProfiles("test")
@SpringBootTest
public class UserFormUnitTest {
  private UserForm userForm;

  private Validator validator;

  private BindingResult bindingResult;

  @BeforeEach
  public void setUp() {
    userForm = UserFormFactory.createUser();

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();

    bindingResult = Mockito.mock(BindingResult.class);
  }

@Nested
  class ユーザーを作成できる場合 {
    @Test
    public void nameとemailとpasswordとpasswordconfirmationが存在すれば登録できる () {
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority1.class);
      assertEquals(0, violations.size());
    }
  }

  @Nested
  class ユーザーを作成できない場合 {
    @Test
    public void nameが空では登録できない () {
      userForm.setNickname("");
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority1.class);
      assertEquals(1, violations.size());
      assertEquals("Nickname can't be blank", violations.iterator().next().getMessage());
    }

    @Test
    public void emailが空では登録できない () {
      userForm.setEmail("");
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority1.class);
      assertEquals(1, violations.size());
      assertEquals("Email can't be blank", violations.iterator().next().getMessage());
    }

    @Test
    public void passwordが空では登録できない() {
      userForm.setPassword("");
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority1.class);
      assertEquals(1, violations.size());
      assertEquals("Password can't be blank", violations.iterator().next().getMessage());
    }

     @Test
    public void emailはアットマークを含まないと登録できない() {
      userForm.setEmail("noatmark");
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority2.class);
      assertEquals(1, violations.size());
      assertEquals("Email should be valid", violations.iterator().next().getMessage());
    }

        @Test
    public void passwordが5文字以下では登録できない() {
      userForm.setPassword("a".repeat(5));
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority2.class);
      assertEquals(1, violations.size());
      assertEquals("Password should be between 6 and 128 characters", violations.iterator().next().getMessage());
    }

    @Test
    public void passwordが129文字以上では登録できない() {
      userForm.setPassword("a".repeat(129));
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority2.class);
      assertEquals(1, violations.size());
      assertEquals("Password should be between 6 and 128 characters", violations.iterator().next().getMessage());
    }

    @Test
    public void passwordとpasswordConfirmationが不一致では登録できない() {
      userForm.setPasswordConfirmation("DifferentPW");
      userForm.validatePasswordConfirmation(bindingResult);
      verify(bindingResult).rejectValue("passwordConfirmation", "error.user", "Password confirmation doesn't match password"); 
    }
  }
}
