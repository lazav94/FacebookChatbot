import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.restfb.types.send.IdMessageRecipient;
import com.restfb.types.send.Message;

public class ReminderListener implements ServletContextListener {
	
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
	

	public void contextInitialized(ServletContextEvent arg0) {
		ServletContext servletContext = arg0.getServletContext();
		System.out.println("*********Remider started*********");

		int delay = 1000;
		Timer timer = new Timer();
		// final Calendar calendar = Calendar.getInstance();
		// System.out.println("Tweet at Time = " + calendar.getTime());
		// calendar.add(Calendar.SECOND, -60);
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {

				FBChat fb = new FBChat();

				for (Map.Entry<IdMessageRecipient, ReminderType> entry : map.entrySet()) {
					if (entry.getValue() == ReminderType.ONCE_A_DAY) {
						if (NUMBER_TIMER_RUN == 0) {
							fb.SendMessage(entry.getKey(), new Message("Good morning, drink water!"));
						}
					} else if (entry.getValue() == ReminderType.TWICE_A_DAY) {
						if (NUMBER_TIMER_RUN != 2) {
							fb.SendMessage(entry.getKey(), new Message("drink water!"));
						}
					} else {
						fb.SendMessage(entry.getKey(), new Message("Hi, drink water!"));
					}
				}

			}// End of Run
		}, delay, 60000);
		servletContext.setAttribute("timer", timer);
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		ServletContext servletContext = arg0.getServletContext();
		// get our timer from the Context
		Timer timer = (Timer) servletContext.getAttribute("timer");

		// cancel all pending tasks in the timers queue
		if (timer != null)
			timer.cancel();

		// remove the timer from the servlet context
		servletContext.removeAttribute("timer");
		System.out.println("Reminder destroyed");

	}
}