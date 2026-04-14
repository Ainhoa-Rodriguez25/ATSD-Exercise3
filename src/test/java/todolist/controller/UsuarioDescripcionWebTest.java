package todolist.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import todolist.authentication.ManagerUserSession;
import todolist.dto.UsuarioData;
import todolist.service.UsuarioService;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql", executionPhase = AFTER_TEST_METHOD)
public class UsuarioDescripcionWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManagerUserSession managerUserSession;

    @Autowired
    private UsuarioService usuarioService;

    @Test
    public void descripcionUsuarioMuestraDatosCorrectamente() throws Exception {
        // GIVEN
        // Un usuario registrado en la BD
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("descripcion@test.es");
        usuario.setPassword("1234");
        usuario.setNombre("Usuario Test");
        usuario.setAdmin(true);
        UsuarioData usuarioRegistrado = usuarioService.registrar(usuario);

        // Se simula que ese usuario está logueado
        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioRegistrado.getId());

        // WHEN, THEN
        // La pagina /registered/{id} muestra los datos del usuario
        this.mockMvc.perform(get("/registered/" + usuarioRegistrado.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("descripcion@test.es")))
                .andExpect(content().string(containsString("Usuario Test")));
    }

    @Test
    public void listadoUsuariosTieneEnlaceADescripcion() throws Exception {
        //GIVEN
        // Un usuario registrado en la BD
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("enlace@test.es");
        usuario.setPassword("1234");
        usuario.setAdmin(true);
        UsuarioData usuarioRegistrado = usuarioService.registrar(usuario);

        // Se simula que el usuario está logueado
        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioRegistrado.getId());

        // WHEN, THEN
        // La pagina /registered contiene el enlace a la descripción del usuario
        this.mockMvc.perform(get("/registered"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/registered/" + usuarioRegistrado.getId())));
    }
}
