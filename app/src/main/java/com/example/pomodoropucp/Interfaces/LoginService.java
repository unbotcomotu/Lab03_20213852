package com.example.pomodoropucp.Interfaces;

import com.example.pomodoropucp.Objects.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface LoginService {
    @FormUrlEncoded
    @POST("/auth/login")
    Call<User>getUser(@Field("username")String username,@Field("password")String password);
}
