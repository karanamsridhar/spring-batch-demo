package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.ChunkListenerSupport;
import org.springframework.batch.core.scope.context.ChunkContext;

public class ChunkExecutionListener extends ChunkListenerSupport {

	private static final Logger logger = LoggerFactory.getLogger(ChunkExecutionListener.class);

	
	@Override
	public void afterChunk(ChunkContext context) {
		logger.info("After Chunk");
		super.afterChunk(context);
	}
	
	@Override
	public void beforeChunk(ChunkContext context) {
		logger.info("Before Chunk");
		super.beforeChunk(context);
	}
}
