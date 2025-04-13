package MathCaptain.weakness.domain.Record.service;

import MathCaptain.weakness.domain.Record.dto.request.FitnessLogEnrollRequest;
import MathCaptain.weakness.domain.Record.dto.request.RunningLogEnrollRequest;
import MathCaptain.weakness.domain.Record.dto.request.StudyLogEnrollRequest;
import MathCaptain.weakness.domain.Record.dto.response.FitnessLogResponse;
import MathCaptain.weakness.domain.Record.dto.response.RunningLogResponse;
import MathCaptain.weakness.domain.Record.dto.response.StudyLogResponse;
import MathCaptain.weakness.domain.Record.entity.ActivityRecord;
import MathCaptain.weakness.domain.Record.entity.UserLog.FitnessDetail;
import MathCaptain.weakness.domain.Record.entity.UserLog.RunningDetail;
import MathCaptain.weakness.domain.Record.entity.UserLog.StudyDetail;
import MathCaptain.weakness.domain.Record.repository.record.RecordRepository;
import MathCaptain.weakness.domain.Record.repository.userLog.FitnessLogRepository;
import MathCaptain.weakness.domain.Record.repository.userLog.RunningLogRepository;
import MathCaptain.weakness.domain.Record.repository.userLog.StudyLogRepository;
import MathCaptain.weakness.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivityDetailService {

    private final FitnessLogRepository fitnessLogRepository;
    private final StudyLogRepository studyLogRepository;
    private final RunningLogRepository runningLogRepository;
    private final RecordRepository recordRepository;

    @Transactional
    public FitnessLogResponse enrollFitnessLog(Long activityId, FitnessLogEnrollRequest request) {
        ActivityRecord record = findRecordBy(activityId);
        FitnessDetail fitnessDetail = FitnessDetail.of(record, request);
        fitnessLogRepository.save(fitnessDetail);
        return FitnessLogResponse.of(fitnessDetail);
    }

    @Transactional
    public RunningLogResponse enrollRunningLog(Long activityId, RunningLogEnrollRequest request) {
        ActivityRecord record = findRecordBy(activityId);
        RunningDetail runningDetail = RunningDetail.of(record, request);
        runningLogRepository.save(runningDetail);
        return RunningLogResponse.of(runningDetail);
    }

    @Transactional
    public StudyLogResponse enrollStudyLog(Long activityId, StudyLogEnrollRequest request) {
        ActivityRecord record = findRecordBy(activityId);
        StudyDetail studyDetail = StudyDetail.of(record, request);
        studyLogRepository.save(studyDetail);
        return StudyLogResponse.of(studyDetail);
    }

    @Transactional
    public FitnessLogResponse getFitnessLog(Long activityId) {
        FitnessDetail fitnessDetail = findFitnessLogBy(activityId);
        return FitnessLogResponse.of(fitnessDetail);
    }

    @Transactional
    public StudyLogResponse getStudyLog(Long activityId) {
        StudyDetail studyDetail = findStudyLogBy(activityId);
        return StudyLogResponse.of(studyDetail);
    }

    @Transactional
    public RunningLogResponse getRunningLog(Long activityId) {
        RunningDetail runningDetail = findRunningLogBy(activityId);
        return RunningLogResponse.of(runningDetail);
    }


    /// 로직

    private StudyDetail findStudyLogBy(Long activityId) {
        return studyLogRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 기록을 찾을 수 없습니다."));
    }

    private RunningDetail findRunningLogBy(Long activityId) {
        return runningLogRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 기록을 찾을 수 없습니다."));
    }

    private FitnessDetail findFitnessLogBy(Long activityId) {
        return fitnessLogRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 기록을 찾을 수 없습니다."));
    }


    private ActivityRecord findRecordBy(Long activityId) {
        return recordRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 기록을 찾을 수 없습니다."));
    }

}
