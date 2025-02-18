package MathCaptain.weakness.Recruitment.service;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.enums.CategoryStatus;
import MathCaptain.weakness.Group.repository.GroupRepository;
import MathCaptain.weakness.Recruitment.domain.Recruitment;
import MathCaptain.weakness.Recruitment.dto.request.CreateRecruitmentRequestDto;
import MathCaptain.weakness.Recruitment.repository.RecruitmentRepository;
import MathCaptain.weakness.User.domain.Users;
import MathCaptain.weakness.User.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor
@TestPropertySource(properties = "spring.security.enabled=false")
class RecruitmentServiceTest {

    private final RecruitmentRepository recruitmentRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final RecruitmentService recruitmentService;

    @Test
    void createRecruitment() {
        // 모집글 생성 테스트

        // given
        CreateRecruitmentRequestDto createRecruitmentRequestDto = CreateRecruitmentRequestDto.builder()
                .recruitGroupId(1L)
                .authorId(1L)
                .category(CategoryStatus.STUDY)
                .title("test Title")
                .content("test Content")
                .build();

        // when
        recruitmentService.createRecruitment(createRecruitmentRequestDto);

        // then
        List<Recruitment> recruitmentList = recruitmentRepository.findAll();

        assertEquals(1, recruitmentList.size());

    }

    @Test
    void getRecruitment() {
    }

    @Test
    void updateRecruitment() {
    }

    @Test
    void getAllRecruitments() {
    }
}