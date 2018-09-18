package br.feevale.bolao.repository;

import br.feevale.bolao.model.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    @Query(value = "select count(*) = 1 from auth where token = ?1 and expiration <= unix_timestamp()", nativeQuery = true)
    boolean isTokenValid(String token);
}
