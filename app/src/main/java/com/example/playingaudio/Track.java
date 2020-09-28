package com.example.playingaudio;

public class Track {

    private String title;
    private int trackId;
    private String artist;
    private int image;

    public Track(String title, int trackId, String artist, int image) {
        this.title = title;
        this.trackId = trackId;
        this.artist = artist;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getTrackId() { return trackId; }

    public void setTrackId(int trackId) { this.trackId = trackId; }
}