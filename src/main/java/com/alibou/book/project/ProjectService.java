package com.alibou.book.project;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.alibou.book.user.User;

import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ResponseEntity<ProjectResponse> createProject(ProjectRequest projectRequest, User currentUser) {
        var project = Project.builder()
                .name(projectRequest.getName())
                .description(projectRequest.getDescription())
                .storyPoints(projectRequest.getStoryPoints())
                .dueDate(projectRequest.getDueDate())
                .cost(projectRequest.getCost())
                .owner(currentUser)
                .build();
        
        projectRepository.save(project);
        return new ResponseEntity<>(
            new ProjectResponse(true, "Project created successfully", Collections.singletonList(project)),
            HttpStatus.CREATED
        );
    }

    public ResponseEntity<ProjectResponse> getProjectById(Integer id, User currentUser) {
        try {
            var project = projectRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));
        
            if (!project.getOwner().getEmail().equals(currentUser.getEmail())) {
                return new ResponseEntity<>(
                    new ProjectResponse(false, "Keine Berechtigung", null),
                    HttpStatus.FORBIDDEN
                );
            }

            return new ResponseEntity<>(
                new ProjectResponse(true, "Project gefunden", Collections.singletonList(project)),
                HttpStatus.OK
            );    
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(
                new ProjectResponse(false, e.getMessage(), null),
                HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                new ProjectResponse(false, "Etwas fehlgeschlagen", null),
                HttpStatus.INTERNAL_SERVER_ERROR
            );    
        }
    }

    public ResponseEntity<ProjectResponse> getProjects(User currentUser) {
        try {
            var projects = projectRepository
                .findByOwnerId(currentUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("Keine Projects gefunden!"));

            System.out.println(projects);
            
            return new ResponseEntity<>(
                new ProjectResponse(true, "Projects gefunden", projects),
                HttpStatus.OK
            );
        }catch (EntityNotFoundException e) {
            return new ResponseEntity<>(
                new ProjectResponse(false, e.getMessage(), null),
                HttpStatus.NOT_FOUND
            );
        } 
        catch (Exception e) {
            return new ResponseEntity<>(
                new ProjectResponse(false, "Etwas fehlgeschlagen", null),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<ProjectResponse> updateProject(
        Integer id, 
        ProjectRequest projectRequest, 
        User currentUser
    ) {
        try {
            var project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kein Projekt gefunden mit id: " + id));

            if (!project.getOwner().getEmail().equals(currentUser.getEmail())) {
                return new ResponseEntity<>(
                    new ProjectResponse(false, "Keine Berechtigung", null),
                    HttpStatus.FORBIDDEN
                );
            }
            
            project.setName(projectRequest.getName());
            project.setDescription(projectRequest.getDescription());
            project.setStoryPoints(projectRequest.getStoryPoints());
            project.setDueDate(projectRequest.getDueDate());
            project.setCost(projectRequest.getCost());

            projectRepository.save(project);

            return new ResponseEntity<>(
                new ProjectResponse(true, "Projekt erfolgreich aktualisiert", Collections.singletonList(project)),
                HttpStatus.OK
            );

        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(
                new ProjectResponse(false, e.getMessage(), null),
                HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                new ProjectResponse(false, "Etwas fehlgeschlagen", null),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<ProjectResponse> deleteProject(Integer id, User currentUser) {
        try {
            var project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kein Projekt gefunden mit id: " + id));

            if (!project.getOwner().getEmail().equals(currentUser.getEmail())) {
                return new ResponseEntity<>(
                    new ProjectResponse(false, "Keine Berechtigung", null),
                    HttpStatus.FORBIDDEN
                );
            }

            projectRepository.deleteById(id);

            return new ResponseEntity<>(
                new ProjectResponse(true, "Projekt erfolgreich gelöscht", null),
                HttpStatus.OK
            );
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(
                new ProjectResponse(false, e.getMessage(), null),
                HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                new ProjectResponse(false, "Etwas fehlgeschlagen", null),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

}
