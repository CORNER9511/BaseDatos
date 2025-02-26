package com.example.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.db.entities.User;
import com.example.db.model.UserDAO;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button btnGuardar;
    private Context context;
    private EditText etDocumento;
    private EditText etUsuario;
    private EditText etNombres;
    private EditText etApellidos;
    private EditText etContraseña;
    private ListView listUsers;
    private SQLiteDatabase sqLiteDatabase;
    private Button btnListUsers;
    private int documento;
    private String nombres;
    private String apellidos;
    private String usuario;
    private String pass;
    int userList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        begin();
        btnGuardar.setOnClickListener(this::createUser);
        btnListUsers.setOnClickListener(this::listUserShow);
    }

    private void createUser(View view) {
        catchData();
        User user = new User(this.documento,this.nombres,this.apellidos, this.usuario, this.pass);
        UserDAO dao = new UserDAO(this.context,view);
        dao.insertUser(user);
        listUserShow(view);
    }
    private void catchData(){

        this.documento = Integer.parseInt(etDocumento.getText().toString());
        this.nombres = etNombres.getText().toString();
        this.apellidos = etApellidos.getText().toString();
        this.usuario = etUsuario.getText().toString();
        this.pass = etContraseña.getText().toString();
    }

    private void listUserShow(View view){
        UserDAO userDAO = new UserDAO(context,findViewById(R.id.lvLista));
        ArrayList<User> users = userDAO.getUsersList();

        ArrayAdapter<User> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,userList);
        listUsers.setAdapter(adapter);
    }


    private void begin(){
        this.context = this;
        this.btnGuardar = findViewById(R.id.btnRegistrar);
        this.btnListUsers = findViewById(R.id.btnListar);
        this.etNombres = findViewById(R.id.etNombres);
        this.etApellidos = findViewById(R.id.etApellidos);
        this.etDocumento = findViewById(R.id.etDocumento);
        this.etUsuario = findViewById(R.id.etUsuario);
        this.etContraseña = findViewById(R.id.etContraseña);
        this.listUsers = findViewById(R.id.lvLista);
    }
    
}