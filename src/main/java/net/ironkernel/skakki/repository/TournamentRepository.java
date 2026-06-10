package net.ironkernel.skakki.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.Tournament;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    List<Tournament> findByOrganizer(Member organizer);

    List<Tournament> findByParticipantsContaining(Member member);

    List<Tournament> findByArbitersContaining(Member member);
}
