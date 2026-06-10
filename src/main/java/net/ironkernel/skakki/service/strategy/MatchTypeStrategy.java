package net.ironkernel.skakki.service.strategy;

import net.ironkernel.skakki.entity.Match;
import net.ironkernel.skakki.entity.MatchResult;

public interface MatchTypeStrategy {
    void setMatchPoints(Match match, MatchResult result);
}
