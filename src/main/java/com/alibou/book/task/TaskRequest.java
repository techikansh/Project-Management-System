package com.alibou.book.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskRequest {

    @NotBlank(message = "Name ist erforderlich")
    @NotEmpty(message = "Name darf nicht leer sein")
    private String name;

    @NotBlank(message = "Beschreibung ist erforderlich")
    @NotEmpty(message = "Beschreibung darf nicht leer sein")
    private String description;

    @NotBlank(message = "Status ist erforderlich")
    @NotEmpty(message = "Status darf nicht leer sein")
    private String status;

}
