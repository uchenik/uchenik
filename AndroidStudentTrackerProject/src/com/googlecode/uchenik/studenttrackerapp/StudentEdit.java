package com.googlecode.uchenik.studenttrackerapp;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class StudentEdit extends Activity {
	private EditText mNameText;
	private EditText mNotesText;
	private EditText mBooknameText;
	private EditText mPagenbrText;
	private EditText mParagraphnbrText;
	private Long mRowId;
	
	private StudentsDbAdapter mDbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new StudentsDbAdapter(this);
		mDbHelper.open();
		setContentView(R.layout.student_edit);
		setTitle(R.string.edit_student);
		mNameText = (EditText) findViewById(R.id.name);
		mNotesText = (EditText) findViewById(R.id.notes);
		mBooknameText = (EditText) findViewById(R.id.bookname);
		mPagenbrText = (EditText) findViewById(R.id.pagenbr);
		mParagraphnbrText = (EditText) findViewById(R.id.paragraphnbr);
		Button confirmButton = (Button) findViewById(R.id.confirm);
		mRowId = (savedInstanceState == null) ? null :
    		(Long) savedInstanceState.getSerializable(StudentsDbAdapter.KEY_ROWID);

	       if (mRowId == null) {
	        	Bundle extras = getIntent().getExtras();
	        	mRowId = extras != null ? extras.getLong(StudentsDbAdapter.KEY_ROWID)
	        							:null;
	       }
	       populateFields();
	
	       confirmButton.setOnClickListener(new View.OnClickListener() {
	    	   public void onClick(View view) {
	    		   setResult(RESULT_OK);
	    		   finish();
	    	   }
	       });
	}

	private void populateFields() {
		if (mRowId != null) {
			Cursor student = mDbHelper.fetchStudent(mRowId);
			startManagingCursor(student);
			mNameText.setText(student.getString(
				    student.getColumnIndexOrThrow(StudentsDbAdapter.KEY_NAME)));
			mNotesText.setText(student.getString(
				    student.getColumnIndexOrThrow(StudentsDbAdapter.KEY_NOTES)));
			mBooknameText.setText(student.getString(
				    student.getColumnIndexOrThrow(StudentsDbAdapter.KEY_BOOKNAME)));
			mPagenbrText.setText(student.getString(
				    student.getColumnIndexOrThrow(StudentsDbAdapter.KEY_PAGENBR)));
			mParagraphnbrText.setText(student.getString(
				    student.getColumnIndexOrThrow(StudentsDbAdapter.KEY_PARAGRAPHNBR)));
			}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putSerializable(StudentsDbAdapter.KEY_ROWID, mRowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}

	private void saveState() {
		String name = mNameText.getText().toString();
		String notes = mNotesText.getText().toString();
		String bookname = mBooknameText.getText().toString();
		String pagenbr = mPagenbrText.getText().toString();
		String paragraphnbr = mParagraphnbrText.getText().toString();
	
		if (mRowId == null) {
			long id = mDbHelper.createStudent(name, notes, bookname, pagenbr, paragraphnbr);
			if (id > 0) {
				mRowId = id;
			}
		} else {
			mDbHelper.updateStudent(mRowId, name, notes, bookname, pagenbr, paragraphnbr);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDbHelper.close();
	}
}
