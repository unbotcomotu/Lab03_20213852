package com.example.pomodoropucp.Interfaces;

import com.example.pomodoropucp.Objects.Todo;
import com.example.pomodoropucp.Objects.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChangeTodoStatusService {

    //Se empleó un LLM para saber cómo mandar parámetros directamente en la rutaxd :( no estaba en el ppt pipipipipi
    @FormUrlEncoded
    @PUT("/todos/{idTodo}")
    Call<Todo>getTodo(@Path("idTodo")Integer idTodo, @Field("completed")Boolean completed);
}
