package com.concordia.springcrud.dao;

import org.springframework.data.jpa.repository.JpaRepository;


import com.concordia.springcrud.entity.Employee;

import java.util.List;

// DAO is a design pattern which makes objects from table to code or from code to table
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

	// that's it ... no need to write any code LOL!

    // add a method to sort by last name
    public List<Employee> findAllByOrderByLastNameAsc();
	
}
