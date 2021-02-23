package com.reactnativenavigation.react.events

import android.util.Log
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import com.reactnativenavigation.viewcontrollers.viewcontroller.ViewController

object EventEmitter {
    private var reactContext: ReactContext? = null

    fun init(reactContext: ReactContext?) {
        this.reactContext = reactContext
    }

    fun appLaunched() = emit(AppLaunched, Arguments.createMap())

    fun emitComponentAppearState(state: String, controller: ViewController<*>) {
        when (state) {
            ComponentWillAppear,
            ComponentDidAppear,
            ComponentDidDisappear -> {
                if (controller.canSendLifecycleEvents())
                    Arguments.createMap().apply {
                        putString("componentId", controller.componentId)
                        putString("componentName", controller.currentComponentName)
                        putString("componentType", controller.componentType?.getName())
                        emit(state, this)
                    }
            }
        }
    }

    fun emitOnNavigationButtonPressed(id: String?, buttonId: String?) {
        val event = Arguments.createMap()
        event.putString("componentId", id)
        event.putString("buttonId", buttonId)
        emit(NavigationButtonPressed, event)
    }

    fun emitBottomTabSelected(unselectedTabIndex: Int, selectedTabIndex: Int) {
        val event = Arguments.createMap()
        event.putInt("unselectedTabIndex", unselectedTabIndex)
        event.putInt("selectedTabIndex", selectedTabIndex)
        emit(BottomTabSelected, event)
    }

    fun emitBottomTabPressed(tabIndex: Int) {
        val event = Arguments.createMap()
        event.putInt("tabIndex", tabIndex)
        emit(BottomTabPressed, event)
    }

    fun emitCommandCompleted(commandName: String?, commandId: String?, completionTime: Long) {
        val event = Arguments.createMap()
        event.putString("commandName", commandName)
        event.putString("commandId", commandId)
        event.putDouble("completionTime", completionTime.toDouble())
        emit(CommandCompleted, event)
    }

    fun emitModalDismissed(id: String?, componentName: String?, modalsDismissed: Int) {
        val event = Arguments.createMap()
        event.putString("componentId", id)
        event.putString("componentName", componentName)
        event.putInt("modalsDismissed", modalsDismissed)
        emit(ModalDismissed, event)
    }

    fun emitScreenPoppedEvent(componentId: String?) {
        val event = Arguments.createMap()
        event.putString("componentId", componentId)
        emit(ScreenPopped, event)
    }

    private fun emit(eventName: String, data: WritableMap) {
        reactContext?.let {
            it.getJSModule(RCTDeviceEventEmitter::class.java)?.emit(eventName, data)
        } ?: Log.e("RNN", "Could not send event $eventName. React context is null!")
    }

    const val ComponentWillAppear = "RNN.ComponentWillAppear"
    const val ComponentDidAppear = "RNN.ComponentDidAppear"
    const val ComponentDidDisappear = "RNN.ComponentDidDisappear"
    private const val AppLaunched = "RNN.AppLaunched"
    private const val CommandCompleted = "RNN.CommandCompleted"
    private const val BottomTabSelected = "RNN.BottomTabSelected"
    private const val BottomTabPressed = "RNN.BottomTabPressed"
    private const val NavigationButtonPressed = "RNN.NavigationButtonPressed"
    private const val ModalDismissed = "RNN.ModalDismissed"
    private const val ScreenPopped = "RNN.ScreenPopped"
}