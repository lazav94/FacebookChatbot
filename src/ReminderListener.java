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

	public static Map<IdMessageRecipient, ReminderType> map = new HashMap<>();
	private static int NUMBER_TIMER_RUN = 0;

	public static void add(IdMessageRecipient recipient, ReminderType type) {
		System.out.println("Reminder add");
		if (map.get(recipient) == null)
			map.put(recipient, type);
		else
			change(recipient, type);
	}

	public static void delete(IdMessageRecipient recipient) {
		System.out.println("Reminder delete");
		if (map.get(recipient) != null)
			map.remove(recipient);
		else
			throw new RuntimeException("Recipient doesn't excist, so can't be deleted");
	}

	public static void change(IdMessageRecipient recipient, ReminderType type) {
		System.out.println("Reminder change");
		if (map.get(recipient) != null) {
			map.remove(recipient);
			map.put(recipient, type);
		} else
			throw new RuntimeException("Recipient doesn't excist, so can't be changed");
	}

	public static boolean haveReminder(IdMessageRecipient recipient) {
		return map.containsKey(recipient);
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

				for (Map.Entry<IdMessageRecipient, ReminderType> entry : map.entrySet()) {
					if (entry.getValue() == ReminderType.ONCE_A_DAY) {
						if (NUMBER_TIMER_RUN == 0) {
							FBChat.SendMessage(entry.getKey(), new Message("Good morning, drink water!"));
						}
					} else if (entry.getValue() == ReminderType.TWICE_A_DAY) {
						if (NUMBER_TIMER_RUN != 2) {
							FBChat.SendMessage(entry.getKey(), new Message("drink water!"));
						}
					} else {
						FBChat.SendMessage(entry.getKey(), new Message("Hi, drink water!"));
					}
					FBChat.SendMessage(entry.getKey(),
							FBChat.createImageMessage("https://media.giphy.com/media/Fx85ye9hVe2vS/giphy.gif"));

				}

				NUMBER_TIMER_RUN = (NUMBER_TIMER_RUN + 1) % 3;

			}
		}, delay, 60000);
		servletContext.setAttribute("timer", timer);
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		ServletContext servletContext = arg0.getServletContext();
		Timer timer = (Timer) servletContext.getAttribute("timer");

		// cancel all pending tasks in the timers queue
		if (timer != null)
			timer.cancel();

		// Remove the timer from the servlet context
		servletContext.removeAttribute("timer");
		System.out.println("Reminder destroyed");

	}
}