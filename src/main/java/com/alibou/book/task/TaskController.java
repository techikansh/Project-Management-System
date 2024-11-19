package com.alibou.book.task;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibou.book.user.User;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@Tag(name = "Task")
@RequestMapping("/api/projects/{projectId}/tasks")
public class TaskController {

    private final TaskService taskService;


    @PostMapping("/create-task")
    public ResponseEntity<TaskResponse> createTask(
        @RequestBody @Valid TaskRequest taskRequest,
        @PathVariable Integer projectId
    ) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return taskService.createTask(taskRequest, projectId, currentUser);   
    }

    @PutMapping("/update-task/{taskId}")
    public ResponseEntity<TaskResponse> updateTask (
        @PathVariable Integer projectId,
        @PathVariable Integer taskId,
        @RequestBody @Valid TaskRequest taskRequest
    ) {
        
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return taskService.updateTask(projectId, taskId, currentUser, taskRequest);
    }

    @DeleteMapping("/delete-task/{taskId}")
    public ResponseEntity<TaskResponse> deleteTask (
        @PathVariable Integer projectId,
        @PathVariable Integer taskId
    ) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return taskService.deleteTask(projectId, taskId, currentUser);
    }


    @GetMapping("")
    public ResponseEntity<TaskResponse> getTasks(
        @PathVariable Integer projectId
    ) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return taskService.getAllTasks(projectId, currentUser);   
    }

    @GetMapping("/{taskId}") 
    public ResponseEntity<TaskResponse> getTask (
        @PathVariable Integer projectId,
        @PathVariable Integer taskId
    ) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return taskService.getTask(projectId, taskId, currentUser);
    }
    
    

}
