package com.chinasofti.oauth2.asserver.web.controller;

import com.chinasofti.oauth2.asserver.entity.User;
import com.chinasofti.oauth2.asserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("userList", userService.findAll());
        return "user/list";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("op", "新增");
        return "user/edit";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(User user, RedirectAttributes redirectAttributes) {
        userService.createUser(user);
        redirectAttributes.addFlashAttribute("msg", "新增成功");
        return "redirect:/user";
    }

    @RequestMapping(value = "/{userId}/update", method = RequestMethod.GET)
    public String showUpdateForm(@PathVariable("userId") String userId, Model model) {
        model.addAttribute("user", userService.findOne(userId));
        model.addAttribute("op", "修改");
        return "user/edit";
    }

    @RequestMapping(value = "/{userId}/update", method = RequestMethod.POST)
    public String update(User user, RedirectAttributes redirectAttributes) {
        userService.updateUser(user);
        redirectAttributes.addFlashAttribute("msg", "修改成功");
        return "redirect:/user";
    }

    @RequestMapping(value = "/{userId}/delete", method = RequestMethod.GET)
    public String showDeleteForm(@PathVariable("userId") String userId, Model model) {
        model.addAttribute("user", userService.findOne(userId));
        model.addAttribute("op", "删除");
        return "user/edit";
    }

    @RequestMapping(value = "/{userId}/delete", method = RequestMethod.POST)
    public String delete(@PathVariable("userId") String userId, RedirectAttributes redirectAttributes) {
        userService.deleteUser(userId);
        redirectAttributes.addFlashAttribute("msg", "删除成功");
        return "redirect:/user";
    }


    @RequestMapping(value = "/{userId}/changePassword", method = RequestMethod.GET)
    public String showChangePasswordForm(@PathVariable("userId") String userId, Model model) {
        model.addAttribute("user", userService.findOne(userId));
        model.addAttribute("op", "修改密码");
        return "user/changePassword";
    }

    @RequestMapping(value = "/{userId}/changePassword", method = RequestMethod.POST)
    public String changePassword(@PathVariable("userId") String userId, String newPassword, RedirectAttributes redirectAttributes) {
        userService.changePassword(userId, newPassword);
        redirectAttributes.addFlashAttribute("msg", "修改密码成功");
        return "redirect:/user";
    }

}
