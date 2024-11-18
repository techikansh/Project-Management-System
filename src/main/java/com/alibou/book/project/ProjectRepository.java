package com.alibou.book.project;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;



public interface ProjectRepository extends JpaRepository<Project, Integer>{

    Optional<List<Project>> findByOwnerId(Integer userId);
    

}
