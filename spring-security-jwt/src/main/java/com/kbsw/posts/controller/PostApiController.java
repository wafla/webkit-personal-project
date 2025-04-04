package com.kbsw.posts.controller;

import com.kbsw.posts.dto.PostDTO;
import com.kbsw.posts.entity.Post;
import com.kbsw.posts.service.PostService;
import com.kbsw.user.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/*
게시글 저장 (POST /api/posts)
파일 업로드 포함
DTO → Entity 변환 후 저장
-----------------------
게시글 목록 조회 (GET /api/posts)
search 파라미터로 검색 기능 추가
-------------------------------
게시글 단건 조회 (GET /api/posts/{id})
--------------------------------
특정 ID의 게시글 조회
게시글 수정 (PUT /api/posts)
----------------------------------
파일 업로드 포함
기존 파일 교체 가능
게시글 삭제 (DELETE /api/posts/{id})
ID 기반으로 삭제 처리
------------------------------------
*/
@RestController
@RequestMapping("/api/posts")
@Slf4j
@RequiredArgsConstructor //생성자 인젝션
public class PostApiController {

    private final PostService postService;

    private static String upDir="C:/fullstack/uploads/"; //파일 업로드 디렉토리
    //파일업로드 경로를 웹에서 참조할 수 있도록 WebConfig 에서 설정해야 함

    @PostMapping("")
    public ResponseEntity<Map> savePost(PostDTO postDTO,
                                        @RequestParam(name="file",required = false) MultipartFile file){
        log.info("postDTO={}",postDTO);
        log.info("file={}",file);
        //업로드 처리하고 postDTO에 업로드 파일명과 파일크기 설정
        fileUpload(postDTO, file);

        if(postDTO.getFileName()==null){//첨부파일이 없는 경우라면
            postDTO.setFileName("noimage.PNG");
        }

        Post newEntity=postService.savePost(postDTO);
        String result=(newEntity!=null)? "success":"fail";
        String msg=(newEntity!=null)? "글 등록 완료":"글 등록 실패";

       // ResponseDTO res=new ResponseDTO(result,msg);
        Map map=Map.of("id",newEntity.getId(),
                "fileUrl",newEntity.getFileName(),
                "result", result,
                "message",msg);
        return ResponseEntity.ok().body(map);
    }

    public void fileUpload(PostDTO dto, MultipartFile file){
        File dir=new File(upDir);
        if(! dir.exists()){
            dir.mkdirs();
        }
        try{
            if(file!=null && !file.isEmpty()){
                String fileName= UUID.randomUUID().toString()+"_"+file.getOriginalFilename();//물리적 파일명
                dto.setFileName(fileName);
                dto.setOriginFileName(file.getOriginalFilename());
                dto.setFileSize((int)file.getSize());
                file.transferTo(new File(upDir,fileName)); //업로드 처리함
                log.info("파일 업로드 성공: {}",upDir);
                log.info("fileName={}",fileName);
            }
        }catch (IOException e){
            throw new RuntimeException("File Upload Error", e);
        }
    }//-----------------------------------

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePost(
        @PathVariable Long id,
        @RequestPart("request") @Validated PostDTO postDTO,
        @RequestPart(name = "file", required = false) MultipartFile file) {
        // TODO : 파일이 널로 들어옴
        log.info("Updating post with id={}", id);
        log.info("postDTO={}", postDTO);
        log.info("file={}", file);
        log.info("content={}",postDTO.getContent());

        // 기존 게시글 조회
        Post existingPost = postService.getPostById(id);
        if (existingPost == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("result", "fail", "message", "게시글을 찾을 수 없습니다."));
        }

        postDTO.setName(existingPost.getName());
        postDTO.setUserId(existingPost.getUserId());

        if (file != null) {
            fileUpload(postDTO, file);
        }
        else{
            postDTO.setFileName(existingPost.getFileName());
            postDTO.setOriginFileName(existingPost.getOriginFileName());
            postDTO.setFileSize(existingPost.getFileSize());
        }

        // 게시글 업데이트
        Post updatedPost = postService.updatePost(id, postDTO);
        String result = (updatedPost != null) ? "success" : "fail";
        String msg = (updatedPost != null) ? "글 수정 완료" : "글 수정 실패";

        Map<String, Object> response = Map.of(
            "id", updatedPost.getId(),
            "fileUrl", updatedPost.getFileName(),
            "result", result,
            "message", msg
        );

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("")
   // public List<PostDTO> getAllPosts( //페이징 처리 안할 때
    public Map<String, Object> getAllPosts(
            @RequestParam(defaultValue = "0") int type,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page, //페이지 번호 (기본값: 0으로 주면 1페이지 가져옴)
            @RequestParam(defaultValue = "3") int size //한 페이지 당 보여줄 목록 개수
            ){
        Pageable pageable= PageRequest.of(page,size, Sort.by("id").descending());
        Page<PostDTO> list= postService.getAllPosts(type,keyword, pageable);
        Map<String,Object> map=Map.of("data",list.getContent(),
                "totalPages",list.getTotalPages(), //페이지수
                "totalCount", list.getTotalElements(), //총 게시글 수
                "pageNum",list.getNumber()+1); //현재 페이지번호 0부터 들어옴
        return map;
    }

    // 특정 글 조회
    @GetMapping("/{id}")
    public PostDTO getPost(@PathVariable("id") Integer id) {
        log.info("Post ID: {}", id);
        return postService.getPostInfo(id);
    }


    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deletePost(@PathVariable("id") Integer id) {
        log.info("Delete Post ID: {}", id);
        postService.deletePost(id);
        return ResponseEntity.ok(new ResponseDTO("success", "삭제 완료", null));
    }


}
