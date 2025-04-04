package com.kbsw.user.controller;

import com.kbsw.user.dto.ListMemberDTO;
import com.kbsw.user.dto.LoginRequestDTO;
import com.kbsw.user.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor //생성자 인젝션
public class LoginViewController {
    private final MemberService memberService;
    @GetMapping("/login")
    public String loginForm(){
        return "login";
        //resources/templates/login.html
    }
    @PostMapping("/login")
    public String loginProcess(LoginRequestDTO dto, HttpSession session){
        //유효성 체크
        if(dto.getEmail()==null||dto.getPasswd()==null
                ||dto.getEmail().trim().isBlank()||dto.getPasswd().trim().isBlank()){
            return "redirect:/login";
        }
        ListMemberDTO loginUser=memberService.loginCheck(dto);
        //회원인증을 받지 못하면 예외 발생함(IllegalArgumentException)
        
        if(loginUser!=null){//회원 인증을 받았다면
            //세션에 로그인한 회원정보를 저장
            //세션에 저장. setAttribute(String key, Object value)
            session.setAttribute("loginUser", loginUser);
            //브라우저 하나 사용하는 동안 같은 세션을 유지
            //세션이 무효화되는 경우=> timeout(30분 디폴트), 브라우저 종료하는 경우, 로그아웃 하는 경우
        }

        return "redirect:/admin/users";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session){
        //특정 세션 변수 삭제=> removeAttribute(key)
        //모든 세션에 저장된 값 삭제==> invalidate()
        session.invalidate();
        return "redirect:/admin/users";
    }
}
