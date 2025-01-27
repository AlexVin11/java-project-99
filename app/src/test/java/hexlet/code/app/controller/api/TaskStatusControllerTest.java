package hexlet.code.app.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.component.AppInitializer;
import hexlet.code.app.dto.TaskStatusDTO.TaskStatusDTO;
import hexlet.code.app.dto.TaskStatusDTO.TaskStatusUpdateDTO;
import hexlet.code.app.mapper.TaskStatusMapper;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.util.ModelGenerator;
import hexlet.code.app.util.UserUtils;
import org.assertj.core.api.Assertions;
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
class TaskStatusControllerTest {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private UserUtils userUtils;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    private User testUser;

    private TaskStatus testTaskStatus;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        for (var entry : AppInitializer.DEFAULT_TASK_STATUSES_SLUGS_AND_NAMES_MAP.entrySet()) {
            TaskStatus taskStatus = new TaskStatus(entry.getKey(), entry.getValue());
            taskStatusRepository.save(taskStatus);
        }
    }

    @Test
    void testIndexTaskStatus() throws Exception {
        var result = mockMvc.perform(get("/api/task_statuses").with(token))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
        List<TaskStatus> statusesInDb = taskStatusRepository.findAll();
        List<TaskStatusDTO> dtoFromResponse = objectMapper.readValue(body, new TypeReference<>() { });
        List<TaskStatus> modelFromResponse = dtoFromResponse.stream().map(taskStatusMapper::map).toList();
        Assertions.assertThat(statusesInDb).containsExactlyInAnyOrderElementsOf(modelFromResponse);
    }

    @Test
    void testShowTaskStatus() throws Exception {
        var createTaskStatusRequest = post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTaskStatus));
        mockMvc.perform(createTaskStatusRequest.with(token)).andExpect(status().isCreated());
        //400 по факту
        var createdTaskStatus = taskStatusRepository.findBySlug(testTaskStatus.getSlug()).get();
        var getTaskStatusRequest = get("/api/task_statuses/{id}", createdTaskStatus.getId());
        var result = mockMvc.perform(getTaskStatusRequest.with(token))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(testTaskStatus.getName()),
                v -> v.node("slug").isEqualTo(testTaskStatus.getSlug())
        );
    }

    @Test
    void testCreateTaskStatus() throws Exception {
        var createTaskStatusRequest = post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTaskStatus));
        mockMvc.perform(createTaskStatusRequest.with(token)).andExpect(status().isCreated());
        var createdTaskStatus = taskStatusRepository.findBySlug(testTaskStatus.getSlug()).get();
        assertThat(createdTaskStatus).isNotNull();
        assertThat(createdTaskStatus.getName()).isEqualTo(testTaskStatus.getName());
        assertThat(createdTaskStatus.getSlug()).isEqualTo(testTaskStatus.getSlug());
    }

    @Test
    void testCreateTaskStatusByUnauthorisedUser() throws Exception {
        var createTaskStatusRequest = post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTaskStatus));
        mockMvc.perform(createTaskStatusRequest).andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateExistingSlugTaskStatus() throws Exception {
        var createTaskStatusRequest = post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTaskStatus));
        mockMvc.perform(createTaskStatusRequest.with(token));
        mockMvc.perform(createTaskStatusRequest.with(token)).andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testUpdateTaskStatus() throws Exception {
        var createTaskStatusRequest = post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTaskStatus));
        mockMvc.perform(createTaskStatusRequest.with(token)).andExpect(status().isCreated());
        var createdTaskStatus = taskStatusRepository.findBySlug(testTaskStatus.getSlug()).get();
        String newTaskName = "New name for slug " + testTaskStatus.getSlug();
        TaskStatusUpdateDTO taskStatusUpdateDTO = new TaskStatusUpdateDTO();
        taskStatusUpdateDTO.setName(JsonNullable.of(newTaskName));
        var updateRequest = put("/api/task_statuses/{id}", createdTaskStatus.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskStatusUpdateDTO));
        mockMvc.perform(updateRequest.with(token)).andExpect(status().isOk()).andReturn();
        var result = mockMvc.perform(get("/api/task_statuses/{id}", createdTaskStatus.getId()).with(token))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(taskStatusUpdateDTO.getName()),
                v -> v.node("slug").isEqualTo(testTaskStatus.getSlug())
        );
    }

    @Test
    void testDeleteTaskStatus() throws Exception {
        var createTaskStatusRequest = post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTaskStatus));
        mockMvc.perform(createTaskStatusRequest.with(token)).andExpect(status().isCreated());
        //400
        var createdTaskStatus = taskStatusRepository.findBySlug(testTaskStatus.getSlug()).get();
        var deleteRequest = delete("/api/task_statuses/{id}", createdTaskStatus.getId());
        mockMvc.perform(deleteRequest.with(token)).andExpect(status().isNoContent());
    }

    @Test
    void testDeleteIncorrectTaskStatus() throws Exception {
        var createTaskStatusRequest = post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTaskStatus));
        mockMvc.perform(createTaskStatusRequest.with(token));
        var deleteRequest = delete("/api/task_statuses/999");
        mockMvc.perform(deleteRequest.with(token)).andExpect(status().isNoContent());
    }
}
