import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.restfb.types.send.IdMessageRecipient;
import com.restfb.types.send.Message;

// Not tested yet

public class Reminder {

	private static Map<IdMessageRecipient, ReminderType> map = new HashMap<>();
	private static int NUMBER_TIMER_RUN = 0;
	
	

	public void add(IdMessageRecipient recipient, ReminderType type) {
		if (map.get(recipient) == null)
			map.put(recipient, type);
		else
			change(recipient, type);
	}

	public void delete(IdMessageRecipient recipient) {
		if (map.get(recipient) != null)
			map.remove(recipient);
		else
			throw new RuntimeException("Recipient doesn't excist, so can't be deleted");
	}

	public void change(IdMessageRecipient recipient, ReminderType type) {
		if (map.get(recipient) != null) {
			map.remove(recipient);
			map.put(recipient, type);
		} else
			throw new RuntimeException("Recipient doesn't excist, so can't be changed");
	}
	


	public void timerRunning() {
		// every night at 2am you run your task + 8 + 16
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 2);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);

		
		Timer timer = new Timer();

		TimerTask task = new TimerTask() {

			@Override
			public void run() {

				FBChat fb = new FBChat();
				
				for (Map.Entry<IdMessageRecipient, ReminderType> entry : map.entrySet()) {
					if (entry.getValue() == ReminderType.ONCE_A_DAY) {
						if(NUMBER_TIMER_RUN == 0){
							fb.SendMessage(entry.getKey(), new Message("Good morning, drink water!"));
						}
					} else if (entry.getValue() == ReminderType.TWICE_A_DAY) {
						if(NUMBER_TIMER_RUN != 2){
							fb.SendMessage(entry.getKey(), new Message("drink water!"));
						}
					} else {
						fb.SendMessage(entry.getKey(), new Message("Hi, drink water!"));
					}
				}

			}
		};

		timer.schedule(task, today.getTime(), TimeUnit.MILLISECONDS.convert(8, TimeUnit.HOURS)); 
		
		NUMBER_TIMER_RUN = (NUMBER_TIMER_RUN + 1 ) % 3;
	}

}
