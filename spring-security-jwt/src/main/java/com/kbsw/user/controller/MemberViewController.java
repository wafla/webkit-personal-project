package com.kbsw.user.controller;

import com.kbsw.user.dto.ListMemberDTO;
import com.kbsw.user.dto.ResponseDTO;
import com.kbsw.user.entity.MemberEntity;
import com.kbsw.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller  //뷰네임을 문자열로 반환 ==> 타임리프
@RequiredArgsConstructor //생성자 인젝션. final 상수들만 생성자를 통해 자동 주입해준다
@Slf4j
public class MemberViewController {

    private final MemberService memberService;

    @GetMapping("/signup")
    public String signup(){

        return "signup";
        //resources/templates/signup.html
    }

    @GetMapping("/admin/users")
    public String getMemberAll(@RequestParam(defaultValue = "0") int findType, String findKeyword,Model model){
        log.info("findType={}, findKeyword={}", findType, findKeyword);
        List<ListMemberDTO> list=null;
        if(findType>0){
            list=memberService.findMember(findType,findKeyword);
        }else{
           list =memberService.findAll();
        }

        log.info("list=={}", list);
        model.addAttribute("userList", list);
        model.addAttribute("findType",findType);
        model.addAttribute("findKeyword", findKeyword);
        return "member/userList";
        //resources/templates/member/userList.html
    }

    // /admin/userDelete?id=100
    @GetMapping("/admin/userDelete")
    public String deleteMember(@RequestParam(defaultValue = "0L") long id){
        if(id==0){
            return "redirect:/admin/users";
        }
        memberService.deleteMember(id);

        return "redirect:/admin/users";
    }
    // /admin/userEdit/100
    @GetMapping("/admin/userEdit/{id}")
    public String getMemberInfo(@PathVariable("id") long id, Model model){
        log.info("id==={}",id);

        ListMemberDTO user=memberService.findMemberById(id);
        log.info("user=={}", user);
        model.addAttribute("user", user);

        return "member/userEdit";
    }

    // //수정 요청은 ajax로 요청이 들어온다
    // @PostMapping("/admin/userEdit")
    // @ResponseBody
    // public ResponseEntity<ResponseDTO> updateMember(@RequestBody ListMemberDTO user){
    //
    //     MemberEntity updatedEntity = memberService.updateMember(user);
    //
    //     String msg="회원정보 수정 완료";
    //     String result="success";
    //     ResponseDTO res=new ResponseDTO(result, msg, null);
    //     return ResponseEntity.status(200).body(res);
    // }


}
