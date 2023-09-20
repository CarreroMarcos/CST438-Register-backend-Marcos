package com.cst438.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;
import com.cst438.service.GradebookService;

@RestController
@CrossOrigin 
public class StudentController {
	
	@Autowired
	CourseRepository courseRepository;
	
	@Autowired
	StudentRepository studentRepository;
	
	@Autowired
	EnrollmentRepository enrollmentRepository;
	
	@GetMapping("/student/{student_id}")
	public StudentDTO getStudent(@PathVariable("student_id") int id) {
	    Student student = studentRepository.findByStudentId(id);
	    if (student != null) {
	        return createStudentDTO(student);
	    } else {
	    	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found with ID: " + id);
	    }
	}
	
	
	@PostMapping("/student")
	@Transactional
	public int createStudent(@RequestBody StudentDTO student) {
		String name = student.name();
		String email = student.studentEmail();
		
		Student test = studentRepository.findByEmail(email);
		
		if (test != null) {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Email already in system  "+email);
		}
		Student newStudent = new Student();
		newStudent.setName(name);
		newStudent.setEmail(email);
		studentRepository.save(newStudent);
		
		return newStudent.getStudent_id();
	}
	
	
	@DeleteMapping("/student/{student_id}")
	public void deleteStudent(@PathVariable("student_id") int id, @RequestParam("force") Optional<String> force) {

	    if (force.isPresent() && force.get().equals("true")) {
	        studentRepository.deleteById(id);
	    } else {
	        Student student = studentRepository.findById(id).orElse(null);
	        if (student != null) {
	        	int student_id = student.getStudent_id();
	            if (enrollmentRepository.findEnrollmentByStudentId(student_id).isEmpty()) {
	                studentRepository.deleteById(id);
	            } else {
	                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete student.");
	            }
	        } else {
	            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found with ID: " + id);
	        }
	    }		
	}
	
	@PutMapping("/student/{student_id}")
	public void updateStudent(@PathVariable("student_id") int id, @RequestBody StudentDTO studentDTO) {
	    Student existingStudent = studentRepository.findById(id).orElse(null);

	    if (existingStudent != null) {
	        existingStudent.setName(studentDTO.name());
	        existingStudent.setEmail(studentDTO.studentEmail());
	        existingStudent.setStatusCode(studentDTO.statusCode());
	        existingStudent.setStatus(studentDTO.status());

	        studentRepository.save(existingStudent);
	    } else {
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found with ID: " + id);
	    }
	}
	
	@GetMapping("/student")
	public StudentDTO[] getAllStudent() {
	    List<Student> students = studentRepository.findAll();
	    return createStudentDTOs(students);
	}
	
	
	
	private StudentDTO[] createStudentDTOs(List<Student> students) {
	    StudentDTO[] result = new StudentDTO[students.size()];
	    for (int i = 0; i < students.size(); i++) {
	        StudentDTO dto = createStudentDTO(students.get(i));
	        result[i] = dto;
	    }
	    return result;
	}

	private StudentDTO createStudentDTO(Student student) {
	    return new StudentDTO(
	        student.getStudent_id(),
	        student.getName(),
	        student.getEmail(),
	        student.getStatusCode(),
	        student.getStatus()
	    );
	}
}
