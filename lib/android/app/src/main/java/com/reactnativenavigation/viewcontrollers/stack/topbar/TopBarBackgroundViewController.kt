package com.reactnativenavigation.viewcontrollers.stack.topbar

import android.app.Activity
import com.reactnativenavigation.options.ComponentOptions
import com.reactnativenavigation.options.Options
import com.reactnativenavigation.react.events.ComponentType
import com.reactnativenavigation.utils.CompatUtils
import com.reactnativenavigation.viewcontrollers.viewcontroller.ViewController
import com.reactnativenavigation.viewcontrollers.viewcontroller.YellowBoxDelegate
import com.reactnativenavigation.viewcontrollers.viewcontroller.overlay.ViewControllerOverlay
import com.reactnativenavigation.views.stack.topbar.TopBarBackgroundView
import com.reactnativenavigation.views.stack.topbar.TopBarBackgroundViewCreator

class TopBarBackgroundViewController(activity: Activity, private val viewCreator: TopBarBackgroundViewCreator)
    : ViewController<TopBarBackgroundView?>(
        activity,
        CompatUtils.generateViewId().toString() + "",
        YellowBoxDelegate(activity),
        Options(),
        ViewControllerOverlay(activity)
) {
    @JvmField
    var component: ComponentOptions? = null

    override fun createView(): TopBarBackgroundView? {
        return component?.let {
            return viewCreator.create(activity, it.componentId.get(), it.name.get())
        }
    }

    override fun sendOnNavigationButtonPressed(buttonId: String) {}
    override fun getComponentId() = component?.componentId?.get()
    override fun getComponentType() = ComponentType.Background
    override fun getCurrentComponentName() = component?.name?.get()
    override fun canSendLifecycleEvents() = true

}