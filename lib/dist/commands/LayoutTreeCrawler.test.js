"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const LayoutType_1 = require("./LayoutType");
const LayoutTreeCrawler_1 = require("./LayoutTreeCrawler");
const Store_1 = require("../components/Store");
const ts_mockito_1 = require("ts-mockito");
const OptionsProcessor_1 = require("./OptionsProcessor");
const CommandName_1 = require("../interfaces/CommandName");
describe('LayoutTreeCrawler', () => {
    let uut;
    let mockedStore;
    let mockedOptionsProcessor;
    beforeEach(() => {
        mockedStore = ts_mockito_1.mock(Store_1.Store);
        mockedOptionsProcessor = ts_mockito_1.mock(OptionsProcessor_1.OptionsProcessor);
        uut = new LayoutTreeCrawler_1.LayoutTreeCrawler(ts_mockito_1.instance(mockedStore), ts_mockito_1.instance(mockedOptionsProcessor));
    });
    it('saves passProps into store for Component nodes', () => {
        const node = {
            id: 'testId',
            type: LayoutType_1.LayoutType.BottomTabs,
            children: [
                {
                    id: 'testId',
                    type: LayoutType_1.LayoutType.Component,
                    data: { name: 'the name', passProps: { myProp: 123 } },
                    children: [],
                },
            ],
            data: {},
        };
        uut.crawl(node, CommandName_1.CommandName.SetRoot);
        ts_mockito_1.verify(mockedStore.updateProps('testId', ts_mockito_1.deepEqual({ myProp: 123 }))).called();
    });
    it('Components: must contain data name', () => {
        const node = { type: LayoutType_1.LayoutType.Component, data: {}, children: [], id: 'testId' };
        expect(() => uut.crawl(node, CommandName_1.CommandName.SetRoot)).toThrowError('Missing component data.name');
    });
    it('Components: omits passProps after processing so they are not passed over the bridge', () => {
        const node = {
            id: 'testId',
            type: LayoutType_1.LayoutType.Component,
            data: {
                name: 'compName',
                passProps: { someProp: 'here' },
            },
            children: [],
        };
        uut.crawl(node, CommandName_1.CommandName.SetRoot);
        expect(node.data.passProps).toBeUndefined();
    });
});
