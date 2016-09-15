package com.firzan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author firzan
 * 4-MAY-2016
 */
public class SqliteDb {
 	//database and its version
 		public static final String MYDATABASE_NAME = "db";
 		public static final int MYDATABASE_VERSION =1;

		private static final String DB_LOCK = "db_lock";
	 //define the table name and columns name...for table- user_location_track_data
	 public static final String TBL_MSGS = "mss_messages"; //table name //daily homework
	 public static final String S_ID = "id";//AI
	 public static final String S_MSG_ID = "msg_id";
	 public static final String S_TITLE = "title";//subject
	 public static final String S_CONTENT = "content";
	 public static final String S_FILE_LINK = "file_link_local";
	 public static final String S_FILE_LINK_SERVER = "server_link";
	 public static final String S_DATE = "_date";
	 public static final String S_MSG_SENDER = "sender";
	 public static final String S_MSG_STATUS = "read_status";//read/unread or 1/0
	 public static final String S_MSG_RECEIVER = "student_id";//local student unique id

	//create table MY_DATABASE  (tbl content data projects)
 	private static final String SCRIPT_CREATE_TBL_MSGS =
		  "create table " + TBL_MSGS + " ("
		  + S_ID + " integer primary key autoincrement, "
		  + S_MSG_ID + " text not null, "
		  + S_TITLE+ " text not null, "
		  + S_CONTENT+ " text not null, "
		  + S_FILE_LINK+ " text , "
		  + S_FILE_LINK_SERVER+ " text , "
		  + S_MSG_SENDER+ " text not null , "
		  + S_MSG_RECEIVER+ " text not null , "
		  + S_MSG_STATUS+ " text not null , "
		  + S_DATE + " text not null);";

	private SQLiteHelper sqLiteHelper;
	SQLiteDatabase sqLiteDatabase;
 	private Context context;
 	public SqliteDb(Context c){
  context = c;
 }
 
 	public SqliteDb openToRead() throws android.database.SQLException {
	 	sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null, MYDATABASE_VERSION);
		 sqLiteDatabase = sqLiteHelper.getReadableDatabase();
	  	return this;
 	}
 
 	public SqliteDb openToWrite() throws android.database.SQLException {
	  sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null, MYDATABASE_VERSION);
	  sqLiteDatabase = sqLiteHelper.getWritableDatabase();
	  return this;
 	}
 
 public void close(){
  sqLiteHelper.close();
 }
 //insrt in TBL_SLTD
 public long insertMessage(String msg_id,String msg_sender,String msg_receivers,String status,String title,String content,String link,String server_link,String timestamp){

	 synchronized (DB_LOCK) {
		 ContentValues contentValues = new ContentValues();
		 contentValues.put(S_MSG_ID, msg_id);
		 contentValues.put(S_MSG_SENDER, msg_sender);
		 contentValues.put(S_MSG_RECEIVER, msg_receivers);
		 contentValues.put(S_MSG_STATUS, status);
		 contentValues.put(S_TITLE, title);
		 contentValues.put(S_CONTENT, content);
		 contentValues.put(S_FILE_LINK, link);
		 contentValues.put(S_FILE_LINK_SERVER, server_link);
		 contentValues.put(S_DATE, timestamp);
		 sqLiteDatabase.beginTransaction();
		 long id = sqLiteDatabase.insert(TBL_MSGS, null, contentValues);
		 sqLiteDatabase.setTransactionSuccessful();
		 sqLiteDatabase.endTransaction();
		 return id;
	 }
 }



	//update in Roll number
	public long updateRollNo(String id,String roll_no){
		ContentValues contentValues=new ContentValues();
		contentValues.put(STUD_ID, id);
		contentValues.put(STUD_ROLL, roll_no);
		return sqLiteDatabase.update(TBL_STUDENT_PROFILE, contentValues, "id" + "= ? " , new String[] { id });
	}
	//update in profile pic
	public long updateProfilePic(String id,String profile){
		ContentValues contentValues=new ContentValues();
		contentValues.put(STUD_ID, id);
		contentValues.put(STUD_PROFILE_PIC_THUMB, profile);
		return sqLiteDatabase.update(TBL_STUDENT_PROFILE, contentValues, "id" + "= ? " , new String[] { id });
	}



	public void deleteOnePta(String id){
		sqLiteDatabase.delete(TBL_PTA, PTA_ID +"= ?" , new String[] { id });//delete one record
	}


	public Cursor checkPTAExist(String school_id_sha1,String msg_id){

			Cursor cursor1 = sqLiteDatabase.rawQuery("SELECT * from "+TBL_PTA + " WHERE "+ PTA_SCHOOL
														+" = ? AND " + PTA_SERVERID +" = ? "
															,new String[]{ school_id_sha1,msg_id });
			return cursor1;
	}

	public long insertHolidays(String msg_id,String school_id,String link,String server_link,String timestamp){
		ContentValues contentValues=new ContentValues();
		contentValues.put(HL_SERVERID, msg_id);
		contentValues.put(HL_SCHOOL, school_id);//sha1
		contentValues.put(HL_FILE_LINK, link);
		contentValues.put(HL_FILE_LINK_SERVER, server_link);
		contentValues.put(HL_DATE, timestamp);
		sqLiteDatabase.delete(TBL_HOLIDAYS_LIST, HL_SCHOOL + "= ? " , new String[] { school_id });//delete old record
		return sqLiteDatabase.insert(TBL_HOLIDAYS_LIST, null, contentValues);
	}



	public int deleteSync(String student_id){
		return 	sqLiteDatabase.delete(TBL_SYNC, SYNC_STUDENT_ID + "= ? " , new String[] { student_id });//delete if student removed
	}
	// update //


	//delete ALL records msgs
	 public int deleteAllMessages(){
		 return sqLiteDatabase.delete(TBL_MSGS, null, null);
	 }

	public int deleteSingleHomework(String id){
		return 	sqLiteDatabase.delete(TBL_MSGS, S_ID + "= ? " , new String[] { id });//delete single homework
	}


	//delete ALL records TBL_PROFILE
	public int deleteAllTblSProfile(){
		 return sqLiteDatabase.delete(TBL_STUDENT_PROFILE, null, null);
	}

	//TBL_SLTD  get all records msg data
	public Cursor getMessages(){
		 String[] columns=new String[]{S_ID,S_TITLE,S_CONTENT,S_FILE_LINK,S_DATE,S_MSG_SENDER,S_MSG_STATUS};
		 Cursor cursor1 = sqLiteDatabase.query(TBL_MSGS, columns,null, null, null, null, "id desc");
		 return cursor1;
	}
	//pagination
	/*public Cursor getPagedMessages(String total){
		Cursor cursor1 = sqLiteDatabase.rawQuery("SELECT * from "+TBL_MSGS + " ORDER BY ID DESC limit ?, 20 ",new String[]{ total });
		return cursor1;
	}*/

	public Cursor getSingleMessage(String id){
		Cursor cursor1 = sqLiteDatabase.rawQuery("SELECT * from "+TBL_MSGS + " WHERE id = ?",new String[]{ id });
		ContentValues cv=new ContentValues();
		cv.put(S_MSG_STATUS,"1");
		sqLiteDatabase.update(TBL_MSGS,cv," id = ?",new String[]{ id });
		return cursor1;
	}
	public int getUnreadMessage(){
		Cursor cursor1 = sqLiteDatabase.rawQuery("SELECT count(*) from "+TBL_MSGS + " WHERE "+ S_MSG_STATUS +"= ?",new String[]{ "0" });
		cursor1.moveToFirst();
		//System.out.println("MESSAGES: "+cursor1.getInt(0));
		return cursor1.getInt(0);
	}

	public Cursor getSingleIndividualNotification(String id){
		Cursor cursor1 = sqLiteDatabase.rawQuery("SELECT * from "+ TBL_SINGLE_NOTIFICATIONS+ " WHERE id = ?",new String[]{ id });
		ContentValues cv=new ContentValues();
		cv.put(SN_MSG_STATUS,"1");
		sqLiteDatabase.update(TBL_SINGLE_NOTIFICATIONS,cv," id = ?",new String[]{ id });
		return cursor1;
	}


	//TBL_MY_PROFILE get all records
	public Cursor getALLTblSProfile(){
		Cursor cursor1 = sqLiteDatabase.rawQuery("SELECT * from "+TBL_STUDENT_PROFILE ,null);
		return cursor1;
	}
	public Cursor getDistinctClassIds(){
		Cursor cursor1 = sqLiteDatabase.rawQuery("SELECT DISTINCT( "+ STUD_CLASS_ID +") from "+TBL_STUDENT_PROFILE ,null);
		return cursor1;
	}
	//profile get where clause records
	public Cursor getProfile(String  id){
		 Cursor cursor1 = sqLiteDatabase.rawQuery("SELECT * from "+TBL_STUDENT_PROFILE + " WHERE id = ?",new String[]{ id });
		 return cursor1;
	}

	//student unique id.
	public Cursor getStudUniqueIdByClass(String  class_id){
		Cursor cursor1 = sqLiteDatabase.rawQuery("SELECT "+ STUD_FNAME +","+STUD_UNIQUE_ID +","+ STUD_SCHOOLID_SHA1 + ","+ STUD_CLASS_NAME +","+ STUD_SCHOOLID + " from "+TBL_STUDENT_PROFILE + " WHERE "+STUD_CLASS_ID+" = ?",new String[]{ class_id });
		return cursor1;
	}


	//get all pta
	public Cursor getPTA(){
		Cursor cursor1 = sqLiteDatabase.
				rawQuery("SELECT "+ TBL_STUDENT_PROFILE +"."+STUD_FNAME +","
						+ TBL_STUDENT_PROFILE +"."+STUD_CLASS_NAME +","
						+ TBL_PTA +"."+ PTA_ID+","
						+ TBL_PTA +"."+ PTA_TITLE+","
						+ TBL_PTA +"."+ PTA_FILE_LINK_SERVER +","
						+ TBL_PTA +"."+ PTA_FILE_LINK
						+"  from "+TBL_STUDENT_PROFILE
						+" INNER JOIN " +TBL_PTA +" ON "
						+ TBL_STUDENT_PROFILE + "." + STUD_SCHOOLID_SHA1 +" = "
						+ TBL_PTA +"."+ PTA_SCHOOL
						,null);
		return cursor1;
	}



/*	public void updatePTA(String school_id,String server_url,String local_url,String server_id){
		ContentValues cv=new ContentValues();
		cv.put(PTA_FILE_LINK_SERVER,server_url);
		cv.put(PTA_FILE_LINK,local_url);
		cv.put(PTA_SERVERID,server_id);
		sqLiteDatabase.update(TBL_PTA,cv, PTA_SCHOOL + "  = ?",new String[]{ school_id });
	}
*/

//TBL_PROFILE get where clause records- Check profile already exists?

 
 public class SQLiteHelper extends SQLiteOpenHelper { 
  public SQLiteHelper(Context context, String name,
    CursorFactory factory, int version){
   	super(context, name, factory, version);
  }

  @Override
  public void onCreate(SQLiteDatabase db){
   // TODO Auto-generated method stub 
	  try{
		  db.execSQL(SCRIPT_CREATE_TBL_MSGS);
		  System.out.println("Table Creation called.");
	  }
	  catch(Exception e){Log.d("exception",e.toString());}
  } 
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
   // TODO Auto-generated method stub
	 	   db.execSQL("DROP TABLE IF EXISTS " + TBL_MSGS);

		   onCreate(db);
  }

 } 
}