package com.cst438.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.security.Principal;

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
import com.cst438.domain.User;
import com.cst438.domain.UserRepository;
import com.cst438.service.GradebookService;

@RestController
@CrossOrigin 
public class StudentController {
	
	@Autowired
	StudentRepository studentRepository;
	
	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	private UserRepository userRepository;
	
	// find by student by ID
	@GetMapping("/student/{student_id}")
	public StudentDTO getStudent(@PathVariable("student_id") int id) {
	    Student student = studentRepository.findByStudentId(id);
	    if (student != null) {
	        return createStudentDTO(student);
	    } else {
	    	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found with ID: " + id);
	    }
	}
	
	// Add a student to the repo
	@PostMapping("/student")
	public int createStudent(Principal principal, @RequestBody StudentDTO sdto) {
		String alias = principal.getName();
		User currentUser = userRepository.findByAlias(alias);
		if(!currentUser.getRole().equals("ADMIN")) {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Not an admin");
		}
		Student check = studentRepository.findByEmail(sdto.studentEmail());
		if (check != null) {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "student email already exists "+sdto.studentEmail());
		}
		Student s = new Student();
		s.setEmail(sdto.studentEmail());
		s.setName(sdto.name());
		s.setStatusCode(sdto.statusCode());
		s.setStatus(sdto.status());
		studentRepository.save(s);

		return s.getStudent_id();
	}
	
	// Deletes a student with optional force param
	@DeleteMapping("/student/{id}")
	public void deleteStudent(Principal principal, @PathVariable("id") int id, @RequestParam("force") Optional<String> force) {
		String alias = principal.getName();
		User currentUser = userRepository.findByAlias(alias);
		if(!currentUser.getRole().equals("ADMIN")) {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Not an admin");
		}
		
		Student s = studentRepository.findById(id).orElse(null);
		if (s!=null) {

			List<Enrollment> list = enrollmentRepository.findByStudentId(id);
			if (list.size()>0 && force.isEmpty()) {
				throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "student has enrollments");
			} else {
				studentRepository.deleteById(id);
			}
		} else {
			return;
		}
		
	}
	
	// Updates existing student with new info
	@PutMapping("/student/{student_id}")
	public void updateStudent(Principal principal, @PathVariable("student_id") int id, @RequestBody StudentDTO studentDTO) {
		String alias = principal.getName();
		User currentUser = userRepository.findByAlias(alias);
		if(!currentUser.getRole().equals("ADMIN")) {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Not an admin");
		}
	    Student existingStudent = studentRepository.findById(id).orElse(null);

	    if (existingStudent != null && !existingStudent.getEmail().equals(studentDTO.studentEmail())) {
	    	Student check = studentRepository.findByEmail(studentDTO.studentEmail());
			if (check != null) {
				throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "student email already exists "+studentDTO.studentEmail());
			}
	        existingStudent.setName(studentDTO.name());
	        existingStudent.setEmail(studentDTO.studentEmail());
	        existingStudent.setStatusCode(studentDTO.statusCode());
	        existingStudent.setStatus(studentDTO.status());

	        studentRepository.save(existingStudent);
	    } else {
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found with ID: " + id);
	    }
	}
	
	// Puts all the students in the DB into an array
	@GetMapping("/student")
	public StudentDTO[] getStudents() {
		Iterable<Student> list = studentRepository.findAll();
		ArrayList<StudentDTO> alist = new ArrayList<>();
		for (Student s : list) {
			StudentDTO sdto = new StudentDTO(s.getStudent_id(), s.getName(), s.getEmail(), s.getStatusCode(), s.getStatus());
			alist.add(sdto);
		}
		return alist.toArray(new StudentDTO[alist.size()]);
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
