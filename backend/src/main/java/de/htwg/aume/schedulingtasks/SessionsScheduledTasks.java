package de.htwg.aume.schedulingtasks;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import de.htwg.aume.service.DatabaseService;

@Component
public class SessionsScheduledTasks {

	@Autowired
	DatabaseService databaseService;

	@Scheduled(fixedDelay = 60, timeUnit = TimeUnit.MINUTES)
	public void deleteOldSessions() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, -2);

		databaseService.getSessions().stream()
				.filter(s -> s.getLastModified() != null && s.getLastModified().getTime() < c.getTime().getTime())
				.forEach(session -> databaseService.deleteSession(session));
	}
}