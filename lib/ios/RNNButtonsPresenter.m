#import "RNNButtonsPresenter.h"
#import "NSArray+utils.h"
#import "RNNButtonBuilder.h"

@interface RNNButtonsPresenter ()
@property(weak, nonatomic) UIViewController<RNNLayoutProtocol> *viewController;
@property(strong, nonatomic) RNNReactComponentRegistry *componentRegistry;
@property(strong, nonatomic) RNNEventEmitter *eventEmitter;
@property(strong, nonatomic) RNNButtonBuilder *buttonBuilder;
@end

@implementation RNNButtonsPresenter

- (instancetype)initWithComponentRegistry:(RNNReactComponentRegistry *)componentRegistry
                             eventEmitter:(RNNEventEmitter *)eventEmitter {
    self = [super init];
    self.componentRegistry = componentRegistry;
    self.buttonBuilder =
        [[RNNButtonBuilder alloc] initWithComponentRegistry:self.componentRegistry];
    self.eventEmitter = eventEmitter;
    return self;
}

- (void)bindViewController:(UIViewController<RNNLayoutProtocol> *)viewController {
    _viewController = viewController;
}

- (void)applyLeftButtons:(NSArray<RNNButtonOptions *> *)leftButtons
            defaultColor:(Color *)defaultColor
    defaultDisabledColor:(Color *)defaultDisabledColor {
    [self setButtons:leftButtons
                        side:@"left"
                    animated:NO
                defaultColor:defaultColor
        defaultDisabledColor:defaultDisabledColor];
}

- (void)applyRightButtons:(NSArray<RNNButtonOptions *> *)rightButtons
             defaultColor:(Color *)defaultColor
     defaultDisabledColor:(Color *)defaultDisabledColor {
    [self setButtons:rightButtons
                        side:@"right"
                    animated:NO
                defaultColor:defaultColor
        defaultDisabledColor:defaultDisabledColor];
}

- (void)applyLeftButtonsColor:(UIColor *)color {
    for (RNNUIBarButtonItem *button in self.viewController.navigationItem.leftBarButtonItems) {
        [button applyColor:color];
    }
}

- (void)applyRightButtonsColor:(UIColor *)color {
    for (RNNUIBarButtonItem *button in self.viewController.navigationItem.rightBarButtonItems) {
        [button applyColor:color];
    }
}

- (void)setButtons:(NSArray<RNNButtonOptions *> *)buttons
                    side:(NSString *)side
                animated:(BOOL)animated
            defaultColor:(Color *)defaultColor
    defaultDisabledColor:(Color *)defaultDisabledColor {
    NSMutableArray *barButtonItems = [NSMutableArray new];
    for (RNNButtonOptions *button in buttons) {
        RNNUIBarButtonItem *barButtonItem = [_buttonBuilder
                        build:[button withDefaultColor:defaultColor
                                         disabledColor:defaultDisabledColor]
            parentComponentId:_viewController.layoutInfo.componentId
                      onPress:^(NSString *buttonId) {
                        [self.eventEmitter
                            sendOnNavigationButtonPressed:self.viewController.layoutInfo.componentId
                                                 buttonId:buttonId];
                      }];
        if (barButtonItem)
            [barButtonItems addObject:barButtonItem];
    }

    if ([side isEqualToString:@"left"]) {
        [self clearPreviousButtonViews:barButtonItems
                            oldButtons:self.viewController.navigationItem.leftBarButtonItems];
        [self.viewController.navigationItem setLeftBarButtonItems:barButtonItems animated:animated];
    }

    if ([side isEqualToString:@"right"]) {
        [self clearPreviousButtonViews:barButtonItems
                            oldButtons:self.viewController.navigationItem.rightBarButtonItems];
        [self.viewController.navigationItem setRightBarButtonItems:barButtonItems
                                                          animated:animated];
    }

    [self notifyButtonsWillAppear:barButtonItems];
    [self notifyButtonsDidAppear:barButtonItems];
}

- (NSArray *)currentButtons {
    NSMutableArray *currentButtons = [NSMutableArray new];
    [currentButtons addObjectsFromArray:self.viewController.navigationItem.leftBarButtonItems];
    [currentButtons addObjectsFromArray:self.viewController.navigationItem.rightBarButtonItems];
    return currentButtons;
}

- (void)componentWillAppear {
    for (UIBarButtonItem *barButtonItem in [self currentButtons]) {
        if ([self isRNNUIBarButton:barButtonItem]) {
            [(RNNUIBarButtonItem *)barButtonItem notifyWillAppear];
        }
    }
}

- (void)componentDidAppear {
    for (UIBarButtonItem *barButtonItem in [self currentButtons]) {
        if ([self isRNNUIBarButton:barButtonItem]) {
            [(RNNUIBarButtonItem *)barButtonItem notifyDidAppear];
        }
    }
}

- (void)componentDidDisappear {
    for (UIBarButtonItem *barButtonItem in [self currentButtons]) {
        if ([self isRNNUIBarButton:barButtonItem]) {
            [(RNNUIBarButtonItem *)barButtonItem notifyDidDisappear];
        }
    }
}

- (void)notifyButtonsWillAppear:(NSArray *)barButtonItems {
    for (UIBarButtonItem *barButtonItem in barButtonItems) {
        if ([self isRNNUIBarButton:barButtonItem]) {
            [(RNNUIBarButtonItem *)barButtonItem notifyWillAppear];
        }
    }
}

- (void)notifyButtonsDidAppear:(NSArray *)barButtonItems {
    for (UIBarButtonItem *barButtonItem in barButtonItems) {
        if ([self isRNNUIBarButton:barButtonItem]) {
            [(RNNUIBarButtonItem *)barButtonItem notifyDidAppear];
        }
    }
}

- (BOOL)isRNNUIBarButton:(UIBarButtonItem *)barButtonItem {
    return [barButtonItem isKindOfClass:[RNNUIBarButtonItem class]];
}

- (void)clearPreviousButtonViews:(NSArray<UIBarButtonItem *> *)newButtons
                      oldButtons:(NSArray<UIBarButtonItem *> *)oldButtons {
    NSArray<UIBarButtonItem *> *removedButtons = [oldButtons difference:newButtons
                                                       withPropertyName:@"customView"];

    for (UIBarButtonItem *buttonItem in removedButtons) {
        RNNReactView *reactView = buttonItem.customView;
        if ([reactView isKindOfClass:[RNNReactView class]]) {
            [reactView componentDidDisappear];
            [_componentRegistry removeChildComponent:reactView.componentId];
        }
    }
}

- (UIEdgeInsets)leftButtonInsets:(RNNInsetsOptions *)insets {
    return [insets edgeInsetsWithDefault:UIEdgeInsetsZero];
}

- (UIEdgeInsets)rightButtonInsets:(RNNInsetsOptions *)insets {
    return [insets edgeInsetsWithDefault:UIEdgeInsetsZero];
}

@end