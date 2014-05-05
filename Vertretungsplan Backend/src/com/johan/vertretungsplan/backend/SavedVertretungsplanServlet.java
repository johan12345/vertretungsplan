package com.johan.vertretungsplan.backend;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.johan.vertretungsplan.objects.SavedVertretungsplan;

import static com.googlecode.objectify.ObjectifyService.ofy;

@SuppressWarnings("serial")
public class SavedVertretungsplanServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String schoolId = req.getParameter("school");
		if(schoolId == null) schoolId = "Schleswig_Lornsenschule";
		
		SavedVertretungsplan sv = ofy().load().type(SavedVertretungsplan.class).id(schoolId).now();
		String json = new Gson().toJson(sv.vertretungsplan);
		resp.setContentType("application/json");
		resp.getOutputStream().print(json);
	}
}
