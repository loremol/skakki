package net.ironkernel.skakki.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import net.ironkernel.skakki.entity.Match;
import net.ironkernel.skakki.entity.MatchResult;
import net.ironkernel.skakki.entity.MatchType;
import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.service.strategy.BlackForfeitMatchStrategy;
import net.ironkernel.skakki.service.strategy.ByeMatchStrategy;
import net.ironkernel.skakki.service.strategy.MatchStrategyFactory;
import net.ironkernel.skakki.service.strategy.MatchTypeStrategy;
import net.ironkernel.skakki.service.strategy.NormalMatchStrategy;
import net.ironkernel.skakki.service.strategy.WhiteForfeitMatchStrategy;

class MatchStrategyTest {
    @Test
    public void normal_whiteWin_setsOneZero() {
        Match m = matchWithBlack();
        new NormalMatchStrategy().setMatchPoints(m, MatchResult.WHITE_WIN);

        assertThat(m.getResult()).isEqualTo(MatchResult.WHITE_WIN);
        assertThat(m.getWhitePoints()).isEqualTo(1.0f);
        assertThat(m.getBlackPoints()).isEqualTo(0.0f);
    }

    @Test
    public void normal_blackWin_setsZeroOne() {
        Match m = matchWithBlack();
        new NormalMatchStrategy().setMatchPoints(m, MatchResult.BLACK_WIN);

        assertThat(m.getResult()).isEqualTo(MatchResult.BLACK_WIN);
        assertThat(m.getWhitePoints()).isEqualTo(0.0f);
        assertThat(m.getBlackPoints()).isEqualTo(1.0f);
    }

    @Test
    public void normal_draw_setsHalfHalf() {
        Match m = matchWithBlack();
        new NormalMatchStrategy().setMatchPoints(m, MatchResult.DRAW);

        assertThat(m.getResult()).isEqualTo(MatchResult.DRAW);
        assertThat(m.getWhitePoints()).isEqualTo(0.5f);
        assertThat(m.getBlackPoints()).isEqualTo(0.5f);
    }

    @Test
    public void normal_nullResult_throws() {
        Match m = matchWithBlack();

        assertThatThrownBy(() -> new NormalMatchStrategy().setMatchPoints(m, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Il risultato è obbligatorio");
    }

    @Test
    public void normal_nullWhite_throws() {
        Match m = matchWithoutWhite();
        m.setBlack(newMember());

        assertThatThrownBy(() -> new NormalMatchStrategy().setMatchPoints(m, MatchResult.WHITE_WIN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sapere chi è il bianco è obbligatorio");
    }

    @Test
    public void normal_nullBlack_throws() {
        Match m = matchWithoutBlack();

        assertThatThrownBy(() -> new NormalMatchStrategy().setMatchPoints(m, MatchResult.WHITE_WIN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sapere chi è il nero è obbligatorio");
    }

    @Test
    public void bye_setsWhiteOneBlackNull() {
        Match m = matchWithoutBlack();
        new ByeMatchStrategy().setMatchPoints(m, null);

        assertThat(m.getResult()).isEqualTo(MatchResult.WHITE_WIN);
        assertThat(m.getWhitePoints()).isEqualTo(1.0f);
        assertThat(m.getBlackPoints()).isEqualTo(0.0f);
        assertThat(m.getBlack()).isNull();
    }

    @Test
    public void bye_nullWhite_throws() {
        Match m = matchWithoutWhite();

        assertThatThrownBy(() -> new ByeMatchStrategy().setMatchPoints(m, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sapere chi è il bianco è obbligatorio");
    }

    @Test
    public void whiteForfeit_blackWins() {
        Match m = matchWithBlack();
        new WhiteForfeitMatchStrategy().setMatchPoints(m, null);

        assertThat(m.getResult()).isEqualTo(MatchResult.BLACK_WIN);
        assertThat(m.getWhitePoints()).isEqualTo(0.0f);
        assertThat(m.getBlackPoints()).isEqualTo(1.0f);
    }

    @Test
    public void whiteForfeit_nullBlack_throws() {
        Match m = matchWithoutBlack();

        assertThatThrownBy(() -> new WhiteForfeitMatchStrategy().setMatchPoints(m, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("WHITE_FORFEIT");
    }

    @Test
    public void whiteForfeit_nullWhite_throws() {
        Match m = matchWithoutWhite();
        m.setBlack(newMember());

        assertThatThrownBy(() -> new WhiteForfeitMatchStrategy().setMatchPoints(m, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sapere chi è il bianco è obbligatorio");
    }

    @Test
    public void blackForfeit_whiteWins() {
        Match m = matchWithBlack();
        new BlackForfeitMatchStrategy().setMatchPoints(m, null);

        assertThat(m.getResult()).isEqualTo(MatchResult.WHITE_WIN);
        assertThat(m.getWhitePoints()).isEqualTo(1.0f);
        assertThat(m.getBlackPoints()).isEqualTo(0.0f);
    }

    @Test
    public void blackForfeit_nullBlack_throws() {
        Match m = matchWithoutBlack();

        assertThatThrownBy(() -> new BlackForfeitMatchStrategy().setMatchPoints(m, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("BLACK_FORFEIT");
    }

    @Test
    public void blackForfeit_nullWhite_throws() {
        Match m = matchWithoutWhite();
        m.setBlack(newMember());

        assertThatThrownBy(() -> new BlackForfeitMatchStrategy().setMatchPoints(m, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sapere chi è il bianco è obbligatorio");
    }

    @Test
    public void factory_returnsCorrectStrategy() {
        MatchStrategyFactory factory = new MatchStrategyFactory();

        MatchTypeStrategy normal = factory.getStrategy(MatchType.NORMAL);
        MatchTypeStrategy bye = factory.getStrategy(MatchType.BYE);
        MatchTypeStrategy whiteForfeit = factory.getStrategy(MatchType.WHITE_FORFEIT);
        MatchTypeStrategy blackForfeit = factory.getStrategy(MatchType.BLACK_FORFEIT);

        assertThat(normal).isInstanceOf(NormalMatchStrategy.class);
        assertThat(bye).isInstanceOf(ByeMatchStrategy.class);
        assertThat(whiteForfeit).isInstanceOf(WhiteForfeitMatchStrategy.class);
        assertThat(blackForfeit).isInstanceOf(BlackForfeitMatchStrategy.class);
    }

    @Test
    public void factory_unknownType_throws() {
        MatchStrategyFactory factory = new MatchStrategyFactory();

        assertThatThrownBy(() -> factory.getStrategy(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tipo di partita non specificato");
    }

    private Member newMember() {
        return new Member();
    }

    private Match matchWithBlack() {
        Match m = new Match();
        m.setWhite(newMember());
        m.setBlack(newMember());
        return m;
    }

    private Match matchWithoutBlack() {
        Match m = new Match();
        m.setWhite(newMember());
        return m;
    }

    private Match matchWithoutWhite() {
        Match m = new Match();
        m.setBlack(newMember());
        return m;
    }
}
