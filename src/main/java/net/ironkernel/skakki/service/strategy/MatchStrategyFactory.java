package net.ironkernel.skakki.service.strategy;

import java.util.Map;

import org.springframework.stereotype.Component;

import net.ironkernel.skakki.entity.MatchType;

@Component
public class MatchStrategyFactory {
    private final Map<MatchType, MatchTypeStrategy> strategies;

    public MatchStrategyFactory() {
        strategies = Map.of(
            MatchType.NORMAL,        new NormalMatchStrategy(),
            MatchType.BYE,           new ByeMatchStrategy(),
            MatchType.WHITE_FORFEIT, new WhiteForfeitMatchStrategy(),
            MatchType.BLACK_FORFEIT, new BlackForfeitMatchStrategy()
        );
    }

    public MatchTypeStrategy getStrategy(MatchType type) {
        if (type == null)
            throw new IllegalArgumentException("Tipo di partita non specificato.");
        MatchTypeStrategy strategy = strategies.get(type);
        if (strategy == null)
            throw new IllegalArgumentException("Tipo di partita sconosciuto: " + type);
        return strategy;
    }
}
