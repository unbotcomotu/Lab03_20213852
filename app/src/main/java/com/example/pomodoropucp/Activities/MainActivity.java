package com.example.pomodoropucp.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pomodoropucp.Interfaces.LoginService;
import com.example.pomodoropucp.Objects.User;
import com.example.pomodoropucp.databinding.ActivityMainBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding b;
    NetworkInfo activeNetworkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b=ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(b.getRoot());

        ConnectivityManager manager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo=manager.getActiveNetworkInfo();

        b.botonIniciarSesion.setOnClickListener(view -> login());

    }

    private boolean hasInternet(){
        return activeNetworkInfo!=null&&activeNetworkInfo.isConnected();
    }

    private void login(){
        if(hasInternet()){
            String nombre=b.inputNombre.getText().toString();
            String password=b.inputContrasena.getText().toString();
            LoginService loginService =new Retrofit.Builder()
                    .baseUrl("https://dummyjson.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(LoginService.class);

            loginService.getUser(nombre,password).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if(response.isSuccessful()){
                        Intent intent=new Intent(getApplicationContext(), TimerActivity.class);
                        intent.putExtra("user",response.body());
                        startActivity(intent);
                    }else {
                        if(response.code()==400){
                            b.textoError.setText("Credenciales incorrectas");
                            b.textoError.setTextColor(Color.RED);
                        }else {
                            b.textoError.setText("Ha ocurrido un error");
                            b.textoError.setTextColor(Color.RED);
                        }
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.d("error",t.toString());
                    b.textoError.setText("Ha ocurrido un error desconocido");
                    b.textoError.setTextColor(Color.RED);
                }
            });
        }else {
            b.textoError.setText("Ha ocurrido un error de conexión a Internet");
        }
    }

}