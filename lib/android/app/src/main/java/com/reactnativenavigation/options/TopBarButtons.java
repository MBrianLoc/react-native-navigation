package com.reactnativenavigation.options;

import android.content.Context;

import com.reactnativenavigation.options.parsers.TypefaceLoader;
import com.reactnativenavigation.utils.CollectionUtils;

import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class TopBarButtons {

    public static TopBarButtons parse(Context context, TypefaceLoader typefaceLoader, JSONObject json) {
        TopBarButtons result = new TopBarButtons();
        if (json == null) return result;

        result.right = parseButtons(context, typefaceLoader, json, "rightButtons");
        result.left = parseButtons(context, typefaceLoader, json, "leftButtons");
        result.back = BackButton.parse(context, json.optJSONObject("backButton"));

        return result;
    }

    @Nullable
    private static ArrayList<ButtonOptions> parseButtons(Context context, TypefaceLoader typefaceLoader, JSONObject json, String buttons) {
        return ButtonOptions.parse(context, json, buttons);
    }

    public BackButton back = new BackButton();
    @Nullable public ArrayList<ButtonOptions> left;
    @Nullable public ArrayList<ButtonOptions> right;

    void mergeWith(TopBarButtons other) {
        if (other.left != null) left = mergeLeftButton(other.left);
        if (other.right != null) right = other.right;
        back.mergeWith(other.back);
    }

    private ArrayList<ButtonOptions> mergeLeftButton(ArrayList<ButtonOptions> other) {
        if (!other.isEmpty() && !CollectionUtils.isNullOrEmpty(left)) {
            ButtonOptions otherLeft = other.get(0);
            if (otherLeft.id == null) {
                left.get(0).mergeWith(otherLeft);
                return left;
            }
        }
        return other;
    }

    void mergeWithDefault(TopBarButtons defaultOptions) {
        if (left == null) {
            left = defaultOptions.left;
        } else if (!CollectionUtils.isNullOrEmpty(defaultOptions.left)){
            for (ButtonOptions button : left) {
                button.mergeWithDefault(defaultOptions.left.get(0));
            }
        }
        if (right == null) {
            right = defaultOptions.right;
        } else if (!CollectionUtils.isNullOrEmpty(defaultOptions.right)) {
            for (ButtonOptions button : right) {
                button.mergeWithDefault(defaultOptions.right.get(0));
            }
        }
        back.mergeWithDefault(defaultOptions.back);
    }

    public boolean hasLeftButtons() {
        return !CollectionUtils.isNullOrEmpty(left) && left.get(0).id != null;
    }
}
