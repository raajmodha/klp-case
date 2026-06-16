package no.raj.klp;

import no.raj.klp.model.UserRequest;
import no.raj.klp.model.UserType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateUserAndReturn201() throws Exception {
        UserRequest request = new UserRequest("raj@klp.no", UserType.USER);

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.email").value("raj@klp.no"))
            .andExpect(jsonPath("$.type").value("USER"));
    }

    @Test
    void shouldCreateAdminUserAndReturn201() throws Exception {
        UserRequest request = new UserRequest("admin@klp.no", UserType.ADMIN);

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.email").value("admin@klp.no"))
            .andExpect(jsonPath("$.type").value("ADMIN"));
    }

    @Test
    void shouldReturn400WhenEmailIsInvalid() throws Exception {
        UserRequest request = new UserRequest("not-an-email", UserType.ADMIN);

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenEmailIsBlank() throws Exception {
        UserRequest request = new UserRequest("   ", UserType.USER);

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenEmailFieldIsMissing() throws Exception {
        String json = """
            { "type": "USER" }
            """;

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenTypeIsMissing() throws Exception {
        String json = """
            { "email": "raj@klp.no" }
            """;

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenBodyIsEmpty() throws Exception {
        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenBodyIsMalformedJson() throws Exception {
        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("not-json"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetUserByIdAfterCreating() throws Exception {
        UserRequest request = new UserRequest("admin@klp.no", UserType.ADMIN);

        String createResponse = mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        Integer id = objectMapper.readTree(createResponse).get("id").asInt();

        mockMvc.perform(get("/user/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.email").value("admin@klp.no"))
            .andExpect(jsonPath("$.type").value("ADMIN"));
    }

    @Test
    void shouldReturn404WhenUserNotFound() throws Exception {
        mockMvc.perform(get("/user/9999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        createUser("user1@klp.no", UserType.USER);
        createUser("admin1@klp.no", UserType.ADMIN);

        mockMvc.perform(get("/user"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldReturnEmptyListWhenNoUsersExist() throws Exception {
        mockMvc.perform(get("/user"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldFilterUsersByType() throws Exception {
        createUser("user1@klp.no", UserType.USER);
        createUser("user2@klp.no", UserType.USER);
        createUser("admin1@klp.no", UserType.ADMIN);

        mockMvc.perform(get("/user").param("type-filter", "USER"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].type").value("USER"))
            .andExpect(jsonPath("$[1].type").value("USER"));

        mockMvc.perform(get("/user").param("type-filter", "ADMIN"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].type").value("ADMIN"));
    }

    @Test
    void shouldReturnEmptyListWhenNoUsersMatchTypeFilter() throws Exception {
        createUser("user1@klp.no", UserType.USER);

        mockMvc.perform(get("/user").param("type-filter", "ADMIN"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturn400WhenTypeFilterIsInvalid() throws Exception {
        mockMvc.perform(get("/user").param("type-filter", "SUPERUSER"))
            .andExpect(status().isBadRequest());
    }

    private void createUser(String email, UserType type) throws Exception {
        mockMvc.perform(post("/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new UserRequest(email, type))));
    }
}
