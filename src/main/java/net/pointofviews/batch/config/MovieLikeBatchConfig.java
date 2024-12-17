package net.pointofviews.batch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.common.service.RedisService;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.exception.MemberException;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.domain.MovieLike;
import net.pointofviews.movie.domain.MovieLikeCount;
import net.pointofviews.movie.exception.MovieException;
import net.pointofviews.movie.repository.MovieLikeCountRepository;
import net.pointofviews.movie.repository.MovieLikeRepository;
import net.pointofviews.movie.repository.MovieRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.Duration;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class MovieLikeBatchConfig {

    private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;
    private final MovieRepository movieRepository;
    private final MovieLikeRepository movieLikeRepository;
    private final MemberRepository memberRepository;
    private final MovieLikeCountRepository movieLikeCountRepository;
    private final RedisService redisService;

    private static final int CHUNK_SIZE = 100;

    @Bean
    public Job movieLikeJob(JobRepository jobRepository) {
        log.trace("MovieLikeJob 초기화 중");

        return new JobBuilder("movieLikeJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(movieLikeStep(jobRepository))
                .build();
    }

    @Bean
    @JobScope
    public Step movieLikeStep(JobRepository jobRepository) {
        log.trace("MovieLikeStep 초기화 중");

        return new StepBuilder("movieLikeStep", jobRepository)
                .<String, MovieLike>chunk(CHUNK_SIZE, transactionManager)
                .reader(movieLikeRedisReader())
                .processor(movieLikeProcessor())
                .writer(movieLikeWriter())
                .build();
    }

    @RequiredArgsConstructor
    public class MovieLikeRedisReader implements ItemReader<String> {
        private final Set<String> keys;
        private final Iterator<String> keysIterator;

        public MovieLikeRedisReader(RedisService redisService) {
            this.keys = redisService.getKeys("MovieLiked:*");
            this.keysIterator = keys.iterator();
        }

        @Override
        public String read() {
            if (keysIterator.hasNext()) {
                return keysIterator.next();
            }
            return null;
        }
    }

    @Bean
    @StepScope
    public MovieLikeRedisReader movieLikeRedisReader(){
        return new MovieLikeRedisReader(redisService);
    }

    @RequiredArgsConstructor
    public class MovieLikeProcessor implements ItemProcessor<String, MovieLike> {
        private final MovieRepository movieRepository;
        private final MemberRepository memberRepository;
        private final MovieLikeRepository movieLikeRepository;
        private final MovieLikeCountRepository movieLikeCountRepository;
        private final RedisService redisService;

        @Override
        public MovieLike process(String key){
            try {
                // MovieLiked:memberId 형식에서 데이터 추출
                String[] parts = key.split(":");
                Long movieId = Long.parseLong(parts[1]);
                UUID memberId = UUID.fromString(parts[2]);

                String likeStatus = redisService.getValue(key);
                boolean isLiked = "true".equals(likeStatus);

                // movie와 member 조회
                Movie movie = movieRepository.findById(movieId)
                        .orElseThrow(() -> MovieException.movieNotFound(movieId));

                Member member = memberRepository.findById(memberId)
                        .orElseThrow(() -> MemberException.memberNotFound(memberId));

                // 리뷰의 총 좋아요 수 처리
                String countKey = "MovieLikedCount:" + movieId;
                String countValue = redisService.getValue(countKey);
                if (countValue != null) {
                    // 기존 DB의 카운트 값을 가져와서 Redis 값을 더함
                    MovieLikeCount likeCount = movieLikeCountRepository
                            .findById(movieId)
                            .orElseGet(() -> MovieLikeCount.builder()
                                    .movie(movie)
                                    .likeCount(0L)
                                    .build());

                    // redis값을 db값으로 대체
                    long newCount = Long.parseLong(countValue);
                    likeCount.updateCount(newCount);
                    movieLikeCountRepository.save(likeCount);

                    // redis와 db값 동기화
                    redisService.setValue(countKey, String.valueOf(newCount), Duration.ofDays(7));
                }

                // 기존 좋아요 기록이 있는지 확인
                MovieLike existingLike = movieLikeRepository
                        .findByMovieAndMember(movie, member)
                        .orElse(null);

                if (existingLike != null) {
                    // 기존 기록이 있으면 상태 업데이트
                    existingLike.updateLikeStatus(isLiked);
                    return existingLike;
                } else {
                    // 새로운 기록 생성
                    return MovieLike.builder()
                            .movie(movie)
                            .member(member)
                            .isLiked(isLiked)
                            .build();
                }
            } catch (Exception e) {
                log.error("영화 좋아요 처리 중 오류: {} - {}", key, e.getMessage());
                return null;  // 오류 발생 시 해당 항목 스킵
            }
        }
    }

    @Bean
    @StepScope
    public MovieLikeProcessor movieLikeProcessor() {
        return new MovieLikeProcessor(
                movieRepository,
                memberRepository,
                movieLikeRepository,
                movieLikeCountRepository,
                redisService
        );
    }

    @RequiredArgsConstructor
    public class MovieLikeWriter implements ItemWriter<MovieLike> {
        private final MovieLikeRepository movieLikeRepository;

        @Override
        public void write(Chunk<? extends MovieLike> items) {
            movieLikeRepository.saveAll(items);
            log.info("{} 개의 영화 좋아요 데이터 저장 완료", items.size());
        }
    }

    @Bean
    @StepScope
    public MovieLikeWriter movieLikeWriter() {
        return new MovieLikeWriter(movieLikeRepository);
    }
}
