package com.golapp.mybooklist.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import com.golapp.mybooklist.ApiRequests.GoogleApiRequest;
import com.golapp.mybooklist.R;
import com.golapp.mybooklist.RoomDatabaseManagement.Book;
import com.golapp.mybooklist.RoomDatabaseManagement.BookViewModel;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity {

    private BigInteger isbn;
    Uri capturedImageUri;
    File image;

    //Activity result codes
    private static final int CAMERA_ACTIVITY_RESULT = 0;
    private static final int ENTER_ISBN_ACTIVITY_RESULT = 1;

    //Permission request codes
    private static final int PERMISSIONS_REQUEST_CAMERA = 0;
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Takes user to page where they can manually enter an ISBN
    public void manuallyEnterIsbnButtonClicked(View view) {
        Intent intent = new Intent(this, ManuallyEnterIsbnActivity.class);
        startActivityForResult(intent, ENTER_ISBN_ACTIVITY_RESULT);
    }

    public void scanBarcodeButtonClicked(View view){
        //Checks if the device has a camera
        if (checkCameraHardware(this)) {
            //If CAMERA permission is not granted it asks for permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
            }
            //If WRITE_EXTERNAL_STORAGE permission is not granted it asks for permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
            //If READ_EXTERNAL_STORAGE permission is not granted it asks for permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
            //If all permissions are already granted, open the camera
            else {
                takePicture();
            }
        }
        //If device does not have a camera, tell user to manually enter ISBN
        else{
            Toast.makeText(getApplicationContext(), R.string.no_camera, LENGTH_LONG).show();
        }
    }

    //Opens camera activity
    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Returns the first activity component that can handle the intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            //Creates a unique output file for the image
            Date  currentTime = Calendar.getInstance().getTime();
            String imageFileName = currentTime.getTime() + ".jpg";
            File directory =  Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            image = new File(directory, imageFileName);

            //Gets the file Uri
            capturedImageUri = Uri.fromFile(image);

            //Adds output file to the intent
            intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);

            //Gives the camera app permission to write into the URL
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            //Starts camera activity for result
            startActivityForResult(intent, CAMERA_ACTIVITY_RESULT);
        }
    }

    //Gets captured image and scans the barcode
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_ACTIVITY_RESULT && resultCode == RESULT_OK) {

            if(image != null) {
                //Retrieves captured image as a bitmap
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());

                //Creates a barcode detector that only detects ISBN
                BarcodeDetector detector = new BarcodeDetector.Builder(this).build();
                //Creates frame from image
                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                //Passes frame to barcode detector and places all detected barcodes in an array
                SparseArray<Barcode> barcodeSparseArray = detector.detect(frame);

                //Deletes the image
                image.delete();

                //Checks that a barcode was detected
                if (barcodeSparseArray.size() > 0) {
                    //Uses the first barcode detected in the image
                    Barcode code = barcodeSparseArray.valueAt(0);
                    isbn = new BigInteger(code.rawValue);
                    acquireBookInformation(isbn);
                } else {
                    Toast.makeText(getApplicationContext(), "Barcode not recognized in image", LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(getApplicationContext(), R.string.image_not_exist, LENGTH_LONG).show();
            }
        }
        if(requestCode == ENTER_ISBN_ACTIVITY_RESULT && resultCode == RESULT_OK) {
            isbn = new BigInteger(data.getStringExtra("ISBN"));
            acquireBookInformation(isbn);
        }
    }

    //Gets the results of the permission request and responds accordingly
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //If writing permission is not granted it asks for permission
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                    //If reading permission is not granted it asks for permission
                    else if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                    else {
                        //If all permissions were granted, calls for a camera app to capture an image
                        takePicture();
                    }
                }
                return;
            }
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE :{
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                    else {
                        //If all permissions were granted, calls for a camera app to capture an image
                        takePicture();
                    }
                }
                return;
            }
            case PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE :{
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //If all permissions were granted, calls for a camera app to capture an image
                    takePicture();
                }
            }
        }
    }

    //Checks if the device has a camera
    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public void showBooksButtonClicked(View view) {
        //Change activity to ShowEntriesActivity
        Intent i = new Intent(this, ShowBooksActivity.class);
        startActivity(i);
    }

    //Sends the ISBN to Google Books API and saves its information in the database
    private void acquireBookInformation(BigInteger isbn){
        Book book;
        BookViewModel viewModel = ViewModelProviders.of(this).get(BookViewModel.class);
        //Queries Google Books for book information
        JSONObject result = new GoogleApiRequest().getBook(isbn);
        if (result != null) {
            try {
                //Parses JSON to make book object
                String title = result.getString("title");
                String author = String.valueOf(result.getJSONArray("authors").get(0));
                book = new Book(isbn, title, author);

                //Sets the optional attributes of the book
                if(result.has("publisher")) {
                    book.setPublisher(result.getString("publisher"));
                }

                if(result.has("publishedDate")) {
                    book.setPublicationDate(result.getString("publishedDate"));
                }

                if(result.has("description")) {
                    book.setDescription(result.getString("description"));
                }

                if(result.has("pageCount")) {
                    book.setPageCount(result.getInt("pageCount"));
                }

                if(result.has("imageLinks")) {
                    book.setCoverPicture(result.getJSONObject("imageLinks").getString("thumbnail"));
                }

                //Inserts book in database
                viewModel.insert(book);

                Toast.makeText(getApplicationContext(), title + " Created", LENGTH_LONG).show();
            } catch (JSONException e) {
                Log.e(getClass().getSimpleName(), "Exception parsing JSON", e);
                Toast.makeText(getApplicationContext(),"Failed to parse JSON correctly", LENGTH_LONG).show();
            }
        }
        //ISBN was not found in Google Book API search
        else{
            String notFound = "Book with the ISBN " + isbn + " was not found";
            Toast.makeText(getApplicationContext(),notFound, LENGTH_LONG).show();
        }
    }
}
