package net.ironkernel.skakki.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.ironkernel.skakki.entity.Match;
import net.ironkernel.skakki.entity.Member;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    boolean existsByWhite(Member member);

    boolean existsByBlack(Member member);
}
