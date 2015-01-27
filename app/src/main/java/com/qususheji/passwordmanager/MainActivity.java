package com.qususheji.passwordmanager;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String PARAM_ID = "param_id";

    private DatabaseHelper myDatabaseHelper;
    private SQLiteDatabase myDatabase;

    private EditText desEdit,accountEdit,passwordEdit;

    private int _id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        _id = intent.getIntExtra(PARAM_ID,-1);

        myDatabaseHelper = new DatabaseHelper(this);

        desEdit = (EditText) findViewById(R.id.des);
        accountEdit = (EditText) findViewById(R.id.account);
        passwordEdit = (EditText) findViewById(R.id.password);
        passwordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener(){

            @Override
            public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                if (id == R.id.action_add || id == EditorInfo.IME_ACTION_DONE) {
                    onSubmitClick();
                    return true;
                }
                return false;
            }
        });

        TextView submitBtn = (TextView) findViewById(R.id.submit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitClick();
            }
        });

        if (_id >= 0){
            initContent(_id);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initContent(int id){
        myDatabase = myDatabaseHelper.getReadableDatabase();

        String selection = DatabaseHelper.TableContent._ID + " = ?";
        String[] selectionArgs = {id+""};
        String des = "",account = "",password = "";
        Cursor cursor = null;
        try {
            cursor = myDatabase.query(DatabaseHelper.TableContent.TABLE_NAME, null, selection, selectionArgs, null, null, null, null);

            if (cursor.moveToFirst()) {
                des = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TableContent.COLUMN_NAME_DES));
                account = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TableContent.COLUMN_NAME_ACCOUNT));
                password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TableContent.COLUMN_NAME_PASSWORD));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (cursor != null){ cursor.close();}
            myDatabase.close();
        }

        desEdit.setText(des);
        accountEdit.setText(account);
        passwordEdit.setText(password);
    }

    private void onSubmitClick(){
        String des = desEdit.getText().toString();
        String account = accountEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        myDatabase = myDatabaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.TableContent.COLUMN_NAME_DES,des);
        contentValues.put(DatabaseHelper.TableContent.COLUMN_NAME_ACCOUNT,account);
        contentValues.put(DatabaseHelper.TableContent.COLUMN_NAME_PASSWORD,password);

        try {
            if (_id >= 0) {
                String whereClause = DatabaseHelper.TableContent._ID + " = ? ";
                String[] whereArgs = {_id + ""};
                myDatabase.update(DatabaseHelper.TableContent.TABLE_NAME, contentValues, whereClause, whereArgs);
                Toast.makeText(this,"修改成功！",Toast.LENGTH_SHORT).show();
            } else {
                myDatabase.insert(DatabaseHelper.TableContent.TABLE_NAME, DatabaseHelper.TableContent._ID, contentValues);
                Toast.makeText(this,"添加成功！",Toast.LENGTH_SHORT).show();
                desEdit.setText("");
                desEdit.requestFocus();
            }


        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,"操作失败！",Toast.LENGTH_SHORT).show();
        }finally {
            myDatabase.close();
        }
    }


}
