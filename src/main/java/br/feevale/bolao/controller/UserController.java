package br.feevale.bolao.controller;

import br.feevale.bolao.exception.CustomException;
import br.feevale.bolao.model.Auth;
import br.feevale.bolao.model.User;
import br.feevale.bolao.service.AuthService;
import br.feevale.bolao.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public List<User> listAll() {
        return userService.findAll();
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/{token}")
    public User getUser(@PathVariable("token") String token) {
        Long userId = authService.getAuthorizedUserId(token);
        if (userId != null) {
            return userService.findById(userId);
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    public void save(@RequestBody User user) {
        if (user.getId() == null) {
            userService.create(user);
        } else {
            userService.update(user);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/change-password")
    public void changePassword(@RequestBody ChangePasswordDTO body) {
        userService.updatePassword(body.getPassword(), body.getToken());
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/start-password-recovery")
    public void startPasswordRecovery(@RequestBody User body) {
        userService.startPasswordRecovery(body.getEmail());
    }

    @ResponseBody
    @PostMapping(value = "/login")
    public Object login(@RequestBody User user) {
        user = userService.findByEmailAndPassword(user.getEmail(), user.getPassword());

        if (user == null) {
            throw new CustomException("E-mail ou senha inválidos");
        }

        String token = authService.authorize(user.getId());

        HashMap<String, String> json = new HashMap<>();

        json.put("token", token);

        return json;
    }

    @ResponseBody
    @PostMapping(value = "/logout")
    public void logout(@RequestBody Auth auth) {
        authService.removeAuth(auth.getToken());
    }

    private class ChangePasswordDTO {
        private String token;
        private String password;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

}
