package net.ironkernel.skakki.service.strategy;

import net.ironkernel.skakki.entity.Match;
import net.ironkernel.skakki.entity.MatchResult;
import net.ironkernel.skakki.entity.MatchType;

public class WhiteForfeitMatchStrategy implements MatchTypeStrategy {
    @Override
    public void setMatchPoints(Match match, MatchResult result) {
        if (match.getWhite() == null)
            throw new IllegalArgumentException(
                    "Sapere chi è il bianco è obbligatorio per il tipo di partita = " + MatchType.WHITE_FORFEIT + ".");
        if (match.getBlack() == null)
            throw new IllegalArgumentException(
                    "Sapere chi è il nero è obbligatorio per il tipo di partita = " + MatchType.WHITE_FORFEIT + ".");
        match.setResult(MatchResult.BLACK_WIN);
        match.setWhitePoints(0.0f);
        match.setBlackPoints(1.0f);
    }
}
