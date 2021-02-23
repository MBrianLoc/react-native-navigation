package com.reactnativenavigation.viewcontrollers.externalcomponent

import android.app.Activity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.FragmentActivity
import com.facebook.react.ReactInstanceManager
import com.reactnativenavigation.BaseTest
import com.reactnativenavigation.options.ExternalComponent
import com.reactnativenavigation.options.Options
import com.reactnativenavigation.options.params.Text
import com.reactnativenavigation.react.events.EventEmitter
import com.reactnativenavigation.viewcontrollers.child.ChildControllersRegistry
import com.reactnativenavigation.viewcontrollers.viewcontroller.Presenter

import io.mockk.verify
import io.mockk.verifyOrder
import org.assertj.core.api.Java6Assertions
import org.json.JSONObject
import org.junit.Test
import org.mockito.Mockito

class ExternalComponentViewControllerTest : BaseTest() {
    private lateinit var uut: ExternalComponentViewController
    private lateinit var componentCreator: FragmentCreatorMock
    private lateinit var activity: Activity
    private lateinit var ec: ExternalComponent
    private lateinit var reactInstanceManager: ReactInstanceManager
    private lateinit var childRegistry: ChildControllersRegistry
    override fun beforeEach() {
        super.beforeEach()
        componentCreator = Mockito.spy(FragmentCreatorMock())
        activity = newActivity()
        ec = createExternalComponent()
        reactInstanceManager = Mockito.mock(ReactInstanceManager::class.java)

        childRegistry = ChildControllersRegistry()
        uut = Mockito.spy(ExternalComponentViewController(activity,
                childRegistry,
                "fragmentId",
                Presenter(activity, Options.EMPTY),
                ec,
                componentCreator,
                reactInstanceManager,
                ExternalComponentPresenter(),
                Options())
        )
    }

    @Test
    fun createView_returnsFrameLayout() {
        val view = uut.view
        Java6Assertions.assertThat(CoordinatorLayout::class.java.isAssignableFrom(view.javaClass)).isTrue()
    }

    @Test
    fun createView_createsExternalComponent() {
        val view = uut.view
        Mockito.verify(componentCreator, Mockito.times(1)).create(activity as FragmentActivity, reactInstanceManager, ec.passProps)
        Java6Assertions.assertThat(view.childCount).isGreaterThan(0)
    }

    @Test
    fun `onViewWillAppear - should emit will appear and then appeared events`() {
        uut.onViewWillAppear()
        verifyOrder {
            EventEmitter.emitComponentAppearState(EventEmitter.ComponentWillAppear, uut)
            EventEmitter.emitComponentAppearState(EventEmitter.ComponentDidAppear, uut)
        }
        verify(inverse = true) { EventEmitter.emitComponentAppearState(EventEmitter.ComponentDidDisappear, uut) }
    }

    @Test
    fun onViewDisappear_disappearEventIsEmitted() {
        uut.onViewWillAppear()
        uut.onViewDisappear()
        verify { EventEmitter.emitComponentAppearState(EventEmitter.ComponentDidDisappear, uut) }
    }

    @Test
    fun registersInChildRegister() {
        uut.onViewWillAppear()
        Java6Assertions.assertThat(childRegistry.size()).isOne()
        uut.onViewDisappear()
        Java6Assertions.assertThat(childRegistry.size()).isZero()
    }

    private fun createExternalComponent(): ExternalComponent {
        val component = ExternalComponent()
        component.name = Text("fragmentComponent")
        component.passProps = JSONObject()
        return component
    }
}