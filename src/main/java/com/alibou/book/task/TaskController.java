package com.alibou.book.task;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibou.book.user.User;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
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

    @GetMapping("")
    public ResponseEntity<TaskResponse> getTasks(
        @PathVariable Integer projectId
    ) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return taskService.getAllTasks(projectId, currentUser);   
    }
    

}
