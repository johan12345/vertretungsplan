package com.joejernst.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import com.johan.vertretungsplan_2.VertretungsplanApplication;

/*
 * Copyright 2012 Joe J. Ernst
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * This class represents an HTTP Request message.
 */
public class Request extends Message<Request> {

    private static final String DEFAULT_ENCODING = "UTF-8";
	HttpURLConnection connection;
    OutputStreamWriter writer;

    URL url;
    Map<String, String> query = new HashMap<String, String>();

    /**
     * The Constructor takes the url as a String.
     *
     * @param url The url parameter does not need the query string parameters if
          *       they are going to be supplied via calls to {@link #addQueryParameter(String, String)}.  You can, however, supply
          *       the query parameters in the URL if you wish.
     * @throws IOException
     */
    public Request(String url) throws IOException {
        this.url = new URL(url);
    }

    /**
     * Adds a Query Parameter to a list.  The list is converted to a String and appended to the URL when the Request
     * is submitted.
     *
     * @param name  The Query Parameter's name
     * @param value The Query Parameter's value
     * @return this Request, to support chained method calls
     */
    public Request addQueryParameter(String name, String value) {
        this.query.put(name, value);
        return this;
    }

    /**
     * Removes the specified Query Parameter.
     *
     * @param name The name of the Query Parameter to remove
     * @return this Request, to support chained method calls
     */
    public Request removeQueryParameter(String name) {
        this.query.remove(name);
        return this;
    }

    /**
     * Sets the URL that this Request will be sent to.
     *
     * @param url The url parameter does not need the query string parameters if
     *            they are going to be supplied via calls to {@link #addQueryParameter(String, String)}.  You can, however, supply
     *            the query parameters in the URL if you wish.
     * @return this Request, to support chained method calls
     * @throws MalformedURLException If the supplied url is malformed.
     */
//    public Request setUrl(String url) throws MalformedURLException {
//        this.url = new URL(url);
//        return this;
//    }

    /**
     * Issues a GET to the server.
     * @return The {@link Response} from the server
     * @throws IOException
     */
    public Response getResource(String encoding) throws IOException {
        buildQueryString();
		connection = (HttpURLConnection) url.openConnection();
        buildHeaders();

        //connection.setDoOutput(true);
        connection.setRequestMethod("GET");

        return readResponse(encoding);
    }
    
    /**
     * Issues a GET to the server and writes the response to a file.
     * @return The {@link Response} from the server
     * @throws IOException
     */
    public Response getResource(File file) throws IOException {
        buildQueryString();
		connection = (HttpURLConnection) url.openConnection();
        buildHeaders();

        //connection.setDoOutput(true);
        connection.setRequestMethod("GET");

        return readResponse(file);
    }
    
    public Response getResource() throws IOException {
    	return getResource(DEFAULT_ENCODING);
    }

    /**
     * Issues a PUT to the server.
     * @return The {@link Response} from the server
     * @throws IOException
     */
    public Response putResource(String encoding) throws IOException {
        return writeResource("PUT", this.body, encoding);
    }
    
    public Response putResource() throws IOException {
    	return putResource(DEFAULT_ENCODING);
    }

    /**
     * Issues a POST to the server.
     * @return The {@link Response} from the server
     * @throws IOException
     */
    public Response postResource(String encoding) throws IOException {
        return writeResource("POST", this.body, encoding);
    }
    
    public Response postResource() throws IOException {
        return postResource(DEFAULT_ENCODING);
    }

    /**
     * Issues a DELETE to the server.
     * @return The {@link Response} from the server
     * @throws IOException
     */
    public Response deleteResource(String encoding) throws IOException {
        buildQueryString();
    	connection = (HttpURLConnection) url.openConnection();
        buildHeaders();

        connection.setDoOutput(true);
        connection.setRequestMethod("DELETE");

        return readResponse(encoding);
    }
    
    public Response deleteResource() throws IOException {
    	return deleteResource(DEFAULT_ENCODING);
    }

    /**
     * A private method that handles issuing POST and PUT requests
     *
     * @param method POST or PUT
     * @param body The body of the Message
     * @return the {@link Response} from the server
     * @throws IOException
     */
    private Response writeResource(String method, String body, String encoding) throws IOException {
        buildQueryString();
        connection = (HttpURLConnection) url.openConnection();
        buildHeaders();

        connection.setDoOutput(true);
        connection.setRequestMethod(method);

        writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(body);
        writer.close();

        return readResponse(encoding);
    }
    
    private void handleSSL() {
    	if (connection.getURL().getProtocol().equals("https")) {
    		HttpsURLConnection conn = (HttpsURLConnection) connection;
    		conn.setSSLSocketFactory(VertretungsplanApplication.sslFactory);
    	}
    }

    /**
     * A private method that handles reading the Responses from the server.
     * @return a {@link Response} from the server.
     * @throws IOException
     */
    private Response readResponse(String encoding) throws IOException {
    	handleSSL();
    	
    	Response resp =  new Response()
        .setResponseCode(connection.getResponseCode())
        .setResponseMessage(connection.getResponseMessage())
        .setHeaders(connection.getHeaderFields());      
    	
    	try {
	        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), encoding));
	
	        StringBuilder builder = new StringBuilder();
	        String line;
	
	        while ((line = reader.readLine()) != null) {
	            builder.append(line);
	        }
	        reader.close();
	        resp.setBody(builder.toString());
    	} catch (IOException e) {
    		
    	}

        return resp;
    }
    
    /**
     * A private method that handles reading the Responses from the server to a file.
     * @return a {@link Response} from the server.
     * @throws IOException
     */
    private Response readResponse(File file) throws IOException {
    	InputStream inputStream = connection.getInputStream();

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte buffer[] = new byte[16 * 1024];
        
        int len1 = 0;
        while ((len1 = inputStream.read(buffer)) > 0) {
            fileOutputStream.write(buffer, 0, len1);
        }
        fileOutputStream.flush();
        fileOutputStream.close();
        inputStream.close();

        return new Response()
                .setResponseCode(connection.getResponseCode())
                .setResponseMessage(connection.getResponseMessage())
                .setHeaders(connection.getHeaderFields());
    }

    /**
     * A private method that loops through the query parameter Map, building a String to be appended to the URL.
     *
     * @throws MalformedURLException
     */
    private void buildQueryString() throws MalformedURLException {
        StringBuilder builder = new StringBuilder();

        // Put the query parameters on the URL before issuing the request
        if (!query.isEmpty()) {
            for (Map.Entry<String, String> param : query.entrySet()) {
                builder.append(param.getKey());
                builder.append("=");
                builder.append(param.getValue());
                builder.append("&");
            }
            builder.deleteCharAt(builder.lastIndexOf("&")); // Remove the trailing ampersand
        }

        if (builder.length() > 0) {
            // If there was any query string at all, begin it with the question mark
            builder.insert(0, "?");
        }

        url = new URL(url.toString() + builder.toString());
    }

    /**
     * A private method that loops through the headers Map, putting them on the Request or Response object.
     */
    private void buildHeaders() {
        if (!headers.isEmpty()) {
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                List<String> values = entry.getValue();

                for (String value : values) {
                    connection.addRequestProperty(entry.getKey(), value);
                }
            }
        }

    }

}
