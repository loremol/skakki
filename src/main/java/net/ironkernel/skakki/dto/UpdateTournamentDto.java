package net.ironkernel.skakki.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ironkernel.skakki.entity.Tournament;

@Getter
@Setter
@NoArgsConstructor
public class UpdateTournamentDto {
    private Long id;

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

    public UpdateTournamentDto(Tournament tournament) {
        this.id = tournament.getId();
        this.name = tournament.getName();
        this.description = tournament.getDescription();
        this.registrationDeadline = tournament.getRegistrationDeadline();
        this.startDate = tournament.getStartDate();
        this.endDate = tournament.getEndDate();
        this.location = tournament.getLocation();
        this.numberOfRounds = tournament.getNumberOfRounds();
    }
}
