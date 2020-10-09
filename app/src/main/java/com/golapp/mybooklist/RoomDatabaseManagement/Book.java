package com.golapp.mybooklist.RoomDatabaseManagement;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.math.BigInteger;

@Entity(tableName = "books")
public class Book implements Serializable {

    //Required attributes
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo
    private int  id;

    @NonNull
    @ColumnInfo
    private String  isbn;

    @NonNull
    @ColumnInfo
    private String title;

    @NonNull
    @ColumnInfo
    private String author;

    //Optional attributes
    @ColumnInfo
    private String publisher;

    @ColumnInfo(name = "publication_date")
    private String publicationDate;

    @ColumnInfo
    private String description;

    @ColumnInfo
    private int pageCount;

    @ColumnInfo(name = "cover_picture")
    private String coverPicture;

    //Ratings Info
    @ColumnInfo
    private double rating;

    @ColumnInfo(name = "ratings_count")
    private int ratingsCount;

    @ColumnInfo(name = "ratings_last_update")
    private String ratingsLastUpdate;


    //Used by room database
    protected Book(int id, String isbn, String title, String author, String publisher, String publicationDate,
                   String description, int pageCount, String coverPicture,
                   double rating, int ratingsCount, String ratingsLastUpdate) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationDate = publicationDate;
        this.description = description;
        this.pageCount = pageCount;
        this.coverPicture = coverPicture;
        this.rating = rating;
        this.ratingsCount = ratingsCount;
        this.ratingsLastUpdate = ratingsLastUpdate;
    }

    //Used by application
    @Ignore
    public Book(BigInteger isbn, String title, String author) {
        this.isbn = isbn.toString();
        this.title = title;
        this.author = author;
    }

    //Getter methods
    public int getId() {
        return id;
    }

    //Used by Room Database
    public String getIsbn() { return isbn;}

    //Used by application
    public BigInteger getIsbnAsBigInteger() {
        return new BigInteger(isbn);
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public String getDescription() {
        return description;
    }

    public int getPageCount() {
        return pageCount;
    }

    public String getCoverPicture() {
        return coverPicture;
    }

    public double getRating() {
        return rating;
    }

    public int getRatingsCount() {
        return ratingsCount;
    }

    public String getRatingsLastUpdate() {
        return ratingsLastUpdate;
    }

    //Setter methods
    public void setId(int id){
        this.id = id;
    }

    //Used by Room Database
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setIsbn(BigInteger isbn) {
        this.isbn = isbn.toString();
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public void setCoverPicture (String coverPicture) {
        this.coverPicture = coverPicture;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setRatingsCount(int ratingsCount) {
        this.ratingsCount = ratingsCount;
    }

    public void setRatingsLastUpdate(String ratingsLastUpdate) {
        this.ratingsLastUpdate = ratingsLastUpdate;
    }

    //ToString used by listView
    @Override
    public String toString() {
        return (title + "\n" + "By: " + author);
    }

}

