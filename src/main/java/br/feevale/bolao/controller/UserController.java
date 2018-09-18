package br.feevale.bolao.controller;

import br.feevale.bolao.model.Auth;
import br.feevale.bolao.model.User;
import br.feevale.bolao.service.AuthService;
import br.feevale.bolao.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

//    @ResponseBody
//    @RequestMapping(method = RequestMethod.GET)
//    public List<User> listAll() {
//        return userService.findAll();
//    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/{userId}")
    public User getUser(@PathVariable("userId") Long userId) {
        return userService.findById(userId);
    }

//    @ResponseBody
//    @RequestMapping(method = RequestMethod.GET, value = "/{userId}")
//    public Resource<User> getUser(@PathVariable("userId") Long userId) {
//        List<String> erros = new ArrayList<>();

//        erros.add("email envalido");
//        erros.add("senha errada");
//        throw new CustomException(erros);

//        throw new CustomException("exemplo de erro");

//        User userNull = null;
//        userNull.getEmail(); // Null Pointer Exception Test Handler
//        return null;
//    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    public void save(@RequestBody User user) {
        userService.save(user);
    }

    @ResponseBody
    @PostMapping(value = "/login")
    public Object login(@RequestBody User user) {
        user = userService.findByEmailAndPassword(user.getEmail(), user.getPassword());

        if (user == null) {
            throw new RuntimeException("E-mail ou senha inválidos");
        }

        String token = authService.authorize(user.getId());

        HashMap<String, String> json = new HashMap<>();

        json.put("token", token);

        return json;
    }

    @ResponseBody
    @PostMapping(value = "/logout")
    public Object logout(@RequestBody Auth auth) {
        authService.removeAuth(auth.getToken());
        return new Object();
    }

}
