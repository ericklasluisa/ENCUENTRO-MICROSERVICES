package ec.edu.espe.ms_authentication.repository;

import ec.edu.espe.ms_authentication.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findAllValidIsFalseOrRevokedIsFalseByUsuario_IdUsuario(Long usuarioId);

    Token findByToken(String token);
}
