package com.concordia.springcrud.controller;

import com.concordia.springcrud.entity.Employee;
import com.concordia.springcrud.service.EmployeeService;
import jakarta.servlet.http.HttpSession;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/employees")
public class EmployeeController {
	private EmployeeService employeeService;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	//@Autowire is optional since we have only 1 constructor
	public EmployeeController(EmployeeService theEmployeeService) {
		employeeService = theEmployeeService;
	}

//	// load employee data
//
//	private List<Employee> theEmployees;
//
//	@PostConstruct
//	private void loadData() {
//
//		// create employees
//		Employee emp1 = new Employee("Leslie", "Andrews", "leslie@concordia.com");
//		Employee emp2 = new Employee("Emma", "Baumgarten", "emma@concordia.com");
//		Employee emp3 = new Employee("Avani", "Gupta", "avani@concordia.com");
//
//		// create the list
//		theEmployees = new ArrayList<>();
//
//		// add to the list
//		theEmployees.add(emp1);
//		theEmployees.add(emp2);
//		theEmployees.add(emp3);
//	}

	// add mapping for "/list"

	@GetMapping("/list")
	public String listEmployees(Model theModel, HttpSession session) {
		String username = (String) session.getAttribute("user");
		if(username == null || username.isEmpty()){
			theModel.addAttribute("isLogin", false);
			return "redirect:/";
		}

		// Get the employee from db
		List<Employee> theEmployee = employeeService.findAll();

		// add db data to the spring model, present data from db
		theModel.addAttribute("employees", theEmployee);
		theModel.addAttribute("isLogin", true);
//		// add loadData() to the spring model, present only the data that we loaded
//		theModel.addAttribute("employees", theEmployees);

		return "employees/list-employees";
	}

	@GetMapping("/search")
	public String searchEmployeesByName(@RequestParam("keyword") String keyword, Model theModel) {
		// Get the employee from db
		String sql = "SELECT * FROM employee WHERE first_name LIKE '%" + keyword + "%'";

		List<Map<String, Object>> res = jdbcTemplate.queryForList(sql);
		List<Employee> theEmployee = new ArrayList<>();

		if (!res.isEmpty()) {
			for (Map<String, Object> row : res) {
				Employee myObject = new Employee();
				myObject.setId((int) row.get("id"));
				myObject.setFirstName((String) row.get("first_name"));
				myObject.setLastName((String) row.get("last_name"));
				myObject.setEmail((String) row.get("email"));

				theEmployee.add(myObject);
			}
			theModel.addAttribute("employees", theEmployee);
			theModel.addAttribute("search", keyword);

		}else {
			theModel.addAttribute("employees",null);
		}
		theModel.addAttribute("isLogin", true);
		return "employees/list-employees";
	}

	//Test test<script>alert('Attack!')</script>
	@PostMapping("/save1")
	@ResponseBody
	public String saveEmployee1(@RequestParam("first_name") String first_name) {

		return "name is:"+first_name;
	}
	@PostMapping("/save2")
	@ResponseBody
	public String saveEmployee2(@RequestParam("first_name") String first_name) {

		String[] values = first_name.split(" ");
		int length = values.length;
		String[] escapseValues = new String[length];
		for(int i = 0;i<length;i++){
			//Filter all possible xss attack strings
			escapseValues[i] = Jsoup.clean(values[i], Whitelist.relaxed()).trim();

			if(!StringUtils.equals(escapseValues[i],values[i])){
				System.out.println("Before xss string filtering："+values[i]+"\t"+"After filtering:"+escapseValues[i]);
			}
		}

		StringBuilder stringBuilder = new StringBuilder();

		for (String str : escapseValues) {
			stringBuilder.append(str);
		}

		return "name is:" + stringBuilder.toString();
	}

	//	add new employee
	@GetMapping("/showFormForAdd")
	public String showFormForAdd(Model theModel) {
		Employee theEmployee = new Employee();
		theModel.addAttribute("employee", theEmployee);
		return "/employees/employee-form";
	}


	//	receives and saves the ModelAttribute Employee
	@PostMapping("/save")
	public String saveEmployee(@ModelAttribute("employee") Employee theEmployee) {
		employeeService.save(theEmployee);
		// redirects to /employees/list to avoid duplicate submission
		return "redirect:/employees/list";
	}

	// update employee
	@GetMapping("/showFormForUpdate")
	public String showFormForUpdate(@RequestParam("employeeId") int theId, Model theModel) {
		// get the employee from the service
		Employee theEmployee = employeeService.findById(theId);
		// set employee as a model attribute to pre-populate the form
		theModel.addAttribute("employee", theEmployee);
		// send over to the employee-form
		return "employees/employee-form";
	}

	// delete employee
	@GetMapping("/delete")
	public String delete(@RequestParam("employeeId") int theId) {
		employeeService.deleteById(theId);
		// redirects to /employees/list to avoid duplicate submission
		return "redirect:/employees/list";
	}

}









