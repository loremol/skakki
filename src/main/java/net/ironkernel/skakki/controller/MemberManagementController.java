package net.ironkernel.skakki.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.ironkernel.skakki.dto.AdminCreateMemberDto;
import net.ironkernel.skakki.dto.UpdateMemberDto;
import net.ironkernel.skakki.entity.Gender;
import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.Role;
import net.ironkernel.skakki.entity.Title;
import net.ironkernel.skakki.service.MemberService;

@RequiredArgsConstructor
@Controller
public class MemberManagementController {
    private final MemberService memberService;

    @GetMapping("/admin/members")
    public String viewMemberList(Model model) {
        List<Member> members = memberService.getAllMembers();
        model.addAttribute("members", members);
        return "admin/member-list";
    }

    @GetMapping("/admin/members/new")
    public String viewCreateMemberPage(Model model) {
        model.addAttribute("createFields", new AdminCreateMemberDto());
        model.addAttribute("roles", Role.values());
        model.addAttribute("genders", Gender.values());
        model.addAttribute("titles", Title.values());
        return "admin/member-create";
    }

    @PostMapping("/admin/members/new")
    public String createMember(@Valid @ModelAttribute("createFields") AdminCreateMemberDto fields,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("genders", Gender.values());
            model.addAttribute("titles", Title.values());
            return "admin/member-create";
        }
        Member created = memberService.createMemberByAdmin(fields);
        return "redirect:/admin/members/" + created.getId() + "?success";
    }

    @GetMapping("/admin/members/{id}")
    public String viewMemberManagementPage(@PathVariable Long id, Model model) {
        Member member = memberService.getMember(id);
        model.addAttribute("member", member);
        model.addAttribute("memberFields", new UpdateMemberDto(member));
        model.addAttribute("roles", Role.values());
        model.addAttribute("genders", Gender.values());
        model.addAttribute("titles", Title.values());
        return "admin/member-manage";
    }

    @PutMapping("/admin/members/{id}")
    public String updateMemberInfo(@PathVariable Long id,
            @Valid @ModelAttribute("memberFields") UpdateMemberDto fields,
            BindingResult bindingResult,
            Model model) {
        if (!id.equals(fields.getId())) {
            throw new IllegalArgumentException("L'id nell'endpoint della richiesta non corrisponde all'id del membro");
        }
        if (bindingResult.hasErrors()) {
            Member member = memberService.getMember(id);
            model.addAttribute("member", member);
            model.addAttribute("roles", Role.values());
            model.addAttribute("genders", Gender.values());
            model.addAttribute("titles", Title.values());
            return "admin/member-manage";
        }
        memberService.updateMember(fields);
        return "redirect:/admin/members/" + id + "?success";
    }

    @DeleteMapping("/admin/members/{id}")
    public String deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return "redirect:/admin/members";
    }
}
