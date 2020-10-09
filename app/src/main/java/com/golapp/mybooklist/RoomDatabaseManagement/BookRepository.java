package com.golapp.mybooklist.RoomDatabaseManagement;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

class BookRepository {

    private BookDAO dao;

    //Constructor
    public BookRepository(Application application) {
        BookRoomDatabase db = BookRoomDatabase.getDatabase(application);
        dao = db.diaryEntryDao();
    }

    //Returns all books from the database
    public List<Book> getAllBooks(){
        try {
            //.get() returns the result of the asyncTask
            return new queryAllBooksAsyncTask(dao).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Insert Book to database
    public void insert (Book book) {
        new insertAsyncTask(dao).execute(book);
    }

    //Update Book in database
    public void update (Book book) {
        new updateAsyncTask(dao).execute(book);
    }

    //Delete Book from database
    public void delete (Book book) {
        new deleteAsyncTask(dao).execute(book);
    }

    private static class insertAsyncTask extends AsyncTask<Book, Void, Void> {

        private BookDAO asyncTaskDao;

        insertAsyncTask(BookDAO dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Book... params) {
            asyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class updateAsyncTask extends AsyncTask<Book, Void, Void> {

        private BookDAO asyncTaskDao;

        updateAsyncTask(BookDAO dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Book... params) {
            asyncTaskDao.update(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Book, Void, Void> {

        private BookDAO asyncTaskDao;

        deleteAsyncTask(BookDAO dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Book... params) {
            asyncTaskDao.delete(params[0]);
            return null;
        }
    }

    private static class queryAllBooksAsyncTask extends AsyncTask<Void, Void, List<Book>> {

        private BookDAO asyncTaskDao;


        queryAllBooksAsyncTask(BookDAO dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected List<Book> doInBackground(Void... voids) {
            return asyncTaskDao.getAllEntries();
        }
    }
}
