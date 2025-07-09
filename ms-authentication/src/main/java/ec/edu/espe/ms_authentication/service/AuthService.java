package ec.edu.espe.ms_authentication.service;

import ec.edu.espe.ms_authentication.dto.LoginDto;
import ec.edu.espe.ms_authentication.dto.RegisterDto;
import ec.edu.espe.ms_authentication.dto.RespuestaDto;
import ec.edu.espe.ms_authentication.model.Asistente;
import ec.edu.espe.ms_authentication.model.Organizador;
import ec.edu.espe.ms_authentication.model.Token;
import ec.edu.espe.ms_authentication.model.Usuario;
import ec.edu.espe.ms_authentication.repository.AsistenteRepository;
import ec.edu.espe.ms_authentication.repository.OrganizadorRepository;
import ec.edu.espe.ms_authentication.repository.TokenRepository;
import ec.edu.espe.ms_authentication.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AsistenteRepository asistenteRepository;

    @Autowired
    private OrganizadorRepository organizadorRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private NotificacionProducer notificacionProducer;

    private static Organizador getOrganizador(RegisterDto registerDto, String contrasenaEncriptada) {
        Organizador organizador = new Organizador();
        organizador.setEmpresa(registerDto.getEmpresa());
        organizador.setRuc(registerDto.getRuc());
        organizador.setNombre(registerDto.getNombre());
        organizador.setApellido(registerDto.getApellido());
        organizador.setCorreo(registerDto.getCorreo());
        organizador.setFechaNacimiento(registerDto.getFechaNacimiento());

        organizador.setContrasena(contrasenaEncriptada);

        return organizador;
    }

    public RespuestaDto login(LoginDto loginDto) {

        System.out.println("Intentando autenticar al usuario: " + loginDto.getCorreo());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getCorreo(),
                        loginDto.getContrasena()
                )
        );

        Usuario usuario = usuarioRepository.findByCorreo(loginDto.getCorreo())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        String jwtToken = jwtService.generarToken(usuario);
        String refreshToken = jwtService.generarRefreshToken(usuario);

        revokeUserTokens(usuario);
        saveUserToken(usuario, jwtToken);

        return new RespuestaDto(
                "Usuario autenticado correctamente",
                jwtToken,
                refreshToken,
                usuario.getNombre(),
                usuario instanceof Organizador ? "ORGANIZADOR" : "ASISTENTE",
                usuario.getIdUsuario()
        );
    }

    public RespuestaDto register(RegisterDto registerDto) {

        try {

            String contrasenaEncriptada = passwordEncoder.encode(registerDto.getContrasena());

            String rol = registerDto.getRol();
            String mensaje;

            if (rol.equals("ORGANIZADOR")) {
                Organizador organizador = getOrganizador(registerDto, contrasenaEncriptada);

                Organizador organizadorBD = organizadorRepository.save(organizador);

                mensaje = "Organizador registrado correctamente";

                String jwtToken = jwtService.generarToken(organizador);
                String refreshToken = jwtService.generarRefreshToken(organizador);

                // Enviar notificación de registro
                notificacionProducer.enviarNotificacionRegistro(organizador.getNombre(), rol);

                return new RespuestaDto(mensaje, jwtToken, refreshToken, organizador.getNombre(), rol, organizadorBD.getIdUsuario());

            } else {
                Asistente asistente = new Asistente();
                asistente.setNombre(registerDto.getNombre());
                asistente.setApellido(registerDto.getApellido());
                asistente.setCorreo(registerDto.getCorreo());
                asistente.setFechaNacimiento(registerDto.getFechaNacimiento());

                asistente.setContrasena(contrasenaEncriptada);

                Asistente asistenteDB = asistenteRepository.save(asistente);

                mensaje = "Asistente registrado correctamente";

                String jwtToken = jwtService.generarToken(asistente);
                String refreshToken = jwtService.generarRefreshToken(asistente);

                saveUserToken(asistenteDB, jwtToken);

                // Enviar notificación de registro
                notificacionProducer.enviarNotificacionRegistro(asistente.getNombre(), rol);

                return new RespuestaDto(mensaje, jwtToken, refreshToken, asistente.getNombre(), rol, asistenteDB.getIdUsuario());

            }

        } catch (Exception e) {
            return new RespuestaDto(
                    "Error al registrar el usuario: " + e.getMessage(),
                    null, null, null, null, null);
        }
    }

    private void saveUserToken(Usuario usuario, String jwtToken) {
        try {

            Token token = new Token();
            token.setToken(jwtToken);
            token.setExpired(false);
            token.setRevoked(false);
            token.setUsuario(usuario);

            tokenRepository.save(token);

        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el token: " + e.getMessage());
        }
    }

    private void revokeUserTokens(Usuario usuario) {
        try {
            List<Token> validUserTokens = tokenRepository.findAllValidIsFalseOrRevokedIsFalseByUsuario_IdUsuario(usuario.getIdUsuario());

            if (!validUserTokens.isEmpty()) {
                for (Token token : validUserTokens) {
                    token.setExpired(true);
                    token.setRevoked(true);
                }
                tokenRepository.saveAll(validUserTokens);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public RespuestaDto refreshToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Refresh Token no válido");
        }

        String refreshToken = authHeader.substring(7);
        String correo = jwtService.extraerCorreo(refreshToken);
        if (correo == null) {
            throw new IllegalArgumentException("Refresh Token no válido");
        }

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (!jwtService.isTokenValid(refreshToken, usuario)) {
            throw new IllegalArgumentException("Refresh Token no válido");
        }

        String jwtToken = jwtService.generarToken(usuario);
        String newRefreshToken = jwtService.generarRefreshToken(usuario);
        revokeUserTokens(usuario);
        saveUserToken(usuario, jwtToken);
        return new RespuestaDto(
                "Refresh Token generado correctamente",
                jwtToken,
                newRefreshToken,
                usuario.getNombre(),
                usuario instanceof Organizador ? "ORGANIZADOR" : "ASISTENTE",
                usuario.getIdUsuario()
        );
    }
}
