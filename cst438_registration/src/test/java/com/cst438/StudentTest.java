package com.cst438;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.cst438.controller.StudentController;
import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class StudentTest {
	
    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentController studentController;
    
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
//        studentController = new StudentController(studentRepository);
//        mvc = MockMvcBuilders.standaloneSetup(studentController).build();
    }
    
    @Test
    public void testGetStudent() throws Exception {
        Student student = new Student();
        student.setStudent_id(1);
        student.setName("John Doe");
        student.setEmail("john.doe@example.com");

        when(studentRepository.findByStudentId(1)).thenReturn(student);

        MockHttpServletResponse response = mvc.perform(
                MockMvcRequestBuilders
                        .get("/student/{student_id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert the HTTP status code
        assertEquals(200, response.getStatus());
   
    }

    @Test
    public void testCreateStudent() throws Exception {
        StudentDTO studentDTO = new StudentDTO(1, "John Doe", "john.doe@example.com", 1, "Active");
        
        ObjectMapper objectMapper = new ObjectMapper();
        String studentDTOJson = objectMapper.writeValueAsString(studentDTO);

		MockHttpServletResponse response;

		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/student")
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(studentDTOJson))
				.andReturn().getResponse();
		
		
		assertEquals(200, response.getStatus());
		
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/student")
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(studentDTOJson))
				.andReturn().getResponse();
		
		
		assertEquals(400, response.getStatus());
   
    }
    

    @Test
    public void testDeleteStudentWithForce() throws Exception {
        int studentId = 1;
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(new Student()));
        String forceQueryParam = "true";
        MockHttpServletResponse response;

        response = mvc.perform(
        		MockMvcRequestBuilders
                .delete("/student/{student_id}", studentId)
                .param("force", forceQueryParam))
        		.andReturn().getResponse();
        assertEquals(200, response.getStatus());
        
    }

    
    @Test
    public void testUpdateStudent() throws Exception {
        int studentId = 1;
        Student existingStudent = new Student();
        existingStudent.setStudent_id(studentId);
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(existingStudent));

        StudentDTO updatedStudentDTO = new StudentDTO(1, "Updated Name", "updated email", 1, "Status");

        ObjectMapper objectMapper = new ObjectMapper();
        String updatedStudentDTOJson = objectMapper.writeValueAsString(updatedStudentDTO);

        MockHttpServletResponse response;
        response = mvc.perform(MockMvcRequestBuilders
                .put("/student/{student_id}", studentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedStudentDTOJson))
        		.andReturn().getResponse();
        assertEquals(200, response.getStatus());
        
        
    }
    
    @Test
    public void testGetAllStudents() throws Exception {
        List<Student> students = new ArrayList<>();

        Student student1 = new Student();
        student1.setName("John");
        student1.setEmail("john@gmail.com");
        student1.setStatusCode(1);
        student1.setStatus("Active");
        students.add(student1);

        Student student2 = new Student();
        student2.setName("Jane");
        student2.setEmail("jane@gmail.com");
        student2.setStatusCode(1);
        student2.setStatus("Active");
        students.add(student2);

        when(studentRepository.findAll()).thenReturn(students);

        MockHttpServletResponse response;
        response = mvc.perform(MockMvcRequestBuilders
                .get("/student")
                .accept(MediaType.APPLICATION_JSON))
        		.andReturn().getResponse();
        assertEquals(200, response.getStatus());
    }


	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> T  fromJsonString(String str, Class<T> valueType ) {
		try {
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
