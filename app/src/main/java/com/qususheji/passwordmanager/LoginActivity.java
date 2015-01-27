package com.qususheji.passwordmanager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends ActionBarActivity  {
    public static final String TAG = LoginActivity.class.getSimpleName();

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private UserModifyTask mModifyTask = null;

    private EditText mPasswordView,oldPasswordView,newPasswordView,newPassword2View;

    private View mProgressView;
    private View mLoginFormView;
    private View mModifyFormView;

    private Button mSignInButton,mModifyButton;

    private boolean ifModifying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.action_login || id == EditorInfo.IME_ACTION_DONE) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        oldPasswordView = (EditText) findViewById(R.id.old_password);
        newPasswordView = (EditText) findViewById(R.id.new_password);
        newPassword2View = (EditText) findViewById(R.id.new_password_again);
        newPassword2View.setOnEditorActionListener(new TextView.OnEditorActionListener(){

            @Override
            public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                if (id == R.id.action_modify || id == EditorInfo.IME_ACTION_DONE) {
                    attemptModify();
                    return true;
                }
                return false;
            }
        });

        mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ifModifying){
                    attemptModify();
                }else {
                    attemptLogin();
                }
            }
        });

        mModifyButton = (Button) findViewById(R.id.modify_button);
        mModifyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ifModifying){
                    mSignInButton.setText(getString(R.string.action_sign_in));
                    mModifyButton.setText(getString(R.string.action_modify));
                    ifModifying = !ifModifying;
                    mPasswordView.setVisibility(View.VISIBLE);
                    mModifyFormView.setVisibility(View.GONE);

                }else {
                    mSignInButton.setText(getString(R.string.action_ok));
                    mModifyButton.setText(getString(R.string.action_cancel));
                    ifModifying = !ifModifying;
                    mPasswordView.setVisibility(View.GONE);
                    mModifyFormView.setVisibility(View.VISIBLE);
                }
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mModifyFormView = findViewById(R.id.modify_form);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(password);
            mAuthTask.execute((Void) null);
        }
    }

    public void attemptModify() {
        if (mModifyTask != null) {
            return;
        }

        // Reset errors.
        oldPasswordView.setError(null);
        newPasswordView.setError(null);
        newPassword2View.setError(null);
        // Store values at the time of the login attempt.
        String oldPassword = oldPasswordView.getText().toString();
        String newPassword = newPasswordView.getText().toString();
        String newPassword2 = newPassword2View.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mModifyTask = new UserModifyTask(oldPassword,newPassword,newPassword2);
            mModifyTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Integer> {

        static final int WRONG_PASS = 0;
        static final int SUCCESS = 2;
        static final int ERROR = 3;
        private final String mPassword;

        UserLoginTask( String password) {
            mPassword = password;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
            SQLiteDatabase database = databaseHelper.getReadableDatabase();
            Cursor cursor = null;
            String password = "";
            try{
                cursor = database.query(DatabaseHelper.TableLogin.TABLE_NAME,null,DatabaseHelper.TableLogin.COLUMN_NAME_ID + " = ?", new String[]{"1"},null,null,null);
                if (cursor.moveToFirst()){
                    password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TableLogin.COLUMN_NAME_PASSWORD));
                    Log.v(TAG, "password:"+password);
                }
                if (TextUtils.isEmpty(password)){
                    return SUCCESS;
                }else if (mPassword.equals(password)){
                    return SUCCESS;
                }else {
                    return WRONG_PASS;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ERROR;
            }finally {
                if (cursor != null) { cursor.close(); }
                database.close();
            }

        }

        @Override
        protected void onPostExecute(final Integer code) {
            mAuthTask = null;
            showProgress(false);
            switch (code){
                case WRONG_PASS:
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                    break;
                case SUCCESS:
                    startActivity(new Intent(LoginActivity.this,DisplayActivity.class));
                    finish();
                    break;
                case ERROR:
                    Toast.makeText(getApplicationContext(),"读数据库出错！",Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public class UserModifyTask extends AsyncTask<Void, Void, Integer> {

        static final int WRONG_PASS = 0;
        static final int DIFF_PASS = 1;
        static final int SUCCESS = 2;
        static final int ERROR = 3;

        private final String mPassword;
        private final String newPassword;
        private final String newPassword2;

        UserModifyTask( String password, String newPassword, String newPassword2) {
            mPassword = password;
            this.newPassword = newPassword;
            this.newPassword2 = newPassword2;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
            SQLiteDatabase database = databaseHelper.getReadableDatabase();
            Cursor cursor = null;
            String password = "";
            try{
                cursor = database.query(DatabaseHelper.TableLogin.TABLE_NAME,null,DatabaseHelper.TableLogin.COLUMN_NAME_ID + " = ?", new String[]{"1"},null,null,null);
                if (cursor.moveToFirst()){
                    password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TableLogin.COLUMN_NAME_PASSWORD));
                }
                if (TextUtils.isEmpty(password) || mPassword.equals(password)){
                    if (newPassword.equals(newPassword2)) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DatabaseHelper.TableLogin.COLUMN_NAME_ID,1);
                        contentValues.put(DatabaseHelper.TableLogin.COLUMN_NAME_PASSWORD, newPassword);
                        database.insertWithOnConflict(DatabaseHelper.TableLogin.TABLE_NAME,
                                DatabaseHelper.TableLogin._ID,
                                contentValues,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        return SUCCESS;
                    }else {
                        return DIFF_PASS;
                    }
                }else {
                    return WRONG_PASS;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ERROR;
            }finally {
                if (cursor != null) { cursor.close(); }
                database.close();
            }

        }

        @Override
        protected void onPostExecute(final Integer code) {
            mModifyTask = null;
            showProgress(false);

            switch (code){
                case WRONG_PASS:
                    oldPasswordView.setError(getString(R.string.error_incorrect_password));
                    oldPasswordView.requestFocus();
                    break;
                case DIFF_PASS:
                    newPasswordView.setError(getString(R.string.diff_password));
                    newPasswordView.requestFocus();
                    break;
                case SUCCESS:
                    startActivity(new Intent(LoginActivity.this,DisplayActivity.class));
                    finish();
                    break;
                case ERROR:
                    Toast.makeText(getApplicationContext(),"读数据库出错！",Toast.LENGTH_SHORT).show();
                    break;
            }

        }

        @Override
        protected void onCancelled() {
            mModifyTask = null;
            showProgress(false);
        }
    }
}



