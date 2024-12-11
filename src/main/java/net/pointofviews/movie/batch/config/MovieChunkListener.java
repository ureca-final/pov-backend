package net.pointofviews.movie.batch.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MovieChunkListener implements ChunkListener {
    @Override
    public void beforeChunk(ChunkContext context) {
        String stepName = context.getStepContext().getStepExecution().getStepName();
        log.info("[Step: {}] Chunk 시작", stepName);
    }

    @Override
    public void afterChunk(ChunkContext context) {
        String stepName = context.getStepContext().getStepExecution().getStepName();
        long itemCount = context.getStepContext().getStepExecution().getWriteCount();
        log.info("[Step: {}] Chunk 종료. 처리한 Item 갯수: {}", stepName, itemCount);
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        String stepName = context.getStepContext().getStepExecution().getStepName();
        log.error("[Step: {}] Chunk 진행 중 오류 발생", stepName);
    }
}