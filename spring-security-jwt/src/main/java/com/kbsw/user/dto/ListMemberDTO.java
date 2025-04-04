package com.kbsw.user.dto;

import com.kbsw.user.entity.MemberEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class ListMemberDTO {

    private Long id;
    private String name;
    private String email;
    private String role;
    private LocalDateTime indate;

    public ListMemberDTO(MemberEntity entity){
        this.id=entity.getId();
        this.name=entity.getName();
        this.email=entity.getEmail();
        this.role=entity.getRole();
        this.indate=entity.getIndate();
    }

}
