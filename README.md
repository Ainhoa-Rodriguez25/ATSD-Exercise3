# To Do List app

ToDoList app usign Spring Boot and Thymeleaf templates.

---

## Author
- **Name:** Ainhoa Rodríguez González
- **Subject:** Agile Techniques For Software Development
- **University:** Miguel Hernández University

---

## Links
- **Trello board:** [P2 – To Do List app](https://trello.com/invite/b/69c2b830550aa12c7c325dcb/ATTI0e9ade6e7f43927d2641f501df1951b951585052/exercise2-to-do-list-app-atsd)
- **GitHub repository:** https://github.com/Ainhoa-Rodriguez25/ATSD-Exercise2
- **Docker Hub:** https://hub.docker.com/r/ainhoaro/p2-todolistapp

---

## Versions
### Version 1.0.1 (25/03/2026)
- About webpage added

### Version 1.1.0 - Technical Documentation (04/04/2026)

### Menu Bar

**Affected pages:** All pages of the application (`/about`, `/usuarios/{id}/tareas`, `/registered`, `/registered/{id}`).

**New fragments in `fragments.html`:**
- `navbar(usuario)` — renders a navigation bar for logged-in users, showing the username and a logout button linked to `GET /logout`.
- `navbar-anonymous` — renders a simple header for anonymous users with no navigation options.

**Implementation notes:** The navbar fragment is included in all views using Thymeleaf's conditional fragment inclusion.
This pattern evaluates whether a `usuario` object is present in the model and renders the appropriate navbar variant.

**Tests:** `NavbarWebTest` verifies that the navbar renders correctly for both logged-in and anonymous users, checking that the username and logout button appear when a user is logged in.


### User Listing

**Endpoint:** `GET /registered`

**Preconditions:**
- An administrator user must be registered in the system.
- The logged-in user must be the administrator.
- Example URL: `http://localhost:8080/registered`

**New methods:**
- `UsuarioService.findAll()` — retrieves all users from the repository using `usuarioRepository.findAll()` and maps them to a list of `UsuarioData` DTOs using ModelMapper.

**New templates:**
- `listaUsuarios.html` — displays a table with all registered users showing their id, email, and action buttons for description, block and unblock.

**Tests:** `UsuarioListWebTest` verifies the page shows all registered users correctly. `UsuarioServiceTest.servicioFindAllDevuelveTodosLosUsuarios` verifies the service method returns all users.


### User Description

**Endpoint:** `GET /registered/{id}`

**Preconditions:**
- An administrator user must be registered in the system.
- The logged-in user must be the administrator.
- Example URL: `http://localhost:8080/registered/1`

**New templates:**
- `descripcionUsuario.html` — displays a specific user's details: id, email, name and birth date.

**Tests:** `UsuarioDescripcionWebTest` verifies the page shows the correct user data and that the user listing contains a link to each user's description page.


### Admin User

**Endpoints:** `GET /registro`, `POST /registro`, `POST /login`

**Preconditions:**
- The admin checkbox in the registration form is only shown when no administrator exists yet.
- Example URL: `http://localhost:8080/registro`

**New fields:**
- `admin` (Boolean, default `false`) added to `Usuario` model, `UsuarioData` and `RegistroData` DTOs.

**New methods:**
- `UsuarioService.existeAdmin()` — retrieves all users and checks if any of them has `admin = true` using Java streams:
```java
@Transactional(readOnly = true)
public boolean existeAdmin() {
    List usuarios = (List) usuarioRepository.findAll();
    return usuarios.stream()
            .anyMatch(u -> u.getAdmin() != null && u.getAdmin());
}
```
This method uses `anyMatch` to return `true` as soon as it finds one admin user, without iterating the entire list unnecessarily.

**Updated templates:**
- `formRegistro.html` — the admin checkbox is conditionally shown using `th:if="${!existeAdmin}"`.

**Tests:** `AdminUsuarioWebTest` verifies the checkbox appears only when no admin exists and that admin users are redirected to `/registered` after login. `UsuarioServiceTest` verifies `existeAdmin()` returns the correct value in both cases.


### Protection

**Endpoints:** `GET /registered`, `GET /registered/{id}`

**Preconditions:**
- If no user is logged in, the request is redirected to `/login`.
- If a non-admin user is logged in, a 401 Unauthorized response is returned.

**New classes:**
- `UsuarioNoAutorizadoException` — exception annotated with `@ResponseStatus(HttpStatus.UNAUTHORIZED)` thrown when a non-admin user attempts to access protected pages.

**Relevant code:**
```java
Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
if (idUsuarioLogeado == null) {
    return "redirect:/login";
}
UsuarioData usuarioLogeado = usuarioService.findById(idUsuarioLogeado);
if (usuarioLogeado.getAdmin() == null || !usuarioLogeado.getAdmin()) {
    throw new UsuarioNoAutorizadoException();
}
```
This pattern is applied to both `/registered` and `/registered/{id}`. It first checks if a user is logged in — redirecting to `/login` if not. Then it verifies the logged-in user is an administrator. If the `admin` field is `null` or `false`, `UsuarioNoAutorizadoException` is thrown, which Spring automatically converts to a 401 Unauthorized HTTP response thanks to the `@ResponseStatus` annotation.

**Tests:** `ProtectionWebTest` verifies four scenarios: non-logged user accessing `/registered` is redirected to `/login`, non-admin user accessing `/registered` receives 401, admin user accessing `/registered` receives 200 OK, non-logged user accessing `/registered/{id}` is redirected to `/login`, and non-admin user accessing `/registered/{id}` receives 401.


### Blocking Users

**Endpoints:**
- `POST /registered/{id}/block`
- `POST /registered/{id}/unblock`

**Preconditions:**
- The logged-in user must be the administrator.
- Example URLs: `http://localhost:8080/registered/1/block`, `http://localhost:8080/registered/1/unblock`

**New fields:**
- `block` (Boolean, default `false`) added to `Usuario` model and `UsuarioData` DTO.
- `USER_BLOCKED` added to the `LoginStatus` enum in `UsuarioService`.

**New methods:**
- `UsuarioService.blockUser(Long id)` — finds the user by id, sets `block` to `true` and saves the change to the database.
- `UsuarioService.unblockUser(Long id)` — finds the user by id, sets `block` to `false` and saves the change to the database.

**Updated templates:**
- `listaUsuarios.html` — shows a block or unblock button for each user depending on their current `block` status using `th:if="${!usuario.block}"` and `th:if="${usuario.block}"`.

**Updated login flow:** `UsuarioService.login()` now checks the `block` field after validating the password. If the user is blocked, `USER_BLOCKED` is returned and `LoginController` shows an error message on the login page.

**Tests:** `BlockingUsersWebTest` verifies that the admin can block a user and that blocked users appear with an unblock button in the list. `UsuarioServiceTest` verifies that `blockUser()` and `unblockUser()` persist the changes correctly and that blocked users receive `USER_BLOCKED` status on login.

---

## Requirements

You need install on your system:

- Java 8 SDK

---

## Ejecución

You can run the app using the goal `run` from Maven's _plugin_ 
on Spring Boot:

```
$ ./mvn spring-boot:run 
```   

You can already create a `jar` file and run it:

```
$ ./mvn package
$ java -jar target/todolist-inicial-0.0.1-SNAPSHOT.jar 
```

Once the app is running, you can open your favourite browser and connect to:

- [http://localhost:8080/login](http://localhost:8080/login)
