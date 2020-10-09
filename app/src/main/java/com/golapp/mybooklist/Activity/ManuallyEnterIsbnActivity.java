package com.golapp.mybooklist.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.golapp.mybooklist.R;

import java.math.BigInteger;

import static android.widget.Toast.LENGTH_LONG;

public class ManuallyEnterIsbnActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manually_enter_isbn);
    }

    public void submitButtonClicked(View view) {
        //Gets the entered ISBN
        TextView text = findViewById(R.id.isbnText);

        String isbn = String.valueOf(text.getText());

        //Checks if isbn can be converted to a BigInteger
        if (isBigInteger(isbn)) {

            //Checks that the isbn is in a format resembling either ISBN-10 or ISBN-13
            if (isIsbn10(isbn) || isIsbn13(isbn)) {
                //Returns ISBN to MainActivity
                Intent data = new Intent();
                data.putExtra("ISBN", isbn);
                setResult(RESULT_OK, data);
                finish();
            }
            else
                Toast.makeText(getApplicationContext(), R.string.wrong_format, LENGTH_LONG).show();
        }
        else{
            //Isbn cannot be converted to a BigInteger
            Toast.makeText(getApplicationContext(), R.string.improper_input, LENGTH_LONG).show();
        }
    }

    //Checks if isbn is in a format resembling ISBN-13
    private boolean isIsbn13(String isbn) {

        if (isbn.length() == 13) {
            BigInteger floor = new BigInteger("9780000000000");
            BigInteger ceiling = new BigInteger("9999999999999");
            BigInteger i = new BigInteger(isbn);
            //BigInteger.compareTo returns 0 if equal to, 1 if greater than, -1 if less than
            if (floor.compareTo(i) <= 0 && ceiling.compareTo(i) >= 0) {
                return true;
            }
        }
        return false;
    }

        //Checks if isbn is in a format resembling ISBN-10
        private boolean isIsbn10 (String isbn){

            if (isbn.length() == 10) {
                return true;
            }
            return false;
        }

        //Checks if isbn can be converted to a BigInteger
        private boolean isBigInteger (String isbn){
            try {
                new BigInteger(String.valueOf(isbn));
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }
