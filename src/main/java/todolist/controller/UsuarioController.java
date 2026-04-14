package todolist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import todolist.authentication.ManagerUserSession;
import todolist.controller.exception.UsuarioNoAutorizadoException;
import todolist.dto.UsuarioData;
import todolist.service.UsuarioService;

import java.util.List;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ManagerUserSession managerUserSession;

    @GetMapping("/registered")
    public String listadoUsuarios(Model model) {
        // Se obtiene el usuario logueado para la navbar
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado(); // Devuelve el id del usuario que está logueado
        if (idUsuarioLogeado == null) {
            return "redirect:/login";
        }

        UsuarioData usuarioLogeado = usuarioService.findById(idUsuarioLogeado); // Buscar datos completos del usuario en base de datos
        if (usuarioLogeado.getAdmin() == null || !usuarioLogeado.getAdmin()) {
            throw new UsuarioNoAutorizadoException();
        }
        model.addAttribute("usuario", usuarioLogeado);

        // Se obtiene la lista de todos los usuarios
        List<UsuarioData> usuarios = usuarioService.findAll();
        model.addAttribute("usuarios", usuarios);

        return "listaUsuarios";
    }

    // Método para obtener descripcion con id usuario logeado
    @GetMapping("/registered/{id}")
    public String descripcionUsuario(@PathVariable(value = "id") Long idUsuario, Model model) {
        // Se obtiene el usuario logueado para la navbar
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado == null) {
            return "redirect:/login";
        }

        // Comprobar que el usuario logueado es admin
        UsuarioData usuarioLogeado = usuarioService.findById(idUsuarioLogeado);
        if (usuarioLogeado.getAdmin() == null || !usuarioLogeado.getAdmin()) {
            throw new UsuarioNoAutorizadoException();
        }
        model.addAttribute("usuario", usuarioLogeado);

        // Se obtiene el usuario que se quiere ver en la descripción
        UsuarioData usuarioDescripcion = usuarioService.findById(idUsuario);
        if (usuarioDescripcion == null) {
            throw new RuntimeException("Usuario no encontrado: " + idUsuario);
        }
        model.addAttribute("usuarioDescripcion", usuarioDescripcion);

        return "descripcionUsuario";
    }

    // Endpoints para bloquear y desbloquear a un usuario
    @PostMapping("/registered/{id}/block")
    public String blockUser(@PathVariable(value = "id") Long idUsuario) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado == null) {
            return "redirect:/login";
        }

        UsuarioData usuarioLogeado = usuarioService.findById(idUsuarioLogeado);
        if (usuarioLogeado.getAdmin() == null || !usuarioLogeado.getAdmin()) {
            throw new UsuarioNoAutorizadoException();
        }

        usuarioService.blockUser(idUsuario);
        return "redirect:/registered";
    }

    @PostMapping("/registered/{id}/unblock")
    public String unblockUser(@PathVariable(value = "id") Long idUsuario) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado == null) {
            return "redirect:/login";
        }

        UsuarioData usuarioLogeado = usuarioService.findById(idUsuarioLogeado);
        if (usuarioLogeado.getAdmin() == null || !usuarioLogeado.getAdmin()) {
            throw new UsuarioNoAutorizadoException();
        }

        usuarioService.unblockUser(idUsuario);
        return "redirect:/registered";
    }
}
