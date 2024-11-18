package com.alibou.book.project;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest {

    @NotBlank(message = "Name is required")
    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Description is required")
    @NotEmpty(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Story points are required")
    private Integer storyPoints;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    @NotNull(message = "Cost is required")
    private Integer cost;

}
