package com.googlecode.uchenik.studenttrackerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple students database access helper class. Defines the basic CRUD operations
 * for the tracker app, and gives the ability to list all students as well as
 * retrieve or modify a specific student.
 * 
 */
public class StudentsDbAdapter {

    public static final String KEY_NAME = "name";
    public static final String KEY_NOTES = "notes";
    public static final String KEY_BOOKNAME = "bookname";
    public static final String KEY_PAGENBR = "pagenbr";
    public static final String KEY_PARAGRAPHNBR = "paragraphnbr";
    public static final String KEY_ROWID = "_id";

    private static final String TAG = "StudentsDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
        "create table students (_id integer primary key autoincrement, "
        + "name text, notes text, bookname text, pagenbr text, paragraphnbr text);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "students";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS students");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public StudentsDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the students database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public StudentsDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new student using the name and notes provided. If the student is
     * successfully created return the new rowId for that student, otherwise return
     * a -1 to indicate failure.
     * 
     * @param name the name of the student
     * @param notes the notes about the student
     * @return rowId or -1 if failed
     */
    public long createStudent(String name, String notes, String bookname, String pagenbr, String paragraphnbr) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_NOTES, notes);
        initialValues.put(KEY_BOOKNAME, bookname);
        initialValues.put(KEY_PAGENBR, pagenbr);
        initialValues.put(KEY_PARAGRAPHNBR, paragraphnbr);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the student with the given rowId
     * 
     * @param rowId id of student to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteStudent(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all students in the database
     * 
     * @return Cursor over all students
     */
    public Cursor fetchAllStudents() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME,
                KEY_NOTES, KEY_BOOKNAME, KEY_PAGENBR, KEY_PARAGRAPHNBR},
                null, null, null, null, KEY_NAME);
    }

    /**
     * Return a Cursor positioned at the student that matches the given rowId
     * 
     * @param rowId id of student to retrieve
     * @return Cursor positioned to matching student, if found
     * @throws SQLException if student could not be found/retrieved
     */
    public Cursor fetchStudent(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_NAME, KEY_NOTES, KEY_BOOKNAME, KEY_PAGENBR, KEY_PARAGRAPHNBR},
                    KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the student using the details provided. The student to be updated is
     * specified using the rowId, and it is altered to use the name and notes
     * values passed in
     * 
     * @param rowId id of student to update
     * @param name value to set student name to
     * @param notes value to set student notes to
     * @param bookname value to set student book name to
     * @param pagenbr value to set student page nbr to
     * @param paragraphnbr value to set student paragraph nbr to
     * @return true if the student was successfully updated, false otherwise
     */
    public boolean updateStudent(long rowId, String name, String notes, String bookname, String pagenbr, String paragraphnbr) {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        args.put(KEY_NOTES, notes);
        args.put(KEY_BOOKNAME, bookname);
        args.put(KEY_PAGENBR, pagenbr);
        args.put(KEY_PARAGRAPHNBR, paragraphnbr);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
