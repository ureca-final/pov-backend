package net.pointofviews.batch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pointofviews.movie.domain.DailyMovieLike;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.dto.DailyMovieLikeDto;
import net.pointofviews.movie.repository.MovieRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class DailyMovieLikeBatchConfig {

    private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;
    private final MovieRepository movieRepository;
    private final JdbcTemplate jdbcTemplate;

    private static final int CHUNK_SIZE = 100;
    private static final int PAGE_SIZE = 100;

    @Bean
    public Job dailyMovieLikeJob(JobRepository jobRepository) throws Exception {
        log.info("DailyMovieLikeJob 초기화 중");

        return new JobBuilder("dailyMovieLikeJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(dailyMovieLikeStep(jobRepository))
                .build();
    }

    @Bean
    public Step dailyMovieLikeStep(JobRepository jobRepository) throws Exception {
        log.info("DailyMovieLikeStep 초기화 중");

        return new StepBuilder("dailyMovieLikeStep", jobRepository)
                .<DailyMovieLikeDto, DailyMovieLike>chunk(CHUNK_SIZE, transactionManager)
                .reader(extractMovieLikeItemReader())
                .processor(top10MovieLikeProcessor())
                .writer(dailyMovieLikeWriter())
                .build();
    }

    @Bean
    public JdbcPagingItemReader<DailyMovieLikeDto> extractMovieLikeItemReader() throws Exception {
        log.info("extractMovieLikeItemReader 시작: 영화 좋아요 데이터 읽기");

        return new JdbcPagingItemReaderBuilder<DailyMovieLikeDto>()
                .name("extractMovieLikeItemReader")
                .dataSource(dataSource)
                .queryProvider(createQueryProvider())
                .pageSize(PAGE_SIZE)
                .rowMapper((rs, rowNum) -> new DailyMovieLikeDto(
                        rs.getLong("movie_id"),
                        rs.getString("movie_title"),
                        rs.getLong("total_like_count")
                ))
                .build();
    }

    @Bean
    public ItemProcessor<DailyMovieLikeDto, DailyMovieLike> top10MovieLikeProcessor() {

        return new ItemProcessor<DailyMovieLikeDto, DailyMovieLike>() {
            private final List<DailyMovieLikeDto> topMovies = new ArrayList<>();

            @Override
            public DailyMovieLike process(DailyMovieLikeDto dto) {
                log.debug("DTO -> DailyMovieLike 로 변환 중: {}", dto);

                topMovies.add(dto);

                // 10개 초과 시 정렬하고 상위 10개만 유지
                if (topMovies.size() > 10) {
                    topMovies.sort(Comparator.comparingLong(DailyMovieLikeDto::likeAmount).reversed());
                    topMovies.subList(10, topMovies.size()).clear();
                }

                Optional<Movie> movie = movieRepository.findById(dto.movieId());

                // DailyMovieLike 로 변환하여 반환
                return DailyMovieLike.builder()
                        .movie(movie.get())
                        .totalCount(dto.likeAmount())
                        .build();
            }
        };
    }

    @Bean
    public ItemWriter<DailyMovieLike> dailyMovieLikeWriter() {

        return new ItemWriter<DailyMovieLike>() {
            @Override
            public void write(Chunk<? extends DailyMovieLike> chunk) throws Exception {
                log.info("DailyMovieLike 저장 중");

                List<? extends DailyMovieLike> items = chunk.getItems();

                List<DailyMovieLike> top10Movies = items.stream()
                        .sorted(Comparator.comparing(DailyMovieLike::getTotalCount).reversed())
                        .limit(10)
                        .collect(Collectors.toList());

                String sql = "INSERT INTO daily_movie_like (movie_id, total_like_count, created_at) VALUES (?, ?, ?)";

                List<Object[]> batchArgs = top10Movies.stream()
                        .map(movie -> new Object[]{
                                movie.getMovie().getId(),
                                movie.getTotalCount(),
                                LocalDateTime.now()
                        })
                        .collect(Collectors.toList());

                jdbcTemplate.batchUpdate(sql, batchArgs);

                log.info("상위 10개 영화 저장 완료: {}", top10Movies.size());
            }
        };
    }

    // 어제자 좋아요 한 영화별 좋아요 추출
    private PagingQueryProvider createQueryProvider() throws Exception {

        SqlPagingQueryProviderFactoryBean factory = new SqlPagingQueryProviderFactoryBean();

        factory.setDataSource(dataSource);
        factory.setSelectClause("m.id as movie_id, m.title as movie_title, COUNT(ml.id) as total_like_count");
        factory.setFromClause("FROM movie_like ml JOIN movie m ON m.id = ml.movie_id");
        factory.setWhereClause("WHERE ml.is_liked = 1 AND ml.created_at >= DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY) AND ml.created_at < CURRENT_DATE");
        factory.setGroupClause("GROUP BY m.id, m.title");
        factory.setSortKeys(Map.of("total_like_count", Order.DESCENDING));

        return factory.getObject();
    }
}
