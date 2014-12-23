package com.johan.vertretungsplan.parser;

import android.util.Log;

import com.google.gson.Gson;
import com.joejernst.http.Request;
import com.joejernst.http.Response;
import com.johan.vertretungsplan.objects.Schule;
import com.johan.vertretungsplan.objects.Vertretungsplan;
import com.johan.vertretungsplan_2.VertretungsplanApplication;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class BackendConnectParser extends BaseParser {

    public static final String BASE_URL = "https://hamilton.rami.io/";
    public static final String VERSION = "v="
            + VertretungsplanApplication.getVersion();
    private String schoolId;

    public BackendConnectParser(Schule schule) {
        super(schule);
        this.schoolId = schule.getId();
    }

    public BackendConnectParser(String schoolId) {
        super(null);
        this.schoolId = schoolId;
    }

    public static List<Schule> getAvailableSchools() throws IOException,
            JSONException {
        String url = BASE_URL + "schools?" + VERSION;
        JSONArray array = new JSONArray(httpGet(url, "UTF-8"));
        List<Schule> schools = new ArrayList<Schule>();
        for (int i = 0; i < array.length(); i++) {
            schools.add(Schule.fromServerJSON(array.getJSONObject(i)));
        }
        return schools;
    }

    @Override
    public Vertretungsplan getVertretungsplan() throws IOException,
            JSONException, VersionException, UnauthorizedException {
        String regId = VertretungsplanApplication.settings.getString("regId", "");
        Log.d("vertretungsplan", schoolId);
        String url = BASE_URL + "vertretungsplan?school=" + schoolId + "&regId=" + URLEncoder.encode(regId, "UTF-8") + "&" + VERSION;
        Log.d("vertretungsplan", url);
        Response response = new Request(url).getResource("UTF-8");
        if (response.getResponseCode() == 400)
            throw new VersionException();
        else if (response.getResponseCode() == 401)
            throw new UnauthorizedException();
        else if (response.getResponseCode() == 200) {
            Vertretungsplan v = new Gson().fromJson(response.getBody(), Vertretungsplan.class);
            return v;
        } else {
            throw new IOException("response: " + response.getResponseCode());
        }
    }

    @Override
    public List<String> getAllClasses() throws IOException, JSONException {
        JSONArray classesJson;
        String url = BASE_URL + "classes?school=" + schoolId + "&" + VERSION;
        classesJson = new JSONArray(httpGet(url, "UTF-8"));
        List<String> classes = new ArrayList<String>();
        for (int i = 0; i < classesJson.length(); i++) {
            classes.add(classesJson.getString(i));
        }
        return classes;
    }

}
