package net.ironkernel.skakki.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "arbiter_applications", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "member_id", "tournament_id" })
})
public class ArbiterApplication extends TournamentRequest {
}
