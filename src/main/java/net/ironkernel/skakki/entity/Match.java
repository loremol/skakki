package net.ironkernel.skakki.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "matches")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Round round;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member white;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member black;

    @Column(columnDefinition = "TEXT")
    private String pgn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchResult result;

    @Column(nullable = false)
    private Float whitePoints;
    @Column(nullable = false)
    private Float blackPoints;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MatchType matchType = MatchType.NORMAL;
}
