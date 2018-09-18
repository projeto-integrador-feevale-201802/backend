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
        service.save(user);
    }

    @ResponseBody
    @PostMapping(value = "/login")
    public User login(@RequestBody User user) {
        return service.findByEmailAndPassword(user.getEmail(), user.getPassword());
    }

}
