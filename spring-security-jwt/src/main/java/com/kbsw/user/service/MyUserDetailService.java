package com.kbsw.user.service;

import com.kbsw.user.entity.MemberEntity;
import com.kbsw.user.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
//Spring Security의 인증을 담당하는 클래스
//UserDetailsService 구현: 시큐리티에서 DB에서 사용자 정보를 가져오는 로직 구성
@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private MemberRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        //DB에서 email을 가지고 회원이 맞는지 체크를 한뒤 맞다면 UserDetails
        //객체를 반환한다==> MyUser(엔티티면서 UserDetails를 상속받음)
        Optional<MemberEntity> opt=userRepository.findByEmail(email);
        opt.orElseThrow(()->new UsernameNotFoundException("인증에 실패했습니다. 아이디와 비밀번호를 확인하세요"));
        return opt.get();
    }
}
