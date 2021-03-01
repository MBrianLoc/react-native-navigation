"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const OptionsProcessor_1 = require("./OptionsProcessor");
const UniqueIdProvider_1 = require("../adapters/UniqueIdProvider");
const Store_1 = require("../components/Store");
const OptionProcessorsStore_1 = require("../processors/OptionProcessorsStore");
const Options_1 = require("../interfaces/Options");
const ts_mockito_1 = require("ts-mockito");
const ColorService_1 = require("../adapters/ColorService");
const AssetResolver_1 = require("../adapters/AssetResolver");
const Deprecations_1 = require("./Deprecations");
const CommandName_1 = require("../interfaces/CommandName");
describe('navigation options', () => {
    let uut;
    let optionProcessorsRegistry;
    const mockedStore = ts_mockito_1.mock(Store_1.Store);
    const store = ts_mockito_1.instance(mockedStore);
    beforeEach(() => {
        const mockedAssetService = ts_mockito_1.mock(AssetResolver_1.AssetService);
        ts_mockito_1.when(mockedAssetService.resolveFromRequire(ts_mockito_1.anyNumber())).thenReturn({
            height: 100,
            scale: 1,
            uri: 'lol',
            width: 100,
        });
        const assetService = ts_mockito_1.instance(mockedAssetService);
        const mockedColorService = ts_mockito_1.mock(ColorService_1.ColorService);
        ts_mockito_1.when(mockedColorService.toNativeColor('red')).thenReturn(0xffff0000);
        ts_mockito_1.when(mockedColorService.toNativeColor('green')).thenReturn(0xff00ff00);
        ts_mockito_1.when(mockedColorService.toNativeColor('blue')).thenReturn(0xff0000ff);
        const colorService = ts_mockito_1.instance(mockedColorService);
        optionProcessorsRegistry = new OptionProcessorsStore_1.OptionProcessorsStore();
        let uuid = 0;
        const mockedUniqueIdProvider = ts_mockito_1.mock(UniqueIdProvider_1.UniqueIdProvider);
        ts_mockito_1.when(mockedUniqueIdProvider.generate(ts_mockito_1.anything())).thenCall((prefix) => {
            return `${prefix}${++uuid}`;
        });
        uut = new OptionsProcessor_1.OptionsProcessor(store, ts_mockito_1.instance(mockedUniqueIdProvider), optionProcessorsRegistry, colorService, assetService, new Deprecations_1.Deprecations());
    });
    describe('Modal Animation Options', () => {
        describe('Show Modal', () => {
            it('processes old options into new options,backwards compatibility ', () => {
                const options = {
                    animations: {
                        showModal: {
                            enabled: false,
                            translationY: {
                                from: 0,
                                to: 1,
                                duration: 3,
                            },
                        },
                        dismissModal: {
                            enabled: true,
                            translationY: {
                                from: 0,
                                to: 1,
                                duration: 3,
                            },
                        },
                    },
                };
                const expected = {
                    animations: {
                        showModal: {
                            enter: {
                                enabled: false,
                                translationY: {
                                    from: 0,
                                    to: 1,
                                    duration: 3,
                                },
                            },
                        },
                        dismissModal: {
                            exit: {
                                enabled: true,
                                translationY: {
                                    from: 0,
                                    to: 1,
                                    duration: 3,
                                },
                            },
                        },
                    },
                };
                uut.processOptions(options, CommandName_1.CommandName.ShowModal);
                expect(options).toEqual(expected);
            });
            it('processes old enabled options into new options,backwards compatibility ', () => {
                const options = {
                    animations: {
                        showModal: {
                            enabled: false,
                        },
                        dismissModal: {
                            enabled: true,
                        },
                    },
                };
                const expected = {
                    animations: {
                        showModal: {
                            enter: {
                                enabled: false,
                            },
                        },
                        dismissModal: {
                            exit: {
                                enabled: true,
                            },
                        },
                    },
                };
                uut.processOptions(options, CommandName_1.CommandName.ShowModal);
                expect(options).toEqual(expected);
            });
            it('should not process new options', () => {
                const options = {
                    animations: {
                        showModal: {
                            enter: {
                                enabled: false,
                                translationY: {
                                    from: 0,
                                    to: 1,
                                    duration: 3,
                                },
                            },
                        },
                        dismissModal: {
                            exit: {
                                enabled: true,
                                translationY: {
                                    from: 0,
                                    to: 1,
                                    duration: 3,
                                },
                            },
                        },
                    },
                };
                const expected = { ...options };
                uut.processOptions(options, CommandName_1.CommandName.ShowModal);
                expect(options).toEqual(expected);
            });
        });
        describe('Dismiss Modal', () => {
            it('processes old options into new options,backwards compatibility ', () => {
                const options = {
                    animations: {
                        showModal: {
                            enabled: false,
                            translationY: {
                                from: 0,
                                to: 1,
                                duration: 3,
                            },
                        },
                        dismissModal: {
                            enabled: true,
                            translationY: {
                                from: 0,
                                to: 1,
                                duration: 3,
                            },
                        },
                    },
                };
                const expected = {
                    animations: {
                        showModal: {
                            enter: {
                                enabled: false,
                                translationY: {
                                    from: 0,
                                    to: 1,
                                    duration: 3,
                                },
                            },
                        },
                        dismissModal: {
                            exit: {
                                enabled: true,
                                translationY: {
                                    from: 0,
                                    to: 1,
                                    duration: 3,
                                },
                            },
                        },
                    },
                };
                uut.processOptions(options, CommandName_1.CommandName.DismissModal);
                expect(options).toEqual(expected);
            });
            it('processes old enabled options into new options,backwards compatibility ', () => {
                const options = {
                    animations: {
                        showModal: {
                            enabled: false,
                        },
                        dismissModal: {
                            enabled: true,
                        },
                    },
                };
                const expected = {
                    animations: {
                        showModal: {
                            enter: {
                                enabled: false,
                            },
                        },
                        dismissModal: {
                            exit: {
                                enabled: true,
                            },
                        },
                    },
                };
                uut.processOptions(options, CommandName_1.CommandName.DismissModal);
                expect(options).toEqual(expected);
            });
            it('should not process new options', () => {
                const options = {
                    animations: {
                        showModal: {
                            enter: {
                                enabled: false,
                                translationY: {
                                    from: 0,
                                    to: 1,
                                    duration: 3,
                                },
                            },
                        },
                        dismissModal: {
                            exit: {
                                enabled: true,
                                translationY: {
                                    from: 0,
                                    to: 1,
                                    duration: 3,
                                },
                            },
                        },
                    },
                };
                const expected = { ...options };
                uut.processOptions(options, CommandName_1.CommandName.DismissModal);
                expect(options).toEqual(expected);
            });
        });
    });
    it('keeps original values if values were not processed', () => {
        const options = {
            blurOnUnmount: false,
            popGesture: false,
            modalPresentationStyle: Options_1.OptionsModalPresentationStyle.fullScreen,
        };
        uut.processOptions(options, CommandName_1.CommandName.SetRoot);
        expect(options).toEqual({
            blurOnUnmount: false,
            popGesture: false,
            modalPresentationStyle: Options_1.OptionsModalPresentationStyle.fullScreen,
        });
    });
    it('passes value to registered processor', () => {
        const options = {
            topBar: {
                visible: true,
            },
        };
        optionProcessorsRegistry.addProcessor('topBar.visible', (value) => {
            return !value;
        });
        uut.processOptions(options, CommandName_1.CommandName.SetRoot);
        expect(options).toEqual({
            topBar: {
                visible: false,
            },
        });
    });
    it('process options object with multiple values using registered processor', () => {
        const options = {
            topBar: {
                visible: true,
                background: {
                    translucent: true,
                },
            },
        };
        optionProcessorsRegistry.addProcessor('topBar.visible', (value) => {
            return !value;
        });
        optionProcessorsRegistry.addProcessor('topBar.background.translucent', (value) => {
            return !value;
        });
        uut.processOptions(options, CommandName_1.CommandName.SetRoot);
        expect(options).toEqual({
            topBar: {
                visible: false,
                background: {
                    translucent: false,
                },
            },
        });
    });
    it('passes commandName to registered processor', () => {
        const options = {
            topBar: {
                visible: false,
            },
        };
        optionProcessorsRegistry.addProcessor('topBar.visible', (_value, commandName) => {
            expect(commandName).toEqual(CommandName_1.CommandName.SetRoot);
        });
        uut.processOptions(options, CommandName_1.CommandName.SetRoot);
    });
    it('passes props to registered processor', () => {
        const options = {
            topBar: {
                visible: false,
            },
        };
        const props = {
            prop: 'prop',
        };
        const processor = (_value, _commandName, passProps) => {
            expect(passProps).toEqual(props);
            return _value;
        };
        optionProcessorsRegistry.addProcessor('topBar.visible', processor);
        uut.processOptions(options, CommandName_1.CommandName.SetRoot, props);
    });
    it('supports multiple registered processors', () => {
        const options = {
            topBar: {
                visible: true,
            },
        };
        optionProcessorsRegistry.addProcessor('topBar.visible', () => false);
        optionProcessorsRegistry.addProcessor('topBar.visible', () => true);
        uut.processOptions(options, CommandName_1.CommandName.SetRoot);
        expect(options).toEqual({
            topBar: {
                visible: true,
            },
        });
    });
    it('supports multiple registered processors deep props', () => {
        const options = {
            topBar: {
                visible: false,
                background: {
                    translucent: false,
                },
            },
            bottomTabs: {
                visible: false,
            },
        };
        optionProcessorsRegistry.addProcessor('topBar.visible', () => true);
        optionProcessorsRegistry.addProcessor('bottomTabs.visible', () => true);
        optionProcessorsRegistry.addProcessor('topBar.background.translucent', () => true);
        uut.processOptions(options, CommandName_1.CommandName.SetRoot);
        expect(options).toEqual({
            topBar: {
                visible: true,
                background: {
                    translucent: true,
                },
            },
            bottomTabs: {
                visible: true,
            },
        });
    });
    it('processes color keys', () => {
        const options = {
            statusBar: { backgroundColor: 'red' },
            topBar: { background: { color: 'blue' } },
        };
        uut.processOptions(options, CommandName_1.CommandName.SetRoot);
        expect(options).toEqual({
            statusBar: { backgroundColor: 0xffff0000 },
            topBar: { background: { color: 0xff0000ff } },
        });
    });
    it('processes image keys', () => {
        const options = {
            backgroundImage: 123,
            rootBackgroundImage: 234,
            bottomTab: { icon: 345, selectedIcon: 345 },
        };
        uut.processOptions(options, CommandName_1.CommandName.SetRoot);
        expect(options).toEqual({
            backgroundImage: { height: 100, scale: 1, uri: 'lol', width: 100 },
            rootBackgroundImage: { height: 100, scale: 1, uri: 'lol', width: 100 },
            bottomTab: {
                icon: { height: 100, scale: 1, uri: 'lol', width: 100 },
                selectedIcon: { height: 100, scale: 1, uri: 'lol', width: 100 },
            },
        });
    });
    it('calls store if component has passProps', () => {
        const passProps = { some: 'thing' };
        const options = { topBar: { title: { component: { passProps, name: 'a' } } } };
        uut.processOptions(options, CommandName_1.CommandName.SetRoot);
        ts_mockito_1.verify(mockedStore.updateProps('CustomComponent1', passProps)).called();
    });
    it('generates componentId for component id was not passed', () => {
        const options = { topBar: { title: { component: { name: 'a' } } } };
        uut.processOptions(options, CommandName_1.CommandName.SetRoot);
        expect(options).toEqual({
            topBar: { title: { component: { name: 'a', componentId: 'CustomComponent1' } } },
        });
    });
    it('copies passed id to componentId key', () => {
        const options = { topBar: { title: { component: { name: 'a', id: 'Component1' } } } };
        uut.processOptions(options, CommandName_1.CommandName.SetRoot);
        expect(options).toEqual({
            topBar: { title: { component: { name: 'a', id: 'Component1', componentId: 'Component1' } } },
        });
    });
    it('calls store when button has passProps and id', () => {
        const passProps = { prop: 'prop' };
        const options = { topBar: { rightButtons: [{ passProps, id: '1' }] } };
        uut.processOptions(options, CommandName_1.CommandName.SetRoot);
        ts_mockito_1.verify(mockedStore.updateProps('1', passProps)).called();
    });
    it('do not touch passProps when id for button is missing', () => {
        const passProps = { prop: 'prop' };
        const options = { topBar: { rightButtons: [{ passProps }] } };
        uut.processOptions(options, CommandName_1.CommandName.SetRoot);
        expect(options).toEqual({ topBar: { rightButtons: [{ passProps }] } });
    });
    it('omits passProps when processing buttons or components', () => {
        const options = {
            topBar: {
                rightButtons: [{ passProps: {}, id: 'btn1' }],
                leftButtons: [{ passProps: {}, id: 'btn2' }],
                title: { component: { name: 'helloThere1', passProps: {} } },
                background: { component: { name: 'helloThere2', passProps: {} } },
            },
        };
        uut.processOptions(options, CommandName_1.CommandName.SetRoot);
        expect(options.topBar.rightButtons[0].passProps).toBeUndefined();
        expect(options.topBar.leftButtons[0].passProps).toBeUndefined();
        expect(options.topBar.title.component.passProps).toBeUndefined();
        expect(options.topBar.background.component.passProps).toBeUndefined();
    });
    it('Will ensure the store has a chance to lazily load components in options', () => {
        const options = {
            topBar: {
                title: { component: { name: 'helloThere1', passProps: {} } },
                background: { component: { name: 'helloThere2', passProps: {} } },
            },
        };
        uut.processOptions(options, CommandName_1.CommandName.SetRoot);
        ts_mockito_1.verify(mockedStore.ensureClassForName('helloThere1')).called();
        ts_mockito_1.verify(mockedStore.ensureClassForName('helloThere2')).called();
    });
    it('show warning on iOS when toggling bottomTabs visibility through mergeOptions', () => {
        jest.spyOn(console, 'warn');
        uut.processOptions({ bottomTabs: { visible: false } }, CommandName_1.CommandName.MergeOptions);
        expect(console.warn).toBeCalledWith('toggling bottomTabs visibility is deprecated on iOS. For more information see https://github.com/wix/react-native-navigation/issues/6416', {
            bottomTabs: { visible: false },
        });
    });
    it('transform searchBar bool to object', () => {
        const options = { topBar: { searchBar: true } };
        uut.processOptions(options, CommandName_1.CommandName.SetRoot);
        expect(options.topBar.searchBar).toStrictEqual({
            visible: true,
            hideOnScroll: false,
            hideTopBarOnFocus: false,
            obscuresBackgroundDuringPresentation: false,
            backgroundColor: null,
            tintColor: null,
            placeholder: '',
        });
    });
    it('transform searchBar bool to object and merges in deprecated values', () => {
        const options = {
            topBar: {
                searchBar: true,
                searchBarHiddenWhenScrolling: true,
                hideNavBarOnFocusSearchBar: true,
                searchBarBackgroundColor: 'red',
                searchBarTintColor: 'green',
                searchBarPlaceholder: 'foo',
            },
        };
        uut.processOptions(options, CommandName_1.CommandName.SetRoot);
        expect(options.topBar.searchBar).toStrictEqual({
            visible: true,
            hideOnScroll: true,
            hideTopBarOnFocus: true,
            obscuresBackgroundDuringPresentation: false,
            backgroundColor: 0xffff0000,
            tintColor: 0xff00ff00,
            placeholder: 'foo',
        });
    });
    describe('process animations options', () => {
        const performOnViewsInvolvedInStackAnimation = (action) => ['content', 'topBar', 'bottomTabs'].forEach(action);
        describe('push', () => {
            it('old *.push api is converted into push.*.enter', () => {
                performOnViewsInvolvedInStackAnimation((view) => {
                    const options = {
                        animations: {
                            push: {
                                [view]: {
                                    alpha: {
                                        from: 0,
                                        to: 1,
                                    },
                                },
                            },
                        },
                    };
                    uut.processOptions(options, CommandName_1.CommandName.SetRoot);
                    expect(options.animations.push).toStrictEqual({
                        [view]: {
                            enter: {
                                alpha: {
                                    from: 0,
                                    to: 1,
                                },
                            },
                        },
                    });
                });
            });
            it('StackAnimationOptions based push api is left as is', () => {
                performOnViewsInvolvedInStackAnimation((view) => {
                    const options = {
                        animations: {
                            push: {
                                [view]: {
                                    exit: {
                                        alpha: {
                                            from: 1,
                                            to: 0,
                                        },
                                    },
                                    enter: {
                                        alpha: {
                                            from: 0,
                                            to: 1,
                                        },
                                    },
                                },
                            },
                        },
                    };
                    uut.processOptions(options, CommandName_1.CommandName.SetRoot);
                    expect(options.animations.push).toStrictEqual({
                        [view]: {
                            exit: {
                                alpha: {
                                    from: 1,
                                    to: 0,
                                },
                            },
                            enter: {
                                alpha: {
                                    from: 0,
                                    to: 1,
                                },
                            },
                        },
                    });
                });
            });
            it('Options not related to views are left as is', () => {
                performOnViewsInvolvedInStackAnimation(() => {
                    const options = {
                        animations: {
                            push: {
                                enabled: false,
                                waitForRender: true,
                                sharedElementTransitions: [],
                                elementTransitions: [],
                            },
                        },
                    };
                    uut.processOptions(options, CommandName_1.CommandName.SetRoot);
                    expect(options.animations.push).toStrictEqual({
                        enabled: false,
                        waitForRender: true,
                        sharedElementTransitions: [],
                        elementTransitions: [],
                    });
                });
            });
        });
        describe('pop', () => {
            it('old pop.content api is converted into pop.content.exit', () => {
                performOnViewsInvolvedInStackAnimation((view) => {
                    const options = {
                        animations: {
                            pop: {
                                [view]: {
                                    alpha: {
                                        from: 0,
                                        to: 1,
                                    },
                                },
                            },
                        },
                    };
                    uut.processOptions(options, CommandName_1.CommandName.SetRoot);
                    expect(options.animations.pop).toStrictEqual({
                        [view]: {
                            exit: {
                                alpha: {
                                    from: 0,
                                    to: 1,
                                },
                            },
                        },
                    });
                });
            });
            it('StackAnimationOptions based pop api is left as is', () => {
                performOnViewsInvolvedInStackAnimation((view) => {
                    const options = {
                        animations: {
                            pop: {
                                [view]: {
                                    exit: {
                                        alpha: {
                                            from: 1,
                                            to: 0,
                                        },
                                    },
                                    enter: {
                                        alpha: {
                                            from: 0,
                                            to: 1,
                                        },
                                    },
                                },
                            },
                        },
                    };
                    uut.processOptions(options, CommandName_1.CommandName.SetRoot);
                    expect(options.animations.pop).toStrictEqual({
                        [view]: {
                            exit: {
                                alpha: {
                                    from: 1,
                                    to: 0,
                                },
                            },
                            enter: {
                                alpha: {
                                    from: 0,
                                    to: 1,
                                },
                            },
                        },
                    });
                });
            });
            it('Options not related to views are left as is', () => {
                performOnViewsInvolvedInStackAnimation(() => {
                    const options = {
                        animations: {
                            pop: {
                                enabled: false,
                                waitForRender: true,
                                sharedElementTransitions: [],
                                elementTransitions: [],
                            },
                        },
                    };
                    uut.processOptions(options, CommandName_1.CommandName.SetRoot);
                    expect(options.animations.pop).toStrictEqual({
                        enabled: false,
                        waitForRender: true,
                        sharedElementTransitions: [],
                        elementTransitions: [],
                    });
                });
            });
        });
        describe('setStackRoot', () => {
            it('ViewAnimationOptions based setStackRoot api is converted to StackAnimationOptions based api', () => {
                const options = {
                    animations: {
                        setStackRoot: {
                            alpha: {
                                from: 0,
                                to: 1,
                            },
                        },
                    },
                };
                uut.processOptions(options, CommandName_1.CommandName.SetRoot);
                expect(options.animations.setStackRoot).toStrictEqual({
                    content: {
                        enter: {
                            alpha: {
                                from: 0,
                                to: 1,
                            },
                        },
                    },
                });
            });
            it('Disabled ViewAnimationOptions based setStackRoot api is converted to StackAnimationOptions based api', () => {
                const options = {
                    animations: {
                        setStackRoot: {
                            enabled: false,
                            waitForRender: true,
                        },
                    },
                };
                uut.processOptions(options, CommandName_1.CommandName.SetRoot);
                expect(options.animations.setStackRoot).toStrictEqual({
                    enabled: false,
                    waitForRender: true,
                    content: {
                        enter: {
                            enabled: false,
                            waitForRender: true,
                        },
                    },
                });
            });
            it('StackAnimationOptions based setStackRoot api is left as is', () => {
                performOnViewsInvolvedInStackAnimation((view) => {
                    const options = {
                        animations: {
                            setStackRoot: {
                                [view]: {
                                    enter: {
                                        alpha: {
                                            from: 0,
                                            to: 1,
                                        },
                                    },
                                },
                            },
                        },
                    };
                    uut.processOptions(options, CommandName_1.CommandName.SetRoot);
                    expect(options.animations.setStackRoot).toStrictEqual({
                        [view]: {
                            enter: {
                                alpha: {
                                    from: 0,
                                    to: 1,
                                },
                            },
                        },
                    });
                });
            });
        });
    });
});