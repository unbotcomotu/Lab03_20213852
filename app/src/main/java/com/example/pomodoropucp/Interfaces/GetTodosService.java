package com.example.pomodoropucp.Interfaces;

import com.example.pomodoropucp.Objects.Todo;
import com.example.pomodoropucp.Objects.TodosResponse;
import com.example.pomodoropucp.Objects.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetTodosService {
    @GET("todos/user/{idUser}")
    Call<TodosResponse>getTodos(@Path("idUser")Integer id);
}
