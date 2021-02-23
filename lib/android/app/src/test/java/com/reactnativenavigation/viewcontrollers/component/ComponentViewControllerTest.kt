package com.reactnativenavigation.viewcontrollers.component

import android.app.Activity
import com.reactnativenavigation.BaseTest
import com.reactnativenavigation.TestUtils
import com.reactnativenavigation.mocks.TestComponentLayout
import com.reactnativenavigation.mocks.TestReactView
import com.reactnativenavigation.options.Options
import com.reactnativenavigation.options.params.Bool
import com.reactnativenavigation.react.events.EventEmitter
import com.reactnativenavigation.utils.StatusBarUtils
import com.reactnativenavigation.viewcontrollers.child.ChildControllersRegistry
import com.reactnativenavigation.viewcontrollers.stack.StackController
import com.reactnativenavigation.viewcontrollers.viewcontroller.Presenter
import com.reactnativenavigation.viewcontrollers.viewcontroller.ReactViewCreator
import com.reactnativenavigation.views.component.ComponentLayout
import io.mockk.verifyOrder
import org.assertj.core.api.Java6Assertions
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

class ComponentViewControllerTest : BaseTest() {
    private lateinit var uut: ComponentViewController
    private lateinit var view: ComponentLayout
    private lateinit var presenter: ComponentPresenter
    private lateinit var parent: StackController
    private lateinit var activity: Activity
    private val resolvedOptions = Options()
    override fun beforeEach() {
        super.beforeEach()
        activity = newActivity()
        StatusBarUtils.saveStatusBarHeight(63)
        view = Mockito.spy(TestComponentLayout(activity, TestReactView(activity)))
        parent = TestUtils.newStackController(activity).build()
        val presenter = Presenter(activity, Options())
        this.presenter = Mockito.spy(ComponentPresenter(Options.EMPTY))
        uut = Mockito.spy(object : ComponentViewController(activity, ChildControllersRegistry(), "componentId1", "componentName", ReactViewCreator { _: Activity?, _: String?, _: String? -> view }, Options(), presenter, this.presenter) {
            override fun resolveCurrentOptions(defaultOptions: Options): Options {
                // Hacky way to return the same instance of resolvedOptions without copying it.
                return resolvedOptions
                        .withDefaultOptions(uut.options)
                        .withDefaultOptions(defaultOptions)
            }
        })
        uut.parentController = parent
        parent.ensureViewIsCreated()
    }

    @Test
    fun setDefaultOptions() {
        val defaultOptions = Options()
        uut.setDefaultOptions(defaultOptions)
        Mockito.verify(presenter).setDefaultOptions(defaultOptions)
    }

    @Test
    fun applyOptions() {
        val options = Options()
        uut.applyOptions(options)
        Mockito.verify(view).applyOptions(options)
        Mockito.verify(presenter).applyOptions(view, resolvedOptions)
    }

    @Test
    fun createsViewFromComponentViewCreator() {
        Java6Assertions.assertThat(uut.view).isSameAs(view)
    }

    @Test
    fun componentViewDestroyedOnDestroy() {
        uut.ensureViewIsCreated()
        Mockito.verify(view, Mockito.times(0)).destroy()
        uut.onViewWillAppear()
        uut.destroy()
        Mockito.verify(view, Mockito.times(1)).destroy()
    }

    @Test
    fun lifecycleMethodsSent() {
        uut.ensureViewIsCreated();
        uut.onViewWillAppear();
        uut.onViewDidAppear()
        uut.onViewDisappear()
        verifyOrder {
            EventEmitter.emitComponentAppearState(EventEmitter.ComponentWillAppear, uut)
            EventEmitter.emitComponentAppearState(EventEmitter.ComponentDidAppear, uut)
            EventEmitter.emitComponentAppearState(EventEmitter.ComponentDidDisappear, uut)
        }
    }

    @Test
    fun onViewDidAppear_componentStartIsEmittedOnlyIfComponentIsNotAppeared() {
        uut.ensureViewIsCreated()
        idleMainLooper()
        uut.onViewDidAppear()
        uut.onViewDidAppear()
        uut.onViewDisappear()
        uut.onViewDidAppear()
        verifyOrder {
            EventEmitter.emitComponentAppearState(EventEmitter.ComponentDidAppear, uut)
            EventEmitter.emitComponentAppearState(EventEmitter.ComponentDidDisappear, uut)
            EventEmitter.emitComponentAppearState(EventEmitter.ComponentDidAppear, uut)
        }
    }

    @Test
    fun isViewShownOnlyIfComponentViewIsReady() {
        Java6Assertions.assertThat(uut.isViewShown).isFalse()
        uut.ensureViewIsCreated()
        Mockito.`when`(view.asView().isShown).thenReturn(true)
        Java6Assertions.assertThat(uut.isViewShown).isFalse()
        Mockito.`when`(view.isReady).thenReturn(true)
        Java6Assertions.assertThat(uut.isViewShown).isTrue()
    }

    @Test
    fun onNavigationButtonPressInvokedOnReactComponent() {
        uut.ensureViewIsCreated()
        uut.sendOnNavigationButtonPressed("btn1")
        Mockito.verify(view, Mockito.times(1)).sendOnNavigationButtonPressed("btn1")
    }

    @Test
    fun mergeOptions_emptyOptionsAreIgnored() {
        val spy = Mockito.spy(uut)
        spy.mergeOptions(Options.EMPTY)
        Mockito.verify(spy, Mockito.times(0)).performOnParentController(ArgumentMatchers.any())
    }

    @Test
    fun mergeOptions_delegatesToPresenterIfViewIsNotShown() {
        val options = Options()
        Java6Assertions.assertThat(uut.isViewShown).isFalse()
        uut.mergeOptions(options)
        Mockito.verifyZeroInteractions(presenter)
        Mockito.`when`(uut.isViewShown).thenReturn(true)
        uut.mergeOptions(options)
        Mockito.verify(presenter).mergeOptions(uut.view, options)
    }

    @Test
    fun applyTopInset_delegatesToPresenter() {
        addToParent(activity, uut)
        uut.applyTopInset()
        Mockito.verify(presenter).applyTopInsets(uut.view, uut.topInset)
    }

    @Test
    fun topInset_returnsStatusBarHeight() {
        uut.parentController = null
        Java6Assertions.assertThat(uut.topInset).isEqualTo(StatusBarUtils.getStatusBarHeight(activity))
    }

    @Test
    fun topInset_resolveWithParent() {
        Java6Assertions
                .assertThat(uut.topInset).isEqualTo(StatusBarUtils.getStatusBarHeight(activity) + parent.getTopInset(uut))
    }

    @Test
    fun topInset_drawBehind() {
        uut.options.statusBar.drawBehind = Bool(true)
        uut.options.topBar.drawBehind = Bool(true)
        Java6Assertions.assertThat(uut.topInset).isEqualTo(0)
    }

    @Test
    fun topInset_drawBehind_defaultOptions() {
        val defaultOptions = Options()
        defaultOptions.statusBar.drawBehind = Bool(true)
        uut.setDefaultOptions(defaultOptions)
        uut.options.topBar.drawBehind = Bool(true)
        Java6Assertions.assertThat(uut.topInset).isEqualTo(0)
    }

    @Test
    fun applyBottomInset_delegatesToPresenter() {
        addToParent(activity, uut)
        uut.applyBottomInset()
        Mockito.verify(presenter).applyBottomInset(uut.view, uut.bottomInset)
    }
}