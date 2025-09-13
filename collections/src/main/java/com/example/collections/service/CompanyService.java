package com.example.collections.service;

import com.example.collections.entity.Department;
import com.example.collections.entity.Employee;
import com.example.collections.entity.Task;
import com.example.collections.repository.DepartmentRepository;
import com.example.collections.repository.EmployeeRepository;
import com.example.collections.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CompanyService {
    
    private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    // Java Collections demonstration
    private List<Employee> employees = new ArrayList<>();
    private Set<String> uniqueSkills = new HashSet<>();
    private Map<Integer, Department> departments = new HashMap<>();
    private Queue<Task> taskQueue = new LinkedList<>();
    private Stack<Task> taskHistory = new Stack<>();
    private Map<Integer, List<Task>> employeeTasks = new HashMap<>();
    
    public CompanyService() {
        logger.info("CompanyService initialized with Java Collections");
    }
    
    // Initialize collections from database
    public void initializeCollections() {
        logger.info("=== Initializing Java Collections from Database ===");
        
        // 1. List<Employee> - Demonstrating List operations
        employees.clear();
        employees.addAll(employeeRepository.findAll());
        logger.info("List<Employee> initialized with {} employees", employees.size());
        logger.info("Employees: {}", employees.stream().map(Employee::getName).collect(Collectors.toList()));
        
        // 2. Set<String> - Demonstrating Set operations (unique skills)
        uniqueSkills.clear();
        List<String> allSkills = employeeRepository.findAllUniqueSkills();
        for (String skills : allSkills) {
            if (skills != null && !skills.trim().isEmpty()) {
                String[] skillArray = skills.split(",");
                for (String skill : skillArray) {
                    uniqueSkills.add(skill.trim());
                }
            }
        }
        logger.info("Set<String> uniqueSkills initialized with {} unique skills", uniqueSkills.size());
        logger.info("Unique skills: {}", uniqueSkills);
        
        // 3. Map<Integer, Department> - Demonstrating Map operations
        departments.clear();
        List<Department> deptList = departmentRepository.findAll();
        for (Department dept : deptList) {
            departments.put(dept.getId(), dept);
        }
        logger.info("Map<Integer, Department> initialized with {} departments", departments.size());
        logger.info("Department mapping: {}", departments);
        
        // 4. Queue<Task> - Demonstrating Queue operations (FIFO)
        taskQueue.clear();
        List<Task> allTasks = taskRepository.findAllOrderByPriorityDesc();
        for (Task task : allTasks) {
            taskQueue.offer(task);
        }
        logger.info("Queue<Task> initialized with {} pending tasks", taskQueue.size());
        
        // 5. Stack<Task> - Demonstrating Stack operations (LIFO)
        taskHistory.clear();
        logger.info("Stack<Task> taskHistory initialized (empty)");
        
        // 6. HashMap<Integer, List<Task>> - Demonstrating complex Map operations
        employeeTasks.clear();
        for (Employee employee : employees) {
            employeeTasks.put(employee.getId(), new ArrayList<>());
        }
        logger.info("HashMap<Integer, List<Task>> initialized for {} employees", employeeTasks.size());
    }
    
    // List operations demonstration
    public List<Employee> getAllEmployees() {
        logger.info("=== List<Employee> Operations ===");
        logger.info("Iterating through employees list (size: {})", employees.size());
        
        // Demonstrate list iteration
        for (int i = 0; i < employees.size(); i++) {
            Employee emp = employees.get(i);
            logger.info("Employee {}: {} (Department: {})", i + 1, emp.getName(), emp.getDepartmentId());
        }
        
        // Demonstrate list filtering
        List<Employee> itEmployees = employees.stream()
                .filter(emp -> emp.getDepartmentId() != null && emp.getDepartmentId() == 1)
                .collect(Collectors.toList());
        logger.info("IT Department employees: {}", itEmployees.stream().map(Employee::getName).collect(Collectors.toList()));
        
        return new ArrayList<>(employees);
    }
    
    // Set operations demonstration
    public Set<String> getUniqueSkills() {
        logger.info("=== Set<String> Operations ===");
        logger.info("Unique skills count: {}", uniqueSkills.size());
        
        // Demonstrate set operations
        Set<String> programmingSkills = uniqueSkills.stream()
                .filter(skill -> skill.toLowerCase().contains("java") || 
                               skill.toLowerCase().contains("spring") ||
                               skill.toLowerCase().contains("excel"))
                .collect(Collectors.toSet());
        logger.info("Programming/Technical skills: {}", programmingSkills);
        
        return new HashSet<>(uniqueSkills);
    }
    
    // Map operations demonstration
    public Map<Integer, Department> getDepartments() {
        logger.info("=== Map<Integer, Department> Operations ===");
        logger.info("Departments count: {}", departments.size());
        
        // Demonstrate map iteration
        departments.forEach((id, dept) -> {
            logger.info("Department ID {}: {}", id, dept.getName());
        });
        
        // Demonstrate map lookup
        Department itDept = departments.get(1);
        if (itDept != null) {
            logger.info("IT Department found: {}", itDept.getName());
        }
        
        return new HashMap<>(departments);
    }
    
    // Queue operations demonstration
    public Queue<Task> getTaskQueue() {
        logger.info("=== Queue<Task> Operations ===");
        logger.info("Pending tasks in queue: {}", taskQueue.size());
        
        // Demonstrate queue operations
        if (!taskQueue.isEmpty()) {
            Task nextTask = taskQueue.peek();
            logger.info("Next task to process: {}", nextTask.getDescription());
        }
        
        return new LinkedList<>(taskQueue);
    }
    
    // Stack operations demonstration
    public Stack<Task> getTaskHistory() {
        logger.info("=== Stack<Task> Operations ===");
        logger.info("Completed tasks in history: {}", taskHistory.size());
        
        // Demonstrate stack operations
        if (!taskHistory.isEmpty()) {
            Task lastCompleted = taskHistory.peek();
            logger.info("Last completed task: {}", lastCompleted.getDescription());
        }
        
        return (Stack<Task>) taskHistory.clone();
    }
    
    // Complex Map operations demonstration
    public Map<Integer, List<Task>> getEmployeeTasks() {
        logger.info("=== HashMap<Integer, List<Task>> Operations ===");
        logger.info("Employee task assignments: {}", employeeTasks.size());
        
        // Demonstrate complex map operations
        employeeTasks.forEach((empId, tasks) -> {
            Employee emp = employees.stream()
                    .filter(e -> e.getId().equals(empId))
                    .findFirst()
                    .orElse(null);
            if (emp != null) {
                logger.info("Employee {} has {} tasks assigned", emp.getName(), tasks.size());
            }
        });
        
        return new HashMap<>(employeeTasks);
    }
    
    // Business operations that use collections
    public void assignTaskToEmployee(Integer employeeId, Task task) {
        logger.info("=== Assigning task to employee ===");
        
        if (employeeTasks.containsKey(employeeId)) {
            List<Task> tasks = employeeTasks.get(employeeId);
            tasks.add(task);
            employeeTasks.put(employeeId, tasks);
            
            // Add to queue
            taskQueue.offer(task);
            
            logger.info("Task '{}' assigned to employee ID {}", task.getDescription(), employeeId);
        } else {
            logger.warn("Employee ID {} not found", employeeId);
        }
    }
    
    public void completeTask() {
        logger.info("=== Completing task ===");
        
        if (!taskQueue.isEmpty()) {
            Task completedTask = taskQueue.poll();
            taskHistory.push(completedTask);
            
            logger.info("Task '{}' completed and moved to history", completedTask.getDescription());
            logger.info("Remaining tasks in queue: {}", taskQueue.size());
            logger.info("Total completed tasks: {}", taskHistory.size());
        } else {
            logger.info("No tasks in queue to complete");
        }
    }
    
    public List<Employee> getEmployeesByDepartment(Integer departmentId) {
        logger.info("=== Getting employees by department ===");
        
        return employees.stream()
                .filter(emp -> emp.getDepartmentId() != null && emp.getDepartmentId().equals(departmentId))
                .collect(Collectors.toList());
    }
    
    public void addEmployee(Employee employee) {
        logger.info("=== Adding new employee ===");
        
        Employee savedEmployee = employeeRepository.save(employee);
        employees.add(savedEmployee);
        
        // Add to unique skills
        if (savedEmployee.getSkills() != null) {
            String[] skills = savedEmployee.getSkills().split(",");
            for (String skill : skills) {
                uniqueSkills.add(skill.trim());
            }
        }
        
        // Initialize task list for new employee
        employeeTasks.put(savedEmployee.getId(), new ArrayList<>());
        
        logger.info("Employee '{}' added to collections", savedEmployee.getName());
        logger.info("Updated unique skills: {}", uniqueSkills);
    }
    
    public void addTask(Task task) {
        logger.info("=== Adding new task ===");
        
        Task savedTask = taskRepository.save(task);
        taskQueue.offer(savedTask);
        
        logger.info("Task '{}' added to queue", savedTask.getDescription());
        logger.info("Total tasks in queue: {}", taskQueue.size());
    }
}
