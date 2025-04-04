package com.kbsw.user.service;

import com.kbsw.user.dto.AddMemberDTO;
import com.kbsw.user.dto.ListMemberDTO;
import com.kbsw.user.dto.LoginRequestDTO;
import com.kbsw.user.dto.UpdateRequestDTO;
import com.kbsw.user.entity.MemberEntity;

import java.util.List;

public interface MemberService {

    MemberEntity saveMember(AddMemberDTO user);

    //이메일 중복체크
    boolean checkDuplicateEmail(String email);
    List<ListMemberDTO> findAll();

    void deleteMember(long id);

    ListMemberDTO findMemberById(long id);

    MemberEntity updateMember(String name, UpdateRequestDTO request);

    List<ListMemberDTO> findMember(int findType, String findKeyword);


    // 로그인 처리 관련
    ListMemberDTO loginCheck(LoginRequestDTO tmpUser);

	MemberEntity getMemberByName(String name);

    boolean deleteMemberById(Long id);
}
