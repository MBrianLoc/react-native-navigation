package com.reactnativenavigation

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.reactnativenavigation.options.params.Bool
import com.reactnativenavigation.react.events.EventEmitter
import com.reactnativenavigation.utils.CollectionUtils
import com.reactnativenavigation.utils.ViewUtils
import com.reactnativenavigation.viewcontrollers.viewcontroller.ViewController
import io.mockk.every
import io.mockk.mockkObject
import org.assertj.core.api.Java6Assertions
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = TestApplication::class)
abstract class BaseTest {
    private val handler = Handler(Looper.getMainLooper())
    private val shadowMainLooper = Shadows.shadowOf(Looper.getMainLooper())

    @Before
    open fun beforeEach() {
        mockkObject(EventEmitter)
        every { EventEmitter.emitComponentAppearState(any(), any()) } returns Unit
        every { EventEmitter.emitBottomTabPressed(any()) } returns Unit
        every { EventEmitter.emitBottomTabSelected(any(), any()) } returns Unit
        every { EventEmitter.emitCommandCompleted(any(), any(), any()) } returns Unit
        every { EventEmitter.emitModalDismissed(any(), any(), any()) } returns Unit
        every { EventEmitter.emitOnNavigationButtonPressed(any(), any()) } returns Unit
        every { EventEmitter.emitScreenPoppedEvent(any()) } returns Unit
    }

    @After
    @CallSuper
    fun afterEach() {
        idleMainLooper()
    }

    fun newActivity(): Activity {
        return Robolectric.setupActivity(AppCompatActivity::class.java)
    }

    fun <T : AppCompatActivity?> newActivityController(clazz: Class<T>?): ActivityController<T> {
        return Robolectric.buildActivity(clazz)
    }

    fun assertIsChild(parent: ViewGroup?, vararg children: ViewController<*>?) {
        CollectionUtils.forEach<ViewController<*>>(Arrays.asList(*children)) { c: ViewController<*> -> assertIsChild(parent, c.view) }
    }

    fun assertIsChild(parent: ViewGroup?, child: View?) {
        Java6Assertions.assertThat(parent).isNotNull()
        Java6Assertions.assertThat(child).isNotNull()
        Java6Assertions.assertThat(ViewUtils.isChildOf(parent, child)).isTrue()
    }

    fun assertNotChildOf(parent: ViewGroup?, vararg children: ViewController<*>?) {
        CollectionUtils.forEach<ViewController<*>>(Arrays.asList(*children)) { c: ViewController<*> -> assertNotChildOf(parent, c.view) }
    }

    fun assertNotChildOf(parent: ViewGroup?, child: View?) {
        Java6Assertions.assertThat(parent).isNotNull()
        Java6Assertions.assertThat(child).isNotNull()
        Java6Assertions.assertThat(ViewUtils.isChildOf(parent, child)).isFalse()
    }

    fun assertMatchParent(view: View) {
        Java6Assertions.assertThat(view.layoutParams.width).isEqualTo(ViewGroup.LayoutParams.MATCH_PARENT)
        Java6Assertions.assertThat(view.layoutParams.height).isEqualTo(ViewGroup.LayoutParams.MATCH_PARENT)
    }

    protected fun disablePushAnimation(vararg controllers: ViewController<*>) {
        for (controller in controllers) {
            controller.options.animations.push.enabled = Bool(false)
        }
    }

    protected fun disablePopAnimation(vararg controllers: ViewController<*>) {
        for (controller in controllers) {
            controller.options.animations.pop.enabled = Bool(false)
        }
    }

    protected fun disableModalAnimations(vararg modals: ViewController<*>) {
        disableShowModalAnimation(*modals)
        disableDismissModalAnimation(*modals)
    }

    protected fun disableShowModalAnimation(vararg modals: ViewController<*>) {
        for (modal in modals) {
            modal.options.animations.showModal.toggle(Bool(false))
        }
    }

    protected fun disableDismissModalAnimation(vararg modals: ViewController<*>) {
        for (modal in modals) {
            modal.options.animations.dismissModal.toggle(Bool(false))
        }
    }

    protected fun dispatchPreDraw(view: View) {
        view.viewTreeObserver.dispatchOnPreDraw()
    }

    protected fun dispatchOnGlobalLayout(view: View) {
        view.viewTreeObserver.dispatchOnGlobalLayout()
    }

    protected fun addToParent(context: Context?, vararg controllers: ViewController<*>) {
        for (controller in controllers) {
            CoordinatorLayout(context!!).addView(controller.view)
        }
    }

    protected fun mockView(activity: Activity): View {
        val mock = Mockito.mock(View::class.java)
        Mockito.`when`(mock.context).thenReturn(activity)
        return mock
    }

    protected fun assertVisible(view: View) {
        Java6Assertions.assertThat(view.visibility).isEqualTo(View.VISIBLE)
    }

    protected fun assertGone(view: View) {
        Java6Assertions.assertThat(view.visibility).isEqualTo(View.GONE)
    }

    protected fun post(runnable: Runnable?) {
        handler.post(runnable)
    }

    protected fun idleMainLooper() {
        shadowMainLooper.idle()
    }
}