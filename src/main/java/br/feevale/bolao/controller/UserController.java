package br.feevale.bolao.controller;

import br.feevale.bolao.model.User;
import br.feevale.bolao.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService service;

//    @ResponseBody
//    @RequestMapping(method = RequestMethod.GET)
//    public List<User> listAll() {
//        return service.findAll();
//    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/{userId}")
    public User getUser(@PathVariable("userId") Long userId) {
        return service.findById(userId);
    }

//    @ResponseBody
//    @RequestMapping(method = RequestMethod.GET, value = "/{userId}")
//    public Resource<User> getUser(@PathVariable("userId") Long userId) {
////        return service.findById(userId);
//        throw new CustomExceptionHandler(200, "teste", null);
//    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    public User save(@RequestBody User user) {
        try {
            return service.save(user);
        } catch (Exception e) {
            return null;
        }
    }

    @CrossOrigin
    @ResponseBody
    @PostMapping(value = "/login")
    public User login(@RequestBody User user) {
        return service.findByEmailAndPassword(user.getEmail(), user.getPassword());
    }

}
