package br.feevale.bolao.service;

import br.feevale.bolao.exception.CustomException;
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

    public User findByEmailAndPassword(String email, String password) {
        return repository.findByEmailAndPassword(email, password);
    }

    public User save(User user) {
        List<String> errors = validateUser(user);
        if (errors.isEmpty()) {
            return repository.save(user);
        }
        throw new CustomException(errors);
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findById(Long userId) {
        return repository.findById(userId).orElse(null);
    }

    private List<String> validateUser(User user) {
        List<String> erros = new ArrayList<>();

        if (user.getEmail() == null || user.getEmail().trim() == "") {
            erros.add("E-mail não pode ser vazio.");
        }

        if (user.getPassword() == null || user.getPassword().trim() == "") {
            erros.add("Senha não pode ser vazia.");
        }

        if (user.getName() == null || user.getName().trim() == "") {
            erros.add("Nome não pode ser vazio.");
        }

        if (erros.isEmpty()) {
            User savedUser = repository.findByEmail(user.getEmail());
            if (savedUser != null) {
                erros.add("E-mail já cadastrado.");
            }
        }

        return erros;
    }
}
