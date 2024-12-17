package net.pointofviews.curation.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.curation.domain.Curation;
import net.pointofviews.curation.dto.request.SaveTodayCurationRequest;
import net.pointofviews.curation.dto.response.ReadUserCurationMovieResponse;
import net.pointofviews.curation.repository.CurationRepository;
import net.pointofviews.curation.service.CurationRedisService;
import net.pointofviews.curation.service.CurationSchedulerService;
import net.pointofviews.movie.repository.MovieRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurationSchedulerServiceImpl implements CurationSchedulerService {

    private final CurationRepository curationRepository;
    private final CurationRedisService curationRedisService;

//    @Scheduled(cron = "0 * * * * *")
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정 실행
    @Override
    public void activateDailyCurations() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);

        // 오늘 활성화될 큐레이션을 DB에서 가져오기
        List<Curation> todaysCurations = curationRepository.findByStartTimeBetween(todayStart, todayEnd);

        // 캐시에 저장
        todaysCurations.forEach(curation -> {
            // 오늘 날짜 큐레이션 ID 저장
            curationRedisService.saveTodayCurationId(curation.getId());
        });
    }
}
