package com.reactnativenavigation.viewcontrollers.stack

import android.app.Activity
import android.graphics.Color
import android.view.MenuItem
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.reactnativenavigation.BaseTest
import com.reactnativenavigation.TestUtils
import com.reactnativenavigation.fakes.IconResolverFake
import com.reactnativenavigation.mocks.ImageLoaderMock.mock
import com.reactnativenavigation.mocks.TitleBarButtonCreatorMock
import com.reactnativenavigation.options.ButtonOptions
import com.reactnativenavigation.options.params.*
import com.reactnativenavigation.options.params.Number
import com.reactnativenavigation.viewcontrollers.stack.topbar.button.ButtonController
import com.reactnativenavigation.viewcontrollers.stack.topbar.button.ButtonPresenter
import com.reactnativenavigation.views.stack.topbar.titlebar.RightButtonsBar
import org.assertj.core.api.Java6Assertions
import org.junit.Test
import org.mockito.Mockito

class TopBarButtonControllerTest : BaseTest() {
    private lateinit var uut: ButtonController
    private lateinit var stackController: StackController
    private lateinit var button: ButtonOptions
    private lateinit var optionsPresenter: ButtonPresenter
    private lateinit var activity: Activity
    private val titleBar: RightButtonsBar
        get() = stackController.topBar.rightButtonsBar

    override fun beforeEach() {
        super.beforeEach()
        button = ButtonOptions()
        activity = newActivity()
        val buttonCreatorMock = TitleBarButtonCreatorMock()
        stackController = Mockito.spy(TestUtils.newStackController(activity).build())
        stackController.view.layout(0, 0, 1080, 1920)
        stackController.topBar.layout(0, 0, 1080, 200)
        titleBar.layout(0, 0, 1080, 200)
        optionsPresenter = Mockito.spy(ButtonPresenter(activity, button, IconResolverFake(activity, mock())))
        uut = ButtonController(activity, optionsPresenter, button, buttonCreatorMock, object : ButtonController.OnClickListener {
            override fun onPress(button: ButtonOptions) {
            }
        })
        stackController.ensureViewIsCreated()
    }

    @Test
    fun buttonDoesNotClearStackOptionsOnAppear() {
        setReactComponentButton()
        uut.ensureViewIsCreated()
        uut.onViewWillAppear()
        Mockito.verify(stackController, Mockito.times(0)).clearOptions()
    }

    @Test
    fun setIconColor_enabled() {
        setIconButton(true)
        uut.addToMenu(titleBar, 0)
        Java6Assertions.assertThat(titleBar.menu.size()).isOne()
        Mockito.verify(optionsPresenter).tint(any(), eq(Color.RED))
    }

    @Test
    fun setIconColor_disabled() {
        setIconButton(false)
        uut.addToMenu(titleBar, 0)
        Mockito.verify(optionsPresenter).tint(any(), eq(Color.LTGRAY))
    }

    @Test
    fun setIconColor_disabledColor() {
        setIconButton(false)
        button.disabledColor = Colour(Color.BLACK)
        uut.addToMenu(titleBar, 0)
        Mockito.verify(optionsPresenter).tint(any(), eq(Color.BLACK))
    }

    @Test
    fun disableIconTint() {
        setIconButton(false)
        button.disableIconTint = Bool(true)
        uut.addToMenu(titleBar, 0)
        Mockito.verify(optionsPresenter, Mockito.times(0)).tint(any(), any())
    }


    private fun setIconButton(enabled: Boolean) {
        button.id = "btn1"
        button.icon = Text("someIcon")
        button.color = Colour(Color.RED)
        button.component.name = NullText()
        button.component.componentId = NullText()
        button.enabled = Bool(enabled)
        button.showAsAction = Number(MenuItem.SHOW_AS_ACTION_ALWAYS)
    }

    private fun setReactComponentButton() {
        button.id = "btnId"
        button.component.name = Text("com.example.customBtn")
        button.component.componentId = Text("component666")
    }
}