package br.feevale.bolao.service;

import br.feevale.bolao.exception.CustomException;
import br.feevale.bolao.model.User;
import br.feevale.bolao.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserService {

    private static String emailRegex = "[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}";
    private static String passwordRegex = "[^\\s]{6,10}";
    private static final String PASS_SALT = "u2cHHUAIEDYKkDjCj2FkKHFKo1EtDuiBFEEVALE";

    @Autowired
    UserRepository repository;

    public User findByEmailAndPassword(String email, String password) {
        User user = repository.findByEmailAndPassword(email, encryptPassword(password));

        if (user != null && user.isChangingPassword()) {
            throw new CustomException("Aguardando confirmação de senha. Verifique seu e-mail.");
        }

        return user;
    }

    public void create(User user) {
        if (user.getEmail() == null || !user.getEmail().matches(emailRegex)) {
            throw new CustomException("E-mail inválido");
        }

        if (repository.findByEmail(user.getEmail()) != null) {
            throw new CustomException("E-mail já cadastrado");
        }

        if (user.getName() == null || user.getName().trim().equals("")) {
            throw new CustomException("Nome inválido");
        }

        repository.save(user);

        startPasswordRecovery(user.getEmail());
    }

    public void update(User user) {
        User currentUser = repository.getOne(user.getId());

        if (currentUser == null) {
            throw new CustomException("Usuário inválido.");
        }

        if (!currentUser.getEmail().equals(user.getEmail())) {
            throw new CustomException("Não é permitido alterar o endereço de e-mail.");
        }

        if (user.getPassword() != null && !user.getPassword().trim().equals("")) {
            if (!user.getPassword().matches(passwordRegex)) {
                throw new CustomException("Senha inválida.");
            }

            if (!user.getConfpassword().equals(user.getPassword())) {
                throw new CustomException("Senha não conferem.");
            }

            user.setPassword(encryptPassword(user.getPassword()));
        } else {
            user.setPassword(currentUser.getPassword());
        }

        if (user.getName() == null || user.getName().trim().equals("")) {
            throw new CustomException("Nome inválido");
        }

        repository.save(user);
    }

    public void updatePassword(String password, String token) {
        User user = repository.findByToken(token);

        if (user == null) {
            throw new CustomException("Usuário não encontrado");
        }

        if (password == null || !password.matches(passwordRegex)) {
            throw new CustomException("Senha inválida");
        }

        user.setPassword(encryptPassword(password));
        user.setChangingPassword(false);
        user.setToken(null);

        repository.save(user);
    }

    public void startPasswordRecovery(String email) {
        User user = repository.findByEmail(email);

        if (user == null) {
            throw new CustomException("Usuário não encontrado");
        }

        String token = UUID.randomUUID().toString();

        user.setToken(token);
        user.setChangingPassword(true);

        repository.save(user);

        // TODO disparar e-mail com o token
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findById(Long userId) {
        User user = repository.findById(userId).orElse(null);
        if (user != null) {
            user.setPassword(null);
            return user;
        }
        return user;
    }

    private String encryptPassword(String password) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), PASS_SALT.getBytes(StandardCharsets.UTF_8), 256, 512);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            return Base64.getEncoder().encodeToString(factory.generateSecret(spec).getEncoded());
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new CustomException("Erro ao salvar.");
        }
    }

}
