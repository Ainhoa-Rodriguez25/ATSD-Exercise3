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

import java.util.Arrays;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql", executionPhase = AFTER_TEST_METHOD)
public class UsuarioListWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManagerUserSession managerUserSession;

    @Autowired
    private UsuarioService usuarioService;

    @Test
    public void userListShowsRegisteredUsers() throws Exception {
        // GIVEN
        // Dos usuarios registrados en la BD
        UsuarioData usuario1 = new UsuarioData();
        usuario1.setEmail("usuario1@test.es");
        usuario1.setPassword("1234");
        usuario1.setAdmin(true);
        UsuarioData usuarioRegistrado = usuarioService.registrar(usuario1);

        UsuarioData usuario2 = new UsuarioData();
        usuario2.setEmail("usuario2@test.es");
        usuario2.setPassword("5678");
        usuario2.setAdmin(false);
        usuarioService.registrar(usuario2);

        // Se simula que el usuario 1 está logueado
        when(managerUserSession.usuarioLogeado())
                .thenReturn(usuarioRegistrado.getId());

        // WHEN, THEN
        // La página /registered muestra los emails de ambos usuarios
        this.mockMvc.perform(get("/registered"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("usuario1@test.es")))
                .andExpect(content().string(containsString("usuario2@test.es")));
    }
}
