package com.golapp.mybooklist.RoomDatabaseManagement;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BookDAO {

    @Insert
    void insert(Book book);

    @Update
    void update(Book book);

    @Delete
    void delete(Book book);

    //Gets all books ordered by the date they were added
    @Query("Select * From books Order By title DESC")
    List<Book> getAllEntries();
}