package com.pcandido.caed.matcher;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class ErrorMatcher extends BaseMatcher<String> {

    private final JsonObject compareTo;

    public ErrorMatcher(String tipo, String descricao) {
        compareTo = new JsonObject();
        compareTo.add("situacao", new JsonPrimitive("ERRO"));
        compareTo.add("tipo", new JsonPrimitive(tipo));
        compareTo.add("descrição", new JsonPrimitive(descricao));
    }

    @Override
    public boolean matches(Object that) {
        JsonElement o1 = JsonParser.parseString(that.toString());
        return o1.equals(compareTo);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(compareTo.toString());
    }

    public static ErrorMatcher isError(String tipo, String descricao) {
        return new ErrorMatcher(tipo, descricao);
    }
}
