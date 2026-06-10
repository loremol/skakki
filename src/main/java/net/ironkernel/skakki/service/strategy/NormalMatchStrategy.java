package net.ironkernel.skakki.service.strategy;

import net.ironkernel.skakki.entity.Match;
import net.ironkernel.skakki.entity.MatchResult;
import net.ironkernel.skakki.entity.MatchType;

public class NormalMatchStrategy implements MatchTypeStrategy {
    @Override
    public void setMatchPoints(Match match, MatchResult result) {
        if (match.getWhite() == null)
            throw new IllegalArgumentException(
                    "Sapere chi è il bianco è obbligatorio per il tipo di partita = " + MatchType.NORMAL + ".");
        if (match.getBlack() == null)
            throw new IllegalArgumentException(
                    "Sapere chi è il nero è obbligatorio per il tipo di partita = " + MatchType.NORMAL + ".");
        if (result == null)
            throw new IllegalArgumentException("Il risultato è obbligatorio per il tipo di partita = " + MatchType.NORMAL + ".");
        match.setResult(result);
        switch (result) {
            case WHITE_WIN -> { match.setWhitePoints(1.0f); match.setBlackPoints(0.0f); }
            case BLACK_WIN -> { match.setWhitePoints(0.0f); match.setBlackPoints(1.0f); }
            case DRAW      -> { match.setWhitePoints(0.5f); match.setBlackPoints(0.5f); }
        }
    }
}
