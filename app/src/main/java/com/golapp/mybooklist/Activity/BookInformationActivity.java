package com.golapp.mybooklist.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.golapp.mybooklist.ApiRequests.GoogleApiRequest;
import com.golapp.mybooklist.R;
import com.golapp.mybooklist.RoomDatabaseManagement.Book;
import com.golapp.mybooklist.RoomDatabaseManagement.BookViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

public class BookInformationActivity extends AppCompatActivity {

    private static final int RESULT_DELETE = 1;

    Book book;
    BookViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_information);

        //Gets the clicked Book
        Intent i = getIntent();
        book = (Book) i.getSerializableExtra("Book");

        //Instantiates the viewModel
        viewModel = ViewModelProviders.of(this).get(BookViewModel.class);

        //Gets today's Date
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        //getTime() returns the current date in default time zone
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        String today = year + "-" + month + "-" + day;

        //Checks if the ratings of the book were not updated today
        if(book.getRatingsLastUpdate() == null || !book.getRatingsLastUpdate().equals(today)) {
            //Queries Google Books API for the book using its ISBN
            JSONObject result = new GoogleApiRequest().getBook(book.getIsbnAsBigInteger());

            //Checks if the JSON object has an averageRating and ratingsCount attribute
            if (result.has("averageRating") && result.has("ratingsCount")) {
                //Retrieves the ratings information
                try {
                    book.setRating(result.getDouble("averageRating"));
                    book.setRatingsCount(result.getInt("ratingsCount"));
                    book.setRatingsLastUpdate(today);
                    //Updates the book in the database with the ratings information
                    viewModel.update(book);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //Sets the book's title and author in the titleText
        TextView titleText = this.findViewById(R.id.titleText);
        titleText.setText(new StringBuilder().append(book.getTitle().replace('\n', ' '))
                .append("\n").append("By: ").append(book.getAuthor()).toString());

        //Sets coverPicture if one is saved in the book
        ImageView imageView = findViewById(R.id.coverPicture);
        if(book.getCoverPicture() != null) {
            try {
                Bitmap image = new GetCoverPicture().execute(book.getCoverPicture()).get();
                imageView.setImageBitmap(Bitmap.createScaledBitmap(image, 600, 800, false));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        else{
            //Makes the image view invisible if there is no cover image
            imageView.setVisibility(View.INVISIBLE);
        }

        //Sets the lowerText with each attribute that isn't null
        TextView lowerText = this.findViewById(R.id.lowerText);

        String text = "";

        if(book.getPublisher() != null) {
            text += "Publisher: " + book.getPublisher() + "\n";
        }
        if(book.getPublicationDate() != null){
            text += "Published: " + book.getPublicationDate() + "\n";
        }

        //Checks that the ratings were updated
        if(book.getRatingsLastUpdate() != null){
            text += "Rating: " + book.getRating() + "/5 From " + book.getRatingsCount() + " Reviews\n";
        }

        if(book.getPageCount() != 0) {
            text += "Page Count: " + book.getPageCount();
        }

        lowerText.setText(text);

        //Sets the description text if it is available
        if(book.getDescription() != null) {
            TextView description = this.findViewById(R.id.descriptionText);
            description.setText(new StringBuilder().append(book.getDescription()).append("\n").toString());
        }
    }

    //Delete the book from the database
    public void deleteButtonClick(View view) {
        viewModel.delete(book);

        //Returns result to BookInformationActivity
        Intent data = new Intent();
        setResult(RESULT_DELETE, data);
        finish();
    }

    //Retrieves the cover picture from the saved url
    private static class GetCoverPicture extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... imageUrl) {
            Bitmap image = null;
            try {
                //Connects to Url and save the cover picture in image
                URL url = new URL(imageUrl[0]);
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (MalformedURLException e) {
                Log.e(getClass().getSimpleName(), "Url saved in book object was not accurate", e);
            } catch (IOException e) {
                Log.e(getClass().getSimpleName(), "Failed to connect to Url", e);
            }
            return image;
        }
    }
}
