package net.ironkernel.skakki.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.Tournament;
import net.ironkernel.skakki.entity.SignupRequest;

@Repository
public interface SignupRequestRepository extends JpaRepository<SignupRequest, Long> {
    boolean existsByMemberAndTournament(Member member, Tournament tournament);

    List<SignupRequest> findByTournament(Tournament tournament);

    List<SignupRequest> findByMember(Member member);

    void deleteByMember(Member member);
}
