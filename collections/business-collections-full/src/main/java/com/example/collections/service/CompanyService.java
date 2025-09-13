package com.example.collections.service;

import com.example.collections.entity.Department;
import com.example.collections.entity.Employee;
import com.example.collections.entity.Task;
import com.example.collections.repository.DepartmentRepository;
import com.example.collections.repository.EmployeeRepository;
import com.example.collections.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CompanyService {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    // List Collections
    private List<Employee> employeeList = new ArrayList<>();
    private LinkedList<Employee> employeeLinkedList = new LinkedList<>();
    
    // Set Collections
    private Set<String> skillSet = new HashSet<>();
    private TreeSet<String> sortedSkills = new TreeSet<>();
    private LinkedHashSet<String> orderedSkills = new LinkedHashSet<>();
    
    // Map Collections
    private Map<Integer, Department> departmentMap = new HashMap<>();
    private TreeMap<Integer, Department> sortedDepartmentMap = new TreeMap<>();
    
    // Queue Collections
    private Queue<Task> taskQueue = new LinkedList<>();
    private PriorityQueue<Task> priorityQueue = new PriorityQueue<>(Comparator.comparing(Task::getPriority));
    
    // Stack and Deque Collections
    private Stack<Task> completedTasks = new Stack<>();
    private Deque<Task> taskDeque = new ArrayDeque<>();
    
    // Vector and Enumeration
    private Vector<String> projectHistory = new Vector<>();
    
    // Initialize collections with data
    public void initializeCollections() {
        System.out.println("=== Initializing Collections ===");
        
        // Load data from database
        List<Employee> employees = employeeRepository.findAll();
        List<Department> departments = departmentRepository.findAll();
        List<Task> tasks = taskRepository.findAll();
        
        // Initialize List collections
        employeeList.addAll(employees);
        employeeLinkedList.addAll(employees);
        System.out.println("ArrayList<Employee> initialized with " + employeeList.size() + " employees");
        System.out.println("LinkedList<Employee> initialized with " + employeeLinkedList.size() + " employees");
        
        // Initialize Set collections with unique skills
        for (Employee employee : employees) {
            if (employee.getSkills() != null) {
                String[] skills = employee.getSkills().split(",");
                for (String skill : skills) {
                    String trimmedSkill = skill.trim();
                    skillSet.add(trimmedSkill);
                    sortedSkills.add(trimmedSkill);
                    orderedSkills.add(trimmedSkill);
                }
            }
        }
        System.out.println("HashSet<String> initialized with " + skillSet.size() + " unique skills");
        System.out.println("TreeSet<String> initialized with " + sortedSkills.size() + " sorted skills");
        System.out.println("LinkedHashSet<String> initialized with " + orderedSkills.size() + " ordered skills");
        
        // Initialize Map collections
        for (Department department : departments) {
            departmentMap.put(department.getId().intValue(), department);
            sortedDepartmentMap.put(department.getId().intValue(), department);
        }
        System.out.println("HashMap<Integer, Department> initialized with " + departmentMap.size() + " departments");
        System.out.println("TreeMap<Integer, Department> initialized with " + sortedDepartmentMap.size() + " departments");
        
        // Initialize Queue collections
        List<Task> pendingTasks = taskRepository.findByStatus("PENDING");
        taskQueue.addAll(pendingTasks);
        priorityQueue.addAll(pendingTasks);
        System.out.println("Queue<Task> (LinkedList) initialized with " + taskQueue.size() + " pending tasks");
        System.out.println("PriorityQueue<Task> initialized with " + priorityQueue.size() + " tasks sorted by priority");
        
        // Initialize Stack and Deque collections
        List<Task> completedTasksList = taskRepository.findByStatus("COMPLETED");
        completedTasks.addAll(completedTasksList);
        taskDeque.addAll(pendingTasks);
        System.out.println("Stack<Task> initialized with " + completedTasks.size() + " completed tasks");
        System.out.println("Deque<Task> (ArrayDeque) initialized with " + taskDeque.size() + " tasks");
        
        // Initialize Vector with project history
        projectHistory.addAll(Arrays.asList(
            "Project Alpha initiated",
            "Team assembled for Project Alpha",
            "Requirements gathering completed",
            "Design phase started",
            "Development phase initiated",
            "Testing phase completed",
            "Project Alpha delivered",
            "Project Beta planning started",
            "Project Beta development in progress"
        ));
        System.out.println("Vector<String> initialized with " + projectHistory.size() + " project history entries");
        
        System.out.println("=== Collections Initialization Complete ===\n");
    }
    
    // List Operations
    public List<Employee> getAllEmployeesAsArrayList() {
        System.out.println("Using ArrayList<Employee> - Fast random access, good for frequent reads");
        return new ArrayList<>(employeeList);
    }
    
    public LinkedList<Employee> getAllEmployeesAsLinkedList() {
        System.out.println("Using LinkedList<Employee> - Efficient for frequent insertions/deletions");
        return new LinkedList<>(employeeLinkedList);
    }
    
    // Set Operations
    public Set<String> getAllUniqueSkills() {
        System.out.println("Using HashSet<String> - Fast lookups, no duplicates, no order");
        return new HashSet<>(skillSet);
    }
    
    public TreeSet<String> getSortedSkills() {
        System.out.println("Using TreeSet<String> - Sorted order, no duplicates");
        return new TreeSet<>(sortedSkills);
    }
    
    public LinkedHashSet<String> getOrderedSkills() {
        System.out.println("Using LinkedHashSet<String> - Insertion order preserved, no duplicates");
        return new LinkedHashSet<>(orderedSkills);
    }
    
    // Map Operations
    public Map<Integer, Department> getAllDepartmentsAsHashMap() {
        System.out.println("Using HashMap<Integer, Department> - Fast lookups, no order");
        return new HashMap<>(departmentMap);
    }
    
    public TreeMap<Integer, Department> getAllDepartmentsAsTreeMap() {
        System.out.println("Using TreeMap<Integer, Department> - Sorted by key, efficient range operations");
        return new TreeMap<>(sortedDepartmentMap);
    }
    
    // Queue Operations
    public Queue<Task> getTaskQueue() {
        System.out.println("Using Queue<Task> (LinkedList) - FIFO order, efficient for processing");
        return new LinkedList<>(taskQueue);
    }
    
    public PriorityQueue<Task> getPriorityTaskQueue() {
        System.out.println("Using PriorityQueue<Task> - Tasks sorted by priority (1=highest, 3=lowest)");
        return new PriorityQueue<>(priorityQueue);
    }
    
    // Stack and Deque Operations
    public Stack<Task> getCompletedTasksStack() {
        System.out.println("Using Stack<Task> - LIFO order, last completed task on top");
        return new Stack<Task>() {{
            addAll(completedTasks);
        }};
    }
    
    public Deque<Task> getTaskDeque() {
        System.out.println("Using Deque<Task> (ArrayDeque) - Double-ended queue, efficient add/remove from both ends");
        return new ArrayDeque<>(taskDeque);
    }
    
    // Vector and Enumeration Operations
    public Vector<String> getProjectHistoryVector() {
        System.out.println("Using Vector<String> - Thread-safe, synchronized operations");
        return new Vector<>(projectHistory);
    }
    
    public Enumeration<String> getProjectHistoryEnumeration() {
        System.out.println("Using Enumeration<String> - Legacy iterator for Vector");
        return projectHistory.elements();
    }
    
    // Additional utility methods
    public void addProjectHistoryEntry(String entry) {
        projectHistory.add(entry);
        System.out.println("Added to Vector<String>: " + entry);
    }
    
    public Task processNextTask() {
        if (!taskQueue.isEmpty()) {
            Task task = taskQueue.poll();
            System.out.println("Processing task from Queue: " + task.getDescription());
            return task;
        }
        return null;
    }
    
    public Task processNextPriorityTask() {
        if (!priorityQueue.isEmpty()) {
            Task task = priorityQueue.poll();
            System.out.println("Processing priority task: " + task.getDescription() + " (Priority: " + task.getPriority() + ")");
            return task;
        }
        return null;
    }
    
    public void completeTask(Task task) {
        task.setStatus("COMPLETED");
        completedTasks.push(task);
        System.out.println("Task completed and added to Stack: " + task.getDescription());
    }
    
    public Task getLastCompletedTask() {
        if (!completedTasks.isEmpty()) {
            Task task = completedTasks.peek();
            System.out.println("Last completed task from Stack: " + task.getDescription());
            return task;
        }
        return null;
    }
}
