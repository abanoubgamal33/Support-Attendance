package com.example.supportattendance.Pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Delete;
import androidx.room.Room;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.supportattendance.R;
import com.example.supportattendance.RecyclerView.RecyclerViewDays.DaysAdapter;
import com.example.supportattendance.RecyclerView.RecyclerViewDays.DaysModel;
import com.example.supportattendance.RecyclerView.RecyclerViewDays.OnClickDaysRecyclerView;
import com.example.supportattendance.RecyclerView.RecyclerViewDays.Room.DaysDatabase;
import com.example.supportattendance.RecyclerView.RecyclerViewNames.NamesModel;
import com.example.supportattendance.RecyclerView.RecyclerViewNames.Room.NamesDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Days extends AppCompatActivity implements OnClickDaysRecyclerView {


    RecyclerView recyclerView;
    DaysAdapter adapter;
    List<DaysModel> list = new ArrayList<>();
    String comm;
    DaysDatabase db;
    NamesDatabase Ndb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_days);

        comm = getIntent().getExtras().getString("clicked");

        ///////////////// room
        db = Room.databaseBuilder(getApplicationContext(), DaysDatabase.class, "Days").allowMainThreadQueries().build();
        Ndb = Room.databaseBuilder(getApplicationContext(), NamesDatabase.class, "Names").allowMainThreadQueries().build();

        Ndb.namesDOA().DeleteAll();
        for (int i = 0; i < 50; i++) {
            Ndb.namesDOA().InsertName(new NamesModel("" + comm + " " + i, comm));
        }

        ////////

        list = db.DaysDOA().DBList(comm);
        ////////////////recyclerviewy
        recyclerView = findViewById(R.id.RecyclerviewDays);
        adapter = new DaysAdapter(list, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }


    public void AddNewDate(View view) {
        try {
            db.DaysDOA().InsertDate(new DaysModel(new Date(), comm));
            list = db.DaysDOA().DBList(comm);
            Toast.makeText(this, "Add New Session to: " + comm, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, AddDate.class);
            intent.putExtra("comm", comm);
            intent.putExtra("day", list.get(list.size() - 1).getDate());
            adapter.notifyDataSetChanged();
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } catch (Exception e) {
            Toast.makeText(this, "You Can't Add Same Session Twice!", Toast.LENGTH_SHORT).show();
        }
    }

    //when click on recycler view
    @Override
    public void onclick(int position) {
        Intent intent = new Intent(this, AttendanceActivity.class);
        intent.putExtra("comm", comm);
        intent.putExtra("day", list.get(position).getDate());
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_name, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Add_Name:
                AddName();
                break;
            case R.id.SHOW_ALL:
                ShowAllName();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void ShowAllName() {
        Intent intent = new Intent(this, Show_All_Name.class);
        intent.putExtra("comm", comm);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void AddName() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Enter Name: ");
        EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (!input.getText().toString().equals("")) {
                            try {
                                Ndb.namesDOA().InsertName(new NamesModel(input.getText().toString(), comm));
                            } catch (Exception e) {
                                Toast.makeText(Days.this, "Name Already Exist", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Days.this, "Add Name!", Toast.LENGTH_SHORT).show();
                        }
                        AddName();
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}