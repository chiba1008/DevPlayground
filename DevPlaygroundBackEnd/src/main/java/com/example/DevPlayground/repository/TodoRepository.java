package com.example.DevPlayground.repository;

import com.example.DevPlayground.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByUserNameOrderByDueDate(String userName);
}