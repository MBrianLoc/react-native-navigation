package com.reactnativenavigation.viewcontrollers.toptabs

import android.app.Activity
import android.view.ViewGroup
import com.reactnativenavigation.BaseTest
import com.reactnativenavigation.TestUtils
import com.reactnativenavigation.mocks.SimpleViewController
import com.reactnativenavigation.mocks.TestComponentViewCreator
import com.reactnativenavigation.options.Options
import com.reactnativenavigation.options.params.Bool
import com.reactnativenavigation.options.params.Text
import com.reactnativenavigation.react.CommandListenerAdapter
import com.reactnativenavigation.react.events.EventEmitter
import com.reactnativenavigation.utils.ViewHelper
import com.reactnativenavigation.viewcontrollers.child.ChildControllersRegistry
import com.reactnativenavigation.viewcontrollers.component.ComponentPresenter
import com.reactnativenavigation.viewcontrollers.component.ComponentViewController
import com.reactnativenavigation.viewcontrollers.stack.StackController
import com.reactnativenavigation.viewcontrollers.viewcontroller.IReactView
import com.reactnativenavigation.viewcontrollers.viewcontroller.Presenter
import com.reactnativenavigation.viewcontrollers.viewcontroller.ViewController
import com.reactnativenavigation.views.toptabs.TopTabsLayoutCreator
import com.reactnativenavigation.views.toptabs.TopTabsViewPager
import io.mockk.verify
import io.mockk.verifyOrder
import org.assertj.core.api.Java6Assertions
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import java.util.*
import java.util.function.Consumer

class TopTabsViewControllerTest : BaseTest() {
    private lateinit var stack: StackController
    private lateinit var uut: TopTabsController
    private var tabControllers: List<ViewController<*>> = ArrayList(SIZE)
    private val options = Options()
    private lateinit var topTabsLayout: TopTabsViewPager
    private lateinit var activity: Activity
    private lateinit var childRegistry: ChildControllersRegistry
    override fun beforeEach() {
        super.beforeEach()
        activity = newActivity()
        childRegistry = ChildControllersRegistry()
        val tabOptions: List<Options> = createOptions()
        tabControllers = createTabsControllers(activity, tabOptions)
        topTabsLayout = Mockito.spy(TopTabsViewPager(activity, tabControllers, TopTabsAdapter(tabControllers)))
        val layoutCreator = Mockito.mock(TopTabsLayoutCreator::class.java)
        Mockito.`when`(layoutCreator.create()).thenReturn(topTabsLayout)
        val presenter = Presenter(activity, Options())
        options.topBar.buttons.back.visible = Bool(false)
        uut = Mockito.spy(TopTabsController(activity, childRegistry, "componentId", tabControllers, layoutCreator, options, presenter))
        tabControllers.forEach(Consumer { viewController: ViewController<*> -> viewController.setParentController(uut) })
        stack = Mockito.spy(TestUtils.newStackController(activity).build())
        stack.ensureViewIsCreated()
    }

    private fun createOptions(): ArrayList<Options> {
        val result = ArrayList<Options>()
        for (i in 0 until SIZE) {
            val options = Options()
            options.topTabOptions.title = Text("Tab $i")
            options.topBar.title.text = Text(createTabTopBarTitle(i))
            result.add(options)
        }
        return result
    }

    private fun createTabsControllers(activity: Activity?, tabOptions: List<Options>): List<ViewController<*>> {
        val tabControllers: MutableList<ViewController<*>> = ArrayList(SIZE)
        for (i in 0 until SIZE) {
            val viewController = ComponentViewController(
                    activity,
                    childRegistry,
                    "idTab$i",
                    "theComponentName",
                    TestComponentViewCreator(),
                    tabOptions[i],
                    Presenter(activity, Options()),
                    ComponentPresenter(Options.EMPTY)
            )
            tabControllers.add(Mockito.spy(viewController))
        }
        return tabControllers
    }

    @Test
    fun createsViewFromComponentViewCreator() {
        uut.ensureViewIsCreated()
        for (i in 0 until SIZE) {
            Mockito.verify(tabControllers[i], Mockito.times(1)).createView()
        }
    }

    @Test
    fun componentViewDestroyedOnDestroy() {
        uut.ensureViewIsCreated()
        val topTabs = uut.view
        for (i in 0 until SIZE) {
            Mockito.verify(tab(topTabs, i), Mockito.times(0)).destroy()
        }
        uut.destroy()
        for (tabController in tabControllers) {
            Mockito.verify(tabController, Mockito.times(1)).destroy()
        }
    }

    @Test
    fun lifecycleMethodsSentWhenSelectedTabChanges() {
        stack.ensureViewIsCreated()
        uut.ensureViewIsCreated()
        tabControllers[0].ensureViewIsCreated()
        tabControllers[1].ensureViewIsCreated()
        val initialTab = tabControllers[0]
        val selectedTab = tabControllers[1]
        uut.switchToTab(1)
        verifyOrder {
            EventEmitter.emitComponentAppearState(EventEmitter.ComponentDidDisappear, initialTab)
            EventEmitter.emitComponentAppearState(EventEmitter.ComponentWillAppear, selectedTab)
            EventEmitter.emitComponentAppearState(EventEmitter.ComponentDidAppear, selectedTab)
        }
        verify(inverse = true) { EventEmitter.emitComponentAppearState(EventEmitter.ComponentDidDisappear, selectedTab) }
    }

    @Test
    fun lifecycleMethodsSentWhenSelectedPreviouslySelectedTab() {
        stack.ensureViewIsCreated()
        uut.ensureViewIsCreated()
        uut.onViewDidAppear()
        uut.switchToTab(1)
        uut.switchToTab(0)
        verifyOrder {
            //initial tab appeared
            EventEmitter.emitComponentAppearState(EventEmitter.ComponentDidAppear, tabControllers[0])

            //switch to 1
            EventEmitter.emitComponentAppearState(EventEmitter.ComponentDidDisappear, tabControllers[0])
            EventEmitter.emitComponentAppearState(EventEmitter.ComponentWillAppear, tabControllers[1])
            EventEmitter.emitComponentAppearState(EventEmitter.ComponentDidAppear, tabControllers[1])

            //switch to 0
            EventEmitter.emitComponentAppearState(EventEmitter.ComponentDidDisappear, tabControllers[1])
            EventEmitter.emitComponentAppearState(EventEmitter.ComponentWillAppear, tabControllers[0])
            EventEmitter.emitComponentAppearState(EventEmitter.ComponentDidAppear, tabControllers[0])
        }
    }

    @Test
    fun setOptionsOfInitialTab() {
        stack.ensureViewIsCreated()
        uut.ensureViewIsCreated()
        uut.onViewWillAppear()
        Mockito.verify(tabControllers[0], Mockito.times(1)).onViewWillAppear()
        Mockito.verify(tabControllers[1], Mockito.times(0)).onViewWillAppear()
        val comp = tabControllers[0]
        Mockito.verify(uut, Mockito.times(1)).applyChildOptions(ArgumentMatchers.any(Options::class.java), ArgumentMatchers.eq(comp))
    }

    @Test
    fun setOptionsWhenTabChanges() {
        stack.ensureViewIsCreated()
        uut.ensureViewIsCreated()
        tabControllers[0].ensureViewIsCreated()
        tabControllers[1].ensureViewIsCreated()
        uut.onViewWillAppear()
        var currentTab = tab(0)
        Mockito.verify(uut, Mockito.times(1)).applyChildOptions(ArgumentMatchers.any(Options::class.java), ArgumentMatchers.eq(currentTab))
        Java6Assertions.assertThat(uut.options.topBar.title.text.get()).isEqualTo(createTabTopBarTitle(0))
        uut.switchToTab(1)
        currentTab = tab(1)
        Mockito.verify(uut, Mockito.times(1)).applyChildOptions(ArgumentMatchers.any(Options::class.java), ArgumentMatchers.eq(currentTab))
        Java6Assertions.assertThat(uut.options.topBar.title.text.get()).isEqualTo(createTabTopBarTitle(1))
        uut.switchToTab(0)
        currentTab = tab(0)
        Mockito.verify(uut, Mockito.times(2)).applyChildOptions(ArgumentMatchers.any(Options::class.java), ArgumentMatchers.eq(currentTab))
        Java6Assertions.assertThat(uut.options.topBar.title.text.get()).isEqualTo(createTabTopBarTitle(0))
    }

    @Test
    fun appliesOptionsOnLayoutWhenVisible() {
        tabControllers[0].ensureViewIsCreated()
        stack.ensureViewIsCreated()
        uut.ensureViewIsCreated()
        uut.onViewWillAppear()
        Mockito.verify(topTabsLayout, Mockito.times(1)).applyOptions(ArgumentMatchers.any(Options::class.java))
    }

    @Test
    fun applyOptions_tabsAreRemovedAfterViewDisappears() {
        val stackController = TestUtils.newStackController(activity).build()
        stackController.ensureViewIsCreated()
        val first: ViewController<*> = SimpleViewController(activity, childRegistry, "first", Options.EMPTY)
        disablePushAnimation(first, uut)
        stackController.push(first, CommandListenerAdapter())
        stackController.push(uut, CommandListenerAdapter())
        uut.onViewWillAppear()
        Java6Assertions.assertThat(ViewHelper.isVisible(stackController.topBar.topTabs)).isTrue()
        disablePopAnimation(uut)
        stackController.pop(Options.EMPTY, CommandListenerAdapter())
        first.onViewWillAppear()
        Java6Assertions.assertThat(ViewHelper.isVisible(stackController.topBar.topTabs)).isFalse()
    }

    @Test
    fun onNavigationButtonPressInvokedOnCurrentTab() {
        uut.ensureViewIsCreated()
        uut.onViewWillAppear()
        uut.switchToTab(1)
        uut.sendOnNavigationButtonPressed("btn1")
        Mockito.verify(tabControllers[1], Mockito.times(1)).sendOnNavigationButtonPressed("btn1")
    }

    private fun tab(topTabs: TopTabsViewPager, index: Int): IReactView {
        return (topTabs.getChildAt(index) as ViewGroup).getChildAt(0) as IReactView
    }

    private fun createTabTopBarTitle(i: Int): String {
        return "Title $i"
    }

    private fun tab(index: Int): ViewController<*> {
        return tabControllers[index]
    }

    companion object {
        private const val SIZE = 2
    }
}