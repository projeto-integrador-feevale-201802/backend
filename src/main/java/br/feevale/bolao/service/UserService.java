package br.feevale.bolao.service;

import br.feevale.bolao.model.User;
import br.feevale.bolao.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserService {

    @Autowired
    UserRepository repository;

    public User findByEmailAndPassword(String email, String password) {
        return repository.findByEmailAndPassword(email, password);
    }

    public User save(User user) {
        return repository.save(user);
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findById(Long userId) {
        return repository.findById(userId).orElse(null);
    }

}
