package com.qususheji.passwordmanager;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;



public class DisplayActivity extends ActionBarActivity implements SearchView.OnQueryTextListener {
    public static final String TAG = DisplayActivity.class.getSimpleName();

    private ListView myListView;
    private MyAdpater myAdpater;
    protected ArrayList<ItemContent> contentList;
    private ReadTask readTask;
    private MenuItem menuItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        myListView = (ListView) findViewById(R.id.listView);
        contentList = new ArrayList<>();
        myAdpater = new MyAdpater();
        myListView.setAdapter(myAdpater);
        readTask = new ReadTask();
        readTask.execute("");
    }

    @Override
    public void onResume(){
        super.onResume();
        readTask.cancel(true);
        readTask = new ReadTask();
        readTask.execute("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_display, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)MenuItemCompat.getActionView(menuItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_create) {
            startActivity(new Intent(DisplayActivity.this, MainActivity.class));
            return true;
        }else if (id == R.id.action_search){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        readTask.cancel(true);
        readTask = new ReadTask();
        readTask.execute(query);
        menuItem.collapseActionView();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        readTask.cancel(true);
        readTask = new ReadTask();
        readTask.execute(newText);
        return true;
    }

    private class ViewHolder{
        TextView desView,accountView,passwordView;
        SwipeLayout swipeLayout;
        ImageButton editeBtn,deleteBtn;
        public ViewHolder(View view){
            desView = (TextView) view.findViewById(R.id.des);
            accountView = (TextView) view.findViewById(R.id.account);
            passwordView = (TextView) view.findViewById(R.id.password);
            swipeLayout = (SwipeLayout)view.findViewById(R.id.swipe_layout);
            editeBtn = (ImageButton)view.findViewById(R.id.edit_btn);
            deleteBtn = (ImageButton)view.findViewById(R.id.delete_btn);
        }
    }

    private class MyAdpater extends BaseAdapter{

        @Override
        public int getCount() {
            return contentList.size();
        }

        @Override
        public ItemContent getItem(int position) {
            return contentList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            ItemContent itemContent = getItem(position);
            final int _id = itemContent.get_id();
            if (convertView != null){
                viewHolder = (ViewHolder)convertView.getTag();
            }else {
                convertView = getLayoutInflater().inflate(R.layout.display_item,parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }

            viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            viewHolder.swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);

            viewHolder.desView.setText(itemContent.des);
            viewHolder.accountView.setText(itemContent.account);
            viewHolder.passwordView.setText(itemContent.password);

            viewHolder.editeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editItem(_id);
                }
            });

            viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItem(_id);
                }
            });

            return convertView;
        }
    }

    public void editItem(int _id){
        Intent intent = new Intent(DisplayActivity.this,MainActivity.class);
        intent.putExtra(MainActivity.PARAM_ID, _id);
        startActivity(intent);
    }

    public void deleteItem(int _id){
        new DeleleTask().execute(_id);
    }

    private class DeleleTask extends AsyncTask<Integer,Integer,Integer>{
        private static final int SUCCESS = 0;
        private static final int ERROR = 1;

        @Override
        protected Integer doInBackground(Integer... params) {
            int _id = params[0];
            DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
            SQLiteDatabase  database = databaseHelper.getWritableDatabase();

            try {
                database.delete(DatabaseHelper.TableContent.TABLE_NAME,
                        DatabaseHelper.TableContent._ID + " = ? ",
                        new String[]{_id+""});
                return SUCCESS;
            }catch (Exception e){
                e.printStackTrace();
                return ERROR;
            }finally {
                database.close();
            }


        }

        @Override
        protected void onPostExecute(Integer code) {
            switch (code){
                case SUCCESS:
                    readTask.cancel(true);
                    readTask = new ReadTask();
                    readTask.execute("");
                    break;
                case ERROR:
                    Toast.makeText(getApplicationContext(),"删除失败！",Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }

    private class ReadTask extends AsyncTask<String,Integer,ArrayList<ItemContent>>{

        @Override
        protected ArrayList<ItemContent> doInBackground(String... params) {
            String containStr = params[0];
            ArrayList<ItemContent> arrayList = new ArrayList();
            DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
            SQLiteDatabase  database = databaseHelper.getReadableDatabase();

            String selection = DatabaseHelper.TableContent.COLUMN_NAME_DES + " LIKE ? OR "
                    + DatabaseHelper.TableContent.COLUMN_NAME_ACCOUNT + " LIKE ? OR "
                    + DatabaseHelper.TableContent.COLUMN_NAME_PASSWORD + " LIKE ? ";
            String[] selectionArgs = {"%"+containStr + "%","%"+containStr + "%","%"+containStr + "%"};
            String des,account,password ;
            int _id;
            Cursor cursor = null;
            try {
                cursor = database.query(DatabaseHelper.TableContent.TABLE_NAME, null, selection, selectionArgs, null, null, null, null);
                while (cursor.moveToNext()) {
                    _id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.TableContent._ID));
                    des = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TableContent.COLUMN_NAME_DES));
                    account = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TableContent.COLUMN_NAME_ACCOUNT));
                    password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TableContent.COLUMN_NAME_PASSWORD));
                    arrayList.add(new ItemContent(_id,des,account,password));
                }

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if (cursor != null){ cursor.close();}
                database.close();
            }
            return arrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<ItemContent> contentList) {
            DisplayActivity.this.contentList = contentList;
            DisplayActivity.this.myAdpater.notifyDataSetChanged();
        }

    }

}
