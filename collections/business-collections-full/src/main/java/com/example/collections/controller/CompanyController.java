package com.example.collections.controller;

import com.example.collections.entity.Department;
import com.example.collections.entity.Employee;
import com.example.collections.entity.Task;
import com.example.collections.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class CompanyController {
    
    @Autowired
    private CompanyService companyService;
    
    @GetMapping("/")
    public String home(Model model) {
        companyService.initializeCollections();
        model.addAttribute("message", "Welcome to Business Collections Demo!");
        return "index";
    }
    
    @GetMapping("/employees")
    public String employees(Model model) {
        System.out.println("\n=== EMPLOYEES ENDPOINT ===");
        
        // Demonstrate List collections
        List<Employee> arrayListEmployees = companyService.getAllEmployeesAsArrayList();
        LinkedList<Employee> linkedListEmployees = companyService.getAllEmployeesAsLinkedList();
        
        model.addAttribute("arrayListEmployees", arrayListEmployees);
        model.addAttribute("linkedListEmployees", linkedListEmployees);
        model.addAttribute("arrayListSize", arrayListEmployees.size());
        model.addAttribute("linkedListSize", linkedListEmployees.size());
        
        return "employees";
    }
    
    @GetMapping("/departments")
    public String departments(Model model) {
        System.out.println("\n=== DEPARTMENTS ENDPOINT ===");
        
        // Demonstrate Map collections
        Map<Integer, Department> hashMapDepartments = companyService.getAllDepartmentsAsHashMap();
        TreeMap<Integer, Department> treeMapDepartments = companyService.getAllDepartmentsAsTreeMap();
        
        model.addAttribute("hashMapDepartments", hashMapDepartments);
        model.addAttribute("treeMapDepartments", treeMapDepartments);
        model.addAttribute("hashMapSize", hashMapDepartments.size());
        model.addAttribute("treeMapSize", treeMapDepartments.size());
        
        return "departments";
    }
    
    @GetMapping("/skills")
    public String skills(Model model) {
        System.out.println("\n=== SKILLS ENDPOINT ===");
        
        // Demonstrate Set collections
        Set<String> hashSetSkills = companyService.getAllUniqueSkills();
        TreeSet<String> treeSetSkills = companyService.getSortedSkills();
        LinkedHashSet<String> linkedHashSetSkills = companyService.getOrderedSkills();
        
        model.addAttribute("hashSetSkills", hashSetSkills);
        model.addAttribute("treeSetSkills", treeSetSkills);
        model.addAttribute("linkedHashSetSkills", linkedHashSetSkills);
        model.addAttribute("hashSetSize", hashSetSkills.size());
        model.addAttribute("treeSetSize", treeSetSkills.size());
        model.addAttribute("linkedHashSetSize", linkedHashSetSkills.size());
        
        return "skills";
    }
    
    @GetMapping("/tasks")
    public String tasks(Model model) {
        System.out.println("\n=== TASKS ENDPOINT ===");
        
        // Demonstrate Queue, PriorityQueue, Stack, and Deque collections
        Queue<Task> taskQueue = companyService.getTaskQueue();
        PriorityQueue<Task> priorityQueue = companyService.getPriorityTaskQueue();
        Stack<Task> completedTasksStack = companyService.getCompletedTasksStack();
        Deque<Task> taskDeque = companyService.getTaskDeque();
        
        // Convert to lists for display
        List<Task> taskQueueList = new ArrayList<>(taskQueue);
        List<Task> priorityQueueList = new ArrayList<>(priorityQueue);
        List<Task> completedTasksList = new ArrayList<>(completedTasksStack);
        List<Task> taskDequeList = new ArrayList<>(taskDeque);
        
        model.addAttribute("taskQueue", taskQueueList);
        model.addAttribute("priorityQueue", priorityQueueList);
        model.addAttribute("completedTasks", completedTasksList);
        model.addAttribute("taskDeque", taskDequeList);
        model.addAttribute("queueSize", taskQueue.size());
        model.addAttribute("priorityQueueSize", priorityQueue.size());
        model.addAttribute("completedTasksSize", completedTasksStack.size());
        model.addAttribute("dequeSize", taskDeque.size());
        
        return "tasks";
    }
    
    @GetMapping("/history")
    public String history(Model model) {
        System.out.println("\n=== HISTORY ENDPOINT ===");
        
        // Demonstrate Vector and Enumeration
        Vector<String> projectHistory = companyService.getProjectHistoryVector();
        Enumeration<String> historyEnumeration = companyService.getProjectHistoryEnumeration();
        
        // Convert Enumeration to List for display
        List<String> enumerationList = new ArrayList<>();
        while (historyEnumeration.hasMoreElements()) {
            enumerationList.add(historyEnumeration.nextElement());
        }
        
        model.addAttribute("projectHistory", projectHistory);
        model.addAttribute("enumerationList", enumerationList);
        model.addAttribute("vectorSize", projectHistory.size());
        model.addAttribute("enumerationSize", enumerationList.size());
        
        return "history";
    }
    
    @PostMapping("/add-history")
    public String addHistoryEntry(@RequestParam String entry) {
        companyService.addProjectHistoryEntry(entry);
        return "redirect:/history";
    }
    
    @PostMapping("/process-task")
    public String processTask(Model model) {
        Task processedTask = companyService.processNextTask();
        if (processedTask != null) {
            model.addAttribute("processedTask", processedTask);
        }
        return "redirect:/tasks";
    }
    
    @PostMapping("/process-priority-task")
    public String processPriorityTask(Model model) {
        Task processedTask = companyService.processNextPriorityTask();
        if (processedTask != null) {
            model.addAttribute("processedTask", processedTask);
        }
        return "redirect:/tasks";
    }
}
