
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.restfb.DefaultFacebookClient;
import com.restfb.DefaultJsonMapper;
import com.restfb.FacebookClient;
import com.restfb.JsonMapper;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.User;
import com.restfb.types.send.Bubble;
import com.restfb.types.send.ButtonTemplatePayload;
import com.restfb.types.send.GenericTemplatePayload;
import com.restfb.types.send.IdMessageRecipient;
import com.restfb.types.send.ListTemplatePayload;
import com.restfb.types.send.ListViewElement;
import com.restfb.types.send.MediaAttachment;
import com.restfb.types.send.Message;
import com.restfb.types.send.PostbackButton;
import com.restfb.types.send.QuickReply;
import com.restfb.types.send.SendResponse;
import com.restfb.types.send.TemplateAttachment;
import com.restfb.types.webhook.WebhookEntry;
import com.restfb.types.webhook.WebhookObject;
import com.restfb.types.webhook.messaging.MessagingItem;

/**
 * git add . & git commit -m "first version" & git push heroku master & heroku
 * logs -t
 */
@WebServlet("/Webhook")
public class FBChat extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private State state = State.BEGIN;

	private String accessToken = "EAAOnZC3VWYUUBAFkD1NQ8vlgjr8niWRFZAIExAg9THb50btvBGjh9tLllccxk63DCSieswPxpiUbQbBvHZAfksylMaZAn7y6S8z29nWXgVtmpkZCVp0rA3FHHZALZAJKjZCuVNNFhLcksfjHAimBZCBp2brVaqSGCIWQzEoZCYjvFsOQZDZD";
	private String verifyToken = "zmajToken";

	// hardcoded string

	String welcomeString = "☑  Daily water reminders\n☑  Personalized AI recommendations\n☑  Number of cups of water drank this week\n☑  Tips about water drinking";
	String recommentCups = "Recommended amount of water per day is eight 8-ounce glasses, equals to about 2 liters, or half a gallon.";
	String champ = "Your'e a real champ 🥂 8 cups is the recommended amount";

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String hubToken = request.getParameter("hub.verify_token");
		String hubChallange = request.getParameter("hub.challenge");

		if (hubToken != null && verifyToken.equals(hubToken)) {
			response.getWriter().write(hubChallange);
			response.getWriter().flush();
			response.getWriter().close();
		} else {
			response.getWriter().write("incorrect token");
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		StringBuffer sb = new StringBuffer();
		BufferedReader br = request.getReader();
		String line = "";
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		System.out.println("POST line: " + sb);

		JsonMapper mapper = new DefaultJsonMapper();
		WebhookObject webhookObj = mapper.toJavaObject(sb.toString(), WebhookObject.class);

		for (WebhookEntry entry : webhookObj.getEntryList()) {
			if (entry.getMessaging() != null) {
				for (MessagingItem mItem : entry.getMessaging()) {
					String senderID = mItem.getSender().getId();

					IdMessageRecipient recipient = new IdMessageRecipient(senderID);
					if (mItem.getMessage() != null && mItem.getMessage().getText() != null) {
						AI(recipient, mItem); // TODO send recipient 2 times
					}
				}
			}
		}
	}

	private void SendMessage(IdMessageRecipient recipient, Message message) {
		FacebookClient pageClient = new DefaultFacebookClient(accessToken, Version.VERSION_2_6);
		pageClient.publish("me/messages", SendResponse.class, Parameter.with("recipient", recipient),
				Parameter.with("message", message));
	}

	private String getFirstName(IdMessageRecipient recipient) {
		FacebookClient fbClient = new DefaultFacebookClient(accessToken, Version.VERSION_2_6);
		User user = fbClient.fetchObject(recipient.getId().toString(), User.class);
		return user.getFirstName();
	}

	private void AI(IdMessageRecipient recipient, MessagingItem mItem) {
		switch (state) {
		case BEGIN:

			String hiMessage = "Hi " + getFirstName(recipient) + "! I am your personal water trainer 🙂";
			SendMessage(recipient, new Message(hiMessage));
			SendMessage(recipient, QuickReplayMessage());

			state = State.AFTER_START;
			break;
		case AFTER_START:

			SendMessage(recipient, new Message("Before we begin..."));
			SendMessage(recipient, QuickReplayMessageCups());

			state = State.CHOICE;
			break;
		case CHOICE:

			String cupChoice = mItem.getMessage().getText();
			switch (cupChoice) {
			case "1-2 cups":
				SendMessage(recipient,
						createImageMessage("http://www.makesafetyfun.com/wp-content/uploads/2016/04/oh-no.jpg"));
				SendMessage(recipient, new Message(recommentCups));
				break;
			case "3-5 cups":
				SendMessage(recipient, createImageMessage(
						"http://i0.kym-cdn.com/photos/images/newsfeed/000/181/367/closeenough.png?1317606898"));
				SendMessage(recipient, new Message(recommentCups));
				break;
			case "6 and more":
				SendMessage(recipient,
						createImageMessage("http://solopracticeuniversity.com/files/2015/10/Champion.jpg"));
				SendMessage(recipient, new Message(champ));
				break;
			case "I don't count":
				SendMessage(recipient, createImageMessage(
						"https://steamuserimages-a.akamaihd.net/ugc/508076146225920843/666A623BF557B8DF6E9765E3BF8C339D5A3F7655/?interpolation=lanczos-none&output-format=jpeg&output-quality=95&fit=inside|637:358&composite-to%3D%2A%2C%2A%7C637%3A358&background-color=black"));
				SendMessage(recipient, new Message(recommentCups));
				break;
			default:
				SendMessage(recipient, new Message(recommentCups));
				SendMessage(recipient, createImageMessage(
						"https://steamuserimages-a.akamaihd.net/ugc/508076146225920843/666A623BF557B8DF6E9765E3BF8C339D5A3F7655/?interpolation=lanczos-none&output-format=jpeg&output-quality=95&fit=inside|637:358&composite-to%3D%2A%2C%2A%7C637%3A358&background-color=black"));
				break;

			}
			SendMessage(recipient, QuickReplayMessageReminders());
			
			state = State.REMINDERS;
			break;
		case REMINDERS:
			// TODO Java reminder + message!

			String reminderChoice = mItem.getMessage().getText();
			switch (reminderChoice) {
			case "3 times a day":
				break;
			case "Twice a day":
				break;
			case "Once a day":
				break;
			case "Stop reminder":
				break;
			default:
				break;
			}
			
			SendMessage(recipient, new Message("Noted 🙂"));
			SendMessage(recipient, QuickReplayMessageNoted());
			
			state = State.IDLE;
		case IDLE:
			state = State.END;
			break;
		case END:
			// FIXME
			SendMessage(recipient, createImageMessage(
					"http://3.bp.blogspot.com/_4jg1jp938Vw/TQ7f-MTi3qI/AAAAAAAAACA/fQy4Cg2lx3c/S1600-R/homerwoohoo_large.jpg"));
			SendMessage(recipient, new Message("Well done Lazar! Keep it up!"));
			SendMessage(recipient,
					new Message("You can always get to the menu by asking for \"Menu\" 🙂 (not implemented yet)"));
			state = State.BEGIN;
			break;

		}
	}

	private Message QuickReplayMessage() {

		Message msg = new Message(welcomeString);
		List<QuickReply> list = new ArrayList<>();
		list.add(new QuickReply("Let's start", "Let's start v2"));
		msg.addQuickReplies(list);
		return msg;

	}

	private Message QuickReplayMessageCups() {

		Message msg = new Message("How many cups of water do you drink a day?");
		List<QuickReply> list = new ArrayList<>();
		list.add(new QuickReply("1-2 cups", "1"));
		list.add(new QuickReply("3-5 cups", "2"));
		list.add(new QuickReply("6 and more", "3"));
		list.add(new QuickReply("I don't count", "4"));
		msg.addQuickReplies(list);
		return msg;

	}

	private Message QuickReplayMessageReminders() {

		Message msg = new Message("Choose the frequency for water break reminders");
		List<QuickReply> list = new ArrayList<>();
		list.add(new QuickReply("3 times a day", "1"));
		list.add(new QuickReply("Twice a day", "2"));
		list.add(new QuickReply("Once a day", "3"));
		msg.addQuickReplies(list);
		return msg;

	}

	private Message QuickReplayMessageNoted() {
		
		Message msg = new Message("Let's give it a try now, drink 1 cup of water and press the button");
		List<QuickReply> list = new ArrayList<>();
		list.add(new QuickReply("Done", "1"));
		msg.addQuickReplies(list);
		return msg;
	}

	public Message createImageMessage(String imageUrl) {
		MediaAttachment image = new MediaAttachment(MediaAttachment.Type.IMAGE, imageUrl);
		Message imageMessage = new Message(image);
		return imageMessage;
	}

	/** Code below is for some other features */

	private Message generic2() {
		GenericTemplatePayload payload = new GenericTemplatePayload();

		Bubble option1 = new Bubble("1");
		PostbackButton postbackButton1 = new PostbackButton("1-2 cups", "1-2 cups bre");
		option1.addButton(postbackButton1);

		Bubble option2 = new Bubble("2");
		PostbackButton postbackButton2 = new PostbackButton("3-5 cups", "3-5 cups bre");
		option2.addButton(postbackButton2);

		Bubble option3 = new Bubble("3");
		PostbackButton postbackButton3 = new PostbackButton("6 and more", "6+ cups bre");
		option3.addButton(postbackButton3);

		Bubble option4 = new Bubble("4");
		PostbackButton postbackButton4 = new PostbackButton("I don'count", "I dont'count");
		option4.addButton(postbackButton4);

		payload.addBubble(option1);
		payload.addBubble(option2);
		payload.addBubble(option3);
		payload.addBubble(option4);

		TemplateAttachment templateAttachment = new TemplateAttachment(payload);
		Message imageMessage = new Message(templateAttachment);
		return imageMessage;
	}

	private Message button() {
		ButtonTemplatePayload payload = new ButtonTemplatePayload("Button");

		// build a button that sends a postback
		PostbackButton postbackButton = new PostbackButton("Let's start", "Before we begin..");

		payload.addButton(postbackButton);

		TemplateAttachment templateAttachment = new TemplateAttachment(payload);
		Message imageMessage = new Message(templateAttachment);

		return imageMessage;
	}

	private Message generic() {
		GenericTemplatePayload payload = new GenericTemplatePayload();

		Bubble option1 = new Bubble("1-2 cups");
		option1.setSubtitle("hej");
		Bubble option2 = new Bubble("3-5 cups");
		option2.setSubtitle("hej");
		// Bubble option3 = new Bubble("6 and more");
		// option3.setImageUrl("http://www.kidsmathgamesonline.com/images/pictures/numbers600/number1.jpg");
		// Bubble option4 = new Bubble("I don'count");
		// option4.setSubtitle("HEJ");

		payload.addBubble(option1);
		payload.addBubble(option2);
		// payload.addBubble(option3);
		// payload.addBubble(option4);

		TemplateAttachment templateAttachment = new TemplateAttachment(payload);
		Message imageMessage = new Message(templateAttachment);
		return imageMessage;
	}

	private Message list() {

		PostbackButton postbackButton1 = new PostbackButton("Let's start", "Before we begin..");
		PostbackButton postbackButton2 = new PostbackButton("Let's start", "Before we begin..");
		PostbackButton postbackButton3 = new PostbackButton("Let's start", "Before we begin..");

		List<ListViewElement> list = new ArrayList<ListViewElement>();
		ListViewElement jedan = new ListViewElement("jedan");
		ListViewElement dva = new ListViewElement("dva");
		ListViewElement tri = new ListViewElement("tri");

		jedan.setSubtitle("DFSJKKL");
		dva.setSubtitle("Idemoo");
		tri.setSubtitle("KDJF");

		jedan.setImageUrl("http://www.kidsmathgamesonline.com/images/pictures/numbers600/number1.jpg");
		dva.setImageUrl(
				"http://cdn.mysitemyway.com/etc-mysitemyway/icons/legacy-previews/icons/black-ink-grunge-stamps-textures-icons-alphanumeric/068671-black-ink-grunge-stamp-textures-icon-alphanumeric-m02-clear.png");
		tri.setImageUrl(
				"https://gallery.yopriceville.com/var/resizes/Free-Clipart-Pictures/Decorative-Numbers/Colourful_Triangles_Number_Three_PNG_Clipart_Image.png?m=1437533701");

		jedan.addButton(postbackButton1);
		dva.addButton(postbackButton2);
		tri.addButton(postbackButton3);

		list.add(jedan);
		list.add(dva);
		list.add(tri);

		ListTemplatePayload payload = new ListTemplatePayload(list);

		TemplateAttachment templateAttachment = new TemplateAttachment(payload);
		Message imageMessage = new Message(templateAttachment);
		return imageMessage;
	}

}
