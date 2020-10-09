package com.golapp.mybooklist.RoomDatabaseManagement;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Book.class}, version = 1)
public abstract class BookRoomDatabase extends RoomDatabase {

    private static BookRoomDatabase instance;

    //Singleton pattern which ensures that there is only ever one instance of the database
    public static BookRoomDatabase getDatabase(final Context context){
        if(instance == null){
            synchronized(BookRoomDatabase.class) {
                if (instance == null) {
                    //creates database instance and saves it in instance
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            BookRoomDatabase.class, "book_database").build();
                }
            }
        }
        return instance;
    }

    //Creates the dao
    public abstract BookDAO diaryEntryDao();
}
