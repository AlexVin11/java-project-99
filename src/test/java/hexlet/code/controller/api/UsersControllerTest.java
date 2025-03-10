package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.component.AppInitializer;
import hexlet.code.dto.UserDTO.UserDTO;
import hexlet.code.dto.UserDTO.UserUpdateDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import hexlet.code.util.UserUtils;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UsersControllerTest {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private UserUtils userUtils;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    private TaskStatus testTaskStatus;

    private Task testTask;

    private User testUser;

    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();
        labelRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
        for (var entry : AppInitializer.DEFAULT_TASK_STATUSES_SLUGS_AND_NAMES_MAP.entrySet()) {
            TaskStatus taskStatus = new TaskStatus(entry.getKey(), entry.getValue());
            taskStatusRepository.save(taskStatus);
        }
        testTask = Instancio.of(modelGenerator.getTaskModel()).create();
    }

    @Test
    public void testIndexUsers() throws Exception {
        userRepository.save(testUser);
        var result = mockMvc.perform(get("/api/users").with(token))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
        List<User> usersInDb = userRepository.findAll();
        List<UserDTO> usersInResponse = objectMapper.readValue(body, new TypeReference<>() { });
        List<User> modelFromResponse = usersInResponse.stream()
                .map(userMapper::map)
                .toList();
        assertThat(modelFromResponse.containsAll(usersInDb));
    }

    @Test
    public void testCreateUser() throws Exception {
        var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser));
        mockMvc.perform(request).andExpect(status().isCreated());
        var user = userRepository.findByEmail(testUser.getEmail()).get();
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(user.getFirstName()).isEqualTo(testUser.getFirstName());
        assertThat(user.getPassword()).isNotEqualTo(testUser.getPassword());
    }

    @Test
    public void testShowUser() throws Exception {
        userRepository.save(testUser);
        var getUserRequest = get("/api/users/{id}", testUser.getId());
        var result = mockMvc.perform(getUserRequest.with(token))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                v -> v.node("lastName").isEqualTo(testUser.getLastName()),
                v -> v.node("email").isEqualTo(testUser.getEmail())
        );
    }

    @Test
    public void testCreateUserWithNotCorrectEmail() throws Exception {
        var dto = userMapper.map(testUser);
        dto.setEmail("qwerty");
        var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));
        mockMvc.perform(request.with(token)).andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateUser() throws Exception {
        userRepository.save(testUser);
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setEmail(JsonNullable.of("someguy14@gmail.com"));
        var updateUserRequest = put("/api/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO));
        mockMvc.perform(updateUserRequest.with(token)).andExpect(status().isOk());
        var updatedUser = userRepository.findById(testUser.getId()).get();
        assertThatJson(updatedUser).and(
                v -> v.node("id").isEqualTo(testUser.getId()),
                v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                v -> v.node("lastName").isEqualTo(testUser.getLastName()),
                v -> v.node("email").isEqualTo(updateDTO.getEmail())
        );
    }

    @Test
    public void testDeleteUserCorrectToken() throws Exception {
        userRepository.save(testUser);
        var createdUser = userRepository.findByEmail(testUser.getEmail()).get();
        var deleteUserRequest = delete("/api/users/{id}", createdUser.getId());
        mockMvc.perform(deleteUserRequest.with(token)).andExpect(status().isNoContent());
        assertThat(userRepository.existsById(createdUser.getId())).isFalse();
    }

    @Test
    public void testDeleteUserIncorrectToken() throws Exception {
        userRepository.save(testUser);
        var deleteUserRequest = delete("/api/users/{id}", testUser.getId());
        var incorrectToken = jwt().jwt(builder -> builder.subject("lumpa14@mail.ru"));
        mockMvc.perform(deleteUserRequest.with(incorrectToken)).andExpect(status().isForbidden());
    }

    @Test
    public void testDeleteUserWithAssignedTask() throws Exception {
        userRepository.save(testUser);
        testTask.setAssignee(testUser);
        taskRepository.save(testTask);
        var deleteUserRequest = delete("/api/users/{id}", testUser.getId());
        mockMvc.perform(deleteUserRequest.with(token)).andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void testDeleteIncorrectUser() throws Exception {
        userRepository.save(testUser);
        var deleteUserRequest = delete("/api/users/999");
        mockMvc.perform(deleteUserRequest.with(token)).andExpect(status().isNotFound());
    }
}
