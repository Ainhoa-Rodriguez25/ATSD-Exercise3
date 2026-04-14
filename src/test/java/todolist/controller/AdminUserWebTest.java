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
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql", executionPhase = AFTER_TEST_METHOD)
public class AdminUserWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManagerUserSession managerUserSession;

    @Autowired
    private UsuarioService usuarioService;

    @Test
    public void registroMuestraCheckboxAdminSiNoExisteAdmin() throws Exception {
        // WHEN, THEN
        // Si no hay admin el formulario de registro muestra el checkbox
        this.mockMvc.perform(get("/registro"))
                .andExpect(content().string(containsString("Registrar como administrador")));
    }

    @Test
    public void registroNoMuestraCheckboxAdminSiYaExiste() throws  Exception {
        // GIVEN
        // Se registra un usuario admin
        UsuarioData admin = new UsuarioData();
        admin.setEmail("admin@test.es");
        admin.setPassword("1234");
        admin.setAdmin(true);
        usuarioService.registrar(admin);

        // WHEN, THEN
        // El formulario de registro no muestra el checkbox de admin
        this.mockMvc.perform(get("/registro"))
                .andExpect(content().string(not(containsString("Registrar como administrador"))));
    }

    @Test
    public void loginAdminRedirigaAListaUsuarios() throws Exception {
        // GIVEN
        // Registramos un usuario admin
        UsuarioData admin = new UsuarioData();
        admin.setEmail("admin2@test.es");
        admin.setPassword("1234");
        admin.setAdmin(true);
        usuarioService.registrar(admin);

        // WHEN, THEN
        // Al hacer login el admin es redirigido a /registered
        this.mockMvc.perform(post("/login")
                        .param("eMail", "admin2@test.es")
                        .param("password", "1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registered"));
    }
}
