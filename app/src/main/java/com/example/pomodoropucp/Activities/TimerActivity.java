package com.example.pomodoropucp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.pomodoropucp.ApplicationThreads;
import com.example.pomodoropucp.Interfaces.ChangeTodoStatusService;
import com.example.pomodoropucp.Interfaces.GetTodosService;
import com.example.pomodoropucp.Objects.Todo;
import com.example.pomodoropucp.Objects.TodosResponse;
import com.example.pomodoropucp.Objects.User;
import com.example.pomodoropucp.R;
import com.example.pomodoropucp.ViewModels.CounterWorkViewModel;
import com.example.pomodoropucp.databinding.ActivityTimerBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.concurrent.ExecutorService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TimerActivity extends AppCompatActivity {

    Integer initialTime=1500;
    Integer initialRestTime=300;
    ActivityTimerBinding b;
    ExecutorService executorService;
    CounterWorkViewModel counterWorkViewModel;
    User user;
    Integer time;
    Integer restTime;
    Integer inRest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        b=ActivityTimerBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        Intent intent=getIntent();
        user=(User) intent.getSerializableExtra("user");
        b.nombreCompleto.setText(user.getFirstName()+" "+user.getLastName());
        b.correo.setText(user.getEmail());
        switch (user.getGender()){
            case "female":
                b.imagenGenero.setImageResource(R.drawable.svgfemale);
                break;
            case "male":
                b.imagenGenero.setImageResource(R.drawable.svgmale);
                break;
        }
        ApplicationThreads applicationThreads=(ApplicationThreads)getApplication();
        executorService=applicationThreads.executorService;
        counterWorkViewModel=new ViewModelProvider(this).get(CounterWorkViewModel.class);
        time=counterWorkViewModel.getCounter().getValue();
        restTime=counterWorkViewModel.getRestCounter().getValue();
        inRest=counterWorkViewModel.getInRest().getValue();
        if(counterWorkViewModel.getTaskExecutingCounter().getValue()==null){
            counterWorkViewModel.getTaskExecutingCounter().postValue(0);
        }
        if(inRest!=null){
            b.botonIniciarCronometro.setIconResource(R.drawable.svgrestart);
        }else {
            b.botonIniciarCronometro.setIconResource(R.drawable.svgarrowright);
        }
        b.botonIniciarCronometro.setOnClickListener(view -> {
            b.botonIniciarCronometro.setIconResource(R.drawable.svgrestart);
            if(inRest==null){
                inRest=0;
                counterWorkViewModel.getInRest().postValue(inRest);
                startTime();
            }else {
                b.tiempoSegundoCronometro.setText("Descanso: 05:00");
                b.tiempoPrimerCronometro.setText("25:00");
                inRest=2;
                counterWorkViewModel.getInRest().postValue(inRest);
            }
        });

        counterWorkViewModel.getCounter().observe(this,value->{
            time=counterWorkViewModel.getCounter().getValue();
            drawUpdatedTime(time);
            if(time==0){
                if(inRest!=null){
                     if(inRest==0){
                         inRest=1;
                         counterWorkViewModel.getInRest().postValue(inRest);
                         startRestTime();
                         verifyTodos();
                     }else if(inRest==2){
                         inRest=0;
                         counterWorkViewModel.getInRest().postValue(inRest);
                         startTime();
                     }else {
                         inRest=0;
                         counterWorkViewModel.getInRest().postValue(inRest);
                     }
                }else {
                    inRest=1;
                    counterWorkViewModel.getInRest().postValue(inRest);
                    startRestTime();
                    verifyTodos();
                }
            }
        });

        counterWorkViewModel.getRestCounter().observe(this,value->{
            restTime=counterWorkViewModel.getRestCounter().getValue();
            drawUpdatedTime(restTime);
            if(restTime==0&&inRest==1){
                inRest=null;
                counterWorkViewModel.getInRest().postValue(inRest);
                launchFinishedRestTimeDialog();
            }
        });
    }

    private void startTime(){
        b.tiempoSegundoCronometro.setText("Descanso: 05:00");
        executorService.execute(() -> {
            time=initialTime;
            counterWorkViewModel.getCounter().postValue(time);
            updateTime();
        });

    }

    private void startRestTime(){
        b.tiempoSegundoCronometro.setText("En descanso");
        Log.d("startRestTime","uwu");
        executorService.execute(() -> {
            restTime=initialRestTime;
            counterWorkViewModel.getRestCounter().postValue(restTime);
            updateRestTime();
        });
    }

    private void updateTime(){

        while (time>0){
            try {
                Thread.sleep(1000);
            }catch (InterruptedException e){
                Log.d("aiuda",e.getMessage());
            }
            inRest=counterWorkViewModel.getInRest().getValue();
            if(inRest==2){
                break;
            }
            time--;
            counterWorkViewModel.getCounter().postValue(time);
        }
        if(inRest==2){
            inRest=0;
            counterWorkViewModel.getInRest().postValue(inRest);
            startTime();
        }else {
            inRest=0;
            counterWorkViewModel.getInRest().postValue(inRest);
        }
    }

    private void updateRestTime(){
        while (restTime>0){
            try {
                Thread.sleep(1000);
            }catch (InterruptedException e){
                Log.d("aiuda",e.getMessage());
            }
            inRest=counterWorkViewModel.getInRest().getValue();
            if(inRest==2){
                break;
            }
            restTime--;
            counterWorkViewModel.getRestCounter().postValue(restTime);
        }
        if(inRest==2){
            inRest=0;
            startTime();
        }else {
            inRest=1;
            counterWorkViewModel.getInRest().postValue(inRest);
        }
    }

    private void drawUpdatedTime(Integer time){
        Integer minutosAux=time/60;
        Integer segundosAux=time%60;
        String minutos=minutosAux.toString().length()==1?"0"+ minutosAux :minutosAux.toString();
        String segundos=segundosAux.toString().length()==1?"0"+ segundosAux :segundosAux.toString();
        b.tiempoPrimerCronometro.setText(minutos+":"+segundos);
    }


    private void launchFinishedTimeDialog(){
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Hora del descanso")
                .setMessage("Ya puede dejar de trabajar y comenzar a descansar")
                .setNeutralButton("Entendido", (dialog, which) -> {

                })
                .show();
    }

    private void launchFinishedRestTimeDialog(){
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Atención")
                .setMessage("Terminó el tiempo de descanso. Dale al botón de reinicio para empezar otro ciclo.")
                .setNeutralButton("Entendido", (dialog, which) -> {
                })
                .show();
    }

    private void verifyTodos(){
        GetTodosService getTodosService =new Retrofit.Builder()
                .baseUrl("https://dummyjson.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GetTodosService.class);
        getTodosService.getTodos(user.getId()).enqueue(new Callback<TodosResponse>() {
            @Override
            public void onResponse(Call<TodosResponse> call, Response<TodosResponse> response) {
                List<Todo>todos=response.body().getTodos();
                if(todos.isEmpty()){
                    launchFinishedTimeDialog();
                }else {

                    Intent intent=new Intent(getApplicationContext(),TodosActivity.class);
                    intent.putExtra("user",user);
                    intent.putExtra("todos",(Serializable) todos);
                    launcher.launch(intent);
                }
            }
            @Override
            public void onFailure(Call<TodosResponse> call, Throwable t) {
                Log.d("aiuda",t.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_appbar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.logout){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    ActivityResultLauncher<Intent>launcher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            o -> {
                Intent intent=o.getData();
                if(intent!=null){
                    boolean finish=intent.getBooleanExtra("finish",false);
                    if(finish){
                        finish();
                    }
                }
    });
}