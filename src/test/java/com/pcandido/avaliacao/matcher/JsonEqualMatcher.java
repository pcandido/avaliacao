package com.pcandido.avaliacao.matcher;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.io.InputStream;
import java.util.Scanner;

public class JsonEqualMatcher extends BaseMatcher<String> {

    private String compareTo;

    public JsonEqualMatcher(String resourceFile) {
        InputStream in = JsonEqualMatcher.class.getClassLoader().getResourceAsStream(resourceFile);
        if (in == null) {
            throw new NullPointerException();
        }
        Scanner sc = new Scanner(in);

        StringBuilder sb = new StringBuilder();
        while (sc.hasNextLine()) {
            sb.append(sc.nextLine());
            sb.append('\n');
        }

        this.compareTo = sb.toString();
    }

    @Override
    public boolean matches(Object that) {
        JsonElement o1 = JsonParser.parseString(this.compareTo);
        JsonElement o2 = JsonParser.parseString(that.toString());
        return o1.equals(o2);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(compareTo);
    }

    public static JsonEqualMatcher jsonEqualTo(String resourceFile) {
        return new JsonEqualMatcher(resourceFile);
    }
}
