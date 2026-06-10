package net.ironkernel.skakki.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.ironkernel.skakki.dto.NewMatchDto;
import net.ironkernel.skakki.entity.Match;
import net.ironkernel.skakki.entity.Round;
import net.ironkernel.skakki.entity.Tournament;
import net.ironkernel.skakki.repository.MatchRepository;
import net.ironkernel.skakki.service.strategy.MatchStrategyFactory;

@RequiredArgsConstructor
@Service
public class MatchService {
    private final MatchRepository matchRepository;
    private final RoundService roundService;
    private final MemberService memberService;
    private final MatchStrategyFactory matchStrategyFactory;

    @Transactional
    public Match registerMatch(Tournament tournament, NewMatchDto matchDto) {
        Round round = roundService.findOrCreateRound(tournament, matchDto.getRoundNumber());

        Match match = new Match();
        match.setRound(round);
        match.setMatchType(matchDto.getMatchType());
        match.setWhite(memberService.getMember(matchDto.getWhiteId()));
        match.setBlack(matchDto.getBlackId() != null ? memberService.getMember(matchDto.getBlackId()) : null);
        matchStrategyFactory.getStrategy(matchDto.getMatchType()).setMatchPoints(match, matchDto.getResult());
        match.setPgn(matchDto.getPgn());

        return matchRepository.save(match);
    }

    @Transactional(readOnly = true)
    public Match getMatch(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nessuna partita con id=" + id + " trovata."));
    }

    @Transactional
    public Match updateMatch(Match match, NewMatchDto matchDto) {
        match.setMatchType(matchDto.getMatchType());
        match.setWhite(memberService.getMember(matchDto.getWhiteId()));
        match.setBlack(matchDto.getBlackId() != null ? memberService.getMember(matchDto.getBlackId()) : null);
        matchStrategyFactory.getStrategy(matchDto.getMatchType()).setMatchPoints(match, matchDto.getResult());
        match.setPgn(matchDto.getPgn());

        return matchRepository.save(match);
    }

    @Transactional
    public void deleteMatch(Long id) {
        matchRepository.deleteById(id);
    }
}
