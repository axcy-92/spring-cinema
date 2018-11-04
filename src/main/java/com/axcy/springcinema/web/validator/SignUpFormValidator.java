package com.axcy.springcinema.web.validator;

import com.axcy.springcinema.entity.user.UserDto;
import com.axcy.springcinema.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Aleksei_Cherniavskii
 */
@Component
public class SignUpFormValidator {

    @Autowired
    private UserService userService;

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$";


    public void validate(UserDto dto, List<String> errors) {

        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            errors.add("Name is required!");
        } else if (!validateUserNameExists(dto.getName())) {
            errors.add("User with this name already exists!");
        }

        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            errors.add("Email is required!");
        } else if (!validateEmail(dto.getEmail())) {
            errors.add("Email is invalid!");
        } else  if (!validateUserEmailExists(dto.getEmail())) {
            errors.add("User with this email already exists!");
        }

        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            errors.add("Password is required!");
        } else if (!validatePassword(dto.getPassword(), dto.getConfirmPassword())) {
            errors.add("Password must match!");
        }

    }

    private boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean validatePassword(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    private boolean validateUserNameExists(String name) {
        return userService.getUserByName(name) == null;
    }
    private boolean validateUserEmailExists(String email) {
        return userService.getUserByEmail(email) == null;
    }

}