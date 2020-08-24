package com.reactnativenavigation.options;


import com.reactnativenavigation.options.params.Bool;
import com.reactnativenavigation.options.params.NullBool;
import com.reactnativenavigation.options.parsers.BoolParser;

import org.json.JSONException;
import org.json.JSONObject;

public class NestedAnimationsOptions {
    public static class FadeAnimation extends NestedAnimationsOptions {
        public FadeAnimation() {
            try {
                JSONObject alpha = new JSONObject();
                alpha.put("from", 0);
                alpha.put("to", 1);
                alpha.put("duration", 300);

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
    public static NestedAnimationsOptions parse(JSONObject json) {
        NestedAnimationsOptions options = new NestedAnimationsOptions();
        if (json == null) return options;

        options.content = new AnimationOptions(json.optJSONObject("content"));
        options.bottomTabs = new AnimationOptions(json.optJSONObject("bottomTabs"));
        options.topBar = new AnimationOptions(json.optJSONObject("topBar"));
        options.enabled = BoolParser.parseFirst(json, "enabled", "enable");
        options.waitForRender = BoolParser.parse(json, "waitForRender");
        options.sharedElements = SharedElements.parse(json);
        options.elementTransitions = ElementTransitions.Companion.parse(json);

        return options;
    }

    public Bool enabled = new NullBool();
    public Bool waitForRender = new NullBool();
    public AnimationOptions content = new AnimationOptions();
    public AnimationOptions bottomTabs = new AnimationOptions();
    public AnimationOptions topBar = new AnimationOptions();
    public SharedElements sharedElements = new SharedElements();
    public ElementTransitions elementTransitions = new ElementTransitions();

    void mergeWith(NestedAnimationsOptions other) {
        topBar.mergeWith(other.topBar);
        content.mergeWith(other.content);
        bottomTabs.mergeWith(other.bottomTabs);
        sharedElements.mergeWith(other.sharedElements);
        elementTransitions.mergeWith(other.elementTransitions);
        if (other.enabled.hasValue()) enabled = other.enabled;
        if (other.waitForRender.hasValue()) waitForRender = other.waitForRender;
    }

    void mergeWithDefault(NestedAnimationsOptions defaultOptions) {
        content.mergeWithDefault(defaultOptions.content);
        bottomTabs.mergeWithDefault(defaultOptions.bottomTabs);
        topBar.mergeWithDefault(defaultOptions.topBar);
        sharedElements.mergeWithDefault(defaultOptions.sharedElements);
        elementTransitions.mergeWithDefault(defaultOptions.elementTransitions);
        if (!enabled.hasValue()) enabled = defaultOptions.enabled;
        if (!waitForRender.hasValue()) waitForRender = defaultOptions.waitForRender;
    }

    public boolean hasValue() {
        return topBar.hasValue() || content.hasValue() || bottomTabs.hasValue() || waitForRender.hasValue();
    }

    public boolean hasElementsTransition() {
        return sharedElements.hasValue() || elementTransitions.hasValue();
    }

    public long contentDuration() {
        return content.getDuration();
    }
}
