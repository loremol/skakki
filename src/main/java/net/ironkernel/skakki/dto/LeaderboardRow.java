package net.ironkernel.skakki.dto;

import net.ironkernel.skakki.entity.Member;

public record LeaderboardRow(int rank, Member member, float totalPoints, float buchholzPoints,
        float sonnebornBergerPoints) {
}
