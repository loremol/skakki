package net.ironkernel.skakki.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import net.ironkernel.skakki.entity.Member;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewTournamentDto {
    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 2000)
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate registrationDeadline;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Size(max = 200)
    private String location;

    @Min(1)
    @Max(99)
    private Short numberOfRounds;

    private Member organizer;
}
