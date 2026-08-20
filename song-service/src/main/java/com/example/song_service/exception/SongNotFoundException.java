package com.example.song_service.exception;

public class SongNotFoundException extends RuntimeException {

    public SongNotFoundException(Integer id) {
        super("Song metadata with ID=" + id + " not found");
    }
}
