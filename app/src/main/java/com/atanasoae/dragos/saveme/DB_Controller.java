package com.atanasoae.dragos.saveme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.TextView;


public class DB_Controller extends SQLiteOpenHelper {
    DB_Controller(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "SAVEME.db", factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE FAMILY(ID INTEGER PRIMARY KEY AUTOINCREMENT, PHONE TEXT UNIQUE);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS FAMILY;");
    }

    void insert_phone_number(String number){
        ContentValues contentValues = new ContentValues();
        contentValues.put("PHONE", number);
        this.getWritableDatabase().insertOrThrow("FAMILY", "", contentValues);
    }

    void delete_phone_number(String number){
        this.getWritableDatabase().delete("FAMILY", "PHONE='"+number+"'", null);
    }

    void update_phone_number(String old_number, String new_number){
        this.getWritableDatabase().execSQL("UPDATE FAMILY SET PHONE='"+new_number+"' WHERE PHONE='"+old_number+"'");
    }

    void show_phone_number(TextView textView){
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM FAMILY", null);
        textView.setText("");
        while (cursor.moveToNext()){
            textView.append(cursor.getString(1));
            cursor.close();
        }
    }
}
