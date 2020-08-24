package com.reactnativenavigation.options;

import org.json.JSONException;
import org.json.JSONObject;

public class FadeAnimation extends NestedAnimationsOptions {
    public final static long DURATION = 300;
    public FadeAnimation() {
        try {
            JSONObject alpha = new JSONObject();
            alpha.put("from", 0);
            alpha.put("to", 1);
            alpha.put("duration", DURATION);

            JSONObject content = new JSONObject();
            content.put("alpha", alpha);

            JSONObject animation = new JSONObject();
            animation.put("content", content);
            mergeWith(parse(animation));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
