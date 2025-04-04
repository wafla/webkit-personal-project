package com.kbsw.user.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import com.kbsw.posts.dto.PostDTO;
import com.kbsw.user.dto.AddMemberDTO;
import com.kbsw.user.dto.ListMemberDTO;
import com.kbsw.user.dto.ResponseDTO;
import com.kbsw.user.dto.UpdateRequestDTO;
import com.kbsw.user.entity.MemberEntity;
import com.kbsw.user.service.MemberService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController //Restful 방식의 서비스를 지원하는 어노테이션  @Controller + @ResponseBody 합친 컨트롤러
@RequestMapping("/api/users")
@Slf4j
public class MemberApiController {

    //@Inject //byType
    @Resource(name="memberService") //byName으로 주입
    private MemberService memberService;

    private static String upDir = "C:/fullstack/uploads/";

    @PostMapping("")
    public ResponseEntity<ResponseDTO> signup(@RequestBody AddMemberDTO user){
        //파라미터 데이터를 json유형으로 보낼 때는 @RequestBody를 붙여준다
        log.info("user==={}", user);
        log.info("memberService={}", memberService);
        MemberEntity newMember=memberService.saveMember(user);
        String result=(newMember!=null)? "success":"fail";
        String msg=(newMember!=null)? "회원가입 성공-로그인으로 이동합니다":"회원가입 실패-이메일 중복 확인하세요";

        ResponseDTO res=new ResponseDTO(result,msg, null);
        return ResponseEntity.ok().body(res);//200 OK
    }

    @PostMapping("/check-name")
    public ResponseEntity<?> checkDuplicateName(@RequestBody ListMemberDTO dto) {
        boolean isDuplicated = memberService.checkDuplicateEmail(dto.getName()); // ****** 수정필요
        if (isDuplicated) {
            return ResponseEntity.ok(new ResponseDTO("fail", "중복된 닉네임입니다.", null));
        } else {
            return ResponseEntity.ok(new ResponseDTO("ok", "사용 가능한 닉네임입니다.", null));
        }
    }

    @PostMapping("/check-email")
    public ResponseEntity<?> checkDuplicateEmail(@RequestBody ListMemberDTO dto) {
        boolean isDuplicated = memberService.checkDuplicateEmail(dto.getEmail());
        if (isDuplicated) {
            return ResponseEntity.ok(new ResponseDTO("fail", "중복된 이메일입니다.", null));
        } else {
            return ResponseEntity.ok(new ResponseDTO("ok", "사용 가능한 이메일입니다.", null));
        }
    }

    @GetMapping("/{name}")
    public ResponseEntity<ResponseDTO> getUserById(@PathVariable String name){
        try{
            MemberEntity member = memberService.getMemberByName(name);
            if(member == null){
                return ResponseEntity.ok(new ResponseDTO("fail", "유저가 없습니다.", null));
            }
            return ResponseEntity.ok(new ResponseDTO("success", "성공", member));
        }catch(Exception e){
            log.error("Error fetching user by ID: ", e);
            return ResponseEntity.status(500).body(new ResponseDTO("fail", "서버에 문제가 발생했습니다.", null));
        }
    }

    @PutMapping("/{name}")
    public ResponseEntity<Map> updateUser(@PathVariable String name,
        @RequestPart("request") @Validated UpdateRequestDTO request,
        @RequestPart(name="file", required = false) MultipartFile file){
        log.info("Update Request: {}", request);
        log.info("file={}", file);

        MemberEntity member = memberService.getMemberByName(name);

        // 파일 업로드 및 파일명 설정
        UpdateRequestDTO updatedRequest = fileUpload(request, file);

        if (updatedRequest.fileName() == null) { // 파일이 없으면 기본 이미지로 설정
            if(member.getFileSize()==null) {
                updatedRequest = new UpdateRequestDTO(
                    updatedRequest.school(),
                    updatedRequest.birthday(),
                    updatedRequest.country(),
                    updatedRequest.gender(),
                    updatedRequest.hobby(),
                    updatedRequest.about_me(),
                    "noimage.PNG", // 기본 파일명
                    updatedRequest.originalFileName(),
                    updatedRequest.fileSize()
                );
            }
            else{
                updatedRequest = new UpdateRequestDTO(
                    updatedRequest.school(),
                    updatedRequest.birthday(),
                    updatedRequest.country(),
                    updatedRequest.gender(),
                    updatedRequest.hobby(),
                    updatedRequest.about_me(),
                    member.getFileName(),
                    member.getOriginalFileName(),
                    member.getFileSize()
                );
            }
        }

        MemberEntity updatedMember = memberService.updateMember(name, updatedRequest);
        String result = (updatedMember != null) ? "success" : "fail";
        String msg = (updatedMember != null) ? "회원 정보 수정 완료" : "회원 정보 수정 실패";

        Map map = Map.of(
            "id", updatedMember.getId(),
            "fileUrl", updatedMember.getFileName(),
            "result", result,
            "message", msg
        );
        return ResponseEntity.ok().body(map);
    }

    private UpdateRequestDTO fileUpload(UpdateRequestDTO dto, MultipartFile file) {
        File dir = new File(upDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            if (file != null && !file.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename(); // 물리적 파일명

                // 기존 객체에서 새로운 값으로 업데이트된 새 객체를 반환
                dto = new UpdateRequestDTO(
                    dto.school(),
                    dto.birthday(),
                    dto.country(),
                    dto.gender(),
                    dto.hobby(),
                    dto.about_me(),
                    fileName,
                    file.getOriginalFilename(),
                    (int) file.getSize()
                );

                file.transferTo(new File(upDir, fileName)); // 파일 업로드 처리
                log.info("파일 업로드 성공: {}", upDir);
                log.info("fileName={}", fileName);
            }
        } catch (IOException e) {
            throw new RuntimeException("File Upload Error", e);
        }

        return dto; // 새로운 객체 반환
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deleteUser(@PathVariable Long id){
        try{
            boolean isDeleted = memberService.deleteMemberById(id);
            if(isDeleted){
                return ResponseEntity.ok(new ResponseDTO("success", "회원 탈퇴가 완료되었습니다.", null));
            } else {
                return ResponseEntity.ok(new ResponseDTO("fail", "회원 탈퇴에 실패했습니다.", null));
            }
        } catch (Exception e) {
            log.error("회원 탈퇴 중 오류 발생: ", e);
            return ResponseEntity.status(500).body(new ResponseDTO("fail", "서버 오류가 발생했습니다.", null));
        }
    }
}
