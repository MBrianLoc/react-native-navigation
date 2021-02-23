package com.reactnativenavigation.options

import org.json.JSONObject

class AnimationsOptions {
    @JvmField var push = StackAnimationOptions()
    @JvmField var pop = StackAnimationOptions()
    @JvmField var setStackRoot = StackAnimationOptions()
    @JvmField var setRoot = AnimationOptions()
    @JvmField var showModal = ModalAnimationOptions()
    @JvmField var dismissModal = ModalAnimationOptions()

    fun mergeWith(other: AnimationsOptions) {
        push.mergeWith(other.push)
        pop.mergeWith(other.pop)
        setRoot.mergeWith(other.setRoot)
        setStackRoot.mergeWith(other.setStackRoot)
        showModal.mergeWith(other.showModal)
        dismissModal.mergeWith(other.dismissModal)
    }

    fun mergeWithDefault(defaultOptions: AnimationsOptions) {
        push.mergeWithDefault(defaultOptions.push)
        pop.mergeWithDefault(defaultOptions.pop)
        setStackRoot.mergeWithDefault(defaultOptions.setStackRoot)
        setRoot.mergeWithDefault(defaultOptions.setRoot)
        showModal.mergeWithDefault(defaultOptions.showModal)
        dismissModal.mergeWithDefault(defaultOptions.dismissModal)
    }

    companion object {
        @JvmStatic
        fun parse(json: JSONObject?): AnimationsOptions {
            val options = AnimationsOptions()
            if (json == null) return options
            options.push = StackAnimationOptions(json.optJSONObject("push"))
            options.pop = StackAnimationOptions(json.optJSONObject("pop"))
            options.setStackRoot = StackAnimationOptions(json.optJSONObject("setStackRoot"))
            options.setRoot = AnimationOptions(json.optJSONObject("setRoot"))
            val showModalJson = json.optJSONObject("showModal")
            showModalJson?.let {
                options.showModal = parseModalAnimationOptions(it)
            }
            val dismissModalJson = json.optJSONObject("dismissModal")
            dismissModalJson?.let {
                options.dismissModal = parseModalAnimationOptions(it)
            }
            return options
        }
    }
}
