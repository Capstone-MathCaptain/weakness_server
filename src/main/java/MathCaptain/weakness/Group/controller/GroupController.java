package MathCaptain.weakness.Group.controller;

import MathCaptain.weakness.Group.service.GroupService;
import MathCaptain.weakness.Group.dto.GroupCreateDto;
import org.springframework.web.bind.annotation.*;
import MathCaptain.weakness.Group.domain.Group;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @GetMapping("/group/{groupId}")
    private Group groupInfo(@PathVariable long groupId) {
        return groupService.getGroupInfo(groupId);
    }

    @PostMapping("/group/{leaderId}")
    public String createGroup(@Valid @PathVariable long leaderId, @RequestBody GroupCreateDto groupCreateDto) {
        Long id = groupService.createGroup(leaderId, groupCreateDto);
        return Long.toString(id) + "번 그룹이 생성되었습니다.";
    }
}
