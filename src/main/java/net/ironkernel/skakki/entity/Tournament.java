package net.ironkernel.skakki.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tournaments")
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 2000)
    private String description;

    private LocalDate registrationDeadline;

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(length = 200)
    private String location;

    @Column(nullable = false)
    private Short numberOfRounds;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member organizer;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("roundNumber ASC")
    private List<Round> rounds = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tournament_participants", joinColumns = @JoinColumn(name = "tournament_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "member_id", nullable = false))
    private Set<Member> participants = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tournament_arbiters", joinColumns = @JoinColumn(name = "tournament_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "member_id", nullable = false))
    private Set<Member> arbiters = new HashSet<>();

    public boolean addParticipant(Member participant) {
        return participants.add(participant);
    }

    public boolean addArbiter(Member arbiter) {
        return arbiters.add(arbiter);
    }

    public boolean removeParticipant(Member participant) {
        return participants.remove(participant);
    }

    public boolean removeArbiter(Member arbiter) {
        return arbiters.remove(arbiter);
    }

    public boolean isArbiter(Member member) {
        return member != null && arbiters.contains(member);
    }

    public boolean isOrganizer(Member member) {
        return member != null && organizer != null && organizer.equals(member);
    }

    public boolean isParticipant(Member member) {
        return member != null && participants.contains(member);
    }

    public List<Match> getMatches() {
        List<Match> matches = new ArrayList<>();
        for (Round round : rounds) {
            matches.addAll(round.getMatches());
        }
        return matches;
    }
}
