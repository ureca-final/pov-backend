package net.pointofviews.batch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.common.service.RedisService;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.exception.MemberException;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.review.domain.Review;
import net.pointofviews.review.domain.ReviewLike;
import net.pointofviews.review.domain.ReviewLikeCount;
import net.pointofviews.review.exception.ReviewException;
import net.pointofviews.review.repository.ReviewLikeCountRepository;
import net.pointofviews.review.repository.ReviewLikeRepository;
import net.pointofviews.review.repository.ReviewRepository;
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
public class ReviewLikeBatchConfig {

    private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final MemberRepository memberRepository;
    private final ReviewLikeCountRepository reviewLikeCountRepository;
    private final RedisService redisService;

    private static final int CHUNK_SIZE = 100;

    @Bean
    public Job reviewLikeJob(JobRepository jobRepository) {
        log.info("ReviewLikeJob 초기화 중");

        return new JobBuilder("reviewLikeJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(reviewLikeStep(jobRepository))
                .build();
    }

    @Bean
    @JobScope
    public Step reviewLikeStep(JobRepository jobRepository) {
        log.info("ReviewLikeStep 초기화 중");

        return new StepBuilder("reviewLikeStep", jobRepository)
                .<String, ReviewLike>chunk(CHUNK_SIZE, transactionManager)
                .reader(reviewLikeRedisReader())
                .processor(reviewLikeProcessor())
                .writer(reviewLikeWriter())
                .build();
    }

    @RequiredArgsConstructor
    public class ReviewLikeRedisReader implements ItemReader<String> {
        private final Set<String> keys;
        private final Iterator<String> keysIterator;

        public ReviewLikeRedisReader(RedisService redisService) {
            this.keys = redisService.getKeys("ReviewLiked:*");
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
    public ReviewLikeRedisReader reviewLikeRedisReader() {
        return new ReviewLikeRedisReader(redisService);
    }

    @RequiredArgsConstructor
    public class ReviewLikeProcessor implements ItemProcessor<String, ReviewLike> {
        private final ReviewRepository reviewRepository;
        private final MemberRepository memberRepository;
        private final ReviewLikeRepository reviewLikeRepository;
        private final ReviewLikeCountRepository reviewLikeCountRepository;
        private final RedisService redisService;

        @Override
        public ReviewLike process(String key) {
            try {
                // ReviewLiked:reviewId:memberId 형식에서 데이터 추출
                String[] parts = key.split(":");
                Long reviewId = Long.parseLong(parts[1]);
                UUID memberId = UUID.fromString(parts[2]);

                String likeStatus = redisService.getValue(key);
                boolean isLiked = "true".equals(likeStatus);

                // review와 member 조회
                Review review = reviewRepository.findById(reviewId)
                        .orElseThrow(() -> ReviewException.reviewNotFound(reviewId));

                Member member = memberRepository.findById(memberId)
                        .orElseThrow(() -> MemberException.memberNotFound(memberId));

                // 리뷰의 총 좋아요 수 처리
                String countKey = "ReviewLikedCount:" + reviewId;
                String countValue = redisService.getValue(countKey);
                if (countValue != null) {
                    // 기존 DB의 카운트 값을 가져와서 Redis 값을 더함
                    ReviewLikeCount likeCount = reviewLikeCountRepository
                            .findById(reviewId)
                            .orElseGet(() -> ReviewLikeCount.builder()
                                    .review(review)
                                    .reviewLikeCount(0L)
                                    .build());

                    // redis값을 db값으로 대체
                    long newCount = Long.parseLong(countValue);
                    likeCount.updateCount(newCount);
                    reviewLikeCountRepository.save(likeCount);

                    // redis와 db값 동기화
                    redisService.setValue(countKey, String.valueOf(newCount), Duration.ofDays(7));
                }

                // 기존 좋아요 기록이 있는지 확인
                ReviewLike existingLike = reviewLikeRepository
                        .findByReviewAndMember(review, member)
                        .orElse(null);

                if (existingLike != null) {
                    // 기존 기록이 있으면 상태 업데이트
                    existingLike.updateLikeStatus(isLiked);
                    return existingLike;
                } else {
                    // 새로운 기록 생성
                    return ReviewLike.builder()
                            .review(review)
                            .member(member)
                            .isLiked(isLiked)
                            .build();
                }
            } catch (Exception e) {
                log.error("리뷰 좋아요 처리 중 오류: {} - {}", key, e.getMessage());
                return null;
            }
        }
    }


    @Bean
    @StepScope
    public ReviewLikeProcessor reviewLikeProcessor() {
        return new ReviewLikeProcessor(
                reviewRepository,
                memberRepository,
                reviewLikeRepository,
                reviewLikeCountRepository,
                redisService
        );
    }

    @RequiredArgsConstructor
    public class ReviewLikeWriter implements ItemWriter<ReviewLike> {
        private final ReviewLikeRepository reviewLikeRepository;

        @Override
        public void write(Chunk<? extends ReviewLike> items) {  // List 대신 Chunk 사용
            reviewLikeRepository.saveAll(items);
            log.info("{} 개의 리뷰 좋아요 데이터 저장 완료", items.size());
        }
    }

    @Bean
    @StepScope
    public ReviewLikeWriter reviewLikeWriter() {
        return new ReviewLikeWriter(reviewLikeRepository);
    }

}
