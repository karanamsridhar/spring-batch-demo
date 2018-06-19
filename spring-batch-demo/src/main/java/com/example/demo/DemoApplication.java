package com.example.demo;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@SpringBootApplication
@EnableBatchProcessing
public class DemoApplication {

	private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Value("${max-threads}")
	private int maxThreads;
	
	@Value("${chunk-size}")
	private int chunkSize;
	
	public static void main(String[] args) {
		long time = System.currentTimeMillis();
		SpringApplication.run(DemoApplication.class, args);
		time = System.currentTimeMillis() - time;
		logger.info("Application runtime: {} seconds.", (time/1000));
	}

	@Bean
	@ConfigurationProperties(prefix="spring.datasource")
	public DataSource batchDataSource(){
		return DataSourceBuilder.create().build();
	}
	
	@Bean
	public AttemptReader processAttemptReader(){
		return new AttemptReader();
	}
	
	@Bean
	public AttemptProcessor processAttemptProcessor(){
		return new AttemptProcessor();
	}
	
	@Bean
	public AttemptWriter processAttemptWriter(){
		return new AttemptWriter();
	}
	
	@Bean
	public JobCompletionNotificationListener jobExecutionListener() {
		return new JobCompletionNotificationListener();
	}
	
	@Bean
	public StepExecutionNotificationListener stepExecutionListener() {
		return new StepExecutionNotificationListener();
	}
	
	@Bean
	public ChunkExecutionListener chunkListener() {
		return new ChunkExecutionListener();
	}
	
	
	@Bean
	public Job job(){
		return jobBuilderFactory.get("csv-to-parquet")
		.incrementer(new RunIdIncrementer())
		.listener(jobExecutionListener())
		.flow(readCsv())
		.end()
		.build();
	}
	
	@Bean
	public Step readCsv(){
		return stepBuilderFactory.get("read-csv")
		.<Attempt, Attempt>chunk(chunkSize)
		.reader(processAttemptReader())
		.processor(processAttemptProcessor())
		.writer(processAttemptWriter())
		.taskExecutor(taskExecutor())
		.listener(stepExecutionListener())
		.listener(chunkListener())
		.throttleLimit(maxThreads)
		.build();
	}
	
	@Bean
	public TaskExecutor taskExecutor(){
		SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
		simpleAsyncTaskExecutor.setConcurrencyLimit(maxThreads);
		return simpleAsyncTaskExecutor;
	}
	
	
	
}
