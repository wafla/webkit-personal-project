package com.kbsw.user.repository;

import com.kbsw.user.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Member;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    MemberEntity findByName(String name);

    Optional<MemberEntity> findByEmail(String email);
    boolean existsByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE MemberEntity m SET m.refreshToken = :refreshToken WHERE m.email = :email")
    int updateRefreshToken(String email, String refreshToken);

    @Query("SELECT m.refreshToken FROM MemberEntity m WHERE m.email = :email")
    String findRefreshTokenByEmail(String email);


    List<MemberEntity> findByNameLike(String name);
    //select * from spring_user where name like '%name%'

    //대소문자 구분하지 않고 like검색
    List<MemberEntity> findByEmailContainingIgnoreCase(String email);
    //select * from spring_user where email like '%name%'
    List<MemberEntity> findByRoleIgnoreCase(String role);
}


