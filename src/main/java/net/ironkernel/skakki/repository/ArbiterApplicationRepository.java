package net.ironkernel.skakki.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.Tournament;
import net.ironkernel.skakki.entity.ArbiterApplication;

@Repository
public interface ArbiterApplicationRepository extends JpaRepository<ArbiterApplication, Long> {
    boolean existsByMemberAndTournament(Member member, Tournament tournament);

    List<ArbiterApplication> findByTournament(Tournament tournament);

    List<ArbiterApplication> findByMember(Member member);

    void deleteByMember(Member member);
}
