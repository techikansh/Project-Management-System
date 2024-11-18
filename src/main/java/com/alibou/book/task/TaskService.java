package com.alibou.book.task;

import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.alibou.book.project.ProjectRepository;
import com.alibou.book.user.User;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public ResponseEntity<TaskResponse> createTask(TaskRequest taskRequest, Integer projectId, User currentUser) {

        try {
            var project = projectRepository
                .findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("kein Projekt mit id " + projectId));

            if (!project.getOwner().getEmail().equals(currentUser.getEmail())) {
                return new ResponseEntity<>(
                    new TaskResponse(false, "Keine Berechtigung!", null),
                    HttpStatus.FORBIDDEN
                );
            }

            var task = Task.builder()
                .name(taskRequest.getName())
                .description(taskRequest.getDescription())
                .status(taskRequest.getStatus())
                .project(project)
                .build();    

            // Not needed, because of CascadeType.ALL in Project.java
            // project.getTasks().add(task);
            // projectRepository.save(project);

            taskRepository.save(task);

            return new ResponseEntity<>(
                new TaskResponse(true, "Task erfolgreich hinzugefügt", Collections.singletonList(task)),
                HttpStatus.CREATED
            );

        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(
                new TaskResponse(false, e.getMessage(), null),
                HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                new TaskResponse(false, "Fehler beim Erstellen der Aufgabe", null),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
        

    }

    public ResponseEntity<TaskResponse> getAllTasks(Integer projectId, User currentUser) {
        try {
            var project = projectRepository
                .findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("kein Projekt mit id " + projectId));

            if (!project.getOwner().getEmail().equals(currentUser.getEmail())) {
                return new ResponseEntity<>(new TaskResponse(false, "Keine Berechtigung", null), HttpStatus.FORBIDDEN);
            }

            return new ResponseEntity<>(
                new TaskResponse(true, "Aufgaben erforderlich geladen", project.getTasks()),
                HttpStatus.OK
            );
            
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(
                new TaskResponse(false, e.getMessage(), null),
                HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                new TaskResponse(false, "Fehler beim Laden der Aufgaben", null),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
        
        
    }

}
