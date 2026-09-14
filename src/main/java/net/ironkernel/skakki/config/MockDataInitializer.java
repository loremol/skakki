package net.ironkernel.skakki.config;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ironkernel.skakki.entity.ArbiterApplication;
import net.ironkernel.skakki.entity.Gender;
import net.ironkernel.skakki.entity.Match;
import net.ironkernel.skakki.entity.MatchResult;
import net.ironkernel.skakki.entity.MatchType;
import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.RequestStatus;
import net.ironkernel.skakki.entity.Role;
import net.ironkernel.skakki.entity.Round;
import net.ironkernel.skakki.entity.SignupRequest;
import net.ironkernel.skakki.entity.Title;
import net.ironkernel.skakki.entity.Tournament;
import net.ironkernel.skakki.repository.ArbiterApplicationRepository;
import net.ironkernel.skakki.repository.MatchRepository;
import net.ironkernel.skakki.repository.MemberRepository;
import net.ironkernel.skakki.repository.RoundRepository;
import net.ironkernel.skakki.repository.SignupRequestRepository;
import net.ironkernel.skakki.repository.TournamentRepository;
import net.ironkernel.skakki.service.strategy.MatchStrategyFactory;

@Slf4j
@Component
@Profile("!test")
@ConditionalOnProperty(prefix = "skakki", name = "demo", havingValue = "true")
@RequiredArgsConstructor
public class MockDataInitializer implements ApplicationRunner {
        private final MemberRepository memberRepository;
        private final TournamentRepository tournamentRepository;
        private final RoundRepository roundRepository;
        private final MatchRepository matchRepository;
        private final SignupRequestRepository signupRequestRepository;
        private final ArbiterApplicationRepository arbiterApplicationRepository;
        private final PasswordEncoder passwordEncoder;
        private final MatchStrategyFactory matchStrategyFactory;

        private static final MatchResult[] RESULT_CYCLE = {
                        MatchResult.WHITE_WIN, MatchResult.DRAW, MatchResult.BLACK_WIN
        };

        @Override
        @Transactional
        public void run(ApplicationArguments args) {
                if (memberRepository.count() > 0)
                        return;
                log.info("Inizializzazione dati di esempio...");
                List<Member> members = createMembers();
                createTournaments(members);
                log.info("Dati di esempio caricati: {} membri, 5 tornei.", members.size());
        }

        // -------------------------------------------------------------------------
        // Members
        // -------------------------------------------------------------------------

        private List<Member> createMembers() {
                List<Member> list = new ArrayList<>();

                list.add(buildMember("admin", "admin@skakki.local", "admin",
                                null, null, LocalDate.of(1975, 1, 1), Gender.MALE, "Firenze", null,
                                Role.ADMIN, Role.ORGANIZER, Role.MEMBER));
                list.add(buildMember("marco.bianchi", "marco.bianchi@email.it", "password",
                                "Marco", "Bianchi", LocalDate.of(1975, 3, 12), Gender.MALE, "Firenze", Title.GM,
                                Role.MEMBER, Role.ORGANIZER));
                list.add(buildMember("giulia.rossi", "giulia.rossi@email.it", "password",
                                "Giulia", "Rossi", LocalDate.of(1982, 7, 22), Gender.FEMALE, "Siena", Title.MI,
                                Role.MEMBER, Role.ORGANIZER));
                list.add(buildMember("luca.ferrari", "luca.ferrari@email.it", "password",
                                "Luca", "Ferrari", LocalDate.of(1990, 11, 5), Gender.MALE, "Pisa", Title.MF,
                                Role.MEMBER));
                list.add(buildMember("anna.conti", "anna.conti@email.it", "password",
                                "Anna", "Conti", LocalDate.of(1988, 4, 30), Gender.FEMALE, "Arezzo", Title.CM,
                                Role.MEMBER));
                list.add(buildMember("davide.esposito", "davide.esposito@email.it", "password",
                                "Davide", "Esposito", LocalDate.of(1995, 8, 17), Gender.MALE, "Livorno", null,
                                Role.MEMBER));
                list.add(buildMember("sara.ricci", "sara.ricci@email.it", "password",
                                "Sara", "Ricci", LocalDate.of(1993, 1, 28), Gender.FEMALE, "Lucca", Title.CM,
                                Role.MEMBER));
                list.add(buildMember("matteo.russo", "matteo.russo@email.it", "password",
                                "Matteo", "Russo", LocalDate.of(1978, 6, 14), Gender.MALE, "Grosseto", null,
                                Role.MEMBER, Role.ORGANIZER));
                list.add(buildMember("chiara.marino", "chiara.marino@email.it", "password",
                                "Chiara", "Marino", LocalDate.of(1985, 9, 3), Gender.FEMALE, "Pistoia", Title.MFF,
                                Role.MEMBER));
                list.add(buildMember("roberto.greco", "roberto.greco@email.it", "password",
                                "Roberto", "Greco", LocalDate.of(1970, 12, 19), Gender.MALE, "Prato", Title.MI,
                                Role.MEMBER));
                list.add(buildMember("federica.lombardi", "federica.lombardi@email.it", "password",
                                "Federica", "Lombardi", LocalDate.of(1998, 2, 25), Gender.FEMALE, "Massa-Carrara",
                                Title.CMF,
                                Role.MEMBER));
                list.add(buildMember("andrea.giordano", "andrea.giordano@email.it", "password",
                                "Andrea", "Giordano", LocalDate.of(2000, 5, 11), Gender.MALE, "Firenze", null,
                                Role.MEMBER));
                list.add(buildMember("valentina.mancini", "valentina.mancini@email.it", "password",
                                "Valentina", "Mancini", LocalDate.of(1986, 10, 7), Gender.FEMALE, "Siena", null,
                                Role.MEMBER));
                list.add(buildMember("stefano.villa", "stefano.villa@email.it", "password",
                                "Stefano", "Villa", LocalDate.of(1979, 3, 29), Gender.MALE, "Arezzo", Title.MF,
                                Role.MEMBER));
                list.add(buildMember("elena.fontana", "elena.fontana@email.it", "password",
                                "Elena", "Fontana", LocalDate.of(1992, 8, 16), Gender.FEMALE, "Pisa", null,
                                Role.MEMBER));
                list.add(buildMember("giuseppe.moretti", "giuseppe.moretti@email.it", "password",
                                "Giuseppe", "Moretti", LocalDate.of(2001, 12, 4), Gender.MALE, "Lucca", null,
                                Role.MEMBER));

                return memberRepository.saveAll(list);
        }

        private Member buildMember(String username, String email, String rawPw,
                        String first, String last, LocalDate dob, Gender gender,
                        String province, Title title, Role... roles) {
                Member m = new Member();
                m.setUsername(username);
                m.setEmail(email);
                m.setPassword(passwordEncoder.encode(rawPw));
                m.setFirstName(first);
                m.setLastName(last);
                m.setDateOfBirth(dob);
                m.setGender(gender);
                m.setProvince(province);
                m.setCountryCode("ITA");
                m.setTitle(title);
                for (Role r : roles)
                        m.getRoles().add(r);
                return m;
        }

        // -------------------------------------------------------------------------
        // Tournaments
        // -------------------------------------------------------------------------

        private void createTournaments(List<Member> m) {
                Member admin = m.get(0);
                Member marco = m.get(1);
                Member giulia = m.get(2);
                Member luca = m.get(3);
                Member anna = m.get(4);
                Member davide = m.get(5);
                Member sara = m.get(6);
                Member matteo = m.get(7);
                Member chiara = m.get(8);
                Member roberto = m.get(9);
                Member federica = m.get(10);
                Member andrea = m.get(11);
                Member valentina = m.get(12);
                Member stefano = m.get(13);
                Member elena = m.get(14);
                Member giuseppe = m.get(15);

                createTorneoPrimaverile(admin, marco, giulia, luca, anna, davide, sara, chiara,
                                roberto, federica, andrea, valentina, stefano, elena);
                createCampionatoSenese(admin, marco, giulia, luca, anna, davide, matteo, chiara,
                                roberto, federica, elena, giuseppe, valentina);
                createTorneoEstivo(admin, marco, giulia, luca, anna, davide, sara, chiara,
                                roberto, andrea, stefano, giuseppe);
                createTorneoAutunno(admin, marco, giulia, luca, anna, davide, sara, matteo, chiara,
                                roberto, federica, andrea, valentina, stefano, elena, giuseppe);
                createGranPremioInvernale(admin, marco, giulia, luca, anna, davide, sara, matteo,
                                chiara, roberto, federica, andrea, valentina, stefano, elena, giuseppe);
        }

        // T1 — Torneo Primaverile di Firenze 2024 (past, completed)
        private void createTorneoPrimaverile(Member admin, Member marco,
                        Member giulia, Member luca, Member anna, Member davide,
                        Member sara, Member chiara, Member roberto, Member federica,
                        Member andrea, Member valentina, Member stefano, Member elena) {

                Tournament t = saveTournament(
                                "Torneo Primaverile di Firenze 2024",
                                "Torneo open primaverile del Circolo Scacchistico Fiorentino. " +
                                                "Dodici partecipanti, sistema svizzero a cinque turni.",
                                LocalDate.of(2024, 2, 29), LocalDate.of(2024, 3, 15), LocalDate.of(2024, 3, 17),
                                "Firenze, Palazzo dello Sport", (short) 5, admin);

                t.addArbiter(marco);
                List<Member> participants = List.of(
                                giulia, luca, anna, davide, sara, chiara,
                                roberto, federica, andrea, valentina, stefano, elena);
                participants.forEach(t::addParticipant);
                tournamentRepository.save(t);

                LocalDate[] dates = {
                                LocalDate.of(2024, 3, 15), LocalDate.of(2024, 3, 15),
                                LocalDate.of(2024, 3, 16), LocalDate.of(2024, 3, 16),
                                LocalDate.of(2024, 3, 17)
                };
                for (int i = 0; i < 5; i++) {
                        Round round = saveRound(t, (short) (i + 1), dates[i]);
                        populateRound(round, rotated(participants, i));
                }
        }

        // T2 — Campionato Scacchistico Senese 2024 (past, completed)
        private void createCampionatoSenese(Member admin, Member marco,
                        Member giulia, Member luca, Member anna, Member davide,
                        Member matteo, Member chiara, Member roberto, Member federica,
                        Member elena, Member giuseppe, Member valentina) {

                Tournament t = saveTournament(
                                "Campionato Scacchistico Senese 2024",
                                "Campionato ufficiale della Federazione Scacchistica Senese. " +
                                                "Undici partecipanti, quattro turni con sistema svizzero.",
                                LocalDate.of(2024, 5, 25), LocalDate.of(2024, 6, 8), LocalDate.of(2024, 6, 9),
                                "Siena, Palazzo Pubblico", (short) 4, marco);

                t.addArbiter(matteo);
                List<Member> participants = List.of(
                                admin, giulia, luca, anna, davide, chiara,
                                roberto, federica, elena, giuseppe, valentina);
                participants.forEach(t::addParticipant);
                tournamentRepository.save(t);

                LocalDate[] dates = {
                                LocalDate.of(2024, 6, 8), LocalDate.of(2024, 6, 8),
                                LocalDate.of(2024, 6, 9), LocalDate.of(2024, 6, 9)
                };
                for (int i = 0; i < 4; i++) {
                        Round round = saveRound(t, (short) (i + 1), dates[i]);
                        populateRound(round, rotated(participants, i));
                }
        }

        // T3 — Torneo dell'Estate Pisana 2024 (past, completed)
        private void createTorneoEstivo(Member admin, Member marco,
                        Member giulia, Member luca, Member anna, Member davide,
                        Member sara, Member chiara, Member roberto, Member andrea,
                        Member stefano, Member giuseppe) {

                Tournament t = saveTournament(
                                "Torneo dell'Estate Pisana 2024",
                                "Torneo estivo del Circolo degli Scacchi di Pisa. " +
                                                "Dieci partecipanti, tre turni rapidi su tre giorni.",
                                LocalDate.of(2024, 8, 5), LocalDate.of(2024, 8, 20), LocalDate.of(2024, 8, 22),
                                "Pisa, Palazzo dei Cavalieri", (short) 3, giulia);

                t.addArbiter(admin);
                List<Member> participants = List.of(
                                marco, luca, anna, davide, sara,
                                chiara, roberto, andrea, stefano, giuseppe);
                participants.forEach(t::addParticipant);
                tournamentRepository.save(t);

                LocalDate[] dates = {
                                LocalDate.of(2024, 8, 20), LocalDate.of(2024, 8, 21), LocalDate.of(2024, 8, 22)
                };
                for (int i = 0; i < 3; i++) {
                        Round round = saveRound(t, (short) (i + 1), dates[i]);
                        populateRound(round, rotated(participants, i));
                }
        }

        // T4 — Torneo d'Autunno Aretino 2025 (ongoing, 3/5 rounds played)
        private void createTorneoAutunno(Member admin, Member marco, Member giulia,
                        Member luca, Member anna, Member davide, Member sara, Member matteo,
                        Member chiara, Member roberto, Member federica, Member andrea,
                        Member valentina, Member stefano, Member elena, Member giuseppe) {

                Tournament t = saveTournament(
                                "Torneo d'Autunno Aretino 2025",
                                "Torneo autunnale organizzato dall'Accademia Scacchistica di Arezzo. " +
                                                "Tredici partecipanti, cinque turni con sistema svizzero. Tre turni già disputati.",
                                LocalDate.of(2025, 8, 31), LocalDate.of(2025, 9, 15), LocalDate.of(2025, 12, 15),
                                "Arezzo, Palazzo della Fraternita", (short) 5, matteo);

                t.addArbiter(giulia);
                List<Member> participants = List.of(
                                admin, marco, giulia, luca, anna, davide,
                                sara, chiara, roberto, federica, andrea, valentina, stefano);
                participants.forEach(t::addParticipant);
                tournamentRepository.save(t);

                // Rounds 1–3: played
                LocalDate[] playedDates = {
                                LocalDate.of(2025, 9, 15),
                                LocalDate.of(2025, 10, 13),
                                LocalDate.of(2025, 11, 10)
                };
                for (int i = 0; i < 3; i++) {
                        Round round = saveRound(t, (short) (i + 1), playedDates[i]);
                        populateRound(round, rotated(participants, i));
                }
                // Rounds 4–5: scheduled, no matches yet
                saveRound(t, (short) 4, LocalDate.of(2025, 11, 24));
                saveRound(t, (short) 5, LocalDate.of(2025, 12, 15));

                savePendingSignupRequest(elena, t);
                savePendingArbiterApplication(giuseppe, t);
        }

        // T5 — Gran Premio Invernale di Grosseto 2026 (future, registration open)
        private void createGranPremioInvernale(Member admin, Member marco, Member giulia,
                        Member luca, Member anna, Member davide, Member sara, Member matteo,
                        Member chiara, Member roberto, Member federica, Member andrea,
                        Member valentina, Member stefano, Member elena, Member giuseppe) {

                Tournament t = saveTournament(
                                "Gran Premio Invernale di Grosseto 2026",
                                "Gran Premio invernale dell'Associazione Scacchistica Maremmana. " +
                                                "Cinque turni con sistema svizzero. Iscrizioni aperte fino al 15 gennaio 2026.",
                                LocalDate.of(2026, 1, 15), LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 5),
                                "Grosseto, Palazzo Aldobrandeschi", (short) 5, admin);

                t.addArbiter(matteo);
                List<Member> participants = List.of(
                                marco, giulia, luca, anna, sara,
                                chiara, roberto, federica, elena, giuseppe, valentina);
                participants.forEach(t::addParticipant);
                tournamentRepository.save(t);

                // No rounds yet — pending/rejected requests
                savePendingSignupRequest(davide, t);
                savePendingSignupRequest(andrea, t);
                savePendingSignupRequest(stefano, t);
                saveRejectedSignupRequest(matteo, t); // arbiter tried to sign up, rejected
        }

        // -------------------------------------------------------------------------
        // Helpers
        // -------------------------------------------------------------------------

        private Tournament saveTournament(String name, String description,
                        LocalDate deadline, LocalDate start, LocalDate end,
                        String location, short rounds, Member organizer) {
                Tournament t = new Tournament();
                t.setName(name);
                t.setDescription(description);
                t.setRegistrationDeadline(deadline);
                t.setStartDate(start);
                t.setEndDate(end);
                t.setLocation(location);
                t.setNumberOfRounds(rounds);
                t.setOrganizer(organizer);
                return tournamentRepository.save(t);
        }

        private Round saveRound(Tournament t, short num, LocalDate date) {
                Round r = new Round();
                r.setTournament(t);
                r.setRoundNumber(num);
                r.setPlayDate(date);
                return roundRepository.save(r);
        }

        private void populateRound(Round round, List<Member> participants) {
                int resultIndex = 0;
                for (int i = 0; i + 1 < participants.size(); i += 2) {
                        Match match = new Match();
                        match.setRound(round);
                        match.setWhite(participants.get(i));
                        match.setBlack(participants.get(i + 1));
                        match.setMatchType(MatchType.NORMAL);
                        MatchResult result = RESULT_CYCLE[resultIndex % RESULT_CYCLE.length];
                        resultIndex++;
                        matchStrategyFactory.getStrategy(MatchType.NORMAL).setMatchPoints(match, result);
                        matchRepository.save(match);
                }
                if (participants.size() % 2 == 1) {
                        Match bye = new Match();
                        bye.setRound(round);
                        bye.setWhite(participants.get(participants.size() - 1));
                        bye.setMatchType(MatchType.BYE);
                        matchStrategyFactory.getStrategy(MatchType.BYE).setMatchPoints(bye, null);
                        matchRepository.save(bye);
                }
        }

        private List<Member> rotated(List<Member> list, int by) {
                int size = list.size();
                int offset = by % size;
                List<Member> result = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                        result.add(list.get((i + offset) % size));
                }
                return result;
        }

        private void savePendingSignupRequest(Member member, Tournament tournament) {
                SignupRequest req = new SignupRequest();
                req.setMember(member);
                req.setTournament(tournament);
                req.setStatus(RequestStatus.PENDING);
                req.setRequestDate(LocalDate.now());
                signupRequestRepository.save(req);
        }

        private void saveRejectedSignupRequest(Member member, Tournament tournament) {
                SignupRequest req = new SignupRequest();
                req.setMember(member);
                req.setTournament(tournament);
                req.setStatus(RequestStatus.REJECTED);
                req.setRequestDate(LocalDate.now().minusDays(10));
                signupRequestRepository.save(req);
        }

        private void savePendingArbiterApplication(Member member, Tournament tournament) {
                ArbiterApplication app = new ArbiterApplication();
                app.setMember(member);
                app.setTournament(tournament);
                app.setStatus(RequestStatus.PENDING);
                app.setRequestDate(LocalDate.now());
                arbiterApplicationRepository.save(app);
        }
}
