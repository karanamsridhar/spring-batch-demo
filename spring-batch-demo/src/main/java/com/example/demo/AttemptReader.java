package com.example.demo;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Value;

public class AttemptReader implements ItemReader<Attempt> {
 
	private static final Logger logger = LoggerFactory.getLogger(AttemptReader.class);
	
	@Value("${input-directory}")
	private String inputDirectoryLocation;
	
	private static Queue<Attempt> attemptQueue = new LinkedList<Attempt>();
	
	@PostConstruct
	public void initialize(){
		logger.info("Scanning file directory {}", inputDirectoryLocation);
		File inputDirectory = new File(inputDirectoryLocation);
		if(!inputDirectory.exists()){
			logger.warn("Input directory {} does not exist. Creating it.", inputDirectoryLocation);
			inputDirectory.mkdirs();
		}
		
		Arrays.stream(inputDirectory.listFiles()).forEach(file -> attemptQueue.add(new Attempt(file)));
		
		logger.info("{} attempts queued.", attemptQueue.size());
	}
	
	@Override
	public synchronized Attempt read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		Attempt attempt = null;
		logger.info("Attemp queue size is {}", attemptQueue.size());
		if(attemptQueue.size() > 0){
			attempt = attemptQueue.remove();
		}
		return attempt;
	}

}
