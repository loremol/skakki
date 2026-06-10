package net.ironkernel.skakki.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import net.ironkernel.skakki.entity.MatchResult;
import net.ironkernel.skakki.entity.MatchType;

@Getter
@Setter
public class NewMatchDto {
    @NotNull
    private Short roundNumber;

    @NotNull
    private Long whiteId;

    private Long blackId;

    @NotNull
    private MatchResult result;

    @Size(max = 50000)
    private String pgn;
    private MatchType matchType = MatchType.NORMAL;
}
