package com.diary.own1ch.diary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.diary.own1ch.diary.Database.Database;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Database database;
    private SQLiteDatabase sqLiteDatabase;
    private LinearLayout llMain;
    private MenuItem watchAllCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = new Database(this);
        sqLiteDatabase = database.getWritableDatabase();
        FloatingActionButton fabBtn = findViewById(R.id.fab);
        fabListener(fabBtn);
        readDB(sqLiteDatabase);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        watchAllCheckBox = menu.findItem(R.id.watch_all);
        watchAllCheckBox.setChecked(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.watch_all:
                if (!watchAllCheckBox.isChecked()) {
                    for (int i = 0; i < llMain.getChildCount(); i++) {
                        llMain.getChildAt(i).setVisibility(View.VISIBLE);
                    }
                    watchAllCheckBox.setChecked(true);
                } else {
                    /*ArrayList<LinearLayout> ll = new ArrayList<>();
                    for (int i = 0; i < llMain.getChildCount(); i++) {
                        if (llMain.getChildAt(i) instanceof LinearLayout) {
                            ll.add((LinearLayout) llMain.getChildAt(i));
                        }
                    }
                    for (int i = 0; i < ll.size(); i++) {
                        for (int j = 0; j < ll.get(i).getChildCount(); j++) {
                            if (ll.get(i).getChildAt(j) instanceof CheckBox) {
                                if (((CheckBox) ll.get(i).getChildAt(j)).isChecked()) {
                                    ll.get(i).setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                    llMain.removeAllViews();
                    for(int i = 0; i < ll.size(); i++) {
                        llMain.addView(ll.get(i));
                    }*/
                    llMain.removeAllViews();
                    readDB(sqLiteDatabase);
                    watchAllCheckBox.setChecked(false);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(llMain != null)
            llMain.removeAllViews();
        readDB(sqLiteDatabase);
    }

    private void fabListener(FloatingActionButton fab) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent noteIntent = new Intent(MainActivity.this, Note.class);
                startActivity(noteIntent);
            }
        });
    }

    private LinearLayout initLinearLayout() {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setHorizontalGravity(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(layoutParams);

        return linearLayout;
    }

    private Button initButton(final Cursor c, final int idColIndex, int themeColIndex) {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT, 100);
        final Button button = new Button(this);
        button.setId(Integer.parseInt(c.getString(idColIndex)));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent noteIntent = new Intent(MainActivity.this, Note.class);
                noteIntent.putExtra("id", button.getId());
                startActivity(noteIntent);
            }
        });
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createAlertDialog(0, button);
                return false;
            }
        });
        button.setText(c.getString(themeColIndex));
        button.setLayoutParams(layoutParams);
        return button;
    }

    private void createAlertDialog(final int id, final Button button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Вы уверены что хотите удалить событие?")
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sqLiteDatabase.execSQL("DELETE FROM diary WHERE id = " + id);
                        button.setVisibility(View.GONE);
                        LinearLayout linearLayout = (LinearLayout) button.getParent();
                        linearLayout.setVisibility(View.GONE);
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private CheckBox initCheckBox(Cursor c, int idColIndex, final int doneColIndex) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        final CheckBox checkBox = new CheckBox(this);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sqLiteDatabase.execSQL("UPDATE diary SET done = ? WHERE id = ?", new String[]{"1", String.valueOf(checkBox.getId())});
                } else
                    sqLiteDatabase.execSQL("UPDATE diary SET done = ? WHERE id = ?", new String[]{"0", String.valueOf(checkBox.getId())});
            }
        });
        checkBox.setId(Integer.parseInt(c.getString(idColIndex)));
        if(c.getString(doneColIndex).equals("0") || c.getString(doneColIndex) == null)
            checkBox.setChecked(false);
        else checkBox.setChecked(true);
        checkBox.setLayoutParams(layoutParams);
        return checkBox;
    }

    private TextView initDateField(Cursor c, int dateColIndex) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        TextView dateField = new TextView(this);
        dateField.setGravity(Gravity.CENTER);
        if(dateColIndex != 0) {
            String date = c.getString(dateColIndex);
            String[] dateMonth = date.split(".");
            if(dateMonth.length != 0) {
                date = dateMonth[0] + "." +
                        checkMonthDigit(dateMonth[1]) + "." +
                        dateMonth[2];
            }
            if(date.indexOf(".") == 4) {
                String temp = date.substring(8,10) + "." +
                        date.substring(5,7) + "." +
                        date.substring(0,4);
                date = temp;
            }
            dateField.setText(date);
        }
        dateField.setLayoutParams(layoutParams);
        return dateField;
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

    private void readDB(SQLiteDatabase sqLiteDatabase) {
        Cursor c = sqLiteDatabase.query("diary", null, null, null, null, null, "date asc");

        if(c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int descriptionColIndex = c.getColumnIndex("description");
            int themeColIndex = c.getColumnIndex("theme");
            int dateColIndex = c.getColumnIndex("date");
            int doneColIndex = c.getColumnIndex("done");

            String previousDate = "0";
            do {
                boolean flag = false;
                llMain = findViewById(R.id.llMain);
                LinearLayout linearLayout = initLinearLayout();
                LinearLayout dateLayout = initLinearLayout();

                if(!previousDate.equals(c.getString(dateColIndex))) {
                    previousDate = c.getString(dateColIndex);
                    TextView dateField = initDateField(c, dateColIndex);
                    dateLayout.addView(dateField);
                    dateLayout.setVisibility(View.GONE);
                    llMain.addView(dateLayout);
                }

                Button button = initButton(c, idColIndex, themeColIndex);
                CheckBox checkBox = initCheckBox(c, idColIndex, doneColIndex);

                linearLayout.addView(button);
                linearLayout.addView(checkBox);

                if(c.getString(doneColIndex).equals("1")) {
                    linearLayout.setVisibility(View.GONE);
                } else {
                    dateLayout.setVisibility(View.VISIBLE);
                }

                llMain.addView(linearLayout);
            } while (c.moveToNext());
        }
    }
}
