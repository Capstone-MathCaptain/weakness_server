package MathCaptain.weakness.global.Image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private static final String UPLOAD_DIR = "./images/";

    public String saveImage(MultipartFile profileImage) throws IOException {
        // 파일 URL 초기화
        String fileUrl = null;

        try {
            if (profileImage != null && !profileImage.isEmpty()) {
                // 파일 이름 생성 (UUID + 확장자)
                String originalFilename = profileImage.getOriginalFilename();
                if (originalFilename == null || !originalFilename.contains(".")) {
                    throw new IllegalArgumentException("유효하지 않은 파일 이름입니다.");
                }
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String uniqueFilename = UUID.randomUUID().toString() + extension;

                // 파일 저장 경로 설정
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) {
                    boolean created = uploadDir.mkdirs(); // 디렉토리가 없으면 생성
                    if (!created) {
                        throw new IOException("이미지 저장 디렉토리를 생성할 수 없습니다.");
                    }
                }

                // 파일 저장
                File destinationFile = new File(UPLOAD_DIR + uniqueFilename);
                profileImage.transferTo(destinationFile);

                // URL 생성 (서버 경로 + 파일 이름)
                fileUrl = "http://localhost:8080/users/images/" + uniqueFilename;

                log.info("프로필 이미지 URL: {}", fileUrl);
            } else {
                throw new IllegalArgumentException("파일이 비어 있거나 null입니다.");
            }
        } catch (IOException e) {
            log.error("프로필 이미지 저장 실패: {}", e.getMessage());
            throw e; // 예외를 다시 던져 호출자가 처리하도록 전달
        }

        return fileUrl;
    }
}