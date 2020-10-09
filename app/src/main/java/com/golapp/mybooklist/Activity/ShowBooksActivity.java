package com.golapp.mybooklist.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.golapp.mybooklist.R;
import com.golapp.mybooklist.RoomDatabaseManagement.Book;
import com.golapp.mybooklist.RoomDatabaseManagement.BookViewModel;

import java.util.List;

import static android.widget.Toast.LENGTH_LONG;

public class ShowBooksActivity extends AppCompatActivity {

    private static final int RESULT = 0;
    private static final int RESULT_DELETE = 1;

    List<Book> books;
    BookViewModel viewModel;
    ArrayAdapter<Book> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_books);
        final ListView listView = findViewById(R.id.listView);
        updateListView();
        //Sets a listener for an item click in the listView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Retrieves the Book clicked on and sends it to BookInformationActivity
                Book book = (Book)listView.getItemAtPosition(position);

                Intent i = new Intent(ShowBooksActivity.this, BookInformationActivity.class);
                i.putExtra("Book", book);
                startActivityForResult(i,RESULT);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If a book is deleted frim the list, the listView is updated
        if (requestCode == RESULT && resultCode == RESULT_DELETE) {
            updateListView();
            Toast.makeText(getApplicationContext(), "Book Removed From List", LENGTH_LONG).show();
        }
    }

    //Updates the listView
    private void updateListView(){
        final ListView listView = findViewById(R.id.listView);

        viewModel = ViewModelProviders.of(this).get(BookViewModel.class);

        //Get all books in the database
        books = viewModel.getAllBooks();

        //Display books in listView
        adapter = new ArrayAdapter<>(this,  R.layout.listview_item, books);
        listView.setAdapter(adapter);
    }
}
