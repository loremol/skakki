package net.ironkernel.skakki.service.strategy;

import net.ironkernel.skakki.entity.Match;
import net.ironkernel.skakki.entity.MatchResult;
import net.ironkernel.skakki.entity.MatchType;

public class BlackForfeitMatchStrategy implements MatchTypeStrategy {
    @Override
    public void setMatchPoints(Match match, MatchResult result) {
        if (match.getWhite() == null)
            throw new IllegalArgumentException(
                    "Sapere chi è il bianco è obbligatorio per il tipo di partita = " + MatchType.BLACK_FORFEIT + ".");
        if (match.getBlack() == null)
            throw new IllegalArgumentException(
                    "Sapere chi è il nero è obbligatorio per il tipo di partita = " + MatchType.BLACK_FORFEIT + ".");
        match.setResult(MatchResult.WHITE_WIN);
        match.setWhitePoints(1.0f);
        match.setBlackPoints(0.0f);
    }
}
