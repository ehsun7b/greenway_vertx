package com.ehsunbehravesh.greenway.telegram.model.request;

import com.google.gson.Gson;

/**
 * stas
 * 8/11/15
 */
public abstract class Keyboard {

    private Gson gson = new Gson();

    @Override
    public final String toString() {
        return gson.toJson(this);
    }

}
