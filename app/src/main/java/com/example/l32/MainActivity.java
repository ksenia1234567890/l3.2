package com.example.l32;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    Button btnOpenStudents, btnAddStudent, btnUpdateLastStudent;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Создание БД
//        databaseHelper.onUpgrade(db, 1, 2);
        databaseHelper = new DatabaseHelper(getApplicationContext());
        db = databaseHelper.getReadableDatabase();

        //Удаление всех данных
        db.execSQL("DELETE FROM "+ DatabaseHelper.TABLE);

        //Добавление начальных данных
        db.execSQL("INSERT OR IGNORE INTO "+ DatabaseHelper.TABLE +" (" + DatabaseHelper.COLUMN_F + ","
                + DatabaseHelper.COLUMN_I + "," + DatabaseHelper.COLUMN_O +
                ", " + DatabaseHelper.COLUMN_TIMEADD  + ") VALUES " +
                "('Баркалов', 'Илья' ,'Петрович', '13.09.2023 11:11:11')," +
                "('Кошелев','Дмитрий', 'Алексеевич', '14.09.2023 12:12:12')," +
                "('Пономарев' ,'Сергей' ,'Александрович', '15.09.2023 13:13:13')," +
                "('Великанов' ,'Илья' ,'Алексеевич', '16.09.2023 14:14:14')," +
                "('Гришанов' ,'Богдан' ,'Петрович', '17.09.2023 15:15:15');");


        btnOpenStudents = (Button) findViewById(R.id.btnOpenStudents);
        btnOpenStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StudentsActivity.class);
                startActivity(intent);
            }
        });

        btnAddStudent = (Button) findViewById(R.id.btnAddStudent);
        btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddWindow();
            }
        });

        //Обновление последней записи в БД
        btnUpdateLastStudent = (Button) findViewById(R.id.btnUpdateLastStudent);
        btnUpdateLastStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db = databaseHelper.getWritableDatabase();
                userCursor = db.rawQuery("SELECT MAX(_id) FROM students", null);
                userCursor.moveToFirst();

                ContentValues cv = new ContentValues();
                cv.put(DatabaseHelper.COLUMN_F, "Иванов");
                cv.put(DatabaseHelper.COLUMN_I, "Иван");
                cv.put(DatabaseHelper.COLUMN_O, "Иванович");

                db.update(DatabaseHelper.TABLE, cv, DatabaseHelper.COLUMN_ID + "=" + userCursor.getInt(0),null);
                userCursor.close();
                db.close();
            }
        });
    }

    //Добавление нового студента через диалоговое окно
    private void showAddWindow(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("Добавить студента");

        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);

        View addStudentWindow = inflater.inflate(R.layout.activity_add_student_window, null);

        dialog.setView(addStudentWindow);

        TextInputEditText F = addStudentWindow.findViewById(R.id.surname);
        TextInputEditText I = addStudentWindow.findViewById(R.id.name);
        TextInputEditText O = addStudentWindow.findViewById(R.id.middleName);


        dialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                String formattedDate = df.format(c.getTime());

                ContentValues cv = new ContentValues();
                cv.put(DatabaseHelper.COLUMN_F, F.getText().toString());
                cv.put(DatabaseHelper.COLUMN_I, I.getText().toString());
                cv.put(DatabaseHelper.COLUMN_O, O.getText().toString());

                cv.put(DatabaseHelper.COLUMN_TIMEADD, formattedDate.toString());

                db = databaseHelper.getReadableDatabase();
                db.insert(DatabaseHelper.TABLE, null, cv);
                db.close();
            }
        });

        dialog.show();
    }

}