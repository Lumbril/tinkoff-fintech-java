package org.example.configs;

import lombok.RequiredArgsConstructor;
import org.example.components.WeatherProducer;
import org.example.dto.WeatherDto;
import org.example.services.WeatherAPIService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class WeatherApiSchedulerConfig implements SchedulingConfigurer {
    private TaskScheduler taskScheduler;
    private ScheduledFuture<?> job;
    private final String [] cities = {"Москва", "Санкт-Петербург", "Самара", "Казань", "Уфа"};
    private int currentCityIndex = 0;

    private final WeatherAPIService weatherAPIService;
    private final WeatherProducer weatherProducer;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix("scheduler-weatherapi-");
        threadPoolTaskScheduler.initialize();

        job(threadPoolTaskScheduler);
        this.taskScheduler = threadPoolTaskScheduler;
        taskRegistrar.setTaskScheduler(threadPoolTaskScheduler);
    }

    private void job(TaskScheduler scheduler) {
        job = scheduler.schedule(() -> {
            String city = cities[currentCityIndex++];
            currentCityIndex %= cities.length;

            WeatherDto weatherDto = weatherAPIService.getWeatherDto(city);
            weatherProducer.send(weatherDto);
            System.out.println(Thread.currentThread().getName() + " " + city + " " + new Date() + " : " + weatherDto);
        }, triggerContext -> {
            String cronExp = "0 * * * * ?";

            return new CronTrigger(cronExp).nextExecution(triggerContext);
        });
    }
}