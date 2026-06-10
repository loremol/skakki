package net.ironkernel.skakki.dto;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ironkernel.skakki.entity.Gender;
import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.Role;
import net.ironkernel.skakki.entity.Title;

@Getter
@Setter
@NoArgsConstructor
public class UpdateMemberDto {
    private Long id;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

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

    public UpdateMemberDto(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.username = member.getUsername();
        this.firstName = member.getFirstName();
        this.lastName = member.getLastName();
        this.dateOfBirth = member.getDateOfBirth();
        this.gender = member.getGender();
        this.fideId = member.getFideId();
        this.fsiId = member.getFsiId();
        this.countryCode = member.getCountryCode();
        this.province = member.getProvince();
        this.title = member.getTitle();
        this.roles = member.getRoles();
    }
}
