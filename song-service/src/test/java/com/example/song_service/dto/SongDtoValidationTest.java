package com.example.song_service.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SongDtoValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    private SongDto valid() {
        return new SongDto(1, "Name", "Artist", "Album", "02:59", "1977");
    }

    @Test
    void validDtoHasNoViolations() {
        assertThat(validator.validate(valid())).isEmpty();
    }

    @Test
    void missingRequiredFieldsAreReported() {
        SongDto dto = new SongDto(null, "", "", "", "", "");
        assertThat(validator.validate(dto)).isNotEmpty();
    }

    @Test
    void durationWithoutLeadingZerosIsInvalid() {
        SongDto dto = new SongDto(1, "Name", "Artist", "Album", "2:5", "1977");
        assertThat(validator.validate(dto))
                .anyMatch(v -> v.getPropertyPath().toString().equals("duration"));
    }

    @Test
    void durationWithSecondsOver59IsInvalid() {
        SongDto dto = new SongDto(1, "Name", "Artist", "Album", "02:60", "1977");
        assertThat(validator.validate(dto))
                .anyMatch(v -> v.getPropertyPath().toString().equals("duration"));
    }

    @Test
    void yearOutOfRangeIsInvalid() {
        SongDto dto = new SongDto(1, "Name", "Artist", "Album", "02:59", "1899");
        assertThat(validator.validate(dto))
                .anyMatch(v -> v.getPropertyPath().toString().equals("year"));
    }

    @Test
    void nameTooLongIsInvalid() {
        String longName = "x".repeat(101);
        SongDto dto = new SongDto(1, longName, "Artist", "Album", "02:59", "1977");
        assertThat(validator.validate(dto))
                .anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }
}
