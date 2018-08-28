package br.feevale.bolao.controller;

import br.feevale.bolao.model.User;
import br.feevale.bolao.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService service;

    @ResponseBody
    @RequestMapping
    public List<User> listAll() {
        return service.listAll();
    }

}
