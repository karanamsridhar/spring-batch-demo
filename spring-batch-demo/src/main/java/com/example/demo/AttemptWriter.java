package com.example.demo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;

public class AttemptWriter implements ItemWriter<Attempt> {

	private static final Logger logger = LoggerFactory.getLogger(AttemptWriter.class);
	
	@Value("${processed-directory}")
	private String processedDirectory;
	
	@Value("${failed-directory}")
	private String failedDirectory;
	
	@Override
	public void write(List<? extends Attempt> attempts) throws Exception {
		for (Attempt attempt : attempts) {
			if(attempt.isSuccess()){
			
			}
			moveFile(attempt);
		}
	}

	private void moveFile(Attempt attempt) {
		if(!attempt.hasSystemError()){
			File processedDateDirectory = new File(processedDirectory
													+ System.getProperty("file.separator")
													+ new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis()));

			File failedDateDirectory = new File(failedDirectory
					+ System.getProperty("file.separator")
					+ new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis()));

			
			if(attempt.isSuccess() && !processedDateDirectory.exists()){
				logger.info("Creating processed date directory {}", processedDateDirectory.getName());
				processedDateDirectory.mkdir();
			}

			if(!attempt.isSuccess() && !failedDateDirectory.exists()){
				logger.info("Creating failed date directory {}", failedDateDirectory.getName());
				failedDateDirectory.mkdir();
			}

			logger.info("Moving {} document: {}.", attempt.isSuccess() ? "processed" : "failed",
					attempt.getFile().getName());
			
			String directory = attempt.isSuccess() ? processedDateDirectory.getAbsolutePath()
								: failedDateDirectory.getAbsolutePath();
			
			attempt.getFile().renameTo(new File(directory + System.getProperty("file.separator")+attempt.getFile().getName()));
		}
	}

}
