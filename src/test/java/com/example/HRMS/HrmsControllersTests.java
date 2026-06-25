package com.example.HRMS;

import com.example.HRMS.DTO.*;
import com.example.HRMS.Entity.*;
import com.example.HRMS.Repositories.EmployeeRepo;
import com.example.HRMS.Repositories.LeaveRepo;
import com.example.HRMS.Repositories.TaskRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class HrmsControllersTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private LeaveRepo leaveRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private Employee hrUser;
    private String hrToken;

    @BeforeEach
    public void setup() throws Exception {
        taskRepo.deleteAll();
        leaveRepo.deleteAll();
        employeeRepo.deleteAll();

        // Create a pre-existing HR user in DB
        hrUser = Employee.builder()
                .name("HR Manager")
                .email("hr@example.com")
                .password(passwordEncoder.encode("hrpass123"))
                .role(Role.HR)
                .build();
        employeeRepo.save(hrUser);

        // Authenticate the HR user to get token
        LoginRequest loginRequest = new LoginRequest("hr@example.com", "hrpass123");
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = loginResult.getResponse().getContentAsString();
        LoginResponse responseDto = objectMapper.readValue(responseString, LoginResponse.class);
        hrToken = responseDto.getToken();
    }

    @Test
    public void testAuthRegistrationAndLogin() throws Exception {
        // 1. Register a new user
        LoginRequest regRequest = new LoginRequest("emp@example.com", "emppass123");
        MvcResult regResult = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponse regResponse = objectMapper.readValue(regResult.getResponse().getContentAsString(), LoginResponse.class);
        assertNotNull(regResponse.getToken());
        assertEquals("emp@example.com", regResponse.getEmail());

        // 2. Login as the newly registered user
        LoginRequest loginRequest = new LoginRequest("emp@example.com", "emppass123");
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponse loginResponse = objectMapper.readValue(loginResult.getResponse().getContentAsString(), LoginResponse.class);
        assertNotNull(loginResponse.getToken());
    }

    @Test
    public void testHrOnboardAndEmployeeProfile() throws Exception {
        // 1. Onboard a new employee (authenticated as HR)
        OnboardRequest onboardRequest = OnboardRequest.builder()
                .name("John Doe")
                .email("john@example.com")
                .phone("1234567890")
                .department("Engineering")
                .designation("Software Engineer")
                .build();

        MvcResult onboardResult = mockMvc.perform(post("/api/v1/hr/onboard")
                        .header("Authorization", "Bearer " + hrToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(onboardRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = onboardResult.getResponse().getContentAsString();
        assertTrue(responseString.contains("john@example.com"));
        assertTrue(responseString.contains("temporaryPassword"));

        // Extract credentials from response map
        java.util.Map<?, ?> responseMap = objectMapper.readValue(responseString, java.util.Map.class);
        String tempPassword = (String) responseMap.get("temporaryPassword");
        String empToken = (String) responseMap.get("token");

        // 2. Get Employee Profile (authenticated as the new employee)
        mockMvc.perform(get("/api/v1/employees/me")
                        .header("Authorization", "Bearer " + empToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.department").value("Engineering"))
                .andExpect(jsonPath("$.designation").value("Software Engineer"));

        // 3. Update Profile Name and Phone
        UpdateProfileRequest updateRequest = new UpdateProfileRequest("Johnathan Doe", "9876543210");
        mockMvc.perform(put("/api/v1/employees/me")
                        .header("Authorization", "Bearer " + empToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Johnathan Doe"))
                .andExpect(jsonPath("$.phone").value("9876543210"));
    }

    @Test
    public void testTaskManagement() throws Exception {
        // 1. Onboard employee to get their ID
        Employee emp = Employee.builder()
                .name("Alice")
                .email("alice@example.com")
                .password(passwordEncoder.encode("alicepass"))
                .role(Role.EMPLOYEE)
                .build();
        employeeRepo.save(emp);

        // 2. HR creates and assigns a task to Alice
        TaskRequest taskRequest = TaskRequest.builder()
                .title("Complete HRMS Integration Plan")
                .description("Write unit tests and clean code.")
                .priority(TaskPriority.HIGH)
                .dueDate(LocalDate.now().plusDays(2))
                .employeeId(emp.getId())
                .build();

        MvcResult taskResult = mockMvc.perform(post("/api/v1/tasks")
                        .header("Authorization", "Bearer " + hrToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Complete HRMS Integration Plan"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andReturn();

        TaskResponse taskResponse = objectMapper.readValue(taskResult.getResponse().getContentAsString(), TaskResponse.class);

        // 3. Authenticate Alice to get her token
        LoginRequest loginRequest = new LoginRequest("alice@example.com", "alicepass");
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String aliceToken = objectMapper.readValue(loginResult.getResponse().getContentAsString(), LoginResponse.class).getToken();

        // 4. Alice gets her tasks
        mockMvc.perform(get("/api/v1/tasks/my")
                        .header("Authorization", "Bearer " + aliceToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Complete HRMS Integration Plan"));

        // 5. Alice updates the task status to IN_PROGRESS
        mockMvc.perform(patch("/api/v1/tasks/" + taskResponse.getId() + "/status")
                        .header("Authorization", "Bearer " + aliceToken)
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    public void testLeaveManagement() throws Exception {
        // 1. Create employee Alice
        Employee emp = Employee.builder()
                .name("Alice")
                .email("alice@example.com")
                .password(passwordEncoder.encode("alicepass"))
                .role(Role.EMPLOYEE)
                .build();
        employeeRepo.save(emp);

        // Authenticate Alice
        LoginRequest loginRequest = new LoginRequest("alice@example.com", "alicepass");
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String aliceToken = objectMapper.readValue(loginResult.getResponse().getContentAsString(), LoginResponse.class).getToken();

        // 2. Alice applies for leave
        LeaveRequestDto leaveDto = LeaveRequestDto.builder()
                .leaveType(LeaveType.SICK)
                .fromDate(LocalDate.now().plusDays(5))
                .toDate(LocalDate.now().plusDays(7))
                .reason("Doctor recommendation.")
                .build();

        MvcResult leaveResult = mockMvc.perform(post("/api/v1/leaves/apply")
                        .header("Authorization", "Bearer " + aliceToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(leaveDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.leaveType").value("SICK"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn();

        LeaveResponse leaveResponse = objectMapper.readValue(leaveResult.getResponse().getContentAsString(), LeaveResponse.class);

        // 3. HR views pending leaves
        mockMvc.perform(get("/api/v1/leaves/pending")
                        .header("Authorization", "Bearer " + hrToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(leaveResponse.getId()));

        // 4. HR approves leave
        mockMvc.perform(patch("/api/v1/leaves/" + leaveResponse.getId() + "/approve")
                        .header("Authorization", "Bearer " + hrToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }
}
