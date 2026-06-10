package net.ironkernel.skakki.controller;

import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import net.ironkernel.skakki.dto.NewMatchDto;
import net.ironkernel.skakki.entity.Match;
import net.ironkernel.skakki.entity.MatchResult;
import net.ironkernel.skakki.entity.MatchType;
import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.Tournament;
import net.ironkernel.skakki.service.AuthenticationService;
import net.ironkernel.skakki.service.MatchService;
import net.ironkernel.skakki.service.TournamentAuthorizationService;
import net.ironkernel.skakki.service.TournamentService;

@RequiredArgsConstructor
@Controller
public class MatchController {
    private final MatchService matchService;
    private final TournamentService tournamentService;
    private final AuthenticationService authenticationService;
    private final TournamentAuthorizationService tournamentAuthorizationService;

    @GetMapping("/matches/{id}")
    public String viewMatchDetails(@PathVariable Long id, Model model) {
        Match match = matchService.getMatch(id);
        Tournament tournament = match.getRound().getTournament();
        Member currentMember = authenticationService.getLoggedInMember();
        model.addAttribute("match", match);
        model.addAttribute("tournament", tournament);
        model.addAttribute("isArbiter", tournament.isArbiter(currentMember));
        return "match-details";
    }

    @GetMapping("/matches/{id}/edit")
    public String viewEditMatchPage(@PathVariable Long id, Model model) {
        Match match = matchService.getMatch(id);
        Tournament tournament = match.getRound().getTournament();
        tournamentAuthorizationService.requireArbiter(tournament);
        NewMatchDto matchDto = new NewMatchDto();
        matchDto.setRoundNumber(match.getRound().getRoundNumber());
        matchDto.setWhiteId(match.getWhite().getId());
        if (match.getBlack() != null)
            matchDto.setBlackId(match.getBlack().getId());
        matchDto.setResult(match.getResult());
        matchDto.setPgn(match.getPgn());
        matchDto.setMatchType(match.getMatchType());
        Set<Member> participants = tournament.getParticipants();
        model.addAttribute("match", match);
        model.addAttribute("tournament", tournament);
        model.addAttribute("participants", participants);
        model.addAttribute("results", MatchResult.values());
        model.addAttribute("matchTypes", MatchType.values());
        model.addAttribute("matchDto", matchDto);
        return "arbiter/edit-match";
    }

    @PutMapping("/matches/{id}/edit")
    public String editMatch(@PathVariable Long id,
            @Valid @ModelAttribute("matchDto") NewMatchDto matchDto,
            BindingResult bindingResult, Model model) {
        Match match = matchService.getMatch(id);
        Tournament tournament = match.getRound().getTournament();
        tournamentAuthorizationService.requireArbiter(tournament);
        if (bindingResult.hasErrors()) {
            model.addAttribute("match", match);
            model.addAttribute("tournament", tournament);
            model.addAttribute("participants", tournament.getParticipants());
            model.addAttribute("results", MatchResult.values());
            model.addAttribute("matchTypes", MatchType.values());
            return "arbiter/edit-match";
        }
        matchService.updateMatch(match, matchDto);
        return "redirect:/matches/" + id + "?success";
    }

    @DeleteMapping("/matches/{id}/delete")
    public String deleteMatch(@PathVariable Long id) {
        Match match = matchService.getMatch(id);
        Tournament tournament = match.getRound().getTournament();
        tournamentAuthorizationService.requireArbiter(tournament);
        matchService.deleteMatch(id);
        return "redirect:/tournaments/" + tournament.getId() + "?success";
    }

    @GetMapping("/tournaments/{id}/register-match")
    public String viewRegisterMatchPage(@PathVariable Long id, Model model) {
        Tournament tournament = tournamentService.getTournament(id);
        tournamentAuthorizationService.requireArbiter(tournament);
        Set<Member> participants = tournament.getParticipants();
        model.addAttribute("tournament", tournament);
        model.addAttribute("participants", participants);
        model.addAttribute("rounds", tournament.getRounds());
        model.addAttribute("results", MatchResult.values());
        model.addAttribute("matchTypes", MatchType.values());
        model.addAttribute("matchDto", new NewMatchDto());
        return "arbiter/register-match";
    }

    @PostMapping("/tournaments/{id}/register-match")
    public String registerMatch(@PathVariable Long id,
            @Valid @ModelAttribute("matchDto") NewMatchDto matchDto,
            BindingResult bindingResult, Model model) {
        Tournament tournament = tournamentService.getTournament(id);
        tournamentAuthorizationService.requireArbiter(tournament);
        if (bindingResult.hasErrors()) {
            model.addAttribute("tournament", tournament);
            model.addAttribute("participants", tournament.getParticipants());
            model.addAttribute("rounds", tournament.getRounds());
            model.addAttribute("results", MatchResult.values());
            model.addAttribute("matchTypes", MatchType.values());
            return "arbiter/register-match";
        }
        matchService.registerMatch(tournament, matchDto);
        return "redirect:/tournaments/" + id + "/register-match?success";
    }
}
