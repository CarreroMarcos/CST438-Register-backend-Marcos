package com.cst438.domain;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
public interface StudentRepository extends CrudRepository <Student, Integer> {
	
	 Student findByEmail(String email); 
	 
	    @Query("SELECT s FROM Student s WHERE s.student_id = :studentId")
	    Student findByStudentId(@Param("studentId") int studentId);
	
	 Student[] findByNameStartsWith(String name);
	 
	 List<Student> findAll();

}