package com.example.song_service.exception;

public class SongNotFoundException extends RuntimeException {

    public SongNotFoundException(Integer id) {
        super("Song metadata for ID=" + id + " not found");
    }
}
