package com.reactnativenavigation.viewcontrollers.modal

import android.app.Activity
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.reactnativenavigation.BaseTest
import com.reactnativenavigation.TestUtils
import com.reactnativenavigation.mocks.SimpleViewController
import com.reactnativenavigation.options.Options
import com.reactnativenavigation.react.CommandListener
import com.reactnativenavigation.react.CommandListenerAdapter
import com.reactnativenavigation.react.events.EventEmitter
import com.reactnativenavigation.viewcontrollers.child.ChildControllersRegistry
import com.reactnativenavigation.viewcontrollers.stack.StackController
import com.reactnativenavigation.viewcontrollers.viewcontroller.ViewController
import io.mockk.verifyOrder
import org.assertj.core.api.Java6Assertions
import org.junit.Test
import org.mockito.Mockito
import java.util.*

class ModalStackTest : BaseTest() {
    private lateinit var uut: ModalStack
    private lateinit var modal1: ViewController<*>
    private lateinit var modal2: ViewController<*>
    private lateinit var modal3: ViewController<*>
    private lateinit var modal4: ViewController<*>
    private lateinit var stack: StackController
    private lateinit var activity: Activity
    private lateinit var childRegistry: ChildControllersRegistry
    private lateinit var presenter: ModalPresenter
    private lateinit var animator: ModalAnimator
    private lateinit var root: ViewController<*>
    private lateinit var emitter: EventEmitter
    override fun beforeEach() {
        super.beforeEach()
        activity = newActivity()
        childRegistry = ChildControllersRegistry()
        root = SimpleViewController(activity, childRegistry, "root", Options())
        val rootLayout = FrameLayout(activity)
        val modalsLayout = CoordinatorLayout(activity)
        val contentLayout = FrameLayout(activity)
        contentLayout.addView(rootLayout)
        contentLayout.addView(modalsLayout)
        activity.setContentView(contentLayout)
        animator = Mockito.spy(ModalAnimatorMock(activity))
        presenter = Mockito.spy(ModalPresenter(animator))
        uut = ModalStack(activity, presenter)
        uut.setModalsLayout(modalsLayout)
        uut.setRootLayout(rootLayout)
        emitter = Mockito.mock(EventEmitter::class.java)
        uut.setEventEmitter(emitter)
        modal1 = Mockito.spy(SimpleViewController(activity, childRegistry, MODAL_ID_1, Options()))
        modal2 = Mockito.spy(SimpleViewController(activity, childRegistry, MODAL_ID_2, Options()))
        modal3 = Mockito.spy(SimpleViewController(activity, childRegistry, MODAL_ID_3, Options()))
        modal4 = Mockito.spy(SimpleViewController(activity, childRegistry, MODAL_ID_4, Options()))
        stack = TestUtils.newStackController(activity)
                .setChildren(modal4)
                .build()
    }

    @Test
    fun modalRefIsSaved() {
        disableShowModalAnimation(modal1)
        val listener: CommandListener = Mockito.spy(CommandListenerAdapter())
        uut.showModal(modal1, root, listener)
        Mockito.verify(listener, Mockito.times(1)).onSuccess(modal1.id)
        Java6Assertions.assertThat(findModal(MODAL_ID_1)).isNotNull()
    }

    @Test
    fun showModal() {
        val listener: CommandListener = Mockito.spy(CommandListenerAdapter())
        uut.showModal(modal1, root, listener)
        Mockito.verify(listener).onSuccess(modal1.id)
        Mockito.verify(modal1).onViewDidAppear()
        Java6Assertions.assertThat(uut.size()).isOne()
        Mockito.verify(presenter).showModal(eq(modal1), eq(root), any())
        Java6Assertions.assertThat(findModal(MODAL_ID_1)).isNotNull()
    }

    @Test
    fun `show modal - should send lifecycle events in the right order`() {
        val listener: CommandListener = Mockito.spy(CommandListenerAdapter())
        uut.showModal(modal1, root, listener)
        uut.showModal(modal2, root, listener)
        idleMainLooper()
        verifyOrder {
            EventEmitter.emitComponentAppearState(EventEmitter.ComponentWillAppear, modal1)
            EventEmitter.emitComponentAppearState(EventEmitter.ComponentDidAppear, modal1)

            EventEmitter.emitComponentAppearState(EventEmitter.ComponentWillAppear, modal2)
            EventEmitter.emitComponentAppearState(EventEmitter.ComponentDidAppear, modal2)
            EventEmitter.emitComponentAppearState(EventEmitter.ComponentDidDisappear, modal1)

        }
    }

    @Test
    fun `dismiss modal - should send lifecycle events in the right order`() {
        val listener: CommandListener = Mockito.spy(CommandListenerAdapter())
        uut.showModal(modal1, root, listener)
        uut.showModal(modal2, root, listener)
        idleMainLooper()
        uut.dismissModal(modal2.id, root, listener)
        idleMainLooper()
        verifyOrder {
            EventEmitter.emitComponentAppearState(EventEmitter.ComponentWillAppear, modal1)
            EventEmitter.emitComponentAppearState(EventEmitter.ComponentDidAppear, modal1)

            EventEmitter.emitComponentAppearState(EventEmitter.ComponentWillAppear, modal2)
            EventEmitter.emitComponentAppearState(EventEmitter.ComponentDidAppear, modal2)
            EventEmitter.emitComponentAppearState(EventEmitter.ComponentDidDisappear, modal1)

            EventEmitter.emitComponentAppearState(EventEmitter.ComponentDidDisappear, modal2)
            EventEmitter.emitComponentAppearState(EventEmitter.ComponentWillAppear, modal1)
            EventEmitter.emitComponentAppearState(EventEmitter.ComponentDidAppear, modal1)
        }
    }

    @Test
    fun showModal_canShowModalBeforeRootIsSet() {
        val listener: CommandListener = Mockito.spy(CommandListenerAdapter())
        uut.showModal(modal1, null, listener)
        Mockito.verify(listener).onSuccess(modal1.id)
    }

    @Test
    fun dismissModal() {
        uut.showModal(modal1, root, CommandListenerAdapter())
        val listener: CommandListener = Mockito.spy(CommandListenerAdapter())
        uut.dismissModal(modal1.id, root, listener)
        Java6Assertions.assertThat(findModal(modal1.id)).isNull()
        Mockito.verify(presenter).dismissModal(eq(modal1), eq(root), eq(root), any())
        Mockito.verify(listener).onSuccess(modal1.id)
    }

    @Test
    fun dismissModal_listenerAndEmitterAreInvokedWithRootViewControllerId() {
        uut.showModal(stack, root, CommandListenerAdapter())
        val listener: CommandListener = Mockito.spy(CommandListenerAdapter())
        uut.dismissModal(modal4.id, root, listener)
        Mockito.verify(listener).onSuccess(stack.id)
        Mockito.verify(emitter).emitModalDismissed(stack.id, modal4.currentComponentName, 1)
    }

    @Test
    fun dismissModal_rejectIfModalNotFound() {
        val listener: CommandListener = Mockito.spy(CommandListenerAdapter())
        val onModalWillDismiss = Mockito.spy(Runnable { })
        uut.dismissModal(MODAL_ID_1, root, listener)
        Mockito.verify(onModalWillDismiss, Mockito.times(0)).run()
        Mockito.verify(listener, Mockito.times(1)).onError(any())
        Mockito.verifyZeroInteractions(listener)
    }

    @Test
    fun dismissModal_dismissDeepModal() {
        disableShowModalAnimation(modal1, modal2, modal3)
        disableDismissModalAnimation(modal1, modal2, modal3)
        uut.showModal(modal1, root, CommandListenerAdapter())
        uut.showModal(modal2, root, CommandListenerAdapter())
        uut.showModal(modal3, root, CommandListenerAdapter())
        Java6Assertions.assertThat(root.view.parent).isNull()
        uut.dismissModal(modal1.id, root, CommandListenerAdapter())
        Java6Assertions.assertThat(root.view.parent).isNull()
        uut.dismissModal(modal3.id, root, CommandListenerAdapter())
        uut.dismissModal(modal2.id, root, CommandListenerAdapter())
        Java6Assertions.assertThat(root.view.parent).isNotNull()
        Java6Assertions.assertThat(root.view.isShown).isTrue()
    }

    @Test
    fun dismissAllModals() {
        uut.showModal(modal1, root, CommandListenerAdapter())
        uut.showModal(modal2, root, CommandListenerAdapter())
        val listener: CommandListener = Mockito.spy(object : CommandListenerAdapter() {
            override fun onSuccess(childId: String) {
                Java6Assertions.assertThat(findModal(modal1.id)).isNull()
                Java6Assertions.assertThat(findModal(modal2.id)).isNull()
                Java6Assertions.assertThat(uut.isEmpty).isTrue()
            }
        })
        uut.dismissAllModals(root, Options.EMPTY, listener)
        Mockito.verify(listener, Mockito.times(1)).onSuccess(any())
        Mockito.verifyZeroInteractions(listener)
    }

    @Test
    fun dismissAllModal_resolvesPromiseSuccessfullyWhenCalledBeforeRootIsSet() {
        val spy = Mockito.spy(CommandListenerAdapter())
        uut.dismissAllModals(null, Options.EMPTY, spy)
        Mockito.verify(spy).onSuccess("")
    }

    @Test
    fun dismissAllModals_resolveSuccessfullyIfEmpty() {
        val spy: CommandListener = Mockito.spy(CommandListenerAdapter())
        uut.dismissAllModals(root, Options.EMPTY, spy)
        Mockito.verify(spy, Mockito.times(1)).onSuccess(root.id)
    }

    @Test
    fun dismissAllModals_optionsAreMergedOnTopModal() {
        uut.showModal(modal1, root, CommandListenerAdapter())
        uut.showModal(modal2, root, CommandListenerAdapter())
        uut.showModal(modal3, root, CommandListenerAdapter())
        val mergeOptions = Options()
        uut.dismissAllModals(root, mergeOptions, CommandListenerAdapter())
        Mockito.verify(modal3).mergeOptions(mergeOptions)
        Mockito.verify(modal1, Mockito.times(0)).mergeOptions(mergeOptions)
        Mockito.verify(modal2, Mockito.times(0)).mergeOptions(mergeOptions)
    }

    @Test
    fun dismissAllModals_onlyTopModalIsAnimated() {
        modal2 = Mockito.spy(modal2)
        val defaultOptions = Options()
        uut.setDefaultOptions(defaultOptions)
        val resolvedOptions = Options()
        Mockito.`when`(modal2.resolveCurrentOptions(defaultOptions)).then { resolvedOptions }
        uut.showModal(modal1, root, CommandListenerAdapter())
        uut.showModal(modal2, root, CommandListenerAdapter())
        val listener: CommandListener = Mockito.spy(CommandListenerAdapter())
        uut.dismissAllModals(root, Options.EMPTY, listener)
        Mockito.verify(presenter).dismissModal(eq(modal2), eq(root), eq(root), any())
        Mockito.verify(listener).onSuccess(modal2.id)
        Mockito.verify(animator, Mockito.never()).dismiss(eq(modal1), eq(modal2), eq(modal1.options.animations.dismissModal), any())
        Mockito.verify(animator).dismiss(eq(root), eq(modal2), eq(resolvedOptions.animations.dismissModal), any())
        Java6Assertions.assertThat(uut.size()).isEqualTo(0)
    }

    @Test
    fun dismissAllModals_bottomModalsAreDestroyed() {
        disableModalAnimations(modal1, modal2)
        uut.showModal(modal1, root, CommandListenerAdapter())
        idleMainLooper()
        uut.showModal(modal2, root, CommandListenerAdapter())
        uut.dismissAllModals(root, Options.EMPTY, CommandListenerAdapter())
        Mockito.verify(modal1).destroy()
        Mockito.verify(modal1).onViewDisappear()
        Java6Assertions.assertThat(uut.size()).isEqualTo(0)
    }

    @Test
    fun isEmpty() {
        Java6Assertions.assertThat(uut.isEmpty).isTrue()
        uut.showModal(modal1, root, CommandListenerAdapter())
        Java6Assertions.assertThat(uut.isEmpty).isFalse()
        uut.dismissAllModals(root, Options.EMPTY, CommandListenerAdapter())
        Java6Assertions.assertThat(uut.isEmpty).isTrue()
    }

    @Test
    fun peek() {
        Java6Assertions.assertThat(uut.isEmpty).isTrue()
        Java6Assertions.assertThatThrownBy { uut.peek() }.isInstanceOf(EmptyStackException::class.java)
        uut.showModal(modal1, root, object : CommandListenerAdapter() {
            override fun onSuccess(childId: String) {
                Java6Assertions.assertThat(uut.peek()).isEqualTo(modal1)
            }
        })
    }

    @Test
    fun onDismiss_onViewAppearedInvokedOnPreviousModal() {
        disableShowModalAnimation(modal1, modal2)
        uut.showModal(modal1, root, CommandListenerAdapter())
        idleMainLooper()
        uut.showModal(modal2, root, CommandListenerAdapter())
        idleMainLooper()
        uut.dismissModal(modal2.id, root, CommandListenerAdapter())
        idleMainLooper()
        Mockito.verify(modal1, Mockito.times(2)).onViewWillAppear()
    }

    @Test
    fun onDismiss_dismissModalInTheMiddleOfStack() {
        disableShowModalAnimation(modal1, modal2, modal3)
        disableDismissModalAnimation(modal1, modal2, modal3)
        uut.showModal(modal1, root, CommandListenerAdapter())
        uut.showModal(modal2, root, CommandListenerAdapter())
        idleMainLooper()
        uut.showModal(modal3, root, CommandListenerAdapter())
        uut.dismissModal(modal2.id, root, CommandListenerAdapter())
        idleMainLooper()
        Java6Assertions.assertThat(uut.size()).isEqualTo(2)
        Mockito.verify(modal2).onViewDisappear()
        Mockito.verify(modal2).destroy()
        Java6Assertions.assertThat(modal1.view.parent).isNull()
    }

    @Test
    fun handleBack_doesNothingIfModalStackIsEmpty() {
        Java6Assertions.assertThat(uut.isEmpty).isTrue()
        Java6Assertions.assertThat(uut.handleBack(CommandListenerAdapter(), root)).isFalse()
    }

    @Test
    fun handleBack_dismissModal() {
        disableDismissModalAnimation(modal1)
        uut.showModal(modal1, root, CommandListenerAdapter())
        idleMainLooper()
        Java6Assertions.assertThat(uut.handleBack(CommandListenerAdapter(), root)).isTrue()
        Mockito.verify(modal1).onViewDisappear()
    }

    @Test
    fun handleBack_ViewControllerTakesPrecedenceOverModal() {
        val backHandlingModal: ViewController<*> = Mockito.spy(object : SimpleViewController(activity, childRegistry, "stack", Options()) {
            override fun handleBack(listener: CommandListener): Boolean {
                return true
            }
        })
        uut.showModal(backHandlingModal, root, CommandListenerAdapter())
        root.view.viewTreeObserver.dispatchOnGlobalLayout()
        Java6Assertions.assertThat(uut.handleBack(CommandListenerAdapter(), any())).isTrue()
        Mockito.verify(backHandlingModal, Mockito.times(1)).handleBack(any())
        Mockito.verify(backHandlingModal, Mockito.times(0)).onViewDisappear()
    }

    @Test
    fun setDefaultOptions() {
        val defaultOptions = Options()
        uut.setDefaultOptions(defaultOptions)
        Mockito.verify(presenter).setDefaultOptions(defaultOptions)
    }

    @Test
    fun destroy() {
        showModalsWithoutAnimation(modal1, modal2)
        uut.destroy()
        Mockito.verify(modal1).destroy()
        Mockito.verify(modal2).destroy()
    }

    private fun findModal(id: String): ViewController<*>? {
        return uut.findControllerById(id)
    }

    private fun showModalsWithoutAnimation(vararg modals: ViewController<*>) {
        for (modal in modals) {
            showModalWithoutAnimation(modal)
        }
    }

    private fun showModalWithoutAnimation(modal: ViewController<*>) {
        disableShowModalAnimation(modal)
        uut.showModal(modal, root, CommandListenerAdapter())
    }

    companion object {
        private const val MODAL_ID_1 = "modalId1"
        private const val MODAL_ID_2 = "modalId2"
        private const val MODAL_ID_3 = "modalId3"
        private const val MODAL_ID_4 = "modalId4"
    }
}