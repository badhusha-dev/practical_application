package com.example.collections.controller;

import com.example.collections.entity.Department;
import com.example.collections.entity.Employee;
import com.example.collections.entity.Task;
import com.example.collections.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

@Controller
@RequestMapping("/")
public class CompanyController {
    
    @Autowired
    private CompanyService companyService;
    
    // Initialize collections on startup
    @PostConstruct
    public void init() {
        companyService.initializeCollections();
    }
    
    // Thymeleaf Views
    
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Welcome to Business Collections Demo");
        return "home";
    }
    
    @GetMapping("/employees")
    public String listEmployees(Model model) {
        List<Employee> employees = companyService.getAllEmployees();
        Map<Integer, Department> departments = companyService.getDepartments();
        
        model.addAttribute("employees", employees);
        model.addAttribute("departments", departments);
        model.addAttribute("totalEmployees", employees.size());
        
        return "employees";
    }
    
    @GetMapping("/departments")
    public String listDepartments(Model model) {
        Map<Integer, Department> departments = companyService.getDepartments();
        
        model.addAttribute("departments", departments);
        model.addAttribute("totalDepartments", departments.size());
        
        return "departments";
    }
    
    @GetMapping("/tasks")
    public String listTasks(Model model) {
        Queue<Task> taskQueue = companyService.getTaskQueue();
        Stack<Task> taskHistory = companyService.getTaskHistory();
        Map<Integer, List<Task>> employeeTasks = companyService.getEmployeeTasks();
        
        model.addAttribute("taskQueue", taskQueue);
        model.addAttribute("taskHistory", taskHistory);
        model.addAttribute("employeeTasks", employeeTasks);
        model.addAttribute("pendingTasks", taskQueue.size());
        model.addAttribute("completedTasks", taskHistory.size());
        
        return "tasks";
    }
    
    // REST API Endpoints
    
    @GetMapping("/api/employees")
    @ResponseBody
    public List<Employee> getEmployees() {
        return companyService.getAllEmployees();
    }
    
    @PostMapping("/api/employees")
    @ResponseBody
    public Employee createEmployee(@RequestBody Employee employee) {
        companyService.addEmployee(employee);
        return employee;
    }
    
    @GetMapping("/api/departments")
    @ResponseBody
    public Map<Integer, Department> getDepartments() {
        return companyService.getDepartments();
    }
    
    @GetMapping("/api/skills")
    @ResponseBody
    public Set<String> getSkills() {
        return companyService.getUniqueSkills();
    }
    
    @GetMapping("/api/tasks/queue")
    @ResponseBody
    public Queue<Task> getTaskQueue() {
        return companyService.getTaskQueue();
    }
    
    @GetMapping("/api/tasks/history")
    @ResponseBody
    public Stack<Task> getTaskHistory() {
        return companyService.getTaskHistory();
    }
    
    @PostMapping("/api/tasks")
    @ResponseBody
    public Task createTask(@RequestBody Task task) {
        companyService.addTask(task);
        return task;
    }
    
    @PostMapping("/api/tasks/complete")
    @ResponseBody
    public String completeTask() {
        companyService.completeTask();
        return "Task completed";
    }
    
    @PostMapping("/api/tasks/assign")
    @ResponseBody
    public String assignTask(@RequestParam Integer employeeId, @RequestBody Task task) {
        companyService.assignTaskToEmployee(employeeId, task);
        return "Task assigned to employee " + employeeId;
    }
    
    @GetMapping("/api/employees/department/{departmentId}")
    @ResponseBody
    public List<Employee> getEmployeesByDepartment(@PathVariable Integer departmentId) {
        return companyService.getEmployeesByDepartment(departmentId);
    }
    
    @GetMapping("/api/employee-tasks")
    @ResponseBody
    public Map<Integer, List<Task>> getEmployeeTasks() {
        return companyService.getEmployeeTasks();
    }
}
