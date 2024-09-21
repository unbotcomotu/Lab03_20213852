package com.example.pomodoropucp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.pomodoropucp.ApplicationThreads;
import com.example.pomodoropucp.Interfaces.ChangeTodoStatusService;
import com.example.pomodoropucp.Objects.Todo;
import com.example.pomodoropucp.Objects.User;
import com.example.pomodoropucp.R;
import com.example.pomodoropucp.databinding.ActivityTodosBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TodosActivity extends AppCompatActivity {
    ActivityTodosBinding b;
    List<Todo>todos;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        b=ActivityTodosBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        Intent intent=getIntent();
        user=(User)intent.getSerializableExtra("user");
        if(savedInstanceState!=null){
            todos=(List<Todo>) savedInstanceState.getSerializable("todos");
        }else{
            todos=(List<Todo>) intent.getSerializableExtra("todos");
        }


        updateSpinner(todos);
        b.textoVerTareas.setText("Ver tareas de "+user.getFirstName()+" "+user.getLastName());
        b.botonCambiarEstado.setOnClickListener(view -> {
            Integer position=b.selectorTareas.getSelectedItemPosition();
            Todo todoSelected=todos.get(position);
            changeStatus(todoSelected,position);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_appbar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void updateSpinner(List<Todo> todos){
        List<String>inputSpinner=new ArrayList<>();
        todos.forEach(todo -> inputSpinner.add(todo.getTodo() + " - " + (todo.getCompleted() ? "Completado" : "No completado")));
        ArrayAdapter<String>adapter=new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,inputSpinner);
        b.selectorTareas.setAdapter(adapter);
    }

    private void changeStatus(Todo todo,Integer position){
        ChangeTodoStatusService changeTodoStatusService =new Retrofit.Builder()
                .baseUrl("https://dummyjson.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ChangeTodoStatusService.class);
        changeTodoStatusService.getTodo(todo.getId(),!todo.getCompleted()).enqueue(new Callback<Todo>() {
            @Override
            public void onResponse(Call<Todo> call, Response<Todo> response) {
                launchSuccesfulHTTPRequestDialog();
                todos.set(position, response.body());
                updateSpinner(todos);
            }

            @Override
            public void onFailure(Call<Todo> call, Throwable t) {
                Log.d("aiudaaa",t.getMessage());
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            finish();
            return true;
        }else if(item.getItemId()==R.id.logout){
            Intent intent=new Intent(this,TimerActivity.class);
            intent.putExtra("finish",true);
            setResult(RESULT_OK,intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchSuccesfulHTTPRequestDialog(){
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Estado cambiado")
                .setMessage("El estado de la tarea fue cambiado exitosamente.")
                .setNeutralButton("Entendido", (dialog, which) -> {
                })
                .show();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("todos",(Serializable)todos);
    }
}