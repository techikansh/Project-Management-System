package com.alibou.book.task;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibou.book.project.ProjectRepository;
import com.alibou.book.user.User;
import com.alibou.book.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;


    public ResponseEntity<TaskResponse> createTask(TaskRequest taskRequest, Integer projectId, User currentUser) {

        try {
            var project = projectRepository
                .findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("kein Projekt mit id " + projectId));

            List<String> membersEmails = project.getMembers().stream().map(User::getEmail).toList();
            if (
                !project.getOwner().getEmail().equals(currentUser.getEmail()) && 
                !membersEmails.contains(currentUser.getEmail())
            ) {
                return new ResponseEntity<>(new TaskResponse(false, "Keine Berechtigung", null), HttpStatus.FORBIDDEN);
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

            
            List<String> membersEmails = project.getMembers().stream().map(User::getEmail).toList();
            if (
                !project.getOwner().getEmail().equals(currentUser.getEmail()) && 
                !membersEmails.contains(currentUser.getEmail())
            ) {
                return new ResponseEntity<>(new TaskResponse(false, "Keine Berechtigung", null), HttpStatus.FORBIDDEN);
            }

            return new ResponseEntity<>(
                new TaskResponse(true, "Tasks erforderlich geladen", project.getTasks()),
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

    public ResponseEntity<TaskResponse> getTask(Integer projectId, Integer taskId, User currentUser) {
        try {
            System.out.println("getTaskService");
            var project = projectRepository
                .findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("kein Projekt mit id: " + projectId));

            
            List<String> membersEmails = project.getMembers().stream().map(User::getEmail).toList();
            if (
                !project.getOwner().getEmail().equals(currentUser.getEmail()) && 
                !membersEmails.contains(currentUser.getEmail())
            ) {
                return new ResponseEntity<>(new TaskResponse(false, "Keine Berechtigung", null), HttpStatus.FORBIDDEN);
            }

            var task = taskRepository
                .findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("kein Task mit id: " + taskId));

            return new ResponseEntity<>(
                new TaskResponse(true, "Task erfolgreich geladen", Collections.singletonList(task)),
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

    // @Transactional
    public ResponseEntity<TaskResponse> deleteTask(Integer projectId, Integer taskId, User currentUser) {
        try {
            var project = projectRepository
            .findById(projectId)
            .orElseThrow(() -> new EntityNotFoundException("kein Project mit id:" + projectId));

            List<String> membersEmails = project.getMembers().stream().map(User::getEmail).toList();
            if (
                !project.getOwner().getEmail().equals(currentUser.getEmail()) && 
                !membersEmails.contains(currentUser.getEmail())
            ) {
                return new ResponseEntity<>(new TaskResponse(false, "Keine Berechtigung", null), HttpStatus.FORBIDDEN);
            }
            
            var task = taskRepository
                .findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("kein Task mit id:" + taskId));

            project.getTasks().remove(task);
            projectRepository.save(project);

            // taskRepository.delete(task);
            // project = projectRepository.findById(projectId).get();
            
            return new ResponseEntity<>(
                new TaskResponse(true, "Task gelöscht", project.getTasks()),
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

    public ResponseEntity<TaskResponse> updateTask(
        Integer projectId, 
        Integer taskId, 
        User currentUser, 
        TaskRequest taskRequest
    ) {
        try {
            var project = projectRepository
                .findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("kein Project mit id:" + projectId));

            List<String> membersEmails = project.getMembers().stream().map(User::getEmail).toList();
            if (
                !project.getOwner().getEmail().equals(currentUser.getEmail()) && 
                !membersEmails.contains(currentUser.getEmail())
            ) {
                return new ResponseEntity<>(new TaskResponse(false, "Keine Berechtigung", null), HttpStatus.FORBIDDEN);
            }

            var task = taskRepository
                .findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("kein Task mit id:" + taskId));

            task.setName(taskRequest.getName());
            task.setDescription(taskRequest.getDescription());
            task.setStatus(taskRequest.getStatus());

            taskRepository.save(task);
            return new ResponseEntity<>(
                new TaskResponse(true, "Task aktualisiert", Collections.singletonList(task)),
                HttpStatus.OK
            );            
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(
                new TaskResponse(false, e.getMessage(), null),
                HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                new TaskResponse(false, "Fehler beim Aktualisieren des Tasks", null),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }



}
