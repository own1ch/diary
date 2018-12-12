package com.diary.own1ch.diary;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.diary.own1ch.diary.Database.Database;

import org.w3c.dom.Text;

import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Note extends AppCompatActivity {

    private ContentValues cv = new ContentValues();
    private SQLiteDatabase sqLiteDatabase;
    private Bundle noteIntent;
    private String id;
    private CheckBox isCalendar;
    private Button saveBtn;
    private TextView themeTV;
    private TextView descriptionTV;
    private TextView dateTV;
    private Spinner day;
    private ArrayAdapter<CharSequence> adapterDays;
    private Spinner month;
    private ArrayAdapter<CharSequence> adapterMonth;
    private Spinner year;
    private ArrayAdapter<CharSequence> adapterYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        Database database = new Database(this);
        sqLiteDatabase = database.getWritableDatabase();

        saveBtn = findViewById(R.id.saveBtn);
        themeTV = findViewById(R.id.theme);
        descriptionTV = findViewById(R.id.description);
        //dateTV = findViewById(R.id.date);
        isCalendar = findViewById(R.id.isCalendar);

        noteIntent = getIntent().getExtras();
        saveBtnInit();

        spinnersInit();

        if(noteIntent != null && noteIntent.get("id") != null) {
            id = noteIntent.get("id").toString();
            Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM diary WHERE id = ?", new String[]{id});
            if(c.moveToFirst()) {
                themeTV.setText(c.getString(1));
                descriptionTV.setText(c.getString(2));
                if(c.getString(4) != null) {
                    String temp = c.getString(4);
                    if(temp.indexOf(".") == 2) {
                        int dayPosition = adapterDays.getPosition(temp.substring(0,1));
                        day.setSelection(dayPosition);
                        int monthPosition = adapterMonth.getPosition(temp.substring(3,4));
                        month.setSelection(monthPosition);
                        int yearPosition  = adapterYear.getPosition(temp.substring(6,temp.length()));
                        year.setSelection(yearPosition);
                    } else if(temp.indexOf(".") == 4) {
                        int yearPosition = adapterYear.getPosition(temp.substring(0,3));
                        year.setSelection(yearPosition);
                        int monthPosition = adapterMonth.getPosition(temp.substring(5,6));
                        month.setSelection(monthPosition);
                        int dayPosition = adapterDays.getPosition(temp.substring(8,9));
                        day.setSelection(dayPosition);
                    }
                }
            }
        }

    }

    private void spinnersInit() {
        day = findViewById(R.id.day);
        adapterDays = ArrayAdapter.createFromResource(this, R.array.days, android.R.layout.simple_spinner_item);
        day.setAdapter(adapterDays);

        month = findViewById(R.id.month);
        adapterMonth = ArrayAdapter.createFromResource(this, R.array.month, android.R.layout.simple_spinner_item);
        month.setAdapter(adapterMonth);

        year = findViewById(R.id.year);
        adapterYear = ArrayAdapter.createFromResource(this, R.array.year, android.R.layout.simple_spinner_item);
        year.setAdapter(adapterYear);
    }

    private void saveBtnInit() {

        if (noteIntent != null && noteIntent.get("id") != null) {
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cv.put("theme", themeTV.getText().toString());
                    cv.put("description", descriptionTV.getText().toString());
                    String date = year.getSelectedItem() + "." + month.getSelectedItem() + "." + day.getSelectedItem();
                    cv.put("date", date);
                    cv.put("done", 0);

                    sqLiteDatabase.update("diary", cv, "id = ?", new String[]{id});
                    saveToCalendar();
                    finish();
                }
            });
        } else
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cv.put("theme", themeTV.getText().toString());
                    cv.put("description", descriptionTV.getText().toString());
                    String monthDigit = checkMonthDigit(month.getSelectedItem());
                    String date = year.getSelectedItem() + "." + monthDigit + "." + day.getSelectedItem();
                    cv.put("date", date);
                    cv.put("done", 0);

                    sqLiteDatabase.insert("diary", null, cv);
                    saveToCalendar();
                    finish();
                }
            });
    }

    private String checkMonthDigit(Object selectedItem) {
        switch (selectedItem.toString()) {
            case "Январь":
                return "01";
            case "Февраль":
                return "02";
            case "Март":
                return "03";
            case "Апрель":
                return "04";
            case "Май":
                return "05";
            case "Июнь":
                return "06";
            case "Июль":
                return "07";
            case "Август":
                return "08";
            case "Сентябрь":
                return "09";
            case "Октябрь":
                return "10";
            case "Ноябрь":
                return "11";
            case "Декабрь":
                return "12";
        }
        return null;
    }

    private void saveToCalendar() {
        if(isCalendar.isChecked()) {
            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra(CalendarContract.Events.TITLE, themeTV.getText().toString());
            intent.putExtra(CalendarContract.Events.DESCRIPTION, descriptionTV.getText().toString());
        }
    }
}
