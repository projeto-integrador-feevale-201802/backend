package br.feevale.bolao.service;

import br.feevale.bolao.model.Auth;
import br.feevale.bolao.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class AuthService {
    @Autowired
    AuthRepository repository;

    public boolean isAuthorized(String token) {
        return repository.isTokenValid(token);
    }

    public String authorize(long idUser) {
        try {
            repository.deleteById(idUser);
        } catch (EmptyResultDataAccessException ex) {
            // ignore!
        }

        Auth auth = new Auth();
        auth.setIdUser(idUser);
        auth.setToken(UUID.randomUUID().toString());
        auth.setExpiration(Instant.now().getEpochSecond() + 3600);

        repository.save(auth);

        return auth.getToken();
    }

    public void removeAuth(String token) {
        Auth auth = new Auth();

        auth.setToken(token);

        try {
            repository.delete(auth);
        } catch (EmptyResultDataAccessException ex) {
            // ignore!
        }
    }
}