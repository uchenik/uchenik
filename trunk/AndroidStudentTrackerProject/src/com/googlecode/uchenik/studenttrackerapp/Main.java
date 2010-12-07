package com.googlecode.uchenik.studenttrackerapp;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;


public class Main extends ListActivity {
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    
    private StudentsDbAdapter mDbHelper;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_list);
        mDbHelper = new StudentsDbAdapter(this);
        mDbHelper.open();
        fillData();
        registerForContextMenu(getListView());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {  
    	super.onCreateOptionsMenu(menu);
        final MenuInflater inflater = getMenuInflater();  
        inflater.inflate(R.menu.appmenu, menu);  
        return true;  
    } 

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.add_student:
    		createStudent();
    		return true;
    	default:  
    		return super.onOptionsItemSelected(item);        }
    }
    
    private void fillData() {
    	// Get all of the students from the database and create the item list
    	Cursor studentsCursor = mDbHelper.fetchAllStudents();
    	startManagingCursor(studentsCursor);
    	
    	String[] from = new String[] { StudentsDbAdapter.KEY_NAME };
    	int[] to = new int[] { R.id.text1 };
    	
    	// Now create an array adapter and set it to display using our row 
    	SimpleCursorAdapter students = 
    		new SimpleCursorAdapter(this, R.layout.student_row, studentsCursor, from, to);
    	setListAdapter(students);
    }
    
    @Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		CharSequence title = ((TextView) info.targetView.findViewById(R.id.text1)).getText();
		menu.setHeaderTitle(title);
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.contextmenu, menu);
	}
    
    @Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.edit_student: 
			editStudent(info.id, info.position);
			return true;
		case R.id.delete_student:
			deleteStudent(info.id);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
    
    private void createStudent() {
    	Intent i = new Intent(this, StudentEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }
    
    private void editStudent(long rowId, int position) {
        Intent i = new Intent(this, StudentEdit.class);
        i.putExtra(StudentsDbAdapter.KEY_ROWID, rowId);
        startActivityForResult(i, ACTIVITY_EDIT);
    }
    
    private void deleteStudent(long rowId) {
    	final long row = rowId;
		new AlertDialog.Builder(this)
    	.setTitle(R.string.deleteConfirmation_title)
    	.setIcon(android.R.drawable.ic_dialog_alert)
    	.setMessage(R.string.deleteConfirmation)
    	.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int id) {
    			dialog.cancel();
    		}
    	})
    	.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int id) {
    			mDbHelper.deleteStudent(row);
    			fillData();
    	    }
    	})
    	.setCancelable(false)
    	.show();
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        editStudent(id, position);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	mDbHelper.close();
    }
}