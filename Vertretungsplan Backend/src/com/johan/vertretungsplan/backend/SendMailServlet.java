package com.johan.vertretungsplan.backend;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class SendMailServlet extends HttpServlet {
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		try {
		    Message msg = new MimeMessage(session);
		    msg.setFrom(new InternetAddress("johan.forstner@gmail.com", "Johan"));
		    msg.addRecipient(Message.RecipientType.TO,
		    	new InternetAddress("johan.forstner@gmail.com", "Johan"));
		    msg.setSubject("Vertretungsplan App - Schule hinzugefuegt");
		    msg.setText(req.getParameter("email") + " " + req.getParameter("name") + "\n" + req.getParameter("json"));
		    Transport.send(msg);
		    resp.setContentType("text/html");
		    resp.getWriter().println("<p>Deine Nachricht wurde gesendet.</p>");

		} catch (AddressException e) {
		    // ...
		} catch (MessagingException e) {
		    // ...
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
