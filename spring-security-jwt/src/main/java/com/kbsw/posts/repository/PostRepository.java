package com.kbsw.posts.repository;

import com.kbsw.posts.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post,Integer> {

    //[1] Query Method  [2] Named Query  [3] @Query JPQL 방법

    //[1] Query Method
    List<Post> findByTitleContainingIgnoreCaseOrderByIdDesc(String title);
    Page<Post> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // [3] @Query JPQL 방법
    @Query("select p from Post p where lower(p.content) like lower(concat('%',:keyword,'%')) order by p.id desc")
    List<Post> findByContent(@Param("keyword") String keyword);

    Post findPostById(Long id);

    Optional<Post> findById(Long id);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.comments WHERE p.id = :id")
    Optional<Post> findByIdWithComments(Integer id);
}
