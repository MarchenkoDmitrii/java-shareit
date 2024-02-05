package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
    private static final String DATE_TIME = "2023-07-23T07:33:00";
    @Autowired
    private JacksonTester<BookingDto> json;
    private BookingDto bookingDto = null;

    @BeforeEach
    public void init() {
        bookingDto = new BookingDto(1L,
                LocalDateTime.parse("2023-07-23T07:33:00"),
                LocalDateTime.parse("2023-07-23T07:33:00"));
    }

    @Test
    @SneakyThrows
    public void startSerializes() {
        assertThat(json.write(bookingDto)).extractingJsonPathStringValue("$.start")
                .isEqualTo(DATE_TIME);
    }

    @Test
    @SneakyThrows
    public void endSerializes() {
        assertThat(json.write(bookingDto)).extractingJsonPathStringValue("$.end")
                .isEqualTo(DATE_TIME);
    }

}
