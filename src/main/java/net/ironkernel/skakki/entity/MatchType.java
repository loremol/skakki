package net.ironkernel.skakki.entity;

public enum MatchType {
    NORMAL, BYE, WHITE_FORFEIT, BLACK_FORFEIT;

    public boolean isBye() {
        return this == BYE;
    }

    public boolean isForfeit() {
        return this == WHITE_FORFEIT || this == BLACK_FORFEIT;
    }
}
