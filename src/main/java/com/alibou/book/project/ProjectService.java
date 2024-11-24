package com.alibou.book.project;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.alibou.book.user.User;
import com.alibou.book.user.UserRepository;

import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.var;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

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
                HttpStatus.CREATED);
    }

    public ResponseEntity<ProjectResponse> getProjectById(Integer id, User currentUser) {
        try {
            var project = projectRepository
                    .findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));

            List<String> membersEmails = project.getMembers().stream().map(User::getEmail).toList();

            if (!project.getOwner().getEmail().equals(currentUser.getEmail()) &&
                    !membersEmails.contains(currentUser.getEmail())) {
                return new ResponseEntity<>(
                        new ProjectResponse(false, "Keine Berechtigung", null),
                        HttpStatus.FORBIDDEN);
            }

            return new ResponseEntity<>(
                    new ProjectResponse(true, "Project gefunden", Collections.singletonList(project)),
                    HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(
                    new ProjectResponse(false, e.getMessage(), null),
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ProjectResponse(false, "Etwas fehlgeschlagen", null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ProjectResponse> getProjects(User currentUser, String searchQuery, LocalDate dueDate) {
        try {
            List<Project> ownedProjects = projectRepository
                    .findByOwnerId(currentUser.getId())
                    .orElse(Collections.emptyList());

            List<Project> memberProjects = projectRepository
                    .findByMembersId(currentUser.getId())
                    .orElse(Collections.emptyList());

            // Combine lists and remove duplicates
            List<Project> allProjects = new ArrayList<>();
            allProjects.addAll(ownedProjects);
            allProjects.addAll(memberProjects);

            List<Project> filteredProjects;
            if (searchQuery != null && dueDate != null) {
                filteredProjects = allProjects
                    .stream()
                    .filter( project ->
                        (project.getName().toLowerCase().contains(searchQuery.toLowerCase()) ||
                        project.getDescription().toLowerCase().contains(searchQuery.toLowerCase())) &&
                        project.getDueDate().isBefore(dueDate)
                    ).toList();
            } else if (searchQuery != null && dueDate == null) {
                filteredProjects = allProjects
                    .stream()
                    .filter( project ->
                        (project.getName().toLowerCase().contains(searchQuery.toLowerCase()) ||
                        project.getDescription().toLowerCase().contains(searchQuery.toLowerCase()))
                    ).toList();
            } else if (searchQuery == null && dueDate != null) {
                filteredProjects = allProjects
                    .stream()
                    .filter( project ->
                        project.getDueDate().isBefore(dueDate)
                    ).toList();
            } else {
                filteredProjects = allProjects
                    .stream()
                    .toList();
            }

            if (filteredProjects.isEmpty()) {
                throw new EntityNotFoundException("Keine Projects gefunden!");
            }

            return new ResponseEntity<>(
                    new ProjectResponse(true, "Projects gefunden", filteredProjects),
                    HttpStatus.OK)
                ;
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

    public ResponseEntity<ProjectResponse> updateProject(
            Integer id,
            ProjectRequest projectRequest,
            User currentUser) {
        try {
            var project = projectRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Kein Projekt gefunden mit id: " + id));

            if (!project.getOwner().getEmail().equals(currentUser.getEmail())) {
                return new ResponseEntity<>(
                        new ProjectResponse(false, "Keine Berechtigung", null),
                        HttpStatus.FORBIDDEN);
            }

            project.setName(projectRequest.getName());
            project.setDescription(projectRequest.getDescription());
            project.setStoryPoints(projectRequest.getStoryPoints());
            project.setDueDate(projectRequest.getDueDate());
            project.setCost(projectRequest.getCost());

            projectRepository.save(project);

            return new ResponseEntity<>(
                    new ProjectResponse(true, "Projekt erfolgreich aktualisiert", Collections.singletonList(project)),
                    HttpStatus.OK);

        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(
                    new ProjectResponse(false, e.getMessage(), null),
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ProjectResponse(false, "Etwas fehlgeschlagen", null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ProjectResponse> deleteProject(Integer id, User currentUser) {
        try {
            var project = projectRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Kein Projekt gefunden mit id: " + id));

            if (!project.getOwner().getEmail().equals(currentUser.getEmail())) {
                return new ResponseEntity<>(
                        new ProjectResponse(false, "Keine Berechtigung", null),
                        HttpStatus.FORBIDDEN);
            }

            projectRepository.deleteById(id);

            return new ResponseEntity<>(
                    new ProjectResponse(true, "Projekt erfolgreich gelöscht", null),
                    HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(
                    new ProjectResponse(false, e.getMessage(), null),
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ProjectResponse(false, "Etwas fehlgeschlagen", null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> addMember(Integer id, String email, User currentUser) {
        try {
            var project = projectRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Kein Projekt gefunden mit id: " + id));

            if (!project.getOwner().getEmail().equals(currentUser.getEmail())) {
                return new ResponseEntity<>(
                        new ProjectResponse(false, "Keine Berechtigung", null),
                        HttpStatus.FORBIDDEN);
            }

            var user = userRepository
                    .findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("Kein User gefunden mit email: " + email));

            project.getMembers().add(user);
            projectRepository.save(project);

            return new ResponseEntity<>(
                    new ProjectResponse(true, "User erfolgreich zum Projekt hinzugefügt",
                            Collections.singletonList(project)),
                    HttpStatus.OK);

        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(
                    new ProjectResponse(false, e.getMessage(), null),
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ProjectResponse(false, "Etwas fehlgeschlagen", null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<FetchEmailsResponse> fetchEmails(String email) {
        try {
            if (email == null || email.length() < 3) {
                return new ResponseEntity<>(
                        new FetchEmailsResponse(false, null),
                        HttpStatus.BAD_REQUEST);
            }

            List<String> matchingEmails = userRepository
                    .findAll()
                    .stream()
                    .map(User::getEmail)
                    .filter(userEmail -> userEmail.toLowerCase()
                            .contains(email.toLowerCase()))
                    .toList();

            if (matchingEmails.isEmpty()) {
                return new ResponseEntity<>(
                        new FetchEmailsResponse(false, null),
                        HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(
                    new FetchEmailsResponse(true, matchingEmails),
                    HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(
                    new FetchEmailsResponse(false, null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ProjectResponse> deleteMember(Integer id, String memberEmail, User currentUser) {
        try {
            var project = projectRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Kein Projekt gefunden mit id: " + id));

            if (!project.getOwner().getEmail().equals(currentUser.getEmail())) {
                return new ResponseEntity<>(
                        new ProjectResponse(false, "Keine Berechtigung", null),
                        HttpStatus.FORBIDDEN);
            }

            var user = userRepository
                    .findByEmail(memberEmail)
                    .orElseThrow(() -> new EntityNotFoundException("Kein User gefunden mit email: " + memberEmail));

            project.getMembers().remove(user);
            projectRepository.save(project);

            return new ResponseEntity<>(
                    new ProjectResponse(true, "User erfolgreich aus dem Projekt entfernt", Collections.singletonList(project)),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ProjectResponse(false, "Etwas fehlgeschlagen", null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
