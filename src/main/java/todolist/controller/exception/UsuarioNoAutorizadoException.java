package todolist.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Usuario sin permisos suficientes")
public class UsuarioNoAutorizadoException extends RuntimeException {
    public UsuarioNoAutorizadoException() {
        super("Usuario sin permisos suficientes para acceder a esta página");
    }
}
