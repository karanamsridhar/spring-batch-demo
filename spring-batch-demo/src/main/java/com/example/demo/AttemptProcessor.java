package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class AttemptProcessor implements ItemProcessor<Attempt, Attempt> {

	private static final Logger logger = LoggerFactory.getLogger(AttemptProcessor.class);
	
	@Override
	public Attempt process(Attempt attempt) throws Exception {
		logger.info("Started processing file {}", new Object[]{attempt.getFile().getName()});
		long startTime = System.currentTimeMillis();
		processAttempt(attempt);
		long endTime = System.currentTimeMillis();
		logger.info("Took {} seconds to process file {}", new Object[]{(endTime-startTime)/1000, attempt.getFile().getName()});
		return attempt;
	}

	private void processAttempt(Attempt attempt) {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
