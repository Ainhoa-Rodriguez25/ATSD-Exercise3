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
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql", executionPhase = AFTER_TEST_METHOD)
public class NavbarWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManagerUserSession managerUserSession;

    @Autowired
    private UsuarioService usuarioService;

    @Test
    public void navbarAnonymousShowsLoginAndRegister() throws Exception {
        // Cuando no hay usuario logueado
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        this.mockMvc.perform(get("/about"))
                .andExpect(content().string(containsString("Login")))
                .andExpect(content().string(containsString("Registro")));
    }

    @Test
    public void  navbarLoggedShowsUsername() throws Exception {
        // Cuando hay un usuario logueado
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("richard@umh.es");
        usuario.setPassword("1234");
        UsuarioData usuarioRegistrado = usuarioService.registrar((usuario));

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioRegistrado.getId());

        this.mockMvc.perform(get("/about"))
                .andExpect(content().string(containsString("richard@umh.es")))
                .andExpect(content().string(containsString("Tareas")));
    }
}
