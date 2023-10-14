package com.cst438.service;



import org.springframework.amqp.core.Queue;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;


import org.springframework.context.annotation.Bean;


@Service
@ConditionalOnProperty(prefix="gradebook", name="service", havingValue = "default", matchIfMissing=true)
public class GradebookServiceDefault implements GradebookService {
	
	Queue gradebookQueue = new Queue("gradebook-queue", true);
	
	@Bean

	Queue createQueue() {

	return new Queue("registration-queue");

	}
	
	public void enrollStudent(String student_email, String student_name, int course_id) {
	}
	
}
