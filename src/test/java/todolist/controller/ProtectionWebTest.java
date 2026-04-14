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

import static org.mockito.Mockito.when;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;


@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql", executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = "/clean-db.sql", executionPhase = AFTER_TEST_METHOD)
public class ProtectionWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManagerUserSession managerUserSession;

    @Autowired
    private UsuarioService usuarioService;

    @Test
    public void accesoSinLoginARegisteredRedirigirALogin() throws Exception {
        // WHEN, THEN
        // Si no hay usuario logueado redirige a login
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        this.mockMvc.perform(get("/registered"))
                .andExpect(status().is3xxRedirection()) // Aplicación redirecciona a otra url
                .andExpect(redirectedUrl("/login")); //url a la que se redirige es /login
    }

    @Test
    public void accesoUsuarioNoAdminARegisteredDevuelveUnauthorized() throws Exception{
        // GIVEN
        // Usuario registrado que no es admin
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("noadmin@test.es");
        usuario.setPassword("1234");
        usuario.setAdmin(false);
        UsuarioData usuarioRegistrado = usuarioService.registrar(usuario);

        // WHEN, THEN
        // Si usuario no es admin, devuelve error 401 Unauthorized
        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioRegistrado.getId());

        this.mockMvc.perform(get("/registered"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void accesoAdminARegisteredPermitido() throws Exception {
        //GIVEN
        // Usuario registrado que es admin
        UsuarioData admin = new UsuarioData();
        admin.setEmail("admin@test.es");
        admin.setPassword("1234");
        admin.setAdmin(true);
        UsuarioData adminRegistrado = usuarioService.registrar(admin);

        // WHEN, THEN
        // Si usuario es admin puede acceder a /registered
        when(managerUserSession.usuarioLogeado()).thenReturn(adminRegistrado.getId());

        this.mockMvc.perform(get("/registered"))
                .andExpect(status().isOk());
    }


    // Tests para la página de descripción de usuario
    @Test
    public void accesoSinLoginADescripcionRedirigirALogin() throws Exception {
        //GIVEN
        // Usuario registrado que es admin
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("protection3@test.es");
        usuario.setPassword("1234");
        UsuarioData usuarioRegistrado = usuarioService.registrar(usuario);

        // WHEN, THEN
        // Si no hay usuario logueado redirige a login
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        this.mockMvc.perform(get("/registered/" + usuarioRegistrado.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void accesoUsuarioNoAdminADescripcionDevuelveUnauthorized() throws Exception {
        // GIVEN
        // Usuario registrado que no es admin
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("noadminin2@test.es");
        usuario.setPassword("1234");
        usuario.setAdmin(false);
        UsuarioData usuarioRegistrado = usuarioService.registrar(usuario);

        // WHEN, THEN
        // Si usuario no es admin, devuelve error 401 Unauthorized
        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioRegistrado.getId());

        this.mockMvc.perform(get("/registered/" + usuarioRegistrado.getId()))
                .andExpect(status().isUnauthorized());
    }
}
