package com.kvstore.config;

import com.kvstore.repository.KeyValueRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Autowired
    private KeyValueRepository repository;

    @Scheduled(fixedRate = 60000)
    public void cleanUpExpiredKeys(){
        repository.findAll().forEach(entity ->{
            if (entity.getExpiresAt() != null && entity.getExpiresAt().isBefore(LocalDateTime.now())){
                repository.delete(entity);
            }
        });
    }
}
