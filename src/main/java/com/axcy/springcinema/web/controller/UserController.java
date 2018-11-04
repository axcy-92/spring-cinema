package com.axcy.springcinema.web.controller;

import com.axcy.springcinema.entity.Role;
import com.axcy.springcinema.entity.user.CustomUserDetails;
import com.axcy.springcinema.entity.user.User;
import com.axcy.springcinema.entity.user.UserAccount;
import com.axcy.springcinema.entity.user.UserDto;
import com.axcy.springcinema.service.Roles;
import com.axcy.springcinema.service.UserAccountService;
import com.axcy.springcinema.service.UserService;
import com.axcy.springcinema.web.validator.SignUpFormValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Aleksei_Cherniavskii
 */

@Controller
@RequestMapping({"/user"})
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    @Autowired
    UserService userService;

    @Autowired
    UserAccountService userAccountService;

    @Autowired
    SignUpFormValidator signUpFormValidator;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @RequestMapping(value = "/sign-up", method = RequestMethod.GET)
    public String signUpUserForm(Model model) {
        model.addAttribute("userForm", new UserDto());
        return "user/sign-up";
    }

    @RequestMapping(value = "/sign-up", method = RequestMethod.POST)
    public String signUpUser(@ModelAttribute("userForm") UserDto userDto, RedirectAttributes redirectAttributes) {
        List<String> registrationError = new ArrayList<>();
        signUpFormValidator.validate(userDto, registrationError);
        if (!registrationError.isEmpty()) {
            redirectAttributes.addFlashAttribute("registrationError", registrationError);
            return "redirect:/user/sign-up";
        } else {
            User user = userService.create(userDto.getName(), userDto.getEmail(), userDto.getPassword(), userDto.getBirthday());
            userService.register(user);
            redirectAttributes.addFlashAttribute("registrationInfo", "You have successfully signed up");
            return "redirect:/login";
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String userInfoPage(@PathVariable("id") long id, ModelMap model) {
        User user = userService.getById(id);
        LOG.info("found user " + user);
        UserAccount userAccount = userAccountService.getAccountByUser(user);
        model.addAttribute("userForm", user);
        model.addAttribute("cash", userAccount.getCash());
        model.addAttribute("isManager", user.isManager());
        model.addAttribute("roleList", Roles.values());
        return "user/user-info";
    }

    @RequestMapping(value= "/update", method = RequestMethod.POST)
    public String userUpdate(@ModelAttribute("userForm") User user,
                             @RequestParam("role") String[] roles,
                             RedirectAttributes redirectAttributes) {
        User oldUser = userService.getById(user.getId());
        if (oldUser != null) {
            LOG.info("old user :" + oldUser);
            LOG.info("new user befora update roles: " + user);
            LOG.info("roles: " + Arrays.toString(roles));
            for (String roleString : roles) {
                user.addRole(new Role(Roles.valueOf(roleString).getDesc()));
            }
            LOG.info("new user: " + user);
            if (bCryptPasswordEncoder.matches(user.getPassword(), oldUser.getPassword())) {
                LOG.info("password correct");
                user = userService.register(user);
                redirectAttributes.addFlashAttribute("message", "Personal info has been updated");
            } else {
                redirectAttributes.addFlashAttribute("error", "Sorry, password is invalid");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Sorry, user not found");
        }
        return "redirect:/user/" + user.getId();
    }

    @PostAuthorize("hasRole('BOOKING_MANAGER')")
    @RequestMapping(value= "/delete/{id}", method = RequestMethod.GET)
    public String deleteUser(@PathVariable("id") long id) throws Exception {
        userService.remove(userService.getById(id));
        return "redirect:/user/all";
    }


    @PostAuthorize("hasRole('BOOKING_MANAGER')")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public String showAllEvents(ModelMap model) throws Exception {
        model.addAttribute("userList", userService.getAll());
        return "user/users";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/top-up-account/{id}", method = RequestMethod.GET)
    public String topUpAccount(@PathVariable("id") long id, ModelMap model) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetails.getId() == id) {
            UserAccount userAccount = userAccountService.getAccountByUser(userService.getById(id));
            model.addAttribute("userAccountId", userAccount.getId());
        } else {
            throw new AccessDeniedException("You do not have permission to access this page!");
        }

        return "/user/userAccount";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/top-up-account/{id}", method = RequestMethod.POST)
    public String topUpAccount(@PathVariable("id") long id,
                               @RequestParam("cash") float cash,
                               @RequestParam("account") long userAccountId,
                               RedirectAttributes redirectAttributes) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails.getId() == id) {
            userAccountService.topUpAnAccount(userAccountService.getById(userAccountId), cash);
            redirectAttributes.addFlashAttribute("message", "Your account is credited for the amount of " + cash + "$");
        } else {
            throw new AccessDeniedException("You do not have permission to access this page!");
        }
        return "redirect:/user/" + id;
    }
}
