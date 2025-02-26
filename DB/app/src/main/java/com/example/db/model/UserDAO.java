package com.example.db.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;

import com.example.db.entities.User;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class UserDAO {

    private ManagerDBUser dbUser;
    private Context context;
    private View view;

    public UserDAO(Context context, View view) {
        this.context = context;
        this.view = view;
        this.dbUser = new ManagerDBUser(this.context);
    }

    // Expresiones regulares para validaciones
    private static final Pattern DOCUMENT_PATTERN = Pattern.compile("^[0-9]{6,12}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]{3,}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{4,}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    // Método para validar los datos antes de registrar
    private boolean validateUser(User user) {
        if (!DOCUMENT_PATTERN.matcher(user.getDocument()).matches()) {
            Snackbar.make(view, "Documento inválido (Debe tener entre 6 y 12 dígitos)", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (!NAME_PATTERN.matcher(user.getNames()).matches()) {
            Snackbar.make(view, "Nombre inválido (Solo letras y mínimo 3 caracteres)", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (!NAME_PATTERN.matcher(user.getLastNames()).matches()) {
            Snackbar.make(view, "Apellido inválido (Solo letras y mínimo 3 caracteres)", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (!USERNAME_PATTERN.matcher(user.getUser()).matches()) {
            Snackbar.make(view, "Usuario inválido (Mínimo 4 caracteres, solo letras, números, guiones y guion bajo)", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (!PASSWORD_PATTERN.matcher(user.getPassword()).matches()) {
            Snackbar.make(view, "Contraseña inválida (Mínimo 8 caracteres, incluyendo mayúsculas, minúsculas, número y símbolo)", Snackbar.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    // Método para verificar si un usuario ya está registrado
    private boolean isUserRegistered(String document, String username) {
        try (SQLiteDatabase db = dbUser.getReadableDatabase();
             Cursor cursor = db.rawQuery("SELECT * FROM users WHERE use_document = ? OR use_user = ?", new String[]{document, username})) {
            return cursor.moveToFirst(); // Devuelve true si encuentra un usuario
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error al verificar usuario: " + e.getMessage());
            return false;
        }
    }

    // Método para insertar un usuario en la base de datos con validaciones
    public void insertUser(User user) {
        if (!validateUser(user)) {
            return; // Si no pasa las validaciones, no lo inserta
        }
        if (isUserRegistered(user.getDocument(), user.getUser())) {
            Snackbar.make(view, "Error: El documento o usuario ya están registrados", Snackbar.LENGTH_LONG).show();
            return;
        }

        try (SQLiteDatabase db = dbUser.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put("use_document", user.getDocument());
            values.put("use_names", user.getNames());
            values.put("use_last_names", user.getLastNames());
            values.put("use_user", user.getUser());
            values.put("use_password", user.getPassword());
            values.put("use_status", 1);

            long result = db.insert("users", null, values);
            if (result != -1) {
                Snackbar.make(view, "Usuario registrado correctamente", Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(view, "Error al registrar usuario", Snackbar.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error al insertar usuario: " + e.getMessage());
        }
    }
    public void updateUser(String searchValue, User updatedUser) {
        if (!validateUser(updatedUser)) {
            return; // Si no pasa validaciones, no se actualiza
        }

        try (SQLiteDatabase db = dbUser.getWritableDatabase()) {
            // Verificar si el usuario existe antes de actualizar
            try (Cursor cursor = db.rawQuery("SELECT * FROM users WHERE use_document = ? OR use_user = ?",
                    new String[]{searchValue, searchValue})) {
                if (!cursor.moveToFirst()) {
                    Snackbar.make(view, "Usuario no encontrado", Snackbar.LENGTH_LONG).show();
                    return;
                }
            }

            ContentValues values = new ContentValues();
            values.put("use_document", updatedUser.getDocument());
            values.put("use_names", updatedUser.getNames());
            values.put("use_last_names", updatedUser.getLastNames());
            values.put("use_user", updatedUser.getUser());
            values.put("use_password", updatedUser.getPassword());

            int rowsAffected = db.update("users", values, "use_document = ? OR use_user = ?",
                    new String[]{searchValue, searchValue});

            if (rowsAffected > 0) {
                Snackbar.make(view, "Usuario actualizado correctamente", Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(view, "Error al actualizar usuario", Snackbar.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error al actualizar usuario: " + e.getMessage());
        }
    }

    public ArrayList<User> getUsersList() {
        ArrayList<User> userList = new ArrayList<>();
        try (SQLiteDatabase db = dbUser.getReadableDatabase();
             Cursor cursor = db.rawQuery("SELECT * FROM users", null)) {

            while (cursor.moveToNext()) {
                User user = new User();
                user.setDocument(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("use_document"))));
                user.setNames(cursor.getString(cursor.getColumnIndexOrThrow("use_names")));
                user.setLastNames(cursor.getString(cursor.getColumnIndexOrThrow("use_last_names")));
                user.setUser(cursor.getString(cursor.getColumnIndexOrThrow("use_user")));
                user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("use_password")));
                userList.add(user);
            }
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error al obtener usuarios: " + e.getMessage());
        }
        return userList;
    }
}
