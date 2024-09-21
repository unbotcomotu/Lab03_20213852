package com.example.pomodoropucp.Objects;

import java.io.Serializable;
import java.util.List;

public class TodosResponse implements Serializable {
    private List<Todo> todos;

    public List<Todo> getTodos() {
        return todos;
    }

    public void setTodos(List<Todo> todos) {
        this.todos = todos;
    }
}
