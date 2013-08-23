/* qTip2 v2.1.1 tips viewport imagemap svg | qtip2.com | Licensed MIT, GPL | Tue Jul 16 2013 11:04:25 */
!function(a,b,c){!function(a){"use strict";"function"==typeof define&&define.amd?define(["jquery","imagesloaded"],a):jQuery&&!jQuery.fn.qtip&&a(jQuery)}(function(d){function e(a,b,c,e){this.id=c,this.target=a,this.tooltip=D,this.elements=elements={target:a},this._id=Q+"-"+c,this.timers={img:{}},this.options=b,this.plugins={},this.cache=cache={event:{},target:d(),disabled:C,attr:e,onTooltip:C,lastClass:""},this.rendered=this.destroyed=this.disabled=this.waiting=this.hiddenDuringWait=this.positioning=this.triggering=C}function f(a){return a===D||"object"!==d.type(a)}function g(a){return!(d.isFunction(a)||a&&a.attr||a.length||"object"===d.type(a)&&(a.jquery||a.then))}function h(a){var b,c,e,h;return f(a)?C:(f(a.metadata)&&(a.metadata={type:a.metadata}),"content"in a&&(b=a.content,f(b)||b.jquery||b.done?b=a.content={text:c=g(b)?C:b}:c=b.text,"ajax"in b&&(e=b.ajax,h=e&&e.once!==C,delete b.ajax,b.text=function(a,b){var f=c||d(this).attr(b.options.content.attr)||"Loading...",g=d.ajax(d.extend({},e,{context:b})).then(e.success,D,e.error).then(function(a){return a&&h&&b.set("content.text",a),a},function(a,c,d){b.destroyed||0===a.status||b.set("content.text",c+": "+d)});return h?f:(b.set("content.text",f),g)}),"title"in b&&(f(b.title)||(b.button=b.title.button,b.title=b.title.text),g(b.title||C)&&(b.title=C))),"position"in a&&f(a.position)&&(a.position={my:a.position,at:a.position}),"show"in a&&f(a.show)&&(a.show=a.show.jquery?{target:a.show}:a.show===B?{ready:B}:{event:a.show}),"hide"in a&&f(a.hide)&&(a.hide=a.hide.jquery?{target:a.hide}:{event:a.hide}),"style"in a&&f(a.style)&&(a.style={classes:a.style}),d.each(P,function(){this.sanitize&&this.sanitize(a)}),a)}function j(a,b){for(var c,d=0,e=a,f=b.split(".");e=e[f[d++]];)d<f.length&&(c=e);return[c||a,f.pop()]}function k(a,b){var c,d,e;for(c in this.checks)for(d in this.checks[c])(e=new RegExp(d,"i").exec(a))&&(b.push(e),("builtin"===c||this.plugins[c])&&this.checks[c][d].apply(this.plugins[c]||this,b))}function l(a){return T.concat("").join(a?"-"+a+" ":" ")}function m(a){if(this.tooltip.hasClass($))return C;clearTimeout(this.timers.show),clearTimeout(this.timers.hide);var b=d.proxy(function(){this.toggle(B,a)},this);this.options.show.delay>0?this.timers.show=setTimeout(b,this.options.show.delay):b()}function n(a){if(this.tooltip.hasClass($))return C;var b=d(a.relatedTarget),c=b.closest(U)[0]===this.tooltip[0],e=b[0]===this.options.show.target[0];if(clearTimeout(this.timers.show),clearTimeout(this.timers.hide),this!==b[0]&&"mouse"===this.options.position.target&&c||this.options.hide.fixed&&/mouse(out|leave|move)/.test(a.type)&&(c||e))try{a.preventDefault(),a.stopImmediatePropagation()}catch(f){}else{var g=d.proxy(function(){this.toggle(C,a)},this);this.options.hide.delay>0?this.timers.hide=setTimeout(g,this.options.hide.delay):g()}}function o(a){return this.tooltip.hasClass($)||!this.options.hide.inactive?C:(clearTimeout(this.timers.inactive),this.timers.inactive=setTimeout(d.proxy(function(){this.hide(a)},this),this.options.hide.inactive),void 0)}function p(a){this.rendered&&this.tooltip[0].offsetWidth>0&&this.reposition(a)}function q(a,c,e){d(b.body).delegate(a,(c.split?c:c.join(eb+" "))+eb,function(){var a=w.api[d.attr(this,S)];a&&!a.disabled&&e.apply(a,arguments)})}function r(a,c,f){var g,i,j,k,l,m=d(b.body),n=a[0]===b?m:a,o=a.metadata?a.metadata(f.metadata):D,p="html5"===f.metadata.type&&o?o[f.metadata.name]:D,q=a.data(f.metadata.name||"qtipopts");try{q="string"==typeof q?d.parseJSON(q):q}catch(r){}if(k=d.extend(B,{},w.defaults,f,"object"==typeof q?h(q):D,h(p||o)),i=k.position,k.id=c,"boolean"==typeof k.content.text){if(j=a.attr(k.content.attr),k.content.attr===C||!j)return C;k.content.text=j}if(i.container.length||(i.container=m),i.target===C&&(i.target=n),k.show.target===C&&(k.show.target=n),k.show.solo===B&&(k.show.solo=i.container.closest("body")),k.hide.target===C&&(k.hide.target=n),k.position.viewport===B&&(k.position.viewport=i.container),i.container=i.container.eq(0),i.at=new y(i.at,B),i.my=new y(i.my),a.data(Q))if(k.overwrite)a.qtip("destroy");else if(k.overwrite===C)return C;return a.attr(R,c),k.suppress&&(l=a.attr("title"))&&a.removeAttr("title").attr(ab,l).attr("title",""),g=new e(a,k,c,!!j),a.data(Q,g),a.one("remove.qtip-"+c+" removeqtip.qtip-"+c,function(){var a;(a=d(this).data(Q))&&a.destroy()}),g}function s(a){return a.charAt(0).toUpperCase()+a.slice(1)}function t(a,b){var d,e,f=b.charAt(0).toUpperCase()+b.slice(1),g=(b+" "+pb.join(f+" ")+f).split(" "),h=0;if(ob[b])return a.css(ob[b]);for(;d=g[h++];)if((e=a.css(d))!==c)return ob[b]=d,e}function u(a,b){return parseInt(t(a,b),10)}function v(a,b){this._ns="tip",this.options=b,this.offset=b.offset,this.size=[b.width,b.height],this.init(this.qtip=a)}var w,x,y,z,A,B=!0,C=!1,D=null,E="x",F="y",G="width",H="height",I="top",J="left",K="bottom",L="right",M="center",N="flipinvert",O="shift",P={},Q="qtip",R="data-hasqtip",S="data-qtip-id",T=["ui-widget","ui-tooltip"],U="."+Q,V="click dblclick mousedown mouseup mousemove mouseleave mouseenter".split(" "),W=Q+"-fixed",X=Q+"-default",Y=Q+"-focus",Z=Q+"-hover",$=Q+"-disabled",_="_replacedByqTip",ab="oldtitle";BROWSER={ie:function(){for(var a=3,c=b.createElement("div");(c.innerHTML="<!--[if gt IE "+ ++a+"]><i></i><![endif]-->")&&c.getElementsByTagName("i")[0];);return a>4?a:0/0}(),iOS:parseFloat((""+(/CPU.*OS ([0-9_]{1,5})|(CPU like).*AppleWebKit.*Mobile/i.exec(navigator.userAgent)||[0,""])[1]).replace("undefined","3_2").replace("_",".").replace("_",""))||C},x=e.prototype,x.render=function(a){if(this.rendered||this.destroyed)return this;var b=this,c=this.options,e=this.cache,f=this.elements,g=c.content.text,h=c.content.title,i=c.content.button,j=c.position,k="."+this._id+" ",l=[];return d.attr(this.target[0],"aria-describedby",this._id),this.tooltip=f.tooltip=tooltip=d("<div/>",{id:this._id,"class":[Q,X,c.style.classes,Q+"-pos-"+c.position.my.abbrev()].join(" "),width:c.style.width||"",height:c.style.height||"",tracking:"mouse"===j.target&&j.adjust.mouse,role:"alert","aria-live":"polite","aria-atomic":C,"aria-describedby":this._id+"-content","aria-hidden":B}).toggleClass($,this.disabled).attr(S,this.id).data(Q,this).appendTo(j.container).append(f.content=d("<div />",{"class":Q+"-content",id:this._id+"-content","aria-atomic":B})),this.rendered=-1,this.positioning=B,h&&(this._createTitle(),d.isFunction(h)||l.push(this._updateTitle(h,C))),i&&this._createButton(),d.isFunction(g)||l.push(this._updateContent(g,C)),this.rendered=B,this._setWidget(),d.each(c.events,function(a,b){d.isFunction(b)&&tooltip.bind(("toggle"===a?["tooltipshow","tooltiphide"]:["tooltip"+a]).join(k)+k,b)}),d.each(P,function(a){var c;"render"===this.initialize&&(c=this(b))&&(b.plugins[a]=c)}),this._assignEvents(),d.when.apply(d,l).then(function(){b._trigger("render"),b.positioning=C,b.hiddenDuringWait||!c.show.ready&&!a||b.toggle(B,e.event,C),b.hiddenDuringWait=C}),w.api[this.id]=this,this},x.destroy=function(a){function b(){if(!this.destroyed){this.destroyed=B;var a=this.target,b=a.attr(ab);this.rendered&&this.tooltip.stop(1,0).find("*").remove().end().remove(),d.each(this.plugins,function(){this.destroy&&this.destroy()}),clearTimeout(this.timers.show),clearTimeout(this.timers.hide),this._unassignEvents(),a.removeData(Q).removeAttr(S).removeAttr("aria-describedby"),this.options.suppress&&b&&a.attr("title",b).removeAttr(ab),this._unbind(a),this.options=this.elements=this.cache=this.timers=this.plugins=this.mouse=D,delete w.api[this.id]}}return this.destroyed?this.target:(a!==B&&this.rendered?(tooltip.one("tooltiphidden",d.proxy(b,this)),!this.triggering&&this.hide()):b.call(this),this.target)},z=x.checks={builtin:{"^id$":function(a,b,c,e){var f=c===B?w.nextid:c,g=Q+"-"+f;f!==C&&f.length>0&&!d("#"+g).length?(this._id=g,this.rendered&&(this.tooltip[0].id=this._id,this.elements.content[0].id=this._id+"-content",this.elements.title[0].id=this._id+"-title")):a[b]=e},"^prerender":function(a,b,c){c&&!this.rendered&&this.render(this.options.show.ready)},"^content.text$":function(a,b,c){this._updateContent(c)},"^content.attr$":function(a,b,c,d){this.options.content.text===this.target.attr(d)&&this._updateContent(this.target.attr(c))},"^content.title$":function(a,b,c){return c?(c&&!this.elements.title&&this._createTitle(),this._updateTitle(c),void 0):this._removeTitle()},"^content.button$":function(a,b,c){this._updateButton(c)},"^content.title.(text|button)$":function(a,b,c){this.set("content."+b,c)},"^position.(my|at)$":function(a,b,c){"string"==typeof c&&(a[b]=new y(c,"at"===b))},"^position.container$":function(a,b,c){this.tooltip.appendTo(c)},"^show.ready$":function(a,b,c){c&&(!this.rendered&&this.render(B)||this.toggle(B))},"^style.classes$":function(a,b,c,d){this.tooltip.removeClass(d).addClass(c)},"^style.width|height":function(a,b,c){this.tooltip.css(b,c)},"^style.widget|content.title":function(){this._setWidget()},"^style.def":function(a,b,c){this.tooltip.toggleClass(X,!!c)},"^events.(render|show|move|hide|focus|blur)$":function(a,b,c){tooltip[(d.isFunction(c)?"":"un")+"bind"]("tooltip"+b,c)},"^(show|hide|position).(event|target|fixed|inactive|leave|distance|viewport|adjust)":function(){var a=this.options.position;tooltip.attr("tracking","mouse"===a.target&&a.adjust.mouse),this._unassignEvents(),this._assignEvents()}}},x.get=function(a){if(this.destroyed)return this;var b=j(this.options,a.toLowerCase()),c=b[0][b[1]];return c.precedance?c.string():c};var bb=/^position\.(my|at|adjust|target|container|viewport)|style|content|show\.ready/i,cb=/^prerender|show\.ready/i;x.set=function(a,b){if(this.destroyed)return this;var c,e=this.rendered,f=C,g=this.options;return this.checks,"string"==typeof a?(c=a,a={},a[c]=b):a=d.extend({},a),d.each(a,function(b,c){if(!e&&!cb.test(b))return delete a[b],void 0;var h,i=j(g,b.toLowerCase());h=i[0][i[1]],i[0][i[1]]=c&&c.nodeType?d(c):c,f=bb.test(b)||f,a[b]=[i[0],i[1],c,h]}),h(g),this.positioning=B,d.each(a,d.proxy(k,this)),this.positioning=C,this.rendered&&this.tooltip[0].offsetWidth>0&&f&&this.reposition("mouse"===g.position.target?D:this.cache.event),this},x._update=function(a,b){var c=this,e=this.cache;return this.rendered&&a?(d.isFunction(a)&&(a=a.call(this.elements.target,e.event,this)||""),d.isFunction(a.then)?(e.waiting=B,a.then(function(a){return e.waiting=C,c._update(a,b)},D,function(a){return c._update(a,b)})):a===C||!a&&""!==a?C:(a.jquery&&a.length>0?b.children().detach().end().append(a.css({display:"block"})):b.html(a),e.waiting=B,(d.fn.imagesLoaded?b.imagesLoaded():d.Deferred().resolve(d([]))).done(function(a){e.waiting=C,a.length&&c.rendered&&c.tooltip[0].offsetWidth>0&&c.reposition(e.event,!a.length)}).promise())):C},x._updateContent=function(a,b){this._update(a,this.elements.content,b)},x._updateTitle=function(a,b){this._update(a,this.elements.title,b)===C&&this._removeTitle(C)},x._createTitle=function(){var a=this.elements,b=this._id+"-title";a.titlebar&&this._removeTitle(),a.titlebar=d("<div />",{"class":Q+"-titlebar "+(this.options.style.widget?l("header"):"")}).append(a.title=d("<div />",{id:b,"class":Q+"-title","aria-atomic":B})).insertBefore(a.content).delegate(".qtip-close","mousedown keydown mouseup keyup mouseout",function(a){d(this).toggleClass("ui-state-active ui-state-focus","down"===a.type.substr(-4))}).delegate(".qtip-close","mouseover mouseout",function(a){d(this).toggleClass("ui-state-hover","mouseover"===a.type)}),this.options.content.button&&this._createButton()},x._removeTitle=function(a){var b=this.elements;b.title&&(b.titlebar.remove(),b.titlebar=b.title=b.button=D,a!==C&&this.reposition())},x.reposition=function(c,e){if(!this.rendered||this.positioning||this.destroyed)return this;this.positioning=B;var f,g,h=this.cache,i=this.tooltip,j=this.options.position,k=j.target,l=j.my,m=j.at,n=j.viewport,o=j.container,p=j.adjust,q=p.method.split(" "),r=i.outerWidth(C),s=i.outerHeight(C),t=0,u=0,v=i.css("position"),w={left:0,top:0},x=i[0].offsetWidth>0,y=c&&"scroll"===c.type,z=d(a),A=o[0].ownerDocument,D=this.mouse;if(d.isArray(k)&&2===k.length)m={x:J,y:I},w={left:k[0],top:k[1]};else if("mouse"===k&&(c&&c.pageX||h.event.pageX))m={x:J,y:I},c=!D||!D.pageX||!p.mouse&&c&&c.pageX?(!c||"resize"!==c.type&&"scroll"!==c.type?c&&c.pageX&&"mousemove"===c.type?c:(!p.mouse||this.options.show.distance)&&h.origin&&h.origin.pageX?h.origin:c:h.event)||c||h.event||D||{}:D,"static"!==v&&(w=o.offset()),A.body.offsetWidth!==(a.innerWidth||A.documentElement.clientWidth)&&(g=d(A.body).offset()),w={left:c.pageX-w.left+(g&&g.left||0),top:c.pageY-w.top+(g&&g.top||0)},p.mouse&&y&&(w.left-=D.scrollX-z.scrollLeft(),w.top-=D.scrollY-z.scrollTop());else{if("event"===k&&c&&c.target&&"scroll"!==c.type&&"resize"!==c.type?h.target=d(c.target):"event"!==k&&(h.target=d(k.jquery?k:elements.target)),k=h.target,k=d(k).eq(0),0===k.length)return this;k[0]===b||k[0]===a?(t=BROWSER.iOS?a.innerWidth:k.width(),u=BROWSER.iOS?a.innerHeight:k.height(),k[0]===a&&(w={top:(n||k).scrollTop(),left:(n||k).scrollLeft()})):P.imagemap&&k.is("area")?f=P.imagemap(this,k,m,P.viewport?q:C):P.svg&&k[0].ownerSVGElement?f=P.svg(this,k,m,P.viewport?q:C):(t=k.outerWidth(C),u=k.outerHeight(C),w=k.offset()),f&&(t=f.width,u=f.height,g=f.offset,w=f.position),w=this.reposition.offset(k,w,o),(BROWSER.iOS>3.1&&BROWSER.iOS<4.1||BROWSER.iOS>=4.3&&BROWSER.iOS<4.33||!BROWSER.iOS&&"fixed"===v)&&(w.left-=z.scrollLeft(),w.top-=z.scrollTop()),(!f||f&&f.adjustable!==C)&&(w.left+=m.x===L?t:m.x===M?t/2:0,w.top+=m.y===K?u:m.y===M?u/2:0)}return w.left+=p.x+(l.x===L?-r:l.x===M?-r/2:0),w.top+=p.y+(l.y===K?-s:l.y===M?-s/2:0),P.viewport?(w.adjusted=P.viewport(this,w,j,t,u,r,s),g&&w.adjusted.left&&(w.left+=g.left),g&&w.adjusted.top&&(w.top+=g.top)):w.adjusted={left:0,top:0},this._trigger("move",[w,n.elem||n],c)?(delete w.adjusted,e===C||!x||isNaN(w.left)||isNaN(w.top)||"mouse"===k||!d.isFunction(j.effect)?i.css(w):d.isFunction(j.effect)&&(j.effect.call(i,this,d.extend({},w)),i.queue(function(a){d(this).css({opacity:"",height:""}),BROWSER.ie&&this.style.removeAttribute("filter"),a()})),this.positioning=C,this):this},x.reposition.offset=function(a,c,e){function f(a,b){c.left+=b*a.scrollLeft(),c.top+=b*a.scrollTop()}if(!e[0])return c;var g,h,i,j,k=d(a[0].ownerDocument),l=!!BROWSER.ie&&"CSS1Compat"!==b.compatMode,m=e[0];do"static"!==(h=d.css(m,"position"))&&("fixed"===h?(i=m.getBoundingClientRect(),f(k,-1)):(i=d(m).position(),i.left+=parseFloat(d.css(m,"borderLeftWidth"))||0,i.top+=parseFloat(d.css(m,"borderTopWidth"))||0),c.left-=i.left+(parseFloat(d.css(m,"marginLeft"))||0),c.top-=i.top+(parseFloat(d.css(m,"marginTop"))||0),g||"hidden"===(j=d.css(m,"overflow"))||"visible"===j||(g=d(m)));while(m=m.offsetParent);return g&&(g[0]!==k[0]||l)&&f(g,1),c};var db=(y=x.reposition.Corner=function(a,b){a=(""+a).replace(/([A-Z])/," $1").replace(/middle/gi,M).toLowerCase(),this.x=(a.match(/left|right/i)||a.match(/center/)||["inherit"])[0].toLowerCase(),this.y=(a.match(/top|bottom|center/i)||["inherit"])[0].toLowerCase(),this.forceY=!!b;var c=a.charAt(0);this.precedance="t"===c||"b"===c?F:E}).prototype;db.invert=function(a,b){this[a]=this[a]===J?L:this[a]===L?J:b||this[a]},db.string=function(){var a=this.x,b=this.y;return a===b?a:this.precedance===F||this.forceY&&"center"!==b?b+" "+a:a+" "+b},db.abbrev=function(){var a=this.string().split(" ");return a[0].charAt(0)+(a[1]&&a[1].charAt(0)||"")},db.clone=function(){return new y(this.string(),this.forceY)},x.toggle=function(a,c){var e=this.cache,f=this.options,g=this.tooltip;if(c){if(/over|enter/.test(c.type)&&/out|leave/.test(e.event.type)&&f.show.target.add(c.target).length===f.show.target.length&&g.has(c.relatedTarget).length)return this;e.event=d.extend({},c)}if(this.waiting&&!a&&(this.hiddenDuringWait=B),!this.rendered)return a?this.render(1):this;if(this.destroyed||this.disabled)return this;var h,i,j=a?"show":"hide",k=this.options[j],l=(this.options[a?"hide":"show"],this.options.position),m=this.options.content,n=this.tooltip.css("width"),o=this.tooltip[0].offsetWidth>0,p=a||1===k.target.length,q=!c||k.target.length<2||e.target[0]===c.target;return(typeof a).search("boolean|number")&&(a=!o),h=!g.is(":animated")&&o===a&&q,i=h?D:!!this._trigger(j,[90]),i!==C&&a&&this.focus(c),!i||h?this:(d.attr(g[0],"aria-hidden",!a),a?(e.origin=d.extend({},this.mouse),d.isFunction(m.text)&&this._updateContent(m.text,C),d.isFunction(m.title)&&this._updateTitle(m.title,C),!A&&"mouse"===l.target&&l.adjust.mouse&&(d(b).bind("mousemove."+Q,this._storeMouse),A=B),n||g.css("width",g.outerWidth(C)),this.reposition(c,arguments[2]),n||g.css("width",""),k.solo&&("string"==typeof k.solo?d(k.solo):d(U,k.solo)).not(g).not(k.target).qtip("hide",d.Event("tooltipsolo"))):(clearTimeout(this.timers.show),delete e.origin,A&&!d(U+'[tracking="true"]:visible',k.solo).not(g).length&&(d(b).unbind("mousemove."+Q),A=C),this.blur(c)),after=d.proxy(function(){a?(BROWSER.ie&&g[0].style.removeAttribute("filter"),g.css("overflow",""),"string"==typeof k.autofocus&&d(this.options.show.autofocus,g).focus(),this.options.show.target.trigger("qtip-"+this.id+"-inactive")):g.css({display:"",visibility:"",opacity:"",left:"",top:""}),this._trigger(a?"visible":"hidden")},this),k.effect===C||p===C?(g[j](),after()):d.isFunction(k.effect)?(g.stop(1,1),k.effect.call(g,this),g.queue("fx",function(a){after(),a()})):g.fadeTo(90,a?1:0,after),a&&k.target.trigger("qtip-"+this.id+"-inactive"),this)},x.show=function(a){return this.toggle(B,a)},x.hide=function(a){return this.toggle(C,a)},x.focus=function(a){if(!this.rendered||this.destroyed)return this;var b=d(U),c=this.tooltip,e=parseInt(c[0].style.zIndex,10),f=w.zindex+b.length;return c.hasClass(Y)||this._trigger("focus",[f],a)&&(e!==f&&(b.each(function(){this.style.zIndex>e&&(this.style.zIndex=this.style.zIndex-1)}),b.filter("."+Y).qtip("blur",a)),c.addClass(Y)[0].style.zIndex=f),this},x.blur=function(a){return!this.rendered||this.destroyed?this:(this.tooltip.removeClass(Y),this._trigger("blur",[this.tooltip.css("zIndex")],a),this)},x.disable=function(a){return this.destroyed?this:("boolean"!=typeof a&&(a=!(this.tooltip.hasClass($)||this.disabled)),this.rendered&&this.tooltip.toggleClass($,a).attr("aria-disabled",a),this.disabled=!!a,this)},x.enable=function(){return this.disable(C)},x._createButton=function(){var a=this,b=this.elements,c=b.tooltip,e=this.options.content.button,f="string"==typeof e,g=f?e:"Close tooltip";b.button&&b.button.remove(),b.button=e.jquery?e:d("<a />",{"class":"qtip-close "+(this.options.style.widget?"":Q+"-icon"),title:g,"aria-label":g}).prepend(d("<span />",{"class":"ui-icon ui-icon-close",html:"&times;"})),b.button.appendTo(b.titlebar||c).attr("role","button").click(function(b){return c.hasClass($)||a.hide(b),C})},x._updateButton=function(a){if(!this.rendered)return C;var b=this.elements.button;a?this._createButton():b.remove()},x._setWidget=function(){var a=this.options.style.widget,b=this.elements,c=b.tooltip,d=c.hasClass($);c.removeClass($),$=a?"ui-state-disabled":"qtip-disabled",c.toggleClass($,d),c.toggleClass("ui-helper-reset "+l(),a).toggleClass(X,this.options.style.def&&!a),b.content&&b.content.toggleClass(l("content"),a),b.titlebar&&b.titlebar.toggleClass(l("header"),a),b.button&&b.button.toggleClass(Q+"-icon",!a)},x._storeMouse=function(c){this.mouse={pageX:c.pageX,pageY:c.pageY,type:"mousemove",scrollX:a.pageXOffset||b.body.scrollLeft||b.documentElement.scrollLeft,scrollY:a.pageYOffset||b.body.scrollTop||b.documentElement.scrollTop}},x._bind=function(a,b,c,e,f){var g="."+this._id+(e?"-"+e:"");b.length&&d(a).bind((b.split?b:b.join(g+" "))+g,d.proxy(c,f||this))},x._unbind=function(a,b){d(a).unbind("."+this._id+(b?"-"+b:""))};var eb="."+Q;d(function(){q(U,["mouseenter","mouseleave"],function(a){var b="mouseenter"===a.type,c=d(a.currentTarget),e=d(a.relatedTarget||a.target),f=this.options;b?(this.focus(a),c.hasClass(W)&&!c.hasClass($)&&clearTimeout(this.timers.hide)):"mouse"===f.position.target&&f.hide.event&&f.show.target&&!e.closest(f.show.target[0]).length&&this.hide(a),c.toggleClass(Z,b)}),q("["+S+"]",V,o)}),x._trigger=function(a,b,c){var e=d.Event("tooltip"+a);return e.originalEvent=c&&d.extend({},c)||this.cache.event||D,this.triggering=B,this.tooltip.trigger(e,[this].concat(b||[])),this.triggering=C,!e.isDefaultPrevented()},x._assignEvents=function(){var c=this.options,e=c.position,f=this.tooltip,g=c.show.target,h=c.hide.target,i=e.container,j=e.viewport,k=d(b),l=(d(b.body),d(a)),q=c.show.event?d.trim(""+c.show.event).split(" "):[],r=c.hide.event?d.trim(""+c.hide.event).split(" "):[],s=[];/mouse(out|leave)/i.test(c.hide.event)&&"window"===c.hide.leave&&this._bind(k,["mouseout","blur"],function(a){/select|option/.test(a.target.nodeName)||a.relatedTarget||this.hide(a)}),c.hide.fixed?h=h.add(f.addClass(W)):/mouse(over|enter)/i.test(c.show.event)&&this._bind(h,"mouseleave",function(){clearTimeout(this.timers.show)}),(""+c.hide.event).indexOf("unfocus")>-1&&this._bind(i.closest("html"),["mousedown","touchstart"],function(a){var b=d(a.target),c=this.rendered&&!this.tooltip.hasClass($)&&this.tooltip[0].offsetWidth>0,e=b.parents(U).filter(this.tooltip[0]).length>0;b[0]===this.target[0]||b[0]===this.tooltip[0]||e||this.target.has(b[0]).length||!c||this.hide(a)}),"number"==typeof c.hide.inactive&&(this._bind(g,"qtip-"+this.id+"-inactive",o),this._bind(h.add(f),w.inactiveEvents,o,"-inactive")),r=d.map(r,function(a){var b=d.inArray(a,q);return b>-1&&h.add(g).length===h.length?(s.push(q.splice(b,1)[0]),void 0):a}),this._bind(g,q,m),this._bind(h,r,n),this._bind(g,s,function(a){(this.tooltip[0].offsetWidth>0?n:m).call(this,a)}),this._bind(g.add(f),"mousemove",function(a){if("number"==typeof c.hide.distance){var b=this.cache.origin||{},d=this.options.hide.distance,e=Math.abs;(e(a.pageX-b.pageX)>=d||e(a.pageY-b.pageY)>=d)&&this.hide(a)}this._storeMouse(a)}),"mouse"===e.target&&e.adjust.mouse&&(c.hide.event&&this._bind(g,["mouseenter","mouseleave"],function(a){this.cache.onTarget="mouseenter"===a.type}),this._bind(k,"mousemove",function(a){this.rendered&&this.cache.onTarget&&!this.tooltip.hasClass($)&&this.tooltip[0].offsetWidth>0&&this.reposition(a)})),(e.adjust.resize||j.length)&&this._bind(d.event.special.resize?j:l,"resize",p),e.adjust.scroll&&this._bind(l.add(e.container),"scroll",p)},x._unassignEvents=function(){var c=[this.options.show.target[0],this.options.hide.target[0],this.rendered&&this.tooltip[0],this.options.position.container[0],this.options.position.viewport[0],this.options.position.container.closest("html")[0],a,b];this.rendered?this._unbind(d([]).pushStack(d.grep(c,function(a){return"object"==typeof a}))):d(c[0]).unbind("."+this._id+"-create")},w=d.fn.qtip=function(a,b,e){var f=(""+a).toLowerCase(),g=D,i=d.makeArray(arguments).slice(1),j=i[i.length-1],k=this[0]?d.data(this[0],Q):D;return!arguments.length&&k||"api"===f?k:"string"==typeof a?(this.each(function(){var a=d.data(this,Q);if(!a)return B;if(j&&j.timeStamp&&(a.cache.event=j),!b||"option"!==f&&"options"!==f)a[f]&&a[f].apply(a,i);else{if(e===c&&!d.isPlainObject(b))return g=a.get(b),C;a.set(b,e)}}),g!==D?g:this):"object"!=typeof a&&arguments.length?void 0:(k=h(d.extend(B,{},a)),w.bind.call(this,k,j))},w.bind=function(a,b){return this.each(function(e){function f(a){function b(){k.render("object"==typeof a||g.show.ready),h.show.add(h.hide).unbind(j)}return k.disabled?C:(k.cache.event=d.extend({},a),k.cache.target=a?d(a.target):[c],g.show.delay>0?(clearTimeout(k.timers.show),k.timers.show=setTimeout(b,g.show.delay),i.show!==i.hide&&h.hide.bind(i.hide,function(){clearTimeout(k.timers.show)})):b(),void 0)}var g,h,i,j,k,l;return l=d.isArray(a.id)?a.id[e]:a.id,l=!l||l===C||l.length<1||w.api[l]?w.nextid++:l,j=".qtip-"+l+"-create",k=r(d(this),l,a),k===C?B:(w.api[l]=k,g=k.options,d.each(P,function(){"initialize"===this.initialize&&this(k)}),h={show:g.show.target,hide:g.hide.target},i={show:d.trim(""+g.show.event).replace(/ /g,j+" ")+j,hide:d.trim(""+g.hide.event).replace(/ /g,j+" ")+j},/mouse(over|enter)/i.test(i.show)&&!/mouse(out|leave)/i.test(i.hide)&&(i.hide+=" mouseleave"+j),h.show.bind("mousemove"+j,function(a){k._storeMouse(a),k.cache.onTarget=B}),h.show.bind(i.show,f),(g.show.ready||g.prerender)&&f(b),void 0)})},w.api={},d.each({attr:function(a,b){if(this.length){var c=this[0],e="title",f=d.data(c,"qtip");if(a===e&&f&&"object"==typeof f&&f.options.suppress)return arguments.length<2?d.attr(c,ab):(f&&f.options.content.attr===e&&f.cache.attr&&f.set("content.text",b),this.attr(ab,b))}return d.fn["attr"+_].apply(this,arguments)},clone:function(a){var b=(d([]),d.fn["clone"+_].apply(this,arguments));return a||b.filter("["+ab+"]").attr("title",function(){return d.attr(this,ab)}).removeAttr(ab),b}},function(a,b){if(!b||d.fn[a+_])return B;var c=d.fn[a+_]=d.fn[a];d.fn[a]=function(){return b.apply(this,arguments)||c.apply(this,arguments)}}),d.ui||(d["cleanData"+_]=d.cleanData,d.cleanData=function(a){for(var b,c=0;(b=d(a[c])).length;c++)if(b.attr(R))try{b.triggerHandler("removeqtip")}catch(e){}d["cleanData"+_].apply(this,arguments)}),w.version="2.1.1",w.nextid=0,w.inactiveEvents=V,w.zindex=15e3,w.defaults={prerender:C,id:C,overwrite:B,suppress:B,content:{text:B,attr:"title",title:C,button:C},position:{my:"top left",at:"bottom right",target:C,container:C,viewport:C,adjust:{x:0,y:0,mouse:B,scroll:B,resize:B,method:"flipinvert flipinvert"},effect:function(a,b){d(this).animate(b,{duration:200,queue:C})}},show:{target:C,event:"mouseenter",effect:B,delay:90,solo:C,ready:C,autofocus:C},hide:{target:C,event:"mouseleave",effect:B,delay:0,fixed:C,inactive:C,leave:"window",distance:C},style:{classes:"",widget:C,width:C,height:C,def:B},events:{render:D,move:D,show:D,hide:D,toggle:D,visible:D,hidden:D,focus:D,blur:D}};var fb,gb="margin",hb="border",ib="color",jb="background-color",kb="transparent",lb=" !important",mb=!!b.createElement("canvas").getContext,nb=/rgba?\(0, 0, 0(, 0)?\)|transparent|#123456/i,ob={},pb=["Webkit","O","Moz","ms"];mb||(createVML=function(a,b,c){return"<qtipvml:"+a+' xmlns="urn:schemas-microsoft.com:vml" class="qtip-vml" '+(b||"")+' style="behavior: url(#default#VML); '+(c||"")+'" />'}),d.extend(v.prototype,{init:function(a){var b,c;c=this.element=a.elements.tip=d("<div />",{"class":Q+"-tip"}).prependTo(a.tooltip),mb?(b=d("<canvas />").appendTo(this.element)[0].getContext("2d"),b.lineJoin="miter",b.miterLimit=100,b.save()):(b=createVML("shape",'coordorigin="0,0"',"position:absolute;"),this.element.html(b+b),a._bind(d("*",c).add(c),["click","mousedown"],function(a){a.stopPropagation()},this._ns)),a._bind(a.tooltip,"tooltipmove",this.reposition,this._ns,this),this.create()},_swapDimensions:function(){this.size[0]=this.options.height,this.size[1]=this.options.width},_resetDimensions:function(){this.size[0]=this.options.width,this.size[1]=this.options.height},_useTitle:function(a){var b=this.qtip.elements.titlebar;return b&&(a.y===I||a.y===M&&this.element.position().top+this.size[1]/2+this.options.offset<b.outerHeight(B))},_parseCorner:function(a){var b=this.qtip.options.position.my;return a===C||b===C?a=C:a===B?a=new y(b.string()):a.string||(a=new y(a),a.fixed=B),a},_parseWidth:function(a,b,c){var d=this.qtip.elements,e=hb+s(b)+"Width";return(c?u(c,e):u(d.content,e)||u(this._useTitle(a)&&d.titlebar||d.content,e)||u(tooltip,e))||0},_parseRadius:function(a){var b=this.qtip.elements,c=hb+s(a.y)+s(a.x)+"Radius";return BROWSER.ie<9?0:u(this._useTitle(a)&&b.titlebar||b.content,c)||u(b.tooltip,c)||0},_invalidColour:function(a,b,c){var d=a.css(b);return!d||c&&d===a.css(c)||nb.test(d)?C:d},_parseColours:function(a){var b=this.qtip.elements,c=this.element.css("cssText",""),e=hb+s(a[a.precedance])+s(ib),f=this._useTitle(a)&&b.titlebar||b.content,g=this._invalidColour,h=[];return h[0]=g(c,jb)||g(f,jb)||g(b.content,jb)||g(tooltip,jb)||c.css(jb),h[1]=g(c,e,ib)||g(f,e,ib)||g(b.content,e,ib)||g(tooltip,e,ib)||tooltip.css(e),d("*",c).add(c).css("cssText",jb+":"+kb+lb+";"+hb+":0"+lb+";"),h},_calculateSize:function(a){var b,c,d,e=a.precedance===F,f=this.options[e?"height":"width"],g=this.options[e?"width":"height"],h="c"===a.abbrev(),i=f*(h?.5:1),j=Math.pow,k=Math.round,l=Math.sqrt(j(i,2)+j(g,2)),m=[this.border/i*l,this.border/g*l];return m[2]=Math.sqrt(j(m[0],2)-j(this.border,2)),m[3]=Math.sqrt(j(m[1],2)-j(this.border,2)),b=l+m[2]+m[3]+(h?0:m[0]),c=b/l,d=[k(c*f),k(c*g)],e?d:d.reverse()},_calculateTip:function(a){var b=this.size[0],c=this.size[1],d=Math.ceil(b/2),e=Math.ceil(c/2),f={br:[0,0,b,c,b,0],bl:[0,0,b,0,0,c],tr:[0,c,b,0,b,c],tl:[0,0,0,c,b,c],tc:[0,c,d,0,b,c],bc:[0,0,b,0,d,c],rc:[0,0,b,e,0,c],lc:[b,0,b,c,0,e]};return f.lt=f.br,f.rt=f.bl,f.lb=f.tr,f.rb=f.tl,f[a.abbrev()]},create:function(){var a=this.corner=(mb||BROWSER.ie)&&this._parseCorner(this.options.corner);return(this.enabled=!!this.corner&&"c"!==this.corner.abbrev())&&(this.qtip.cache.corner=a.clone(),this.update()),this.element.toggle(this.enabled),this.corner},update:function(a,b){if(!this.enabled)return this;var c,e,f,g,h,i,j,k=(this.qtip.elements,this.element),l=k.children(),m=this.options,n=this.size,o=m.mimic,p=Math.round;a||(a=this.qtip.cache.corner||this.corner),o===C?o=a:(o=new y(o),o.precedance=a.precedance,"inherit"===o.x?o.x=a.x:"inherit"===o.y?o.y=a.y:o.x===o.y&&(o[a.precedance]=a[a.precedance])),e=o.precedance,a.precedance===E?this._swapDimensions():this._resetDimensions(),c=this.color=this._parseColours(a),c[1]!==kb?(j=this.border=this._parseWidth(a,a[a.precedance]),m.border&&1>j&&(c[0]=c[1]),this.border=j=m.border!==B?m.border:j):this.border=j=0,g=this._calculateTip(o),i=this.size=this._calculateSize(a),k.css({width:i[0],height:i[1],lineHeight:i[1]+"px"}),h=a.precedance===F?[p(o.x===J?j:o.x===L?i[0]-n[0]-j:(i[0]-n[0])/2),p(o.y===I?i[1]-n[1]:0)]:[p(o.x===J?i[0]-n[0]:0),p(o.y===I?j:o.y===K?i[1]-n[1]-j:(i[1]-n[1])/2)],mb?(l.attr(G,i[0]).attr(H,i[1]),f=l[0].getContext("2d"),f.restore(),f.save(),f.clearRect(0,0,3e3,3e3),f.fillStyle=c[0],f.strokeStyle=c[1],f.lineWidth=2*j,f.translate(h[0],h[1]),f.beginPath(),f.moveTo(g[0],g[1]),f.lineTo(g[2],g[3]),f.lineTo(g[4],g[5]),f.closePath(),j&&("border-box"===tooltip.css("background-clip")&&(f.strokeStyle=c[0],f.stroke()),f.strokeStyle=c[1],f.stroke()),f.fill()):(g="m"+g[0]+","+g[1]+" l"+g[2]+","+g[3]+" "+g[4]+","+g[5]+" xe",h[2]=j&&/^(r|b)/i.test(a.string())?8===BROWSER.ie?2:1:0,l.css({coordsize:n[0]+j+" "+(n[1]+j),antialias:""+(o.string().indexOf(M)>-1),left:h[0]-h[2]*Number(e===E),top:h[1]-h[2]*Number(e===F),width:n[0]+j,height:n[1]+j}).each(function(a){var b=d(this);b[b.prop?"prop":"attr"]({coordsize:n[0]+j+" "+(n[1]+j),path:g,fillcolor:c[0],filled:!!a,stroked:!a}).toggle(!(!j&&!a)),!a&&b.html(createVML("stroke",'weight="'+2*j+'px" color="'+c[1]+'" miterlimit="1000" joinstyle="miter"'))})),b!==C&&this.calculate(a)},calculate:function(a){if(!this.enabled)return C;var b,c,e,f=this,g=this.qtip.elements,h=this.element,i=this.options.offset,j=(this.qtip.tooltip.hasClass("ui-widget"),{});return a=a||this.corner,b=a.precedance,c=this._calculateSize(a),e=[a.x,a.y],b===E&&e.reverse(),d.each(e,function(d,e){var h,k,l;e===M?(h=b===F?J:I,j[h]="50%",j[gb+"-"+h]=-Math.round(c[b===F?0:1]/2)+i):(h=f._parseWidth(a,e,g.tooltip),k=f._parseWidth(a,e,g.content),l=f._parseRadius(a),j[e]=Math.max(-f.border,d?k:i+(l>h?l:-h)))}),j[a[b]]-=c[b===E?0:1],h.css({margin:"",top:"",bottom:"",left:"",right:""}).css(j),j},reposition:function(a,b,d){if(this.enabled){var e,f,g=b.cache,h=this.corner.clone(),i=d.adjusted,j=b.options.position.adjust.method.split(" "),k=j[0],l=j[1]||j[0],m={left:C,top:C,x:0,y:0},n={};this.corner.fixed!==B&&(k===O&&h.precedance===E&&i.left&&h.y!==M?h.precedance=h.precedance===E?F:E:k!==O&&i.left&&(h.x=h.x===M?i.left>0?J:L:h.x===J?L:J),l===O&&h.precedance===F&&i.top&&h.x!==M?h.precedance=h.precedance===F?E:F:l!==O&&i.top&&(h.y=h.y===M?i.top>0?I:K:h.y===I?K:I),h.string()===g.corner.string()||g.cornerTop===i.top&&g.cornerLeft===i.left||this.update(h,C)),e=this.calculate(h,i),e.right!==c&&(e.left=-e.right),e.bottom!==c&&(e.top=-e.bottom),e.user=this.offset,(m.left=k===O&&!!i.left)&&(h.x===M?n[gb+"-left"]=m.x=e[gb+"-left"]-i.left:(f=e.right!==c?[i.left,-e.left]:[-i.left,e.left],(m.x=Math.max(f[0],f[1]))>f[0]&&(d.left-=i.left,m.left=C),n[e.right!==c?L:J]=m.x)),(m.top=l===O&&!!i.top)&&(h.y===M?n[gb+"-top"]=m.y=e[gb+"-top"]-i.top:(f=e.bottom!==c?[i.top,-e.top]:[-i.top,e.top],(m.y=Math.max(f[0],f[1]))>f[0]&&(d.top-=i.top,m.top=C),n[e.bottom!==c?K:I]=m.y)),this.element.css(n).toggle(!(m.x&&m.y||h.x===M&&m.y||h.y===M&&m.x)),d.left-=e.left.charAt?e.user:k!==O||m.top||!m.left&&!m.top?e.left:0,d.top-=e.top.charAt?e.user:l!==O||m.left||!m.left&&!m.top?e.top:0,g.cornerLeft=i.left,g.cornerTop=i.top,g.corner=h.clone()
}},destroy:function(){this.qtip._unbind(this.qtip.tooltip,this._ns),this.qtip.elements.tip&&this.qtip.elements.tip.find("*").remove().end().remove()}}),fb=P.tip=function(a){return new v(a,a.options.style.tip)},fb.initialize="render",fb.sanitize=function(a){a.style&&"tip"in a.style&&(opts=a.style.tip,"object"!=typeof opts&&(opts=a.style.tip={corner:opts}),/string|boolean/i.test(typeof opts.corner)||(opts.corner=B))},z.tip={"^position.my|style.tip.(corner|mimic|border)$":function(){this.create(),this.qtip.reposition()},"^style.tip.(height|width)$":function(a){this.size=size=[a.width,a.height],this.update(),this.qtip.reposition()},"^content.title|style.(classes|widget)$":function(){this.update()}},d.extend(B,w.defaults,{style:{tip:{corner:B,mimic:C,width:6,height:6,border:B,offset:0}}}),P.viewport=function(c,d,e,f,g,h,i){function j(a,b,c,e,f,g,h,i,j){var k=d[f],m=p[a],n=q[a],o=c===O,r=-w.offset[f]+v.offset[f]+v["scroll"+f],s=m===f?j:m===g?-j:-j/2,t=n===f?i:n===g?-i:-i/2,u=y&&y.size?y.size[h]||0:0,x=y&&y.corner&&y.corner.precedance===a&&!o?u:0,z=r-k+x,A=k+j-v[h]-r+x,B=s-(p.precedance===a||m===p[b]?t:0)-(n===M?i/2:0);return o?(x=y&&y.corner&&y.corner.precedance===b?u:0,B=(m===f?1:-1)*s-x,d[f]+=z>0?z:A>0?-A:0,d[f]=Math.max(-w.offset[f]+v.offset[f]+(x&&y.corner[a]===M?y.offset:0),k-B,Math.min(Math.max(-w.offset[f]+v.offset[f]+v[h],k+B),d[f]))):(e*=c===N?2:0,z>0&&(m!==f||A>0)?(d[f]-=B+e,l.invert(a,f)):A>0&&(m!==g||z>0)&&(d[f]-=(m===M?-B:B)+e,l.invert(a,g)),d[f]<r&&-d[f]>A&&(d[f]=k,l=p.clone())),d[f]-k}var k,l,m,n=e.target,o=c.elements.tooltip,p=e.my,q=e.at,r=e.adjust,s=r.method.split(" "),t=s[0],u=s[1]||s[0],v=e.viewport,w=e.container,x=c.cache,y=c.plugins.tip,z={left:0,top:0};return v.jquery&&n[0]!==a&&n[0]!==b.body&&"none"!==r.method?(k="fixed"===o.css("position"),v={elem:v,width:v[0]===a?v.width():v.outerWidth(C),height:v[0]===a?v.height():v.outerHeight(C),scrollleft:k?0:v.scrollLeft(),scrolltop:k?0:v.scrollTop(),offset:v.offset()||{left:0,top:0}},w={elem:w,scrollLeft:w.scrollLeft(),scrollTop:w.scrollTop(),offset:w.offset()||{left:0,top:0}},("shift"!==t||"shift"!==u)&&(l=p.clone()),z={left:"none"!==t?j(E,F,t,r.x,J,L,G,f,h):0,top:"none"!==u?j(F,E,u,r.y,I,K,H,g,i):0},l&&x.lastClass!==(m=Q+"-pos-"+l.abbrev())&&o.removeClass(c.cache.lastClass).addClass(c.cache.lastClass=m),z):z},P.polys={polygon:function(a,b){var c,d,e,f={width:0,height:0,position:{top:1e10,right:0,bottom:0,left:1e10},adjustable:C},g=0,h=[],i=1,j=1,k=0,l=0;for(g=a.length;g--;)c=[parseInt(a[--g],10),parseInt(a[g+1],10)],c[0]>f.position.right&&(f.position.right=c[0]),c[0]<f.position.left&&(f.position.left=c[0]),c[1]>f.position.bottom&&(f.position.bottom=c[1]),c[1]<f.position.top&&(f.position.top=c[1]),h.push(c);if(d=f.width=Math.abs(f.position.right-f.position.left),e=f.height=Math.abs(f.position.bottom-f.position.top),"c"===b.abbrev())f.position={left:f.position.left+f.width/2,top:f.position.top+f.height/2};else{for(;d>0&&e>0&&i>0&&j>0;)for(d=Math.floor(d/2),e=Math.floor(e/2),b.x===J?i=d:b.x===L?i=f.width-d:i+=Math.floor(d/2),b.y===I?j=e:b.y===K?j=f.height-e:j+=Math.floor(e/2),g=h.length;g--&&!(h.length<2);)k=h[g][0]-f.position.left,l=h[g][1]-f.position.top,(b.x===J&&k>=i||b.x===L&&i>=k||b.x===M&&(i>k||k>f.width-i)||b.y===I&&l>=j||b.y===K&&j>=l||b.y===M&&(j>l||l>f.height-j))&&h.splice(g,1);f.position={left:h[0][0],top:h[0][1]}}return f},rect:function(a,b,c,d){return{width:Math.abs(c-a),height:Math.abs(d-b),position:{left:Math.min(a,c),top:Math.min(b,d)}}},_angles:{tc:1.5,tr:7/4,tl:5/4,bc:.5,br:.25,bl:.75,rc:2,lc:1,c:0},ellipse:function(a,b,c,d,e){var f=P.polys._angles[e.abbrev()],g=c*Math.cos(f*Math.PI),h=d*Math.sin(f*Math.PI);return{width:2*c-Math.abs(g),height:2*d-Math.abs(h),position:{left:a+g,top:b+h},adjustable:C}},circle:function(a,b,c,d){return P.polys.ellipse(a,b,c,c,d)}},P.imagemap=function(a,b,c){b.jquery||(b=d(b));var e,f,g,h=b.attr("shape").toLowerCase().replace("poly","polygon"),i=d('img[usemap="#'+b.parent("map").attr("name")+'"]'),j=b.attr("coords"),k=j.split(",");if(!i.length)return C;if("polygon"===h)result=P.polys.polygon(k,c);else{if(!P.polys[h])return C;for(g=-1,len=k.length,f=[];++g<len;)f.push(parseInt(k[g],10));result=P.polys[h].apply(this,f.concat(c))}return e=i.offset(),e.left+=Math.ceil((i.outerWidth(C)-i.width())/2),e.top+=Math.ceil((i.outerHeight(C)-i.height())/2),result.position.left+=e.left,result.position.top+=e.top,result},P.svg=function(a,c,e){for(var f,g,h,j=d(b),k=c[0],l={};!k.getBBox;)k=k.parentNode;if(!k.getBBox||!k.parentNode)return C;switch(k.nodeName){case"rect":g=P.svg.toPixel(k,k.x.baseVal.value,k.y.baseVal.value),h=P.svg.toPixel(k,k.x.baseVal.value+k.width.baseVal.value,k.y.baseVal.value+k.height.baseVal.value),l=P.polys.rect(g[0],g[1],h[0],h[1],e);break;case"ellipse":case"circle":g=P.svg.toPixel(k,k.cx.baseVal.value,k.cy.baseVal.value),l=P.polys.ellipse(g[0],g[1],(k.rx||k.r).baseVal.value,(k.ry||k.r).baseVal.value,e);break;case"line":case"polygon":case"polyline":for(points=k.points||[{x:k.x1.baseVal.value,y:k.y1.baseVal.value},{x:k.x2.baseVal.value,y:k.y2.baseVal.value}],l=[],i=-1,len=points.numberOfItems||points.length;++i<len;)next=points.getItem?points.getItem(i):points[i],l.push.apply(l,P.svg.toPixel(k,next.x,next.y));l=P.polys.polygon(l,e);break;default:if(f=k.getBBox(),mtx=k.getScreenCTM(),root=k.farthestViewportElement||k,!root.createSVGPoint)return C;point=root.createSVGPoint(),point.x=f.x,point.y=f.y,tPoint=point.matrixTransform(mtx),l.position={left:tPoint.x,top:tPoint.y},point.x+=f.width,point.y+=f.height,tPoint=point.matrixTransform(mtx),l.width=tPoint.x-l.position.left,l.height=tPoint.y-l.position.top}return l.position.left+=j.scrollLeft(),l.position.top+=j.scrollTop(),l},P.svg.toPixel=function(a,b,c){var d,e,f=a.getScreenCTM(),g=a.farthestViewportElement||a;return g.createSVGPoint?(e=g.createSVGPoint(),e.x=b,e.y=c,d=e.matrixTransform(f),[d.x,d.y]):C}})}(window,document);