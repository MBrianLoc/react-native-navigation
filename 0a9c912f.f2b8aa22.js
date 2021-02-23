(window.webpackJsonp=window.webpackJsonp||[]).push([[12],{372:function(t,e,n){"use strict";n.d(e,"a",(function(){return p})),n.d(e,"b",(function(){return f}));var r=n(0),a=n.n(r);function i(t,e,n){return e in t?Object.defineProperty(t,e,{value:n,enumerable:!0,configurable:!0,writable:!0}):t[e]=n,t}function o(t,e){var n=Object.keys(t);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(t);e&&(r=r.filter((function(e){return Object.getOwnPropertyDescriptor(t,e).enumerable}))),n.push.apply(n,r)}return n}function c(t){for(var e=1;e<arguments.length;e++){var n=null!=arguments[e]?arguments[e]:{};e%2?o(Object(n),!0).forEach((function(e){i(t,e,n[e])})):Object.getOwnPropertyDescriptors?Object.defineProperties(t,Object.getOwnPropertyDescriptors(n)):o(Object(n)).forEach((function(e){Object.defineProperty(t,e,Object.getOwnPropertyDescriptor(n,e))}))}return t}function l(t,e){if(null==t)return{};var n,r,a=function(t,e){if(null==t)return{};var n,r,a={},i=Object.keys(t);for(r=0;r<i.length;r++)n=i[r],e.indexOf(n)>=0||(a[n]=t[n]);return a}(t,e);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(t);for(r=0;r<i.length;r++)n=i[r],e.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(t,n)&&(a[n]=t[n])}return a}var b=a.a.createContext({}),u=function(t){var e=a.a.useContext(b),n=e;return t&&(n="function"==typeof t?t(e):c(c({},e),t)),n},p=function(t){var e=u(t.components);return a.a.createElement(b.Provider,{value:e},t.children)},s={inlineCode:"code",wrapper:function(t){var e=t.children;return a.a.createElement(a.a.Fragment,{},e)}},d=a.a.forwardRef((function(t,e){var n=t.components,r=t.mdxType,i=t.originalType,o=t.parentName,b=l(t,["components","mdxType","originalType","parentName"]),p=u(n),d=r,f=p["".concat(o,".").concat(d)]||p[d]||s[d]||i;return n?a.a.createElement(f,c(c({ref:e},b),{},{components:n})):a.a.createElement(f,c({ref:e},b))}));function f(t,e){var n=arguments,r=e&&e.mdxType;if("string"==typeof t||r){var i=n.length,o=new Array(i);o[0]=d;var c={};for(var l in e)hasOwnProperty.call(e,l)&&(c[l]=e[l]);c.originalType=t,c.mdxType="string"==typeof t?t:r,o[1]=c;for(var b=2;b<i;b++)o[b]=n[b];return a.a.createElement.apply(null,o)}return a.a.createElement.apply(null,n)}d.displayName="MDXCreateElement"},378:function(t,e,n){"use strict";var r=n(0),a=n(20);e.a=function(){var t=Object(r.useContext)(a.a);if(null===t)throw new Error("Docusaurus context not provided");return t}},379:function(t,e,n){"use strict";n.d(e,"b",(function(){return i})),n.d(e,"a",(function(){return o}));var r=n(378),a=n(380);function i(){var t=Object(r.a)().siteConfig,e=(t=void 0===t?{}:t).baseUrl,n=void 0===e?"/":e,i=t.url;return{withBaseUrl:function(t,e){return function(t,e,n,r){var i=void 0===r?{}:r,o=i.forcePrependBaseUrl,c=void 0!==o&&o,l=i.absolute,b=void 0!==l&&l;if(!n)return n;if(n.startsWith("#"))return n;if(Object(a.b)(n))return n;if(c)return e+n;var u=!n.startsWith(e)?e+n.replace(/^\//,""):n;return b?t+u:u}(i,n,t,e)}}}function o(t,e){return void 0===e&&(e={}),(0,i().withBaseUrl)(t,e)}},380:function(t,e,n){"use strict";function r(t){return!0===/^(\w*:|\/\/)/.test(t)}function a(t){return void 0!==t&&!r(t)}n.d(e,"b",(function(){return r})),n.d(e,"a",(function(){return a}))},63:function(t,e,n){"use strict";n.r(e),n.d(e,"frontMatter",(function(){return c})),n.d(e,"metadata",(function(){return l})),n.d(e,"rightToc",(function(){return b})),n.d(e,"default",(function(){return p}));var r=n(2),a=n(6),i=(n(0),n(372)),o=n(379),c={id:"options-navigationBar",title:"Navigation Bar Options",sidebar_label:"Navigation Bar"},l={unversionedId:"api/options-navigationBar",id:"api/options-navigationBar",isDocsHomePage:!1,title:"Navigation Bar Options",description:"The Navigation Bar is the area at the bottom of the screen containing Android's three navigation buttons: Back, Home and Recents.",source:"@site/docs/api/options-navigationBar.mdx",slug:"/api/options-navigationBar",permalink:"/react-native-navigation/next/api/options-navigationBar",editUrl:"https://github.com/wix/react-native-navigation/edit/master/website/docs/api/options-navigationBar.mdx",version:"current",sidebar_label:"Navigation Bar",sidebar:"api",previous:{title:"Modal Options",permalink:"/react-native-navigation/next/api/options-modal"},next:{title:"Overlay",permalink:"/react-native-navigation/next/api/options-overlay"}},b=[{value:"<code>visible</code>",id:"visible",children:[]},{value:"<code>backgroundColor</code>",id:"backgroundcolor",children:[]}],u={rightToc:b};function p(t){var e=t.components,n=Object(a.a)(t,["components"]);return Object(i.b)("wrapper",Object(r.a)({},u,n,{components:e,mdxType:"MDXLayout"}),Object(i.b)("p",null,"The Navigation Bar is the area at the bottom of the screen containing Android's three navigation buttons: Back, Home and Recents."),Object(i.b)("p",null,"An example of a dark navigation bar:"),Object(i.b)("img",{width:"30%",src:Object(o.a)("/img/navBar_black.png")}),Object(i.b)("br",null),Object(i.b)("br",null),Object(i.b)("pre",null,Object(i.b)("code",{parentName:"pre",className:"language-js"},"const options = {\n  navigationBar: {}\n};\n")),Object(i.b)("h3",{id:"visible"},Object(i.b)("inlineCode",{parentName:"h3"},"visible")),Object(i.b)("p",null,"Set the navigation bar visibility."),Object(i.b)("table",null,Object(i.b)("thead",{parentName:"table"},Object(i.b)("tr",{parentName:"thead"},Object(i.b)("th",{parentName:"tr",align:null},"Type"),Object(i.b)("th",{parentName:"tr",align:null},"Required"),Object(i.b)("th",{parentName:"tr",align:null},"Platform"))),Object(i.b)("tbody",{parentName:"table"},Object(i.b)("tr",{parentName:"tbody"},Object(i.b)("td",{parentName:"tr",align:null},"boolean"),Object(i.b)("td",{parentName:"tr",align:null},"No"),Object(i.b)("td",{parentName:"tr",align:null},"Android")))),Object(i.b)("h3",{id:"backgroundcolor"},Object(i.b)("inlineCode",{parentName:"h3"},"backgroundColor")),Object(i.b)("p",null,"Set the navigation bar color. When a light background color is used, the color of the navigation bar icons will adapt to a dark color."),Object(i.b)("img",{width:"30%",src:Object(o.a)("/img/navBar_white.png")}),Object(i.b)("table",null,Object(i.b)("thead",{parentName:"table"},Object(i.b)("tr",{parentName:"thead"},Object(i.b)("th",{parentName:"tr",align:null},"Type"),Object(i.b)("th",{parentName:"tr",align:null},"Required"),Object(i.b)("th",{parentName:"tr",align:null},"Platform"),Object(i.b)("th",{parentName:"tr",align:null},"Default"))),Object(i.b)("tbody",{parentName:"table"},Object(i.b)("tr",{parentName:"tbody"},Object(i.b)("td",{parentName:"tr",align:null},"Color"),Object(i.b)("td",{parentName:"tr",align:null},"No"),Object(i.b)("td",{parentName:"tr",align:null},"Android"),Object(i.b)("td",{parentName:"tr",align:null},"'black'")))))}p.isMDXComponent=!0}}]);