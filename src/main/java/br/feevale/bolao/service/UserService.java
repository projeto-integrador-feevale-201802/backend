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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserService {

    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private static final int PASS_MIN_LENGTH = 6;

    private static final int PASS_MAX_LENGTH = 10;

    private static final String PASS_SALT = "u2cHHUAIEDYKkDjCj2FkKHFKo1EtDuiBFEEVALE";

    @Autowired
    UserRepository repository;

    public User findByEmailAndPassword(String email, String password) {
        return repository.findByEmailAndPassword(email, encryptPassword(password));
    }

    public void save(User user) {
        List<String> errors = validateUser(user);
        if (errors.isEmpty()) {
            validateEmail(user.getEmail());
            repository.save(validatePassword(user));
        } else {
            throw new CustomException(errors);
        }
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

    private void validateEmail(String email) {
        Matcher matcher = EMAIL_REGEX.matcher(email);
        if (!matcher.find()) {
            throw new CustomException("E-Mail inválido.");
        }
    }

    private User validatePassword(User user) {
        if (user.getPassword() != null && user.getPassword().length() <= PASS_MAX_LENGTH && user.getPassword().length() >= PASS_MIN_LENGTH) {
            user.setPassword(encryptPassword(user.getPassword()));
            return user;
        }

        throw new CustomException(String.format("Senha deve possuir entre %s e %s caracteres.", PASS_MIN_LENGTH, PASS_MAX_LENGTH));
    }

    private String encryptPassword(String password) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), PASS_SALT.getBytes(StandardCharsets.UTF_8), 256, 512);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            return new String(factory.generateSecret(spec).getEncoded(), StandardCharsets.UTF_8);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new CustomException("Erro ao salvar.");
        }
    }

}
