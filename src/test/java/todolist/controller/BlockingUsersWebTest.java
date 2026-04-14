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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql", executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = "/clean-db.sql", executionPhase = AFTER_TEST_METHOD)
public class BlockingUsersWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManagerUserSession managerUserSession;

    @Autowired
    private UsuarioService usuarioService;

    @Test
    public void adminPuedeBloquearUsuario() throws Exception {
        // GIVEN
        UsuarioData admin = new UsuarioData();
        admin.setEmail("admin@test.es");
        admin.setPassword("1234");
        admin.setAdmin(true);
        UsuarioData adminRegistrado = usuarioService.registrar(admin);

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("usuario@test.es");
        usuario.setPassword("1234");
        UsuarioData usuarioRegistrado = usuarioService.registrar(usuario);

        when(managerUserSession.usuarioLogeado()).thenReturn(adminRegistrado.getId());

        // WHEN, THEN
        this.mockMvc.perform(post("/registered/" + usuarioRegistrado.getId() + "/block"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registered"));
    }

    @Test
    public void usuarioBloqueadoApareceComoDesbloquearEnLista() throws Exception {
        // GIVEN
        UsuarioData admin = new UsuarioData();
        admin.setEmail("admin2@test.es");
        admin.setPassword("1234");
        admin.setAdmin(true);
        UsuarioData adminRegistrado = usuarioService.registrar(admin);

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("usuario2@test.es");
        usuario.setPassword("1234");
        UsuarioData usuarioRegistrado = usuarioService.registrar(usuario);
        usuarioService.blockUser(usuarioRegistrado.getId());

        when(managerUserSession.usuarioLogeado()).thenReturn(adminRegistrado.getId());

        // WHEN, THEN
        this.mockMvc.perform(get("/registered"))
                .andExpect(content().string(containsString("Desbloquear")));
    }
}
