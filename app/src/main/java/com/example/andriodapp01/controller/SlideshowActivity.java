package com.example.andriodapp01.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.andriodapp01.R;
import com.example.andriodapp01.model.Album;
import com.example.andriodapp01.model.AlbumManager;
import com.example.andriodapp01.model.Photo;

import java.util.List;

public class SlideshowActivity extends AppCompatActivity {

    public static final String EXTRA_ALBUM_ID = "album_id";

    private AlbumManager albumManager;
    private Album album;
    private List<Photo> photos;
    private int currentPhotoIndex = 0;

    private ImageView slideshowImageView;
    private TextView photoPositionTextView;
    private ImageButton previousButton;
    private ImageButton nextButton;

    /**
     * Helper method to start this activity
     */
    public static void start(Context context, String albumId) {
        Intent intent = new Intent(context, SlideshowActivity.class);
        intent.putExtra(EXTRA_ALBUM_ID, albumId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.slideshowToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize album manager
        albumManager = AlbumManager.getInstance(this);

        // Get album ID from intent
        String albumId = getIntent().getStringExtra(EXTRA_ALBUM_ID);
        if (albumId != null) {
            album = albumManager.getAlbumById(albumId);
        }

        // Check if album exists
        if (album == null) {
            finish();
            return;
        }

        // Get photos
        photos = album.getPhotos();

        // Check if album has photos
        if (photos.isEmpty()) {
            finish();
            return;
        }

        // Set up toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(album.getName() + " Slideshow");
        }

        // Initialize views
        slideshowImageView = findViewById(R.id.slideshowImageView);
        photoPositionTextView = findViewById(R.id.photoPositionTextView);
        previousButton = findViewById(R.id.previousButton);
        nextButton = findViewById(R.id.nextButton);

        // Set up click listeners
        previousButton.setOnClickListener(v -> showPreviousPhoto());
        nextButton.setOnClickListener(v -> showNextPhoto());

        // Show first photo
        showCurrentPhoto();
        updateNavigationButtons();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showCurrentPhoto() {
        if (currentPhotoIndex >= 0 && currentPhotoIndex < photos.size()) {
            Photo photo = photos.get(currentPhotoIndex);
            Bitmap bitmap = photo.getBitmap();
            if (bitmap != null) {
                slideshowImageView.setImageBitmap(bitmap);
            }

            // Update position text
            String positionText = (currentPhotoIndex + 1) + " of " + photos.size();
            photoPositionTextView.setText(positionText);
        }
    }

    private void showNextPhoto() {
        if (currentPhotoIndex < photos.size() - 1) {
            currentPhotoIndex++;
            showCurrentPhoto();
            updateNavigationButtons();
        }
    }

    private void showPreviousPhoto() {
        if (currentPhotoIndex > 0) {
            currentPhotoIndex--;
            showCurrentPhoto();
            updateNavigationButtons();
        }
    }

    private void updateNavigationButtons() {
        previousButton.setEnabled(currentPhotoIndex > 0);
        previousButton.setAlpha(currentPhotoIndex > 0 ? 1.0f : 0.5f);

        nextButton.setEnabled(currentPhotoIndex < photos.size() - 1);
        nextButton.setAlpha(currentPhotoIndex < photos.size() - 1 ? 1.0f : 0.5f);
    }
}