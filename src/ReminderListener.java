import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Application Lifecycle Listener implementation class MyServletContextListener
 *
 */
public class ReminderListener implements ServletContextListener {

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		ServletContext servletContext = arg0.getServletContext();
		System.out.println("*********ServletContextListener started*********");

		int delay = 1000;
		Timer timer = new Timer();
		// final Calendar calendar = Calendar.getInstance();
		// System.out.println("Tweet at Time = " + calendar.getTime());
		// calendar.add(Calendar.SECOND, -60);
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				System.out.println("Running this code every 1 minute....");
			}// End of Run
		}, delay, 60000);
		servletContext.setAttribute("timer", timer);
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		ServletContext servletContext = arg0.getServletContext();
		// get our timer from the Context
		Timer timer = (Timer) servletContext.getAttribute("timer");

		// cancel all pending tasks in the timers queue
		if (timer != null)
			timer.cancel();

		// remove the timer from the servlet context
		servletContext.removeAttribute("timer");
		System.out.println("ServletContextListener destroyed");

	}
}