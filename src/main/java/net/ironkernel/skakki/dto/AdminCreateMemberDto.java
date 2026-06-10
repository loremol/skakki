package net.ironkernel.skakki.dto;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import net.ironkernel.skakki.entity.Gender;
import net.ironkernel.skakki.entity.Role;
import net.ironkernel.skakki.entity.Title;

@Getter
@Setter
public class AdminCreateMemberDto {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8)
    private String password;

    @Size(max = 100)
    private String firstName;
    @Size(max = 100)
    private String lastName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private Gender gender;

    @Size(max = 20)
    private String fideId;
    @Size(max = 20)
    private String fsiId;
    @Size(max = 3)
    private String countryCode;
    @Size(max = 100)
    private String province;
    private Title title;

    private Set<Role> roles = new HashSet<>();
}
