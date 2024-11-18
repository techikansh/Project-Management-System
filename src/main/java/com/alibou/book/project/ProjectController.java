package com.alibou.book.project;

import java.util.List;

import org.springframework.http.HttpStatus;
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
import org.springframework.web.service.annotation.GetExchange;

import com.alibou.book.user.User;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;



    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Integer id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return projectService.getProjectById(id, currentUser);
    }

    @GetMapping("/")
    public ResponseEntity<ProjectResponse> getProjects() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return projectService.getProjects(currentUser);
    }


    @PostMapping("/create-project")
    public ResponseEntity<ProjectResponse> createProject(@RequestBody @Valid ProjectRequest projectRequest) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentUser == null) {
            return new ResponseEntity<>(new ProjectResponse(
                    false,
                    "kein User gefunden",
                    null
            ),
                    HttpStatus.UNAUTHORIZED
            );
        }
        return projectService.createProject(projectRequest, currentUser);
    }

    @PutMapping("/update-project/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
        @PathVariable Integer id,
        @RequestBody @Valid ProjectRequest projectRequest
    ) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return projectService.updateProject(id, projectRequest, currentUser);
    }

    @DeleteMapping("/delete-project/{id}")
    public ResponseEntity<ProjectResponse> deleteProject(@PathVariable Integer id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return projectService.deleteProject(id, currentUser);
    }




}
