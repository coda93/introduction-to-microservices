package com.example.song_service.exception;

public class SongAlreadyExistsException extends RuntimeException {

    public SongAlreadyExistsException(Integer id) {
        super("Metadata for resource ID=" + id + " already exists");
    }
}
