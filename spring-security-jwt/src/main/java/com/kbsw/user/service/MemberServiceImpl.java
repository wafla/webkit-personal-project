package com.kbsw.user.service;

import com.kbsw.user.dto.AddMemberDTO;
import com.kbsw.user.dto.ListMemberDTO;
import com.kbsw.user.dto.LoginRequestDTO;
import com.kbsw.user.dto.UpdateRequestDTO;
import com.kbsw.user.entity.MemberEntity;
import com.kbsw.user.repository.MemberRepository;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("memberService")
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final BCryptPasswordEncoder passwordEncoder;

    @Inject //byType으로 주입 @Autowired와 동일
    private MemberRepository memberRepository;

    public boolean checkDuplicateEmail(String email) {
        return memberRepository.findByEmail(email).isPresent(); // 이메일이 이미 존재하면 true 반환
    }
    @Override
    public MemberEntity saveMember(AddMemberDTO user) {
        //DTO를 Entity로 변환하는 로직 필요함
        MemberEntity entity=MemberEntity.builder()
                .name(user.getName())
                .email(user.getEmail())
                .passwd(passwordEncoder.encode(user.getPasswd()))
                .role(user.getRole())
                .build(); //빌더 패턴

        MemberEntity createdEntity= memberRepository.save(entity);
        //System.out.println("새로 생성된 회원정보: "+createdEntity);
        return createdEntity;
    }

    @Override
    public List<ListMemberDTO> findAll() {
        List<MemberEntity> list=memberRepository.findAll(Sort.by("id").descending());
        //respository에서 받은 엔티티 객체==> 표현계층에 전달할 때는 DTO로 변환
        List<ListMemberDTO> arrList=
                list.stream().map(entity->new ListMemberDTO(entity)).collect(Collectors.toList());
        return arrList;
    }

    @Override
    public void deleteMember(long id) {
        memberRepository.deleteById(id);//delete문 실행
    }

    @Override
    public ListMemberDTO findMemberById(long id) {
       Optional<MemberEntity> opt= memberRepository.findById(id);
       if(opt.isPresent()){
            MemberEntity entity=opt.get();
            return new ListMemberDTO(entity);
       }//if---
        throw new IllegalArgumentException("없는 회원번호에요");
    }

    //Service객체에 @Transactional 붙여준다  Dirty Checking
    @Override
    public MemberEntity updateMember(String name, UpdateRequestDTO request) {
        MemberEntity member= memberRepository.findByName(name);
        member.setSchool(request.school());
        member.setBirthDay(request.birthday());
        member.setCountry(request.country());
        member.setGender(request.gender());
        member.setHobby(request.hobby());
        member.setAboutMe(request.about_me());
        member.setFileName(request.fileName());
        member.setOriginalFileName(request.originalFileName());
        member.setFileSize(request.fileSize());
        return memberRepository.save(member);
    }

    @Override
    public List<ListMemberDTO> findMember(int findType, String findKeyword) {
        List<MemberEntity> list=null;
        switch(findType){
            case 1: list=memberRepository.findByNameLike("%"+findKeyword+"%");
                break;
            case 2: list=memberRepository.findByEmailContainingIgnoreCase(findKeyword);
                break;
            case 3: list=memberRepository.findByRoleIgnoreCase(findKeyword);
                break;
        }
        if(list!=null){
            return list.stream().map(entity->new ListMemberDTO(entity)).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public MemberEntity getMemberByName(String name) {
        return memberRepository.findByName(name);
    }

    @Override
    public boolean deleteMemberById(Long id) {
        Optional<MemberEntity> member = memberRepository.findById(id);
        if (member.isPresent()) {
            memberRepository.deleteById(id);
            return true;
        }
        return false;
    }

    //로그인 회원인증 처리
    @Override
    public ListMemberDTO loginCheck(LoginRequestDTO tmpUser){
            //1. 이메일로 db에서 회원정보 받아오기
        MemberEntity dbUser=memberRepository.findByEmail(tmpUser.getEmail())
                .orElseThrow(()-> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않아요"));
             //2. tmpUser비밀번호와 dbUser의 비밀번호 일치여부 체크
        //***BCryptPasswordEncoder의 matches() 사용하여 체크하자*****
        if (!passwordEncoder.matches(tmpUser.getPasswd(), dbUser.getPasswd())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않아요");
        }
            return new ListMemberDTO(dbUser);
    }//loginCheck---------------------

}//class end////////////////////////////////
