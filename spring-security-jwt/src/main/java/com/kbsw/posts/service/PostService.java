package com.kbsw.posts.service;

import com.kbsw.posts.dto.PostDTO;
import com.kbsw.posts.entity.Post;
import com.kbsw.posts.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public Post savePost(PostDTO postDTO){
        Post newPost=postRepository.save(postDTO.toEntity());
        return newPost;
    }

    public Page<PostDTO> getAllPosts(int type, String keyword, Pageable pageable){
        System.out.println(keyword+"/"+type);
        if(keyword.isBlank()){
            Page<Post>  entityList=postRepository.findAll(pageable);
            return entityList.map(post->PostDTO.fromEntity(post));
        }else if(type==1){
            Page<Post> entityList=postRepository.findByTitleContainingIgnoreCase(keyword,pageable);
            System.out.println(entityList);
            return entityList.map(post->PostDTO.fromEntity(post));
        } else{
            return null;
        }
    }

    public List<PostDTO> getAllPosts(int type,String keyword) {
        if("".equals(keyword.trim())){
            List<Post> entityList=postRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
            //모든 게시글 검색 (글번호 내림차순)
            return entityList.stream().map(entity->PostDTO.fromEntity(entity)).collect(Collectors.toList());
        }else{
            List<Post> entityList=null;
            switch(type){
                case 1: //title 검색 ===> Query Method이용
                    entityList=postRepository.findByTitleContainingIgnoreCaseOrderByIdDesc(keyword);
                    break;
                case 2://content검색 ==> @Query 를 활용한 JPQL방식 이용
                    entityList=postRepository.findByContent(keyword);
                    break;
            }
            return (entityList!=null)? entityList.stream().map(entity->PostDTO.fromEntity(entity)).collect(Collectors.toList())
                    : new ArrayList<PostDTO>();
        }
    }//-----------------------------------

    /**
     * 게시글 조회, 조회수 증가
     */
    public PostDTO getPostInfo(int id) {
        Post post = postRepository.findByIdWithComments(id).orElseThrow(() -> new RuntimeException("Post not found"));
        post.setReadNum(post.getReadNum() + 1);  // 조회수 증가
        postRepository.save(post);  // 변경 사항 저장
        return PostDTO.fromEntity(post);
    }

    /**
     * 게시글 수정
     */
    public Post updatePost(Long id, PostDTO postDTO) {
        Post existingPost = postRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        existingPost.setTitle(postDTO.getTitle());
        existingPost.setContent(postDTO.getContent());
        existingPost.setFileName(postDTO.getFileName());
        existingPost.setOriginFileName(postDTO.getOriginFileName());
        existingPost.setFileSize(postDTO.getFileSize());
        return postRepository.save(existingPost);
    }

    /**
     * 게시글 삭제
     */
    public void deletePost(int id) {
        postRepository.deleteById(id);  // 게시글 삭제
    }

    public Post getPostById(Long id) {
        return postRepository.findPostById(id);
	}
}
