package br.feevale.bolao.service;

import br.feevale.bolao.model.User;
import br.feevale.bolao.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserService {

    @Autowired
    UserRepository repository;

    public List<User> listAll() {
        User u = new User();
        u.setName("Fulano de tal B");
        List<User> list = new ArrayList<>();
        list.add(u);
        return repository.findAll();
//        return list;
    }

}
