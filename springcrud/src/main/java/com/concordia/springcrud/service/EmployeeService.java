package com.concordia.springcrud.service;

import com.concordia.springcrud.entity.Employee;

import java.util.List;

// di connects to interface
public interface EmployeeService {

	List<Employee> findAll();
	
	Employee findById(int theId);
	
	void save(Employee theEmployee);
	
	void deleteById(int theId);
	
}