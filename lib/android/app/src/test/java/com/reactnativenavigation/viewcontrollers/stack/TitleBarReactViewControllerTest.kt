package com.reactnativenavigation.viewcontrollers.stack

import android.app.Activity
import com.reactnativenavigation.BaseTest
import com.reactnativenavigation.mocks.TitleBarReactViewCreatorMock
import com.reactnativenavigation.options.ComponentOptions
import com.reactnativenavigation.options.params.Text
import com.reactnativenavigation.viewcontrollers.stack.topbar.title.TitleBarReactViewController
import org.junit.Test
import org.mockito.Mockito

class TitleBarReactViewControllerTest : BaseTest() {
    private lateinit var uut: TitleBarReactViewController
    private lateinit var viewCreator: TitleBarReactViewCreatorMock
    private lateinit var activity: Activity
    private lateinit var component: ComponentOptions
    override fun beforeEach() {
        super.beforeEach()
        viewCreator = Mockito.spy(TitleBarReactViewCreatorMock())
        activity = newActivity()
        component = createComponent()
        uut = TitleBarReactViewController(activity, viewCreator, component)
    }


    @Test
    fun createView() {
        uut.createView()
        Mockito.verify(viewCreator).create(activity, component.componentId.get(), component.name.get())
    }

    private fun createComponent(): ComponentOptions {
        val component = ComponentOptions()
        component.componentId = Text("compId")
        component.name = Text("compName")
        return component
    }
}