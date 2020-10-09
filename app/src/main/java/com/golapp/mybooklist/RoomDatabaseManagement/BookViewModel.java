package com.golapp.mybooklist.RoomDatabaseManagement;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import java.util.List;

public class BookViewModel extends AndroidViewModel {

    private BookRepository repository;

    //Constructor
    public BookViewModel(Application application) {
        super(application);
        repository = new BookRepository(application);
    }

    //Database operations
    public List<Book> getAllBooks() {
        return repository.getAllBooks();
    }

    public void insert(Book book) {
        repository.insert(book);
    }

    public void update(Book book) {
        repository.update(book);
    }

    public void delete(Book book) {
        repository.delete(book);
    }
}
