package com.welcomehome.app;

import java.io.InputStream;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSON {

	public static JSONObject getJSONObject(InputStream stream)
			throws JSONException {
		return getJSONObject(compileContent(stream));
	}

	public static JSONObject getJSONObject(String content) throws JSONException {
		return new JSONObject(content);
	}

	public static JSONArray getJSONArray(InputStream stream)
			throws JSONException {
		return getJSONArray(compileContent(stream));
	}

	public static JSONArray getJSONArray(String content) throws JSONException {
		return new JSONArray(content);
	}

	private static String compileContent(InputStream stream) {
		String compiledContent = "";
		Scanner s = new Scanner(stream);
		while (s.hasNext()) {
			compiledContent += s.next();
			if (s.hasNext()) {
				compiledContent += " ";
			}
		}
		return compiledContent;
	}
}
