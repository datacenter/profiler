/* ***********************************************************
   ******************** CODETABS SCRIPT **********************
   *********************************************************** */


/* NOTES
Index function
 > var
 > init
 > m
 > prop
 > render
 > load
 > image
 > thumbnail
 > api
 > position
 > size
 > slideTo
 > events
 > update
 > show
 > fxFunc
 > layer
 > video
 > map
 > res
 > fullscreen
 > slideshow
 > timer
 > apis


Abbreviate value :
 + ds: shortcut 'dash', varible for layout dash
 + st: shortcut 'slideTo', store value in method slideTo()
 + xt: x value of touch
 + px: vi tri x, y
 + m : shortcut method function
 + va.wRes : shortcut of width responsive
 + va: đối tượng lưu trữ value, nếu key muốn giữ ở 'o' thì luu trữ trên 'va.'
 + is.ts: có hỗ trợ transition css3 hay không
 + va.tlx0/1 : shorutcut string 'translateX' & ')'
 + is.tl3d : shortcut isTranslate3d
 + is.into : check slider into window.document
 + o.hCode != hCode.
 + da: shortcut data store
 + o.idEnd: id slide sau khi transition, layout dash cung vay.
 + va.can, va.pag: luu tru gia tri de ho tro swipe gesture, bao gom: xCanvas --> x, wTranslate --> w
 + va.can.w: thay the cho wTranslate
 + px: bien tap hop kich thuoc, chieu dai --> deu lien quan toi don vi pixel
 + ti: shortcut timer id
 + is.cenLoop : ket hop giua o.isCenter va o.isLoop --> tiet kiem size
 + oo: options last --> ho tro luu lai option cu khi update option bang api


Slider center position
 + Begin: Sap sep id slide --> dat slide current nam giua va translate de dang
 + SlideTo: ...
 + Update: ...

 Chu viet hoa tuong trung cho viet tat hay la function
************************************************************** */

;(function($) {
    'use strict';

    // Khoi tao plugin
    var PLUGIN = $.csPlugin;
    if( !PLUGIN ) PLUGIN = $.csPlugin = {};

    // Bien chung cho moi plugin
    if( !window.codeVar ) window.codeVar = {};


    $.codetabs = function(element, o) {


        /* Varibles global --> giup code ngan hon
        ================================================== */
        var $cs = element,
            $w  = $(window), $doc = $(document),

            cs  = {},
            va  = {},
            ds  = {},
            px  = {},
            is  = {},
            da  = {},
            ti  = {},
            oo  = $.extend(true, {}, o),                    // Luu tru option luc dau
            one = { 'o': o, 'va': va, 'ti': ti },           // Ho tro plugin
            
            MATH      = Math,
            UNDEFINED = undefined,
            codekey   = MATH.ceil( MATH.random()*1e9 ),     // Codekey for codetab --> tranh xung dot multi code

            $canvas, $viewport, $s,
            $nav, $prev, $next, $playpause, $media,
            $pag, $pagItem, $pagInner, $thumbItem, $timer, $timerItem, $cap,

            num,
            speed, delay,
            hCode, wViewport, rCanvas, wTranslate,
            xCanvas,
            cssTf, cssTs, cssD, cssD0, cssD1, cssT, cssAD, cssAT, tranEnd,
            tDelay, xTimer,
            i, j,

            divdiv = '<div></div>';


        
        /* Init
        ================================================== */
        var INIT = {

            check : function() {
                cs.ev.trigger('init');                  // Callback event begin init
                M.setupFirst();                         // Setup bien dau tien ki init code
                M.browser();                            // Browser detect --> nam o tren phuc vu cho proto.ajax
                M.cssName();                            // CSS: get prefixed css style
                PROP.get();                             // Slider: get properties in data
                PROP.codeNested();                      // Kiem tra nested

                // Kiem tra phien ban
                if( NOISREV.check() ) {

                    // // Kiem tra ajax image load
                    // if( o.flickr.photosetID ) flickr.photoset();

                    // // Kiem tra slider co doi tuong con hay khong
                    // else INIT.pre();
                    // Delay de ho tro get size dung trong nested
                    if( is.nestedParent ) setTimeout(INIT.pre, 200);
                    else                  INIT.pre();
                }
                else $cs.remove();
            },


            // Kiem tra slider co o che do sleep(option 'show', 'showFrom') hay khong
            pre : function() {
                if( $cs.children().length ) {
                    SHOW.get();                                     // Show: get properties

                    // Check func 
                    if( is.show ) {

                        // Setup bien is.awake --> ho tro option showFrom
                        !!o.showFrom && SHOW.check();

                        // Chuyen sang INIT.ready hoac dang ki event resize
                        is.awake ? INIT.ready() : SHOW.resizeON();
                    }

                    // Slide se remove neu thiet bi khong dung
                    else $cs.remove();
                }
            },


            ready : function() {
                RENDER.structure();                                     // Slider: create canvas
                PROP.slider();                                          // Slider: get properties
                                                                        // --> o tren RENDER.pag vi can thuoc tinh is.pag truoc

                is.pag && RENDER.pag();                                 // Slider: render Pagnation
                is.nav && RENDER.nav();                                 // Slider: render Navigation
                o.isCap && RENDER.cap();                                // Slider: render Caption

                !$playpause && o.slideshow.isPlayPause
                && o.isSlideshow && RENDER.play();                      // Slider: render playpause
                RENDER.timer();                                         // Slider: render Timer

                PROP.slide();                                           // Slide: properties, below RENDER.pag() --> can $pagItem dinh nghia truoc
                RENDER.other();                                         // Slider: render other elements

                // Load slider dau tien
                LOAD.slideBegin($s.eq(cs.idCur));
            },


            load : function() {
                is.initLoaded = 1;                                      // Bien luu gia tri slider da show
                $cs.addClass(o.ns +'ready').removeClass(o.ns +'init');  // Slider da san sang --> hien slider

                is.pag && !is.pagList && PAG.sizeItem();                // Ho tro cho fn ben duoi 'SIZE.general()'
                SIZE.general();                                         // Slide: Setup other elements (can` height slider neu huong la 'vertical')

                cs.idCur == 0 && cs.ev.trigger('start');                // Event start
                o.layout == 'dash' ? M.toggleDash()
                                   : M.toggle();                        // First-item: add Actived

                is.nav && EVENTS.nav();                                 // Navigation: click event
                is.pag && EVENTS.pag();                                 // Pagnation: click event
                EVENTS.resize();                                        // Window resize
                EVENTS.swipe();
                EVENTS.click();
                EVENTS.keyboard();
                EVENTS.mousewheel();
                EVENTS.pageLoaded();
                DEEPLINKING.events();                                   // Deeplink events

                o.isSlideshow && SLIDESHOW.init();                      // Can bien va.hWin truoc o EVENTS.resize()
                PROP.initEnd();                                         // Setup nhung thu con lai sau init end
            }
        },





        /* Methods
        ================================================== */
        M = {

            /* Setup nhung bien cho he tong code
            ---------------------------------------------- */
            setupFirst : function() {

                // Id cua code --> ho tro nhieu code trong page
                var codeNum = codeVar.num;
                if( codeNum == UNDEFINED ) codeNum = 0;
                else                       codeNum++;

                va.codeID = codeVar.num = codeNum;

                // Luu tru codekey --> get codekey tu fn ngoai
                va.codekey = codekey;

                // id timer cua tat ca layer --> loai bo 1 luc tat ca de dang
                ti.layer = [];

                // Su dung cho slideshow co video va map --> tat ca video phai dong thi slideshow tiep tuc duoc
                va.nVideoOpen = va.nMapOpen = 0;

                // Mac dinh mo khoa click event
                is.click = 1;

                // Ten hieu ung --> ho tro toggle class hieu ung
                va.fxLast = va.fxCur = 'none';

                // Them class khac nhau vao code tuy theo moi slide
                va.classAdd = [];
            },



            /* Browser detect va kiem tra ho tro thuoc tinh html5
            ---------------------------------------------- */
            browser : function() {

                // Bien shortcut va khoi tao ban dau
                var navAgent = navigator.userAgent;

                is.ie = /*@cc_on!@*/false || document.documentMode;     // At least IE6
                is.safari = /Constructor/i.test(Object.prototype.toString.call(window.HTMLElement));
                is.opera = !!window.opera || /\sOPR\//i.test(navAgent);
                is.chrome = !!window.chrome && !is.opera;               // Chrome 1+
                is.firefox = window.InstallTrigger !== UNDEFINED;

                // Kiem tra ie11 --> ie11 khong ho tro 'conditional compilation' nua
                is.ie11 = !!(is.ie && !new Function('/*@cc_on return @_jscript_version; @*/')());
                is.ie7  = !!(is.ie && /MSIE\s7\./i.test(navAgent));

                
                // Ten cua browser - neu khong tim dc tra ve UNDEFINED
                var browser = ['ie', 'safari', 'opera', 'chrome', 'firefox'];
                for (i = browser.length; i >= 0; i--) {
                    if( !!is[browser[i]] ) { is.browser = browser[i]; break; }
                }

                // Kiem tra browser ho tro 'console'
                is.e = typeof console === 'object';

                // Kiem tra browser co ho tro html5.canvas
                is.canvas2d = (function() {
                    var el = document.createElement('canvas');
                    return !!(el.getContext && el.getContext('2d'));
                }());

                // Kiem tra browser co ho tro touch event
                is.msGesture = !!(window.navigator && window.navigator.msPointerEnabled && window.MSGesture);
                is.evTouch = !!(("ontouchstart" in window) || (window.DocumentTouch && document instanceof DocumentTouch));
                is.evPointer = !!window.PointerEvent;
                is.evMSPointer = !!window.MSPointerEvent;
                is.touchSupport = is.evTouch || is.evPointer || is.evMSPointer;

                // Kiem tra co phai mobile, dua tren 3 yeu to:
                // + Ho tro touch/pointer events
                // + Ho tro huong xoay "orientation" --> tren mobile simular khong ho tro
                // + UserAgent thuoc cac loai trinh duyen thong dung hien nay "Android|webOS|iPhone|iPad ...."
                is.mobile = is.touchSupport
                && /Android|webOS|iPhone|iPod|BlackBerry|IEMobile|Opera|Windows\sPhone|Chrome|PSP|Dolphin|Silk/i.test(navAgent);

                // Kiem tra co phai Android native browser(khac Chrome) va version < 4.4
                is.androidNative = is.mobile && /Mozilla\/5\.0/i.test(navAgent) && /Android/i.test(navAgent)
                                             && /AppleWebKit/i.test(navAgent) && !(/Chrome/i.test(navAgent))
                                             && !(/Android\s+4\.4/i.test(navAgent));


                // Setup ten cua tat ca loai event
                var suffix    = '.code' + codekey,
                    touchName = ['', '', ''];
                
                if     ( is.evTouch )     touchName = ['touchstart', 'touchmove', 'touchend'];
                else if( is.evPointer )   touchName = ['pointerdown', 'pointermove', 'pointerup'];
                else if( is.evMSPointer ) touchName = ['MSPointerDown', 'MSPointerMove', 'MSPointerUp'];

                va.ev = {
                    'click' : 'click'     + suffix,
                    'drag'  : 'dragstart' + suffix,
                    'resize': 'resize'    + suffix,
                    'hash'  : 'hashchange'+ suffix,

                    'touch' : {
                        'start' : touchName[0] + suffix,
                        'move'  : touchName[1] + suffix,
                        'end'   : touchName[2] + suffix,
                        'type'  : 'touch' },

                    'mouse' : {
                        'start' : 'mousedown' + suffix,
                        'move'  : 'mousemove' + suffix,
                        'end'   : 'mouseup'   + suffix,
                        'type'  : 'mouse' }
                };
            },



            /* CSS Name - prefixed
            ---------------------------------------------- */
            cssName : function() {

                /* Kiem tra co ho tro tranform3d hay khong
                 * @lorenzopolidori: https://gist.github.com/lorenzopolidori/3794226 */
                var transform3dCheck = function() {
                    var el         = document.createElement('p'),
                        transforms = {
                            'webkitTransform' : '-webkit-transform',
                            'MozTransform'    : '-moz-transform',
                            'transform'       : 'transform'
                        },
                        has3d;

                    // Add it to the body to get the computed style
                    document.body.insertBefore(el, null);

                    for(var t in transforms){
                        if( el.style[t] !== UNDEFINED ){
                            el.style[t] = 'translate3d(1px,1px,1px)';
                            has3d = window.getComputedStyle(el).getPropertyValue(transforms[t]);
                        }
                    }
                    document.body.removeChild(el);
                    return (has3d !== UNDEFINED && has3d.length > 0 && has3d !== "none");
                }
                if( codeVar.isTf3D == UNDEFINED ) codeVar.isTf3D = transform3dCheck();



                // Ham kiem tra prefix browser
                // Opts: ['is', 'pre']
                var prefixCheck = function(property, opts) {
                    var style  = document.createElement('p').style,
                        vender = ['Webkit','Moz', 'ms', 'O'],           // Browser tra lai vender
                        prefix = ['-webkit-', '-moz-', '-ms-', '-o-'];  // Chuyen doi vender thanh prefix css

                    if( style[property] === '' )
                        return opts == 'is' ? 1 : (opts == 'pre' ? '' : property);

                    
                    property = property.charAt(0).toUpperCase() + property.slice(1);
                    for (i = vender.length; i--; ) { 
                        if( style[vender[i] + property] === '' )
                            return (opts == 'is') ? 1 : (opts == 'pre' ? prefix[i] : vender[i] + property);
                    }
                    return 0;       // Browser khong co prefixed
                };




                /* Kiem tra prefix va bien transfrom co ban */
                var tf = 'transform',
                    ts = 'transition';

                // CSS check
                is.ts = prefixCheck(ts, 'is');

                // Store prefix support transition
                if( is.ts ) {
                    var prefix = va.prefix = prefixCheck(tf, 'pre'),
                        timing = '-timing-function';
                    
                    cssTf = prefix + tf;
                    cssTs = prefixCheck(ts, 'pre') + ts;
                    cssD  = cssTs + '-duration';
                    cssT  = cssTs + timing;
                    cssAD = prefix + 'animation-duration';
                    cssAT = prefix + 'animation' + timing;
                }



                // Translate type: fix in safari mobile and ie
                // Shortcut translate begin/end
                var tl3D   = 'translate3d(',
                    isTf3D = codeVar.isTf3D;

                va.tl0   = isTf3D ? tl3D        : 'translate(';
                va.tl1   = isTf3D ? ',0)'       : ')';
                va.tlx0  = isTf3D ? tl3D        : 'translateX(';
                va.tlx1  = isTf3D ? ',0,0)'     : ')';
                va.tly0  = isTf3D ? tl3D +'0,'  : 'translateY(';
                va.tly1  = isTf3D ? ',0)'       : ')';
            },


            easeName : function(name) {

                // Easing linear
                if( name == 'linear' ) return 'linear';

                // Modern browser ho tro css3
                if( is.ts ) {

                    // Easing swing
                    if( name == 'swing' ) return 'ease';

                    var ease; name = name.replace('ease', '');
                    switch( name ) {
                        case 'InSine'       : ease = '.47,0,.745,.715'; break;
                        case 'OutSine'      : ease = '.39,.575,.565,1'; break;
                        case 'InOutSine'    : ease = '.445,.05,.55,.95'; break;

                        case 'InQuad'       : ease = '.55,.085,.68,.53'; break;
                        case 'OutQuad'      : ease = '.25,.46,.45,.94'; break;
                        case 'InOutQuad'    : ease = '.455,.03,.515,.955'; break;

                        case 'InCubic'      : ease = '.55,.055,.675,.19'; break;
                        case 'OutCubic'     : ease = '.215,.61,.355,1'; break;
                        case 'InOutCubic'   : ease = '.645,.045,.355,1'; break;

                        case 'InQuart'      : ease = '.895,.03,.685,.22'; break;
                        case 'OutQuart'     : ease = '.165,.84,.44,1'; break;
                        case 'InOutQuart'   : ease = '.77,0,.175,1'; break;

                        case 'InQuint'      : ease = '.755,.05,.855,.06'; break;
                        case 'OutQuint'     : ease = '.23,1,.32,1'; break;
                        case 'InOutQuint'   : ease = '.86,0,.07,1'; break;

                        case 'InExpo'       : ease = '.95,.05,.795,.035'; break;
                        case 'OutExpo'      : ease = '.19,1,.22,1'; break;
                        case 'InOutExpo'    : ease = '1,0,0,1'; break;

                        case 'InCirc'       : ease = '.6,.04,.98,.335'; break;
                        case 'OutCirc'      : ease = '.075,.82,.165,1'; break;
                        case 'InOutCirc'    : ease = '.785,.135,.15,.86'; break;

                        case 'InBack'       : ease = '.6,-.28,.735,.045'; break;
                        case 'OutBack'      : ease = '.175,.885,.32,1.275'; break;
                        case 'InOutBack'    : ease = '.68,-.55,.265,1.55'; break;

                        case 'InElastic'    :
                        case 'OutElastic'   :
                        case 'InOutElastic' :

                        case 'InBounce'     :
                        case 'OutBounce'    :
                        case 'InOutBounce'  :

                        default : ease = '.25,.1,.25,1';
                    }
                    return 'cubic-bezier(' + ease + ')';
                }

                // Old browser: ho tro jQuery easing
                // Kiem tra plugin easing va ten easing co ho add vao chua --> neu khong su dung easing mac dinh 'swing'
                else {
                    if( !!$.easing && !!$.easing[name] ) return name;
                    else                                 return 'swing';
                }
            },



            /* Slider & pag: toggle class
             * Bien isFirstActived --> ho tro nhan bien lan dau active sau khi tai xong slide dau tien
            ---------------------------------------------- */
            toggle : function() {

                // Bien shortcut va khoi tao ban dau
                var idCur   = cs.idCur,
                    idLast  = cs.idLast,
                    $slCur  = $s.eq(idCur),
                    $slLast = $s.eq(idLast),
                    current = o.ns + o.current;


                // Slide: toggle class actived
                // $s.not($slCur).removeClass(current);
                $slLast.removeClass(current);
                $slCur.addClass(current);


                // Callback event toggle
                idLast != UNDEFINED && cs.ev.trigger('deselectID', idLast);
                cs.ev.trigger('selectID', idCur);
                

                // Pag: toggle class actived
                // Su dung phuong phap cu, tuong tu o tren!
                if( is.pag ) {

                    // var $pagCur = $pagItem.eq(idCur);
                    // $pagItem.not($pagCur).removeClass(current);
                    // $pagCur.addClass(current);
                    $pagItem.eq(idLast).removeClass(current);
                    $pagItem.eq(idCur).addClass(current);
                }

                // Nav: toggle class inactive
                if( is.nav ) {
                    var inActived = o.ns + o.inActived;

                    if( !o.isLoop ) {
                        if( idCur == 0 )     $prev.addClass(inActived);
                        if( idCur == num-1 ) $next.addClass(inActived);
                        
                        if( idCur != 0 && $prev.hasClass(inActived) )     $prev.removeClass(inActived);
                        if( idCur != num-1 && $next.hasClass(inActived) ) $next.removeClass(inActived);
                    }
                    else {
                        if( $prev.hasClass(inActived) ) $prev.removeClass(inActived)
                        if( $next.hasClass(inActived) ) $next.removeClass(inActived)
                    }
                }


                // Cap: toggle Content
                o.isCap && CAPTION.toggle($slCur, $slLast);

                // Load slide hien tai dang xem, mac du chua toi luot phai load
                LOAD.add($slCur);

                // Toggle Height native
                o.height == 'auto' && SLIDETO.swapHNative();

                // Toggle classAdd tren code
                CLASSADD.toggle();

                // Toggle Deeplink & Cookie
                // Them dieu kien: idLast != undefined --> ngan can luc dau fire function
                if( idLast != UNDEFINED ) {
                    o.isDeeplinking && DEEPLINKING.write();
                    o.isCookie && COOKIE.write();
                }
            },



            toggleDash : function() {

                // Pag: toggle Class current
                if( is.pag ) {

                    // id pag: tim kiem id bat dau hien thi tren slider
                    var i       = ds.pagNum-1,
                        current = o.ns + o.current;
                    while ( ds.nEnd < ds.pagID[i] ) { i-- }

                    // pagitem: toggle class
                    var $pActived = $pagItem.eq(ds.pagID[i]);
                    $pagItem.not($pActived).removeClass(current);
                    $pActived.addClass(current);
                }


                // Nav: toggle class inactive
                if( is.nav && !o.isLoop ) {
                    var inActived = o.ns + o.inActived;

                    if( ds.nBegin == 0 )   $prev.addClass(inActived);
                    if( ds.nEnd == num-1 ) $next.addClass(inActived);

                    if( ds.nBegin != 0 && $prev.hasClass(inActived) )   $prev.removeClass(inActived);
                    if( ds.nEnd != num-1 && $next.hasClass(inActived) ) $next.removeClass(inActived);
                }
            },



            toggleFree : function() {

                var OUT = o.fName + '-out',
                    IN  = o.fName + '-in',

                    $slCur  = $s.eq(cs.idCur),
                    $slLast = $s.eq(cs.idLast);


                if( $slCur.hasClass(OUT) ) $slCur.removeClass(OUT);
                if( !$slCur.hasClass(IN) ) $slCur.addClass(IN);

                if( !$slLast.hasClass(OUT) ) $slLast.addClass(OUT);
                if( $slLast.hasClass(IN) )   $slLast.removeClass(IN);
            },


            /* Cac truong hop:
                + is = -1: loai bo het class
                + is = 0: toggle sang class[0]
                + is = 1: toggle sang class[1]
            */
            toggleClass : function(type, _is) {

                // if( type == 'swipe' ) {
                //     var $console = $('#console');
                //     $console.html( $console.html() +' ADD CLASS SWIPE '+ '<br/>');
                // }
                

                // Toi uu tren mobile --> khong can toggle class 'grabbing'
                if( !(type == 'grab' && is.mobile) ) {

                    // Bien shortcut va khoi tao ban dau, them namespace
                    var c     = o.className[type],
                        c0    = o.ns + c[0],
                        c1    = o.ns + c[1],
                        add   = _is ? c0 : c1,
                        del   = _is ? c1 : c0,
                        $view = M.sSwap().viewport;         // Shortcut $viewport


                    // is = -1 --> REMOVE ALL
                    if( _is == -1 ) {
                        $view.hasClass(c0) && $view.removeClass(c0);
                        $view.hasClass(c1) && $view.removeClass(c1);
                    }
                    // is = 0 --> chuyen sang class[0]
                    // is = 1 --> chuyen sang class[1]
                    else {
                        !$view.hasClass(add) && $view.addClass(add);
                        $view.hasClass(del)  && $view.removeClass(del);
                    }
                }
            },





            /* CSS: get value
            ---------------------------------------------- */
            valueX : function(str) {

                // Array: get value
                var a = str.substr(7, str.length - 8).split(', ');

                // Array: return value 5
                return parseInt( a[4] );
            },


            /* Properties: get Value of string
             * CAN TOI UU MA CODE !!!
            ---------------------------------------------- */
            valueName : function(str, prefix, back, aName, isArray) {

                // Bien global --> viet hoa de phan biet bien trong func()
                var HEX  = '#[0-9a-f]{3}([0-9a-f]{3})?'
                  , OPA  = '(\\s*(1|0?\\.?\\d*))?\\s*'
                  , RGBA = '(rgba|rgb)\\(\\s*((\\d{1,2}|1\\d+\\d|2([0-4]\\d|5[0-5]))\\s*,?){3}'+ OPA +'\\)'
                  , HSLA = '(hsla|hsl)\\(\\s*(\\d{1,2}|[1-2]\\d{2}|3[0-5]\\d)\\s*,(\\s*(\\d{1,2}|100)\\%\\s*,?){2}' + OPA +'\\)'
                  , TEXT = '((\u0022|\u0027).*(\u0022|\u0027)\\-?){1,3}'                    // Complex string with mark' or mark"
                  , ARR  = '(\\u005B.*\\u005D\\-?){1,3}'
                  // , OBJ  = '(\\u007B.*\\u007D\\-?){1,3}'
                  , OBJ  = '(\\u007B.*\\u007D\\-?){1,3}'
                  , NSTR = '((\\d*\\.?\\d+)|\\w*)(\\-+((\\d*\\.?\\d+)|\\w*)){0,3}'          // Number Float and String


                  // Uu tien value phuc tap truoc
                  // Ki tu nao cung them dau //
                  , reStr   = prefix + '\\-+('+ HEX +'|'+ RGBA +'|'+ HSLA +'|'+ OBJ +'|'+ ARR +'|'+ TEXT +'|'+ NSTR +')'
                  , reText  = /^(\u0022|\u0027).*(\u0022|\u0027)$/g
                  , reArr   = /^\u005B.*\u005D$/g
                  , reObj   = /^\u007B.*\u007D$/g
                  , csStr   = 'csStoreStr'          // Tu khoa de thay de cho chuoi trong objParse va arrParse
                  , re, value;


                // check first
                var checkFirst = function(v) {
                    v = v.replace(prefix + '-', '');   // Get value only

                    // opt Array: check exist
                    if( typeof aName == 'object' ) {
                        if( aName.indexOf(v) != -1 ) return v;


                        // Hien thong bao khong trung ten co trong onlyName
                        else {
                            var msg = '[ codetabs: no value name \u0027' +v + '\u0027 ]';
                            is.e && console.log(msg);
                            return back;
                        }
                    }
                    return M.pFloat(v);
                };

                // Function focus only on layer data
                var layerParse = function(v) {

                    if( /\w+\-+\w+/g.test(v) ) {
                        var n = str.match(/\-+\d*\.?\d*/g);

                        // Value la number int | float
                        if( n[0] != '-' && n[1] != '-' ) {
                            for (var i = n.length-1; i >= 0; i--) {

                                n[i] = n[i].replace(/^\-/g, '');
                                n[i] = parseFloat(n[i]);
                            }
                        }

                        // Value la string
                        else {
                            n = str.match(/\-+\w+/g);

                            for (var i = n.length-1; i >= 0; i--) {
                                n[i] = n[i].replace(/^\-/g, '');
                                n[i] = M.pFloat(n[i]);
                            }
                        }
                        v = n;
                    }

                    return M.pFloat(v);
                };


                // Kiem tra loai cua du lieu
                var checkType = function(v) {

                    if     ( reObj.test(v) ) v = objParse(v);
                    else if( reArr.test(v) ) v = arrParse(v);
                    else if( reText.test(v)) v = textParse(v);
                    return v;
                };



                // Tao vong lap de tim kiem chuoi va luu tru chuoi do vao trong mang _aStrore
                // Muc dich de dang tach cach doi tuong bang dau ',' ma khong lan lan co trong mang
                var strConvert = function(v) {
                    var aStore = [],                        // Mang de luu lai tat ca cac chuoi
                        isLeft = 1,                         // Kiem tra thay the ben trai hay khong
                        index  = v.indexOf('\u0027');       // Index bat dau "'"

                    while (index != -1) {

                        // Dau "'" ben trai --> thay the bang dau '<<'
                        if( isLeft) {
                            v = v.replaceAt('\u00AB', index);
                            isLeft = 0;
                        }

                        // Dau "'" ben phai --> thay the bang dau '>>'
                        // Setup hau het o day.
                        else {
                            v = v.replaceAt('\u00BB', index);
                            isLeft = 1;

                            // Vi tri cua dau '<<' va '>>' --> phuc vu cac func sau
                            var i0 = v.indexOf('\u00AB');
                            var i1 = v.indexOf('\u00BB');

                            // Tach chuoi, loai bo dau "'", va luu tru trong mang aStore
                            var sub = v.substr(i0+1, i1-i0-1);
                            aStore.push(sub);

                            // String chinh duoc thay the bang tu khoa
                            v = v.replaceAt(csStr, i0, i1-i0+1);
                        }

                        // Tiep tuc tim kiem vi tri cua dau "'" cho toi khi khong con
                        index = v.indexOf('\u0027');
                    }

                    // Tra ve doi tuong luu tri va value
                    return { 'array': aStore, 'value': v };
                };

                var objParse = function(v) {

                    // Setup chi lay chuoi truoc dau '}' dau tien --> con lai loai bo
                    // Sau do loai bo dau '{' dau tien
                    v = v.substr(0, v.indexOf('\u007D')).replace(/^\u007B/g, '');


                    // Thay the chuoi trong v
                    // Loai bo khoang cach ' ' va tach mang bang dau ','
                    var raw = strConvert(v),
                        pre = raw['value'].replace(/\s+/g, '').split(',');

                    // Thu tu object quan trong
                    var obj = {};
                    for (var i = 0, len = pre.length; i < len; i++) {

                        // Tach chuoi bang dau ':'
                        var out = pre[i].split(':');

                        // Neu raw khong co dau ':' thi tra ve gia tri null
                        if( out.length == 1 ) out[1] = null;


                        // Convert lai thanh chuoi da luu tru neu trung ten 'csStrStore'
                        if( out[0] == csStr ) out[0] = raw['array'].shift();
                        if( out[1] == csStr ) out[1] = raw['array'].shift();

                        // Var Obj: chuyen thanh number(neu phai) va them name va value vao doi tuong --> convert hoan tat
                        obj[out[0]] = M.pFloat(out[1]);
                    }

                    return obj;
                };

                var arrParse = function(v) {

                    // Setup chi lay chuoi truoc dau ']' dau tien --> con lai loai bo
                    // Sau do loai bo dau '{[' dau tien
                    v = v.substr(0, v.indexOf('\u005D')).replace(/^\u005B/g, '');


                    // Thay the chuoi trong v
                    // Loai bo khoang cach ' ' va tach mang bang dau ','
                    var raw = strConvert(v),
                        out = raw['value'].replace(/\s+/g, '').split(',');


                    // Thu tu object quan trong
                    var arr = [];
                    for (var i = 0, len = out.length; i < len; i++) {

                        // Convert lai thanh chuoi da luu tru neu trung ten 'csStrStore'
                        if( out[i] == csStr ) out[i] = raw['array'].shift();

                        // Chuyen thanh number (neu phai)
                        out[i] = M.pFloat(out[i]);

                        // Var Obj: them name va value vao doi tuong
                        arr.push(out[i]);
                    }

                    return arr;
                };

                var textParse = function(v) {

                    // Loai bo dau "'"
                    v = v.replace(/(^\u0027|\u0027$)/g, '');

                    // Tach 2 chuoi ra, neu chi co 1 chuoi, chuyen thanh string
                    var t = v.split(/\u0027\-\u0027/g);
                    if( t.length == 1 ) t = t[0];

                    return t;
                };




                // Value: kiem tra
                re = new RegExp(reStr, 'g');
                value = str.match(re);
                // if(prefix == 'thumb') console.log(value, str);

                // Truong hop value khac null
                if( value != null ) {

                    // Mac dinh value tra ve la array nhu 'media' --> phuc vu cho truong hop tong the
                    var _length = value.length, V = [];
                    for (i = 0; i < _length; i++) {

                        // Kiem tra va tach prefix va value boi dau '-', tra ve value lay duoc
                        V[i] = checkFirst(value[i]);
                        // if(prefix == 'thumb') console.log(V[i]);

                        
                        // Kiem tra gia tri value moi tra ve,
                        // neu la number --> convert ra number, convert lan 2
                        // neu la gia tri rong --> tra ve gia tri 'back'
                        if( V[i] == '' && back != UNDEFINED) V[i] = back;
                        V[i] = M.pFloat(V[i]);


                        // Tach du lieu cho data layer, function rieng do su phuc tap cua trong data layer
                        if( !!isArray ) V[i] = layerParse(V[i]);
                    }


                    // Neu Array chi co 1 gia tri --> loai bo array
                    if( _length == 1 ) V = V[0];

                    // Neu gia tri la text phuc tap, object hoac la mang --> convert lai cho dung.
                    V = checkType(V);


                    // Tra lai ket qua cuoi cung
                    return V;
                }

                // Truong hop Value la null, khong co gia tri, tra ve gia tri 'back' hoac boolean
                else {
                    if( back != UNDEFINED ) return back;
                    else                    return false;
                }
            },



            /* Lay gia tri cao nhat trong cac doi tuong
            ---------------------------------------------- */
            valueMax : function($arr, attr, opt) {
                var max = 0, value;

                for (i = $arr.length-1; i >= 0; i--) {

                    // Lay gia tri cac thuoc tinh
                    // Co cach nao tiet kiem so sanh hay khong ???
                    value = opt ? $arr.eq(i)[attr](opt) : $arr.eq(i)[attr]();

                    // Kiem tra margin co gia tri hay khong --> cach nay ho tro ie7+ (khong co gia tri tra ve 'auto')
                    value = M.pInt(value);

                    // So sanh de lay gia tri lon nhat
                    if( value > max ) max = value;

                    // Kiem tra width trong cac trinh duyet khac nhau
                    // if( attr == 'width') console.log('value '+ value +' '+ $arr.eq(i)[0].offsetWidth);
                }
                return max;
            },





            /* Array.indexOf: fixed =< IE8
            ---------------------------------------------- */
            proto : {
                arrayIndex : function() {

                    Array.prototype.indexOf = function(elt) {
                        var len = this.length >>> 0
                          , from =  0;

                        for (; from < len; from++) {
                          if (from in this && this[from] === elt)
                            return from;
                        }
                        return -1;
                    }
                },

                replaceAt : function() {

                    // Thay the ki tu bang func substr
                    // Mac dinh thay the 1 ki tu, them tuy chon so luong ki tu thay the
                    String.prototype.replaceAt = function(_newStr, _index, _nChar) {
                        // Mac dinh thay the 1 ki tu
                        if( _nChar == UNDEFINED ) _nChar = 1;

                        return this.substr(0, _index) + _newStr + this.substr(_index + _nChar);
                    }
                },

                ajax : function() {

                    // Plugin created by MoonScript
                    if (!$.support.cors && $.ajaxTransport && window.XDomainRequest) {
                        var sameSchemeRegEx = new RegExp('^'+location.protocol, 'i');

                        // ajaxTransport exists in jQuery 1.5+
                        $.ajaxTransport('* text html xml json', function(options, userOptions, jqXHR){

                            // XDomainRequests must be: asynchronous, GET or POST methods, HTTP or HTTPS protocol, and same scheme as calling page
                            if (options.crossDomain && options.async && /^get|post$/i.test(options.type) && /^https?:\/\//i.test(options.url) && sameSchemeRegEx.test(options.url)) {
                                var xdr = null;
                                var userType = (userOptions.dataType||'').toLowerCase();

                                window.isXDomainRequest = 1;    // Bien thong bao da add plugins
                                return {

                                    send: function(headers, complete) {
                                        xdr = new XDomainRequest();

                                        if (/^\d+$/.test(userOptions.timeout)) {
                                            xdr.timeout = userOptions.timeout;
                                        }

                                        xdr.ontimeout = function(){ complete(500, 'timeout') };
                                        xdr.onload = function() {
                                            var allResponseHeaders = 'Content-Length: ' + xdr.responseText.length + '\r\nContent-Type: ' + xdr.contentType;
                                            var status = {
                                                code: 200,
                                                message: 'success'
                                            };

                                            var responses = { text: xdr.responseText };
                                            try {

                                                if (userType === 'html' || /text\/html/i.test(xdr.contentType)) {
                                                    responses.html = xdr.responseText;
                                                }

                                                else if (userType === 'json' || (userType !== 'text' && /\/json/i.test(xdr.contentType))) {
                                                    try {
                                                        responses.json = $.parseJSON(xdr.responseText);
                                                    } catch(e) {
                                                        status.code = 500;
                                                        status.message = 'parseerror';
                                                        //throw 'Invalid JSON: ' + xdr.responseText;
                                                    }
                                                }

                                                else if (userType === 'xml' || (userType !== 'text' && /\/xml/i.test(xdr.contentType))) {
                                                    var doc = new ActiveXObject('Microsoft.XMLDOM');
                                                    doc.async = false;

                                                    try { doc.loadXML(xdr.responseText) }
                                                    catch(e) { doc = UNDEFINED }

                                                    if (!doc || !doc.documentElement || doc.getElementsByTagName('parsererror').length) {
                                                        status.code = 500;
                                                        status.message = 'parseerror';
                                                        throw 'Invalid XML: ' + xdr.responseText;
                                                    }
                                                    responses.xml = doc;
                                                }
                                            }
                                            catch(parseMessage) { throw parseMessage }
                                            finally { complete(status.code, status.message, responses, allResponseHeaders) }
                                        };

                                        // set an empty handler for 'onprogress' so requests don't get aborted
                                        xdr.onprogress = function() {};
                                        xdr.onerror = function(){
                                            complete(500, 'error', { text: xdr.responseText});
                                        };

                                        var postData = '';
                                        if (userOptions.data) {
                                            postData = ($.type(userOptions.data) === 'string') ? userOptions.data : $.param(userOptions.data);
                                        }
                                        xdr.open(options.type, options.url);
                                        xdr.send(postData);
                                    },

                                    abort: function() { xdr && xdr.abort() }
                                };
                            }
                        });
                    }
                }
            },




            scroll : {

                setup : function() {

                    // Truong hop options slideshow chi run khi o trong vung nhin thay
                    if( o.slideshow.isRunInto ) {
                        is.into = 0;
                        M.scroll.check();

                        var t = 200;
                        $w.on('scroll.code'+ codekey, function() {
                            M.tc(ti.scroll);
                            ti.scroll = setTimeout(function() { !is.pauseActive && M.scroll.check() }, t);
                        });
                    }
                    
                    // Truong hop slideshow run khong can trong vung nhin thay
                    else { is.into = 1; SLIDESHOW.go(); }
                },

                check : function(isNoGo) {
                    // console.log('scroll check', va.topW, va.topCS, va.botW, va.botCS);
                    M.scroll.position();

                    // Code: into window with vary 100px
                    // Voi chieu cao CODE lon hon hWindow --> headache!! xem lai
                    var isInto = (va.topW <= va.topCS + 100 && va.botW >= va.botCS - 100)
                              || ((hCode >= va.hWin) && (va.botW - 50 >= va.topCS && va.topW - 50 <= va.botCS));

                    if( isInto ) {
                        if( !is.into ) { is.into = 1; !isNoGo && SLIDESHOW.go(); }
                    }
                    else {
                        if( is.into ) { is.into = 0; !isNoGo && SLIDESHOW.go(); }
                    }
                },

                position : function() {

                    // Lay Vi tri top/bottom cua Window
                    va.topW = $w.scrollTop();
                    va.botW = va.hWin + va.topW;


                    // Slider offset
                    va.topCS = $cs.offset().top;
                    va.botCS = va.topCS + hCode;
                }
            },





            /* Short method and value
            ---------------------------------------------- */
            // Nhung ham lien quan den Math
            a     : function(v)         { return MATH.abs(v) },
            r     : function(v)         { return MATH.round(v) },
            c     : function(v)         { return MATH.ceil(v) },
            ra    : function()          { return MATH.random() },
            rm    : function(_m,_n)     { return M.ra()*(_n-_m)+_m },
            raExcept : function(nBegin, nEnd, nExcept) {
                var nRandom;

                // Truong hop Except name o DAU hay CUOI
                if     ( nExcept == nBegin ) nRandom = M.rm(nBegin+1, nEnd);
                else if( nExcept == nEnd)    nRandom = M.rm(nBegin, nEnd-1);

                // Truong Except nam o GIUA
                // Tach lam 2 mang khong co so Except --> chon so random tu 1 trong 2 mang
                // Truong hop mang co begin = end --> chon thang luon so begin hoac end
                else {
                    var nBegin1 = nBegin,
                        nEnd1   = nExcept - 1,
                        nBegin2 = nExcept + 1,
                        nEnd2   = nEnd;

                    nRandom = M.ra() >= .5 ? (nBegin1 == nEnd1 ? nBegin1 : M.ra(nBegin1, nEnd1))
                                           : (nBegin2 == nEnd2 ? nEnd2   : M.ra(nBegin2, nEnd2));
                }

                // Tra ve so random
                return M.r(nRandom);
            },

            // Ham lien quan den transition
            cssD1 : function()          { cssD1[cssD] = speed[cs.idCur] + 'ms'; },
            tl    : function(x,y,u)     { var u = u ? u : 'px'; return va.tl0 + x + u +', ' + y + u + va.tl1; },

            // Translate x/y , ho tro fallback transition
            tlx   : function(x,u)       { var u = u ? u : 'px'; return is.ts ? (va.tlx0 + x + u + va.tlx1) : (x + u); },
            tly   : function(y,u)       { var u = u ? u : 'px'; return is.ts ? (va.tly0 + y + u + va.tly1) : (y + u); },

            // Add hay remove transition tren doi tuong co dinh
            tsAdd : function($obj, sp, es)   {
                var ts = {};
                if(!es) es = va.ease;

                ts[cssTs] = cssTf +' '+ sp +'ms '+ es;
                $obj.css(ts);
            },
            tsRemove : function($obj) {
                var ts = {}; ts[cssTs] = 'none';            // transition == none moi co hieu qua tren firefox va IE
                $obj.css(ts);
                setTimeout(function() { ts[cssTs] = ''; $obj.css(ts) }, 0);
            },
            tfRemove : function($obj) { var tf = {}; tf[cssTf] = ''; $obj.css(tf); },
            ts    : function(p, s, a, d) {
                a = a ? ' ' + a : '';
                d = d ? ' ' + d + 'ms' : '';
                var t = {}; t[cssTs] = p + ' ' + s + 'ms' + a + d;
                return t;
            },

            // Kiem tra va convert thanh so float
            // Voi dieu kien < 9007199254740992 --> lon hon ket qua khong dung
            pFloat : function(n) {
                if( /^\-?\d*\.?\d+$/g.test(n) ) {
                    var n1 = parseFloat(n);
                    if (n1 < 9007199254740992 ) return n1;
                }

                // + them kiem tra co phai boolean hay khong
                else if( /(^false$)|(^off$)/g.test(n) ) n = false;
                // else if( /(^true$)|(^on$)/g.test(n) )   n = true;    // --> tiet kiem size --> boolean 'string' luon bang true
                return n;
            },

            // Chuyen doi gia tri thuoc tinh lay boi css() sang so nguyen
            pInt : function(v) { return /^\-?\d+/g.test(v) ? parseInt(v) : 0; },

            // Function for array object
            shift : function($obj, isShift) { isShift ? $obj.shift() : $obj.pop() },
            push  : function($obj, v, isPush) { isPush ? $obj.push(v) : $obj.unshift(v) },

            // Swipe swap varible
            sSwap : function() { var p = $canvas.is(va.swipeCur) ? va.can : va.pag; return p; },

            // ClearTimeout shortcut
            tc : function(t) { return clearTimeout(t) }
        },






        /* Properties
        ================================================== */
        PROP = {

            /* Get & Split properties
            ---------------------------------------------- */
            get : function() {

                // prototype: Array.indexOf && String.replaceAt
                !Array.prototype.indexOf && M.proto.arrayIndex();
                !String.prototype.replaceAt && M.proto.replaceAt();
                is.ie && !window.isXDomainRequest && M.proto.ajax();


                // Slider options
                var onlyName = {
                        layout    : ['line', 'dot', 'dash', 'free'],
                        view      : ['basic', 'coverflow', 'scale', 'mask'],
                        // fx        : o.fxName,
                        height    : ['auto', 'fixed'],
                        imgWidth  : ['none', 'autofit', 'smallfit', 'largefit'],
                        imgHeight : ['none', 'autofit', 'smallfit', 'largefit'],
                        img       : ['autofit', 'autofill', 'smallfit', 'largefit', 'smallfill', 'largefill'],
                        timer     : ['none', 'bar', 'arc', 'number'],
                        dirs      : ['hor', 'ver']
                    };


                // Options: tach va nhap gia tri vao option goc
                var opts = {};
                PROP.split(opts, $cs.attr('data-'+ o.dataSlider), onlyName);
                o = $.extend(true, o, opts);

                // Add bieu tuong tren DOM --> de nhan biet codetabs, ho tro nested
                // $cs.attr('data-code', '');
            },


            split : function(opts, data, onlyName, isShort) {

                // isShort: vua get short value, vua convert to array
                if( data != UNDEFINED && data != '' ) {


                    // Bien str0 loai bo dau enter cung voi khoang trang di chung voi nhau == 1 khoang trang
                    // Bien str1 la ban sao str0 --> loai bo khoang trang va setup khong anh huong den str0
                    var str0 = data.replace(/\s*\n+\s*/g, ' '),
                        str1 = data.replace(/\s+/g, ' ').replace(/^\s*|\s*$/g, '').split(' ');


                    for (var i = 0, len = str1.length; i < len; i++) {

                        var prefix = str1[i].match(/^\w*/g)[0],             // Shortcut name prefix
                            hyphen = str1[i].indexOf('-'),                  // Value bat buoc phai co dau '-'
                            v      = str1[i].replace(prefix + '-', '');     // Shortcut value


                        // Kiem tra: bat buoc phai co dau '-' && value phai khac empty string
                        if( hyphen != -1 && v != '' ) {

                            var only = onlyName[prefix],                    // Shortcut onlyName
                                s    = isShort ? str1[i] : str0;            // Short value, string only get small range, focus in layer


                            // Value la gia tri boolean 'is'
                            if( prefix.substr(0, 2) == 'is' ) {
                                if     ( 'ontrue1'.indexOf(v) !== -1 )  opts[prefix] = 1;
                                else if( 'offalse0'.indexOf(v) !== -1 ) opts[prefix] = 0;
                            }

                            // Value others
                            else {
                                only = (!!only) ? only : 0;
                                opts[prefix] = M.valueName(s, prefix, opts[prefix], only, isShort);
                            }
                        }
                    }
                    // Store value
                    if( !isShort ) opts.strData = str0;
                }
            },





            /* Chain : tach va setup chuoi phuc tap nhu 'media'
            ---------------------------------------------- */
            // Split and store value of varible have 3 value, i.e. 'media' 
            chain3 : function(c, vName) {

                // Convert to array again
                // Case 1: number -> chi 1 so, value dc uu tien hon responsive
                // Case 2: string -> only 1 gia tri
                if     ( typeof c == 'number' ) c = [c + '-0-100000'];
                else if( typeof c == 'string' ) c = [c];


                var value = { num : c.length },
                    wMax  = 0,                              // Gia tri cao nhat trong mang
                    name  = !!vName ? vName : 'value';      // Kiem tra value Name, mac dinh la 'value'

                for (i = value.num-1; i >= 0; i--) {

                    // Bo sung: tu dong bo sung var con thieu
                    if( typeof c[i] == 'number' ) c[i] += '-0-100000';

                    // Tach chuoi
                    var a = c[i].split('-');
                    value[i] = {
                        'from'  : parseInt(a[1]),
                        'to'    : parseInt(a[2])
                    };
                    value[i][name] = parseFloat(a[0]);      // included float number

                    // wMax: width-to maximum
                    wMax = (wMax < parseInt(a[2])) ? a[2] : wMax;
                }

                value.wMax = parseInt(wMax);
                return value;
            },

            // Tuong tu nhu chain, nhung array co 4 gia tri
            chain4 : function(c) {

                if     ( typeof c == 'number' ) c = [c + '-' + c + '-0-100000'];
                else if( typeof c == 'string' ) c = [c];


                var value = { num : c.length },
                    wMax  = 0;

                for (i = value.num-1; i >= 0; i--) {

                    // Bo sung: tu dong bo sung var con thieu
                    if( typeof c[i] == 'number' ) c[i] += '-' + c[i] + '-0-100000';

                    // Tach chuoi
                    var a = c[i].split('-');

                    // Case: auto set from/to
                    if( a.length == 2 ) { a[2] = 0; a[3] = 1e5; }

                    // Case: double first value -> value left = value right
                    else if( a.length == 3 ) { a.unshift(a[0]) }


                    // Array: set value
                    value[i] = {
                        'left'  : parseInt(a[0]),
                        'right' : parseInt(a[1]),
                        'from'  : parseInt(a[2]),
                        'to'    : parseInt(a[3])
                    };

                    // wMax: width-to maximum
                    wMax = (wMax < parseInt(a[3])) ? a[3] : wMax;
                }

                value.wMax = parseInt(wMax);
                return value;
            },





            /* Cac func nho trong func setup()
            ---------------------------------------------- */
            setupBegin : function() {

                // Setup var && store value luc Dau
                if( !is.setupInit ) {

                    // Luu tru height slider ban dau --> setup image backgrond center
                    o.height0 = o.height;

                    // Doi tuong swipe mac dinh la canvas
                    va.swipeCur = $canvas;

                    // Thuoc tinh cua canvas va pagination --> su dung cho swipe
                    va.can = { viewport: $viewport };
                    va.pag = {};            // Chua setup vi chua check isPag va $pag
                    is.swipeLimit = 0;

                    // Ho tro plugin
                    one.M    = M;
                    one.PROP = PROP;


                    // Add class ten browser firefox vao codetabs --> ho tro fix transform bang css
                    var ns    = ' '+ o.ns,
                        CLASS = '';
                    if( is.browser == 'firefox' ) CLASS += ns +'firefox';
                    if( is.ie7 )                  CLASS += ns +'ie7';
                    if( is.mobile )               CLASS += ns +'mobile';
                    if( is.androidNative )        CLASS += ns +'androidNative';
                    $cs.addClass(CLASS);
                }
            },


            swipeEvent : function() {

                // Setup hanh dong swipe --> Phan biet thanh swipeBody va swipePag
                // Neu tren mobile ma isMobile == false --> khong co swipe event
                if( o.isSwipe && !(is.mobile && !o.swipe.isMobile) ) {
                    is.swipePag  = 1;

                    // Setup tu dong swipe tren mobile neu tat o desktop
                    var oSwipe = o.swipe;
                    if( is.mobile && !!oSwipe.isBodyOnMobile && !oSwipe.isBody )
                         is.swipeBody = 1;
                    else is.swipeBody = !!oSwipe.isBody;
                }
                else { is.swipePag = is.swipeBody = 0 }
            },


            idNum : function() {

                // ID slide current setup
                // Tu dong chuyen doi idBegin thanh id slide cuoi neu gia tri la '-1'
                // Tu dong chuyen doi id thanh 0 neu lon hon so slide hoac nho hon 0
                var idBegin = o.idBegin;
                if     ( idBegin == -1 )                 idBegin = num-1;
                else if( idBegin < 0 || idBegin >= num ) idBegin = 0;
                if( !cs.idCur ) cs.idCur = o.idBegin = idBegin;

                // Tiep tuc update idCur va idBegin khi 'deeplink' va 'cookie' turn-on
                // Neu co 'deeplink' va 'cookie' cung luc --> uu tien cho 'deeplink'
                if( !is.setupInit ) {
                    if( o.isDeeplinking )    DEEPLINKING.read();
                    else if( o.isCookie ) COOKIE.read();
                }


                // Slide: only 1
                // Khoa cac thuoc tinh slider
                is.nav    = o.isNav;
                is.pag    = o.isPag;
                is.center = o.isCenter;

                if( num == 1 ) {
                    is.nav = is.center = 0;
                    if( o.pag.type != 'tab' ) is.pag = 0;
                }

                // Slide: only 2
                // Below type auto convert && above Layout line setting
                // when update isLoop --> error
                if( num == 2 && o.layout == 'line' ) is.center = 0;
            },


            center : function() {

                // Tao bien moi de so sanh cho de dang --> vua center vua loop (mac dinh)
                // Truong hop pagination la 'tab' --> load theo kieu binh thuong
                is.cenLoop = is.center && o.isLoop;


                // Slider center position
                if( is.cenLoop ) {

                    // Center: init
                    // va.center = PROP.chain3(o.wSlide, 'width');
                    va.center = {};

                    // Check number slide is odd or even
                    var center = va.center;
                    center.isNum = (M.c(num/2) > num/2) ? 'odd' : 'even';

                    // Id map
                    POSITION.centerIdMap();

                    // slide clone: reset
                    !!va.sClone && va.sClone.remove();
                    va.sClone = $('');


                    // So luong slide left/right --> luu vao namespace va.center
                    var nLeft  = ~~( (num-1)/2 ),
                        nRight = (center.isNum == 'odd') ? nLeft : nLeft + 1;

                    center.n = { 'left': nLeft, 'right': nRight };
                }

                // Update: reset varible
                else { va.center = null; o.isLoop = 0; }
            },


            slideshow : function() {

                // Timer
                var auto = o.slideshow;
                is.timer = !!(o.isSlideshow && auto.isTimer);  
                va.timer = (auto.timer == 'arc' && !is.canvas2d) ? 'bar' : auto.timer;

                // Setup autoRun --> autoRun cho false khi dong thoi co playpause va isAutoRun false
                is.autoRun = !(auto.isPlayPause && !auto.isAutoRun);
                is.pauseActive = !is.autoRun;
            },


            transform : function() {

                // CSS duration options
                // Note: before translateX() func
                cssD0 = {}; cssD1 = {};
                cssD0[cssD] = '';   // Before: '0s'
                xTimer = 100;


                // Canvas: set Transition timing function
                // Bien va.ease da ho tro browser fallback
                if( o.layout != 'free' ) {
                    va.ease = M.easeName(o.easeTouch);
                    is.ease = is.easeLast = 'touch';
                }
            },


            layout : function() {

                // Bien shortcut
                var layout = o.layout;

                // Layout Dot auto convert khi co effect
                if( layout == 'line' && (o.fx || o.fxIn || o.fxOne) ) o.layout = 'dot';

                // Layout free tu dong chuyen sang layoutFall khi khong ho tro CSS3
                else if( layout == 'free' && !is.ts ) o.layout = o.layoutFall;



                // Uu tien chuyen doi layout sang 'dot' neu co fx-LINE
                if( o.fx == 'line') o.layout = 'line';

                // Layout dash: properties
                if( layout == 'dash' ) {
                    is.thumb = 0;

                    // n shortcut 'number', p shortcut  'position'
                    ds.nBegin = 0;
                    ds.nEnd   = 0;
                    ds.pMax   = 0;

                    if( !is.setupInit ) ds.height = [];

                    // Loop: always is false
                    o.isLoop = 0;
                }

                // Layout line & dot
                else {
                    o.stepNav  = 1;
                    o.stepPlay = 1;
                }
            },


            fullscreen : function() {

                // Fullscreen setup
                if( o.isFullscreen ) {

                    // Height type auto convert
                    o.height = 'fixed';

                    // Offset by container setup
                    if( o.offsetBy != null ) {

                        if( typeof o.offsetBy == 'string' )
                            va.offsetBy = { 'top': o.offsetBy , 'bottom': null };

                        else if( typeof o.offsetBy == 'object' )
                            va.offsetBy = { 'top': o.offsetBy[0], 'bottom': o.offsetBy[1] };
                    }
                }
            },


            res : function() {

                // Responsive: get value
                var responsive = o.responsive;      // Shortcut o.responsive
                if( !!responsive ) {

                    if( typeof responsive == 'number' ) {
                        va.wRes = responsive;
                        va.hRes = 0;
                    }
                    else if( typeof responsive == 'string' ) {
                        var _r = responsive.split('-');
                        va.wRes = parseInt(_r[0]);
                        va.hRes = parseInt(_r[1]);

                        // Height type: auto convert
                        if( !!va.hRes ) o.height = 'fixed';
                    }

                    // Fullscreen: setup
                    if( o.isFullscreen ) {

                        // Height responsive : auto add value when not setup --> used for fullscreen 
                        if( va.hRes == 0 ) va.hRes = va.wRes;

                        // Ratio responsive
                        va.rRes = va.wRes / va.hRes;
                    }

                    // Update fix: height-type restore
                    // Khi update api, luc dau co hRes, nhung luc sau khong co hRes.
                    // Nhung khi co responsive ma muo'n height-fixed by css, cho nen --> loai bo this func
                    // if( is.setupInit && !va.hRes && o.height == 'fixed' ) o.height = 'auto';
                }
                is.res = !!responsive;




                // Media: setup
                if( !!o.media ) va.media = PROP.chain3(o.media);
                else            va.media = null;    // Func update: reset value

                // Padding: setup
                va.pa = { 'left': o.padding, 'top': 0 };    // va.pa always != UNDEFINED
                if( o.padding != 0 ) va.paRange = PROP.chain3(o.padding);
                else                 va.paRange = null;     // Func update: reset value

                // Margin: setup
                if( o.margin != 0 ) va.maRange = PROP.chain4(o.margin);
                else                va.maRange = null;      // Func update: reset value




                // Rate: init
                // Update fix: setup only one at init
                if( !is.setupInit ) {

                    if( is.res ) {

                        wViewport = $viewport.width();      // Setup responsive can co wViewport truoc

                        RES.varible();
                        va.rateLast = va.rate;              // Get rateLast at first va.rate setup
                    }
                    else va.rate = 1;
                }
                // Reupdate: neu khong responsive, va.rate luon luon = 1
                if( is.setupInit && !is.res )  va.rate = 1;
            },


            grab : function() {

                // Grab cursor: toggle class
                if( is.swipeBody ) M.toggleClass('grab', 1);
                else               M.toggleClass('grab', -1);

                // Grab stop
                if( o.isViewGrabStop ) $viewport.addClass(o.ns+'grabstop');
                else                   $viewport.removeClass(o.ns+'grabstop');
            },


            direction : function(oAdd) {

                // Swipe direction
                // Do o.dirs duoc dam bao co 2 gia tri 'hor' va 'ver' --> short setup
                // Bo sung oAdd --> Ho tro update VER TO HOR
                va.can.dirs = (o.dirs == 'ver' && !is.mobile) ? 'ver' : 'hor';
                if( !(oAdd && oAdd.pagDirs) ) va.pag.dirs = o.pag.dirs;


                // Bien cssTf fallback thay doi theo huong swipe --> xem xet loai bo
                // Chi su dung tren canvas
                if( !is.ts ) cssTf = (va.can.dirs == 'hor') ? 'left' : 'top';


                // Cac thuoc tinh canvas va pagination giong nhau
                var _sameValue = function(name) {
                    var isHor = va[name].dirs == 'hor';

                    // Ten transform, ho tro fallback
                    va[name].cssTf = is.ts ? cssTf
                                           : (isHor ? 'left' : 'top');

                    // Ten bien pageX thay doi theo huong trong canvas va pagination
                    va[name].pageX = isHor ? 'pageX' : 'pageY';
                };
                _sameValue('can');
                _sameValue('pag');


                // Loai bo thuoc tinh khong can thiet tren slides khi update direction
                var tf = { 'left' : '', 'top' : '' };
                $s.css(tf);
            },


            pagination : function() {

                // Setup cho pagination type free --> chi render khong co event
                is.pagList = o.pag.type == 'list';
                if( is.pagList ) is.swipePag = 0;


                // Kiem tra TAB VERTICAL
                var _isVerTab = function(opt, pag) {
                    return !is.pagOutside
                        && !is.pagList
                        && (opt.isPag && pag.type == 'tab' && pag.dirs == 'ver');
                };


                // Kiem tra loai Pagination TAB VERTICAL
                is.tabVer = _isVerTab(o, o.pag) && va.pag.dirs == 'ver' ? (o.pag.pos == 'top' ? 'top' : 'bottom')
                                                                        : null;

                // Reset MARGINs tren viewport neu truoc kia la TAB VERTICAL
                if( !!is.setupInit && _isVerTab(oo, oo.pag) ) {
                    $viewport.css({ 'margin-left': '', 'margin-right': '' });
                }
            },


            codeNested : function() {

                // Kiem tra trong code co code nested hay khong va`
                // Kiem tra code nay co phai nested child hay khong
                // --> ho tro khong cho swipe gesture trong code nested
                var codeSelect    = '[data-'+ o.dataSlider +']',
                    $nestedChild  = va.$nestedChild  = $cs.find(codeSelect),
                    $nestedParent = va.$nestedParent = $cs.parent().closest(codeSelect);

                is.nestedParent = !!$nestedChild.length;
                is.nestedChild  = !!$nestedParent.length;
            },


            setupEnd : function() {
                // Setup var && store value luc dau khi can nhung options o tren
                if( !is.setupInit ) {

                    // Loadway: init, chi run 1 lan
                    LOAD.way();
                }
                else {

                    // Update fixed: remove Viewport-height inline
                    if( o.height == 'fixed' ) $viewport.css('height', '');
                }

                // Loai bo cac options trong version free
                o.rev[0] == 'eerf' && NOISREV.eerf();
            },



            /* Setup
            ---------------------------------------------- */
            setup : function(oAdd) {
                PROP.setupBegin();

                num = $s.length;                                // Slide: number
                va.wRange = PROP.chain3(o.wSlide, 'width');     // Media chieu width cua slide


                PROP.swipeEvent();
                PROP.idNum();
                PROP.center();
                PROP.slideshow();


                // Kiem tra type pag co phai thumbnail
                is.thumb = o.pag.type === 'thumb' || o.pag.type ==='hover';

                // Speed, delay: minimun
                if( o.speed < 200 ) o.speed = 200;
                if( o.slideshow.delay < 500 ) o.slideshow.delay = 500;



                PROP.transform();
                PROP.layout();


                // Height type auto convert to fixed when have o.hCode
                if( !!o.hCode ) o.height = 'fixed';

                
                PROP.fullscreen();
                PROP.res();

                PROP.grab();
                PROP.direction(oAdd);
                PROP.pagination();                              // Nam duoi swipe event
                PROP.setupEnd();
            },





            /* Slider properties
            ---------------------------------------------- */
            slider : function(oAdd) {

                // Properties setup
                PROP.setup(oAdd);


                // Slider: clear datas after first setup slider
                !is.setupInit && $cs.removeAttr('data-' + o.dataSlider).removeData(o.dataSlider);

                // Varible to recognize call PROP.setup() run first
                if( is.setupInit == UNDEFINED ) is.setupInit = 1;

                // Add class khi setup xong properties
                UPDATE.addClass(oAdd);
            },






            /* Slide properties
            ---------------------------------------------- */
            slide : function() {

                // Bien shortcut va khoi tao ban dau
                var n       = 0,
                    aDelay  = [],
                    aSpeed  = [],
                    aFx     = [],
                    aFxType = [],
                    aFxEase = [],
                    aSlot   = [],

                    isLine  = o.layout == 'line',
                    fxName0 = isLine ? null : (o.fx || o.fxDefault),
                    fxType0 = 'js';


                // Hieu ung mac dinh --> Thu tu uu tien: fxOne > fxIn > fx
                if( o.fxOne != null ) {
                    fxName0 = o.fxOne;
                    fxType0 = 'cssOne';
                }
                else if( o.fxIn != null ) {
                    fxName0 = [o.fxIn, o.fxOut];
                    fxType0 = 'css';
                }


                // Stup each slide :
                // ID cua tung slide
                // ID cua tung pagination --> setup o dau de them slide moi vao
                // Pagnum cua tung pagitem
                // Fx, speed, delay cua tung slide
                $s.each(function() {
                    var $el    = $(this),
                        str    = $el.attr('data-'+ o.dataSlide),
                        opt    = $el.data('slideLast') || {},   // Lay option trong data neu da setup 1 lan roi
                        fxName = fxName0,               // Mac dinh effect NAME
                        fxType = fxType0;               // Mac dinh effect TYPE


                    // slide: thu tu dac biet gan vao cac doi tuong
                    // Chi setup 1 lan hoac co dieu kien dac biet nhu is.apiAdd
                    if( is.setupInit == 1 || is.apiAdd || is.apiRemove ) {
                        if( n == 0 )     va.$s0 = $el;
                        if( n == 1 )     va.$s1 = $el;
                        if( n == 2 )     va.$s2 = $el;
                        if( n == num-1 ) va.$sn = $el;
                    }

                    // LUU TRU ID CHO SLIDE
                    $el.data({ 'id' : n });


                    // Pagitem: store id va setup pagnum
                    is.pag && $pagItem.eq(n).data('id', n);

                    // Tach cac options trong slide
                    if( str != UNDEFINED && str != '' ) {
                        PROP.split(opt, str, {}, 0);
                    }


                    // Setup Fx name va Fx type --> uu tien effect custom
                    if     ( opt.fxOne ) { fxName = opt.fxOne; fxType = 'cssOne'; }
                    else if( opt.fxIn )  { fxName = [opt.fxIn, opt.fxOut]; fxType = 'css'; }
                    else if( opt.fx )    { fxName = opt.fx; fxType = 'js'; }


                    // Setup Fx name va Fx type
                    aFx.push(fxName);
                    aFxType.push(fxType);

                    // Setup Fx Easing ---> only cho css
                    var fxEase = opt.fxEasing || o.fxEasing;
                    if( !!fxEase ) fxEase = M.easeName(fxEase);
                    aFxEase.push(fxEase);

                    // Setup Others options
                    aSlot.push(  opt.slot  || o.slot );
                    aSpeed.push( opt.speed || o.speed);
                    aDelay.push( opt.delay || o.slideshow.delay);


                    // Luu tru classAdd cua tung slide
                    va.classAdd[n] = CLASSADD.filter(opt);

                    // Slider: loai bo va luu tru data moi tren slide
                    $el.removeAttr('data-'+ o.dataSlide).data('slideLast', opt);
                    n++;
                });

                
                // Slide: addon
                (o.layout == 'free') && PROP.slideAddon();


                // Properties: swap value
                va.fx     = aFx;
                va.fxType = aFxType;
                va.fxEase = aFxEase;
                va.slot   = aSlot;
                va.fxNum  = o.fxName.length;
                speed     = aSpeed;
                delay     = aDelay;
                tDelay    = delay[cs.idCur];
                // console.log(va.fx, va.fxType);


                // is SetupInit
                // value 1: for init slider; value 2: for init slide
                if( is.setupInit == 1 ) is.setupInit = 2;
            },






            /* Slide: layout free addon
            ---------------------------------------------- */
            slideAddon : function() {

                var _nLoop = 0,
                    _num   = (o.fLoop > 1) ? o.fLoop : num,
                    _nLast = 0,
                    _n     = 0,

                    _ra = function() {
                        _nLast = _n;
                        _n = M.r( M.ra()*(_num-1) );
                    };

                
                for (i = 0; i < num; i++) {
                    var $el = $s.eq(i);

                    // Slide: add number
                    $el.addClass(o.fName + i);


                    // Slide: add 'in' 'out' at begin
                    if( o.isInOutBegin ) {
                        if( i == cs.idCur ) $el.addClass(o.fName + '-in');
                        else                $el.addClass(o.fName + '-out');
                    }


                    // Slide: add fx number
                    if( o.isClassRandom ) {

                        do { _ra() } while (_n == _nLast && o.fLoop > 2);
                        $el.addClass('fx' + _n );
                    }

                    else {
                        if( o.fLoop > 1 ) {
                            $el.addClass('fx' + _nLoop);

                            _nLoop++;
                            if( _nLoop >= o.fLoop ) _nLoop = 0;
                        }
                    }
                }



                
                // Slide: as pag
                if( o.isSlAsPag ) {

                    // PagItem: check exist
                    if( !is.pag ) $pagItem = $('');

                    // PagItem: add item
                    for (i = 0; i < num; i++) {
                        $pagItem = $pagItem.add($s.eq(i));
                    }

                    // PagItem: event
                    !is.pag && EVENTS.pag();

                    // Code: add class
                    $cs.addClass('slide-as-pag');
                }
            },




            /* INIT END: setup nhung thuoc tinh con lai
            ---------------------------------------------- */
            initEnd : function() {

                // Cho phep chieu cao cua slide thay doi theo noi dung ben trong
                o.height == 'auto' && $viewport.addClass(o.ns + 'hNative');
            }
        },







        /* Noisrev
        ================================================== */
        NOISREV = {
            check : function() {

                // Bien khoi tao ban dau
                var ver   = o.rev[0],
                    isRun = false;

                // Phien ban pre
                if     ( ver == 'erp' || ver == 'eerf' ) isRun = true;
                else if( ver == 'omed') {

                    var demoURL = o.rev[1].split('').reverse().join('');
                    if( document.URL.indexOf(demoURL) != -1 ) isRun = true;
                }
                return isRun;
            },

            // Thuoc tinh cua phien ban free
            eerf : function() {

                // Options chung
                var options = {
                    fxOne       : null,
                    fxIn        : null,
                    fxOut       : null,
                    fxEasing    : null,

                    isSlideshow : false,
                    name        : null
                };
                o  = $.extend(true, o, options);

                // Layout line
                if( o.fx == null ) { o.fx = o.layout = 'line' }

                // 'pag' options
                o.pag.dirs = 'hor';
            }
        },






        /* Render
        ================================================== */
        RENDER = {

            /* Structure slider at init
            ---------------------------------------------- */

            // Tao viewport markup
            viewport : function() {

                // Bien shortcut va khoi tao ban dau
                var viewClass = o.ns + o.viewportName,
                    viewport  = $cs.children('.' + viewClass);


                // Tim kiem viewport
                if( viewport.length ) $viewport = viewport;
                else {
                    $cs.wrapInner( $(divdiv, {'class': viewClass}) );
                    $viewport = $cs.children('.' + viewClass);
                }
            },



            // Canvas: setup markup
            // Mac dinh tagName cua canvas la 'div'
            // Co the thay doi tagName cua canvas by options 'canvasTag'
            // Tu dong thay doi tagName cua canvas neu phat hien tagName slide la 'li'
            canvas : function() {
                
                // Bien shortcut va khoi tao ban dau
                var canvasClass = o.ns + o.canvasName,
                    canvasTag   = o.canvasTag,
                    canvas      = $viewport.children('.' + canvasClass);


                // Canvas DOM ton tai, get tagName cua canvas lan nua
                if( canvas.length ) {
                    canvasTag = canvas[0].tagName.toLowerCase();
                }
                // Canvas DOM not exist, create canvas DOM with tagName options
                else {

                    // Tu dong convert canvasTagName neu phat hien tagName children la 'li'
                    if( canvasTag == 'div' && $viewport.children()[0].tagName.toLowerCase() == 'li' ) canvasTag = 'ul';

                    var html = (canvasTag == 'ul') ? '<ul></ul>' : divdiv;
                    $viewport.children().wrapAll( $(html, {'class': canvasClass}) );
                }

                // $canvas refer to DOM, and store data --> reuse for later
                $canvas = $viewport.children('.' + canvasClass);
                $canvas.data({ 'tagName': canvasTag, 'pos' : { 'x' : 0 } });
            },





            // Slide setup markup
            // Wrap 'div'/'li'  cho slide khong co wraper
            // Add class 'cs-slide' va add icon loader vao slide
            slide : function($sl) {
                var c = o.ns + o.slideName,
                    t = $sl[0].tagName.toLowerCase(),
                    isEmpty = 0;       // Kiem tra slide rong


                // Slide co wrapper la 'div'/'li' hoac class 'cs-slide'
                if( t == 'li' || t == 'div' || $sl.hasClass(c) ) {

                    // Slide html empty -> remove, khong add vao var $s
                    if( !$sl.children().length ) {
                        $sl.removeClass(c);
                        isEmpty = 1;
                    }
                }

                // Slide khong co wrapper, chi co 1 thanh phan nhu '<a>'
                else {
                    var cTag   = $canvas.data('tagName'),
                        html   = (cTag == 'ul') ? '<li></li>' : divdiv,
                        parent = $(html, {'class': c});

                    $sl.wrap(parent);
                    $sl = $sl.closest('.' + c);
                }


                // Setup tiep tuc neu khong phai la slide rong
                if( !isEmpty ) {

                    // Slide: add class --> de chac chan slide co class 'cs-slide'
                    // Slides assign to varible $s, add class 'sleep' to setup height 100% , hidden all children
                    $sl.addClass(c).addClass(o.ns + 'sleep');

                    // Slide store data ban dau de khong khi get thong tin --> khong bi loi
                    $sl.data({
                        // 'height' : 0,
                        'is'     : { 'loading': 0, 'loaded': 0, 'imgback': 0, 'layer': 0, 'video': 0 },
                        '$'      : {},
                        'html'   : {},
                        'item'   : {}
                    });


                    // Create icon loader
                    RENDER.icon.add($sl, $sl, 'slLoader');

                    // Slide add to varible $s
                    $s = $s.add($sl);

                    // Function return slide: use for add new slide by api
                    return $sl;
                }
            },




            // Capitem va Pagitem: tim kiem va add vao mang de su dung sau nay
            capPagHTML : function($sl) {

                // Caption item: tim noi dung trong image background
                var cap  = '',
                    $img = $sl.find('img, a.' + o.ns + o.imgName);

                if( $img.length ) {
                    $img.each(function() {

                        var $i = $(this);
                        if( $i.data(o.layerName) == UNDEFINED
                        &&  $i.parent('.'+ o.ns + o.slideName).length ) {

                            // Noi dung caption tuy theo tag
                            // Neu la image thi la noi dung trong attr 'alt'
                            // Neu la link tag thi lay noi dung ben trong
                            var tag = this.tagName.toLowerCase();
                            if     ( tag == 'img' ) cap = $i.attr('alt');
                            else if( tag == 'a' )   cap = $i.html();

                            // Slide: store checked is imageback
                            $sl.data('is')['imgback'] = 1;
                        }
                    });
                }


                // Caption: tiep tuc tim kiem neu phat hien '.capitem'
                // --> uu tien lay noi dung trong '.capitem'
                // var $capItem = $sl.find('.' + o.ns + 'capitem');
                var $capItem = $sl.children('.' + o.ns + 'capitem');
                if( $capItem.length ) { cap = $capItem.html(); $capItem.remove(); }
                $sl.data('html')['cap'] = cap;     // Caption item add to data slide --> su dung sau nay



                /* PAGINATION ITEM SETUP */
                // Pagination item: tim kiem '.pagitem' --> luu tru vao data slide
                var pItem = $sl.children('.'+ o.ns +'pagitem');

                // Neu khong co thi tao dom
                if( !pItem.length ) pItem = $(divdiv, { 'class': o.ns + 'pagitem' });

                // Luu tru vao trong slide roi loai bo
                $sl.data('$')['pagItem'] = pItem;
                pItem.remove();
            },





            structure : function() {

                // Setup markup first: Viewport, canvas
                RENDER.viewport();
                RENDER.canvas();


                // Slides: setup markup
                // Tao var $s rong --> de add new slide trong vong lap
                $s = $('');
                $canvas.children().each(function() { RENDER.slide($(this)) });


                // Caption, Pagitem, imgback: setup
                $s.each(function() { RENDER.capPagHTML($(this)) });
            },






            /* Search: navigation, pagination
            ---------------------------------------------- */
            searchDOM : function(_class) {

                // Bien shortcut va khoi tao ban dau
                var $dom = $(),
                    NAME = o.name;

                if( !!NAME || (NAME >= 0 && NAME != null) ) {

                    var $el = $(_class);
                    if( $el.length ) {
                        $el.each(function() {

                            // var str = $(this).data(o.dataSlider),
                            var str = $(this).attr('data-'+ o.dataSlider),
                                name;

                            if( str != UNDEFINED && str != '' )
                                name = M.valueName(str, 'name');

                            if( name == NAME ) $dom = $(this);
                        });
                    }
                }


                if( $dom.length ) return $dom;

                // Loai tru tim kiem trong cac slide --> ho tro nested
                else {
                    var $find = $cs.find(_class);
                    $find.length && $find.each(function() {

                        var $self = $(this);
                        if( $self.closest('.'+ o.ns + o.viewportName).length == 0 ) return $self;
                    });
                    return $();
                }
            },





            /* Search: navigation, pagination
            ---------------------------------------------- */
            into : function(intoParent, $child) {

                // Setup doi tuong parent
                var $parent;
                switch( intoParent ) {

                    case 'nav':
                        if( !$nav ) $nav = $(divdiv, {'class' : o.ns + o.navName}).appendTo($cs);
                        
                        $parent = $nav;
                        break;

                    case 'media':
                        if( !$media ) $media = $(divdiv, {'class' : o.ns + 'media'}).appendTo($cs);

                        $parent = $media;
                        break;

                    case 'none':
                        $parent = $cs;
                        break;
                }

                // Add doi tuong con va parent moi vua setup
                $parent.append($child);
            },




            /* Navigation
            ---------------------------------------------- */
            nav : function() {

                // Navigation: search DOM
                var _c       = '.' + o.ns + o.navName,
                    $navHTML = RENDER.searchDOM(_c);
                

                // Navigation kiem tra ton tai tren HTML
                if( $navHTML.length ) {

                    $nav      = $navHTML;
                    var $n    = $cs.find('.'+ o.ns + o.nextName)
                      , $p    = $cs.find('.'+ o.ns + o.prevName)
                      , $play = $cs.find('.'+ o.ns + o.playName);

                    if( $n.length ) $next = $n;
                    if( $p.length ) $prev = $p;
                    if( $play.length) { $playpause = $play; o.slideshow.isPlayPause = 1; }
                }

                
                // Navigation: created if not HTML exist
                if( $nav == UNDEFINED )
                    $nav = $(divdiv, {'class' : o.ns + o.navName });

                if( $prev == UNDEFINED ) {
                    $prev = $(divdiv, {'class' : o.ns + o.prevName, 'text' : 'prev'});
                    $nav.append($prev);
                }

                if( $next == UNDEFINED ) {
                    $next = $(divdiv, {'class' : o.ns + o.nextName, 'text' : 'next'});
                    $nav.append($next);
                }


                // Navigation: add to codeslide
                if( !$navHTML.length ) $cs.append($nav);
            },


            play : function() {

                // Navigation: search DOM
                var _class    = '.'+ o.ns + o.playName,
                    $playHTML = RENDER.searchDOM(_class);

                if( $playHTML.length ) $playpause = $playHTML;
                else {

                    $playpause = $(divdiv, {'class' : o.ns + o.playName, 'text' : 'play/pause'});

                    // Add playpause vao markup
                    RENDER.into(o.markup.playInto, $playpause);
                }

                // Add class actived vao playpause neu isAutoRun false
                if( !is.autoRun ) {
                    is.pauseActive = 1;
                    $playpause.addClass(o.ns + o.actived);
                }
            },





            /* Pagination
            ---------------------------------------------- */
            pagitem : function($sl) {

                // Lay pagItem tu data slide
                var p = $sl.data('$')['pagItem'];

                // Thumbnail item: them vao pagitem va luu tru vao $thumbItem de su dung sau nay
                is.thumb && PAG.preThumb($sl, p);

                // Pagitem: store in object --> su dung sau nay
                $pagItem = $pagItem.add(p);

                // Usefor add new slide by API.add
                return p;
            },


            pag : function() {

                // Pagination: search DOM
                var ns       = ' '+ o.ns,
                    nsPag    = ns + 'pag-',
                    pag      = o.pag,
                    pagOut   = ns + 'outside',
                    dirs     = pag.dirs,
                    pagClass = ns + o.pagName + ns + pag.type + nsPag + dirs + nsPag + pag.pos,
                    $pagHTML = RENDER.searchDOM('.'+ o.ns + o.pagName);


                // Pagination: tao dom voi className --> class type va dirs se duoc update sau
                is.pagOutside = !!$pagHTML.length;
                $pag          = $pagHTML.length ? $pagHTML.addClass(pagClass + pagOut)
                                                : $(divdiv, { 'class' : pagClass });
                

                // Them DOM paginner vao pagination
                $pagInner = $(divdiv, {'class' : o.ns + 'paginner'});

                
                // Pagitems setup
                // Add pagitem vao pagination container
                $pagItem = $(''); $thumbItem = $('');
                $s.each(function() { RENDER.pagitem($(this)) });


                // Pagitem: append to pagination, ngoai tru layout dash
                if( o.layout != 'dash' ) $pagInner.append($pagItem);


                // Pagination append to slider
                // Vi tri top --> pagination append vao vi tri dau tien cua slider
                $pag.append($pagInner);
                if( !$pagHTML.length ) (pag.pos == 'top') ? $cs.prepend($pag) : $cs.append($pag);


                // Add bien viewport va namespace va.pag
                va.pag.viewport = $pag;



                // Them class vao code --> ho tro tab style custom
                var csClass = '';
                if( pag.type == 'tab' ) {

                    // Them class chieu huong va vi tri
                    csClass += nsPag + dirs + nsPag + pag.pos;
                    if( is.pagOutside ) csClass += pagOut;

                    // Assign tat ca class vao CODE
                    $cs.addClass(csClass);
                }
            },





            /* Captions
            ---------------------------------------------- */
            cap : function() {

                // Caption: search DOM
                var _c = '.' + o.ns + o.capName
                  , $capHTML = RENDER.searchDOM(_c);

                if( $capHTML.length ) $cap = $capHTML;
                else                  $cap = $(divdiv, {'class' : o.ns + o.capName});


                // Them capCur va capLast --> them hieu ung cho caption
                va.$capCur   = $(divdiv, { 'class': o.ns + 'cap-cur' });
                va.$capLast  = $(divdiv, { 'class': o.ns + 'cap-last' });
                va.$capInner = $(divdiv, { 'class': o.ns + 'capinner' });
                va.$capInner.append(va.$capCur, va.$capLast).appendTo($cap);

                // Cap: add to slider
                if( !$capHTML.length ) $cs.append($cap);
            },




            /* Timer
            ---------------------------------------------- */
            timer : function() {

                // Timer: remove last timer
                !!$timer && $timer.remove();
                if( is.timer ) {

                    // Timer: search DOM
                    var className = o.ns + o.timerName,                     // Class name
                        classType = className + '-' + va.timer,             // Class type
                        $timerHTML = RENDER.searchDOM('.'+ className);


                    // Timer: them vao markup
                    if( $timerHTML.length ) $timer = $timerHTML.addClass(classType);
                    else {
                        $timer = $(divdiv, {'class' : className +' '+classType});

                        // Add timer vao markup
                        RENDER.into(o.markup.timerInto, $timer);
                    }



                    // Timer bar
                    if( va.timer == 'bar' ) {
                        $timerItem = $(divdiv, {'class' : className +'item'});
                        $timer.append($timerItem);

                        // Properties init
                        TIMER.setup.bar();
                    }

                    // Timer arc
                    else if( va.timer == 'arc' ) {
                        $timerItem = $('<canvas></canvas>');
                        $timer.append($timerItem);

                        // Setup init
                        TIMER.arcProp();
                    }

                    // Timer number
                    else if( va.timer == 'number' ) {
                        $timerItem = $('<span></span>', {'class' : className +'item', 'data-num': 0, 'text': 0 });
                        $timer.append($timerItem);
                    }
                }
            },





            /* Toggle simple div+image: overlay & shadow
            ---------------------------------------------- */
            divImg : function(name, parent, isAfter) {

                var c         = o.ns + o[name+'Name'],
                    nameUpper = name.charAt(0).toUpperCase() + name.slice(1);     // nameUpper: overlay -> Overlay

                va[name] = $cs.find('.' + c);

                // Option co TURN ON --> setup
                if( o['is'+ nameUpper] ) {
                    if( !va[name].length ) {

                        // Kiem tra image o trong container
                        var src = $cs.data('img'+name),
                            tag = (!!src) ? '<div class="'+ c +'"><img src="'+ src +'" alt="['+ name +']"></div>'
                                          : '<div class="'+ c +'"></div>';

                        // Chon lua chen after hay before so voi doi tuong parent
                        isAfter && parent.after($(tag)) || parent.before($(tag));
                    }
                }

                // Option co TURN OFF --> loai bo --> ho tro cho update api
                else if( va[name].length ) va[name].remove();
            },


            refresh : function() {

                (oo.isOverlay != o.isOverlay) && RENDER.divImg('overlay', $canvas, 1);
                (oo.isShadow  != o.isShadow ) && RENDER.divImg('shadow', $viewport, 0);
            },



            
            /* Icon loader
               Layout dash error:! fix later
            ---------------------------------------------- */
            icon : {
                add : function($sl, $parent, name) {

                    var loader = $(divdiv, {'class': o.ns + 'loader', 'text': 'loading'});
                    $sl.data('$')[name] = loader;
                    $parent.append(loader);
                },

                remove : function($sl, name) {

                    var loader = $sl.data('$')[name];
                    loader && loader.remove();
                }
            },



            /* Other elements
            ---------------------------------------------- */
            other : function() {

                // Overlay & shadow
                RENDER.refresh();
            }
        },






        /* Load method
         * Thuc hien chuc nang sau:
         *      + Bat dau load id slide khac 0
         *      + Load theo hinh zigzag phai/trai neu load id slide != 0
         *      + Preload truoc bao nhieu slide, mac dinh la 1
         *      + Load dong thoi cac slide khac nhau de toi uu toc load
         *      + Khi chua load xong, di chuyen toi slide khac --> uu tien load slide do
        ================================================== */
        LOAD = {

            /* Load way
             * Luu tru id-slide vao array --> de dang load tung id-slide
             *      dinh san the thu trong mang
            ---------------------------------------------- */
            way : function() {

                // Khoi tao gia tri ban dau, su dung cho nhung fn khac
                va.nAddLoad = 0;        // Number of slide add to loading
                va.nLoaded  = 0;        // Number of slide already loaded
                is.preload  = 0;        // Kiem tra preload slide xong chua

                var aLoad   = [],       // Shortcut array id slide to load
                    idCur   = cs.idCur; // Shortcut ID current


                /* So luong slide load song song
                    Luc dau sau khi preload xong, va.nPaLoaded luon luon -1,
                    --> cho nen + 1 luc dau tien --> can bang va khoi rac roi */
                va.nPaLoaded = o.loadAmount + 1;


                /* Preload convert
                    + neu 'all', load toan bo slides
                    + neu == 0 --> luon luon load truoc 1slide --> == 0 xem sau */
                if( o.preload == 'all' ) o.preload = num;
                if( o.preload <= 0 )     o.preload = 1;


                /* LOAD CENTER ZIGZAG */
                if( is.cenLoop && !(o.isPag && o.pag.type == 'tab') ) {
                    var idMap    = va.idMap,
                        idCenter = M.c(num/2 - 1),
                        idCur    = idCenter,
                        nLeft    = 1,
                        nRight   = 1,
                        isRight  = 1;

                    // Setup id ban dau
                    aLoad[0] = idMap[idCur];
                    for (i = 1; i < num; i++) {

                        if( isRight ) {
                            idCur = idCenter + nRight;
                            nRight++;
                            isRight = 0;
                        }
                        else {
                            idCur = idCenter - nLeft;
                            nLeft++;
                            isRight = 1;
                        }

                        aLoad[i] = idMap[idCur];
                    }
                }

                else {

                    /* LOAD LINEAR */
                    // Load theo thu tu tu` 0,1,2,3...
                    if( idCur == 0 ) {
                        for (i = 0; i < num;) aLoad[i] = i++;
                    }

                    /* LOAD ZIGZAG */
                    // load vi tri current, roi load phai trai
                    else {

                        var right     = 1,      // Default: load right first
                            n         = 1,
                            leftEnd   = 0,      // Shortcut leftEnd
                            rightrEnd = 0;      // Shortcut rightEnd

                        aLoad[0] = o.idBegin;
                        for (i = 1; i < num; i++) {

                            if( (idCur != num-1) && (right || leftEnd) ) {
                                aLoad[i] = idCur + n;

                                // Left: end
                                if( leftEnd ) n++;
                                else          right = 0;

                                // Right: check end
                                if( aLoad[i] >= num-1 ) rightrEnd = 1;
                            }
                            else {
                                aLoad[i] = idCur - n;
                                n++;

                                // Right: end
                                right = !rightrEnd;

                                // Left: check end
                                if( aLoad[i] <= 0 ) leftEnd = 1;
                            }
                        }
                    }
                }


                // Assign gia tri vao namespace
                va.aLoad = aLoad;
            },



            // Setup luc bat dau load slide moi
            // Setup load dong thoi nhieu slide cung luc
            setupBegin : function() {

                va.aLoad.shift();   // id slide hien tai duoc lay ra
                va.nAddLoad++;

                // Kiem tra de load dong thoi slide moi
                // Luc nay LOAD.slideBegin() o LOAD.setupEnd() bi tam dung
                if( va.nAddLoad < o.preload ) {
                    LOAD.slideBegin( $s.eq( va.aLoad[0]) );
                }
            },


            setupEnd : function($sl) {

                // Varible use for preload
                va.nLoaded++;

                // Tat ca slide preload da load xong
                if( !is.preload && va.nLoaded == o.preload ) is.preload = 1;


                // LoadAmount chi thuc hien neu nhu preLoad da xong
                // Kiem tra reset lai gia tri va.nPaLoaded neu va.nPaLoaded == 0
                if( is.preload ) {

                    va.nPaLoaded--;
                    if( !va.nPaLoaded ) va.nPaLoaded = o.loadAmount;
                }


                // Load next slide
                // Dieu kien: va.aLoadd array khac empty va is.preload da load xong
                // Neu is.preload chua load xong thi LOAD.slideBegin() bi tam dung --> load new slide chuyen sang LOAD.setupBegin()
                // Them dieu kien: LOAD.add() khong thuc hien --> tranh run func nay nhieu lan cung luc
                var aLoad = va.aLoad;
                if( aLoad != null && is.preload && va.nPaLoaded >= o.loadAmount && !$sl.data('isLoadAdd') ) {

                    // Kiem tra va.aLoad lan nua, khong empty -> cho truong hop: va.nPaLoaded > va.aLoad.length
                    for (i = va.nPaLoaded; i > 0 && aLoad != null && aLoad.length; i--) {
                        LOAD.slideBegin( $s.eq( aLoad[0]) );
                    }
                }
            },


            add : function($sl) {
                var _isLoading = $sl.data('is');

                // Kiem tra: khong thuc thi khi tat ca slide da load xong,
                //      hoac slide da load hoac slide dang LOAD.
                if( !is.loadAll && (!_isLoading || !!(_isLoading && !_isLoading['loaded'])) ) {

                    // Vi khong biet id slide current trong va.aLoad[] --> su dung vong lap
                    // Lay index id trong mang va.aLoad
                    for (i = va.aLoad.length-1; i >= 0; i--) {
                        if( va.aLoad[i] == cs.idCur ) {

                            // Hoan doi id trong va.aLoad[], neu khong co trong thu tu load tiep theo
                            va.aLoad.splice(0, 0, va.aLoad.splice(i, 1)[0]);

                            // Store data to know load by func LOAD.add() --> not run loadAmount
                            $sl.data('isLoadAdd', 1);

                            // console.log('add -> load begin');
                            LOAD.slideBegin( $s.eq(va.aLoad[0]) );
                            break;
                        }
                    }
                }
            },






            /* Slide: load image
             * isNewAdd: used for API.add new slide
            ---------------------------------------------- */
            slideBegin : function($slide, _isNewAdd) {

                // Load: setup begin
                var id = $slide.data('id');
                cs.ev.trigger('loadBegin', [$slide, id]);
                !_isNewAdd && LOAD.setupBegin();

                // Remove class 'sleep' --> remove height = 100% && all children show
                $slide.removeClass(o.ns + 'sleep');

                // Slide: store data image
                var nsOnly      = o.ns.substr(0, o.ns.length-1),
                    nsImg       = o.ns + o.imgName,
                    $imgs       = $slide.find('img, a.'+ nsImg),
                    $imgsNested = $slide.find('.'+ nsOnly + ' img, .'+ nsOnly +' a.'+ nsImg);   // Image trong nested

                // Loai bo image trong code nested --> khoi bi chong cheo' tinh toan
                $imgs = $imgs.not($imgsNested);

                // Bien khac
                var imgNum = $imgs.length,
                    _nCur  = 0;


                // slide: setup data
                $slide.data('is')['loading'] = 1;
                $slide.data({'imgNum' : imgNum, 'nCur' : 0 });


                if(id == o.idBegin) {
                    $cs.addClass(o.ns + 'init');
                    o.height == 'auto' && IMAGE.initHeight.wait();
                }

                // Slider: get height at first slide
                // Below PROP.setup() responsive & va.rate --> to get va.rate first then calculation hCode
                // Because waiting slider addClass 'height-fix' in PROP.slider(), so it setup at here
                (o.height == 'fixed' && $slide.data('id') == o.idBegin) && SIZE.sliderHeightFix();


                // Fullscreen: re calculation padding & va.rate
                // Nead hCode first!! --> so below sliderHeightFix() and into slideBegin()
                is.res && o.isFullscreen && FULLSCREEN.varible();


                // Slide: get all image
                if( imgNum ) {

                    // Image
                    $imgs.each(function() {
                        var $i  = $(this);

                        // Image background: check
                        var _isBack = $i.data(o.layerName) == UNDEFINED
                                   && $i.parent('.' + o.ns + o.slideName).length
                                   && o.layout != 'dash'
                                    ? 1 : 0;


                        // Image lazyload: tag swap
                        if( this.tagName.toLowerCase() == 'a' )
                            $i = IMAGE.tagSwap($i);



                        // Image: setup data
                        $i.data({ 
                            '$'   : { 'slide': $slide },
                            'is'  : { 'imgback': _isBack, 'srcOutside': 0, 'loaded': 0 },
                            'src' : []
                        });


                        // Image background: setup others
                        if( _isBack ) {

                            // Store object image background
                            $slide.data('$')['imgback'] = $i;
                            $slide.data('is')['imgback'] = 1;

                            // Wrap image
                            IMAGE.wrap($i);
                        }


                        // Src image setup, cho vao mang theo thu tu uu tien --> roi get tu` dau
                        var _src = $i.data('src');

                        var _srcSelf = $i.attr('src');
                        if( _srcSelf == '' ) _srcSelf = '//:0';
                        _src.push(_srcSelf);

                        var _srcLazy = $i.attr('data-'+ o.lazyName);
                        if( _srcLazy != UNDEFINED ) {
                            _src.push(_srcLazy);
                            $i.removeAttr('data-'+ o.lazyName);
                        }


                        // Image: check data image && setup data image
                        IMAGE.data($i);


                        // Image kiem tra src co o ngoai (server flickr)
                        // Neu la srcOutside thi cho cho load xml xong, neu khong thi chay thang IMAGE.load
                        var _i = $i.data();
                        // if( _i.flickr && _i.flickr.photoID ) flickr.photo($i);
                        // else                                 IMAGE.load($i);
                        IMAGE.load($i);
                    });
                }

                // Slide: no image
                else LOAD.slideEnd($slide);
            },


            // Khi trong 1 slide loaded het image, se goi toi ham nay
            slideEnd : function($slide) {

                // Shortcut height, id slide
                var hSlide = $slide.height(),
                    id     = $slide.data('id');


                // Slide current: setting data
                $slide.data('height', hSlide);
                $slide.data('is')['loaded'] = 1;


                // Action run only one time --> Show slider when preload slides
                // o.preload - 1, boi vi va.nLoaded chua +1 vao, o cuoi function nay moi +1 vao
                // if( !is.initLoaded && va.nLoaded == o.preload-1 ) {
                if( !is.initLoaded ) {

                    // Canvas: set height first
                    // Neu hSlide = 0 --> van setup hCode
                    if( o.height == 'auto' && !hCode ) IMAGE.initHeight.set(hSlide);

                    // Init: load continue
                    INIT.load();
                }

                // ImageBack: dat vi tri horizontal center
                IMAGE.backCenterHor($slide);

                // Image background: in height-fixed mode set center vertical
                if( o.height == 'fixed') IMAGE.backCenter.setup($slide);


                // Layout dash
                if( o.layout == 'dash' ) {
                    ds.height[id] = $slide.outerHeight(true);

                    // Update height slider
                    SIZE.sliderHeight();
                }


                // layer init when loaded slides, mainly for show layer
                $slide.addClass(o.ns + 'ready');


                // Layer: init, need hCode first!
                // layer.init($slide);
                // (id == cs.idCur) && layer.run(id, 'start');

                // Hotspot: init --> tuong tu nhu layer
                // hotspot.init($slide);
                // var HOTSPOT = PLUGIN.hotspot;
                // HOTSPOT && HOTSPOT.init(one, $slide);
                

                // Video, Map: init
                // video.init($slide);
                // map.init($slide);


                // Icon loader: remove
                RENDER.icon.remove($slide, 'slLoader');


                // SLideshow: play next
                !!$slide.data('isPlayNext') && cs.play();


                // Events trigger: slide loaded
                var _nLoad = 'loadSlide.' + id;
                cs.ev.trigger(_nLoad);
                cs.ev.trigger('loadEnd', [$slide, id]);


                // Events 'loadAll' : khi va.aLoad[] empty
                if( va.aLoad != null && va.aLoad.length == 0 ) {
                    is.loadAll = 1;
                    va.aLoad   = null;

                    cs.ev.trigger('loadAll');
                    EVENTS.codeLoaded();
                }


                // Setup khi add new slide bang api add
                if( is.apiAdd ) {
                    cs.refresh();       // Refresh slider lan nua khi load xong
                    is.apiAdd = 0;      // Bien return false --> de biet ket thuc update add
                }


                // Slide: load next, varible $slide for new add loading
                // O duoi cuoi cung --> tien func & so sanh o tren khong bi anh huong khi load new slide moi
                LOAD.setupEnd($slide);
            }
        },






        /* Image of slide
        ================================================== */
        IMAGE = {

            /* Lay kich thuoc cua slide som nhat co the khi imageback chua tai xong
             * khi slider setup luc dau, ap dung cho height type 'auto'
            ---------------------------------------------- */
            initHeight : {

                wait : function() {
                    var $sl = $s.eq(o.idBegin),
                        t   = 200;      // Thoi gian de get chieu cao cua slide

                    ti.initHeight = setInterval(function() {
                        var h = $sl.height(),
                            i = $sl.find('.'+ o.ns + 'imgback > img');

                        // Kiem tra
                        if( i.length && i[0].height > 50 ) {

                            // Neu imgback da loaded thi da * va.rate roi --> neu chua loaded thi *va.rate
                            h = i.data('is')['loaded'] ? h : h * va.rate;
                            IMAGE.initHeight.set(h);
                        }
                    }, t);
                },

                set : function(h) {

                    // Lay kich thuoc height slider
                    hCode = h;
                    clearInterval(ti.initHeight);

                    // Setup chieu cao slider
                    $viewport.css('height', h);

                    // Lay kich thuoc width slider --> lay cung luc voi height
                    SIZE.wCode();
                }
            },



            /* Get 'data-image' --> check image src support flickr host
            ---------------------------------------------- */
            data : function($i) {

                var str = $i.data('image');
                if( str != UNDEFINED && str != '' ) {

                    // Options split from string & store in data 'image'
                    var _i = {}; PROP.split(_i, str, {});
                    $i.data(_i);

                    // Clear data attribute on image
                    $i.removeAttr('data-image');
                }
            },





            /* Image event load
            ---------------------------------------------- */
            load : function($i) {
                
                // Mini function: da load tat ca image co trong slide --> setup sau khi load xong
                var _loadAll = function() {

                    // Shortcut slide of image
                    var $sl = $i.data('$')['slide'];


                    // Kiem tra da load het image --> neu load het --> setup slideEnd
                    $sl.data('nCur', $sl.data('nCur') + 1);
                    ($sl.data('nCur') == $sl.data('imgNum')) && setTimeout(function() { LOAD.slideEnd($sl) }, 10);


                    /* THEM THUMBNAIL */
                    var isAddThumb = $sl.data('is')['addThumb'],
                        isImgBack  = $i.data('is')['imgback'];

                    // Kiem tra xem co add thumbnail bang imgback hay khong
                    if( is.pag && isAddThumb && isImgBack ) {

                        // Clone imgback voi thuoc tinh --> add vao pagination
                        var $thumb = $sl.data('$')['thumb'],
                            $iThumb = $i.clone(true);
                        PAG.addThumb($iThumb, $thumb, $sl);
                    }
                };


                // Image setup && events load/error
                var i = new Image();
                var _src = $i.data('src');

                i.onload = function() {

                    // Image: set properties
                    // Truyen agrument bang image DOM --> nhanh va lay size Width/Height chinh xac hon jquery selector
                    IMAGE.prop($i, this);

                    // Image: all image loaded
                    _loadAll();
                }

                // Image: load error
                i.onerror = function() {

                    // Neu src trong mang con value --> load tiep tuc src con lai trong mang
                    if( _src.length ) IMAGE.load($i);

                    // Neu mang src empty --> bao loi khong load duoc
                    else {

                        // Image: change alt
                        $i.attr('alt', '[ img load fail ]');
                        is.e && console.warn('[ codetabs: img load fail ] -> ('+_src +')');

                        // Image: all image loaded
                        _loadAll();
                    }
                }

                // Image src: get, o duoi function i.onload --> fixed bug for ie
                // Lay src trong data --> lay theo thu tu uu tien.
                var _i = _src.pop();
                $i.attr('src', _i);
                i.src = _i;
            },





            /* Image setup properties sau khi da load xong
            ---------------------------------------------- */
            prop : function($i, i) {

                // Image get size, ratio --> for fit/fill image
                var wImg = i.width, hImg = i.height, rImg = wImg/hImg;

                // Image: store data
                $i.data({'width':wImg, 'height':hImg, 'rate':rImg});
                $i.data('is')['loaded'] = 1;

                // Responsive: set size image-bg + image-layer
                if( is.res && va.rate < 1 )
                    $i.css({ 'width' : M.r(wImg*va.rate), 'height': M.r(hImg*va.rate) });


                // Image background: set size, can phai luu width/height/rate tren data cua image truoc
                if( $i.data('is')['imgback'] ) {

                    // Image Width/Height fit, included responsive image
                    // Slide: vertical center -> no needed!
                    IMAGE._fit($i);
                }


                // Thumbnail image get va render trong pagination
                // KHOA TAM THOI
                // is.pag && is.thumb && PAG.createThumb($i);
            },





            /* Tag Swap: <a> --> <img>
             * Video: wrap by div, (div > img)
            ---------------------------------------------- */
            tagSwap : function($a) {

                var _l = {}; _l['data-'+ o.lazyName] = $a.attr('href')          // Data lazy Name
                var _c = $a.attr('class')               // class
                  , _i = $a.attr('id')                  // id
                  , _s = $a.attr('style')               // style
                  , _g = $a.attr('data-image')          // data image
                  , _v = $a.attr('data-'+ o.dataVideo)  // data video
                  , _m = $a.attr('data-'+ o.dataMap)    // data Map
                  , _t = $a.attr('data-imgthumb')       // thumbnail
                  , _a = o.isCap ? '[img]' : $a.text()  // alt of image
                  , _n = o.ns + o.imgName               // Shortcut imgName
                  , _r = 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7';
                  // , _r = '//:0';
                

                // Image: setup
                var _img = $('<img>', {'src': _r, 'alt': _a}).attr(_l);


                // Video check and convert
                var _ln = 'data-' + o.layerName         // Shortcut layer name
                  , _la = $a.attr(_ln)                  // Shortcut layer attribute
                  , _va = $a.attr('data-'+ o.dataVideo) // Shortcut video attribute
                  , _ma = $a.attr('data-'+ o.dataMap)   // Shortcut map attribute
                  , _obj;                               // object can be image or parent video


                // Video on layer, wrap by 'div', then move attribute to that 'div'
                // --> function video will parse late
                if( _la && (_va || _ma) ) {
                    var _parent = $(divdiv);
                    _parent.append(_img);
                    _obj = _parent;
                }

                // Default setup, only focus on image
                else { _obj = _img }

                // Image: add data-layer
                if( _la ) { var _l = {}; _l[_ln] = _la; _obj.attr(_l); }


                // Image: remove class 'cs-img' in img background & layer
                // Image background: class 'cs-img' will restore in IMAGE.wrap()
                (_c != _n) && _obj.addClass(_c).removeClass(_n);

                // Image or parent video: restore attribute & data
                _i && _obj.attr('id', _i);
                _s && _obj.attr('style', _s);
                _g && _obj.attr('data-image', _g);
                _v && _obj.attr('data-'+ o.dataVideo, _v);
                _m && _obj.attr('data-'+ o.dataMap, _m);
                _t && _obj.attr('data-imgthumb', _t);


                // IE fix: remove attr width/height
                is.ie && _img.removeAttr('width height');


                // Object: append
                $a.after(_obj).remove();
                return _img;
            },




            /* Image background: wrap
            ---------------------------------------------- */
            wrap : function($i) {

                var _c = o.ns + 'imgback'
                  , $iWrap = $(divdiv, {'class': _c});

                (o.layout != 'dash') && $i.wrap($iWrap).removeClass(_c);
            },





            /* Image fit
            ---------------------------------------------- */
            _fit : function($i) {

                var _r = $i.data('rate')        // Shortcut rate image
                  , _w = $i.data('width')       // Shortcut width image
                  , _h = $i.data('height');     // Shortcut height image


                var fitW = function() { $i.css({'width': wViewport, 'height': M.r(wViewport/_r)}); }
                  , fitH = function() { $i.css({'width': M.r(hCode*_r),'height': hCode}); }

                    // Remove width/height inline, va.rate luc nao co value
                  , fit0 = function() { IMAGE.updateSize($i); };


                // Image Width
                if( (o.imgWidth == 'autofit')
                ||  (o.imgWidth == 'smallfit' && _w < wViewport)
                ||  (o.imgWidth == 'largefit' && _w > wViewport) )
                    { fitW($i) }

                else if( o.height == 'fixed' ) {

                    // Image Height
                    if( (o.imgHeight == 'autofit')
                    ||  (o.imgHeight == 'smallfit' && _h < hCode)
                    ||  (o.imgHeight == 'largefit' && _h > hCode) )
                        { fitH($i) }


                    // Image WH
                    else if( o.img == 'autofit' )
                        (_r > rCanvas) ? fitW($i) : fitH($i);

                    else if( o.img == 'smallfit' && _w < wViewport && _h < hCode )
                        (_r > rCanvas) ? fitW($i) : fitH($i);

                    else if( o.img == 'largefit' && _w > wViewport && _h > hCode )
                        (_r > rCanvas) ? fitW($i) : fitH($i);


                    else if( o.img == 'autofill' )
                        (_r > rCanvas) ? fitH($i) : fitW($i);

                    else if( o.img == 'smallfill' && _w < wViewport && _h < hCode )
                        (_r > rCanvas) ? fitH($i) : fitW($i);

                    else if( o.img == 'largefill' && _w > wViewport && _h > hCode )
                        (_r > rCanvas) ? fitH($i) : fitW($i);

                    else fit0($i);
                }
                else fit0($i);
            },





            /* Image update size: included image background & image layer
            ---------------------------------------------- */
            updateSize : function($i) {

                if( va.rate < 1 ) {
                    $i.css({
                        'width' : M.r( $i.data('width') * va.rate ),
                        'height': M.r( $i.data('height') * va.rate )
                    });
                }
                else $i.css({'width': '', 'height': ''});
            },




            /* Image background vertical CENTER: add css top
            ---------------------------------------------- */
            backCenter : {

                // Return parent of image background
                // Get parent to setup 'top'(important) and get 'height'
                get : function($sl) {

                    var _$ = $sl.data('$');
                    if( !!_$ && !!_$['imgback'] ) return _$['imgback'].parent('.'+ o.ns + 'imgback');
                    else                          return null;
                },

                // Setup image background to center
                setup : function($sl) {

                    var _parent = IMAGE.backCenter.get($sl);
                    if( _parent != null ) {
                        va.top = M.r( (hCode - _parent.height())/2 );

                        var _top = (va.top == 0) ? '' : va.top;
                        _parent.css('top', _top);
                    }
                },

                // Reset image background
                reset : function($sl) {
                    var _parent = IMAGE.backCenter.get($sl);
                    if( _parent != null ) _parent.css('top', '');
                }
            },


            /* Image background horizontal CENTER: add css left */
            backCenterHor : function($slide) {

                // Bien khoi tao ban dau
                var centerFn = function($sl) {

                    var $imgback = $sl.data('$')['imgback'];
                    if( $imgback != UNDEFINED ) {

                        var left0 = M.pInt( $imgback.css('left') ),
                            left1 = ~~( -($imgback.width() - va.wCanvas) / 2);

                        (left0 !== left1) && $imgback.css('left', left1);
                    }
                };


                // SETUP: phan biet 1 slide va tat ca slide
                if( $slide == UNDEFINED ) $s.each(function() { centerFn($(this)) });
                else                      centerFn($slide);
            },




            /* Image background update size to fit/fill
            ---------------------------------------------- */
            backUpdate : function() {

                var $img = $canvas.find('.' + o.ns + 'imgback > img');
                if( $img.length ) {
                    $img.each(function() {

                        var $el = $(this);
                        if( $el.data('is')['imgback'] ) {

                            // Truong hop image co fit/fill
                            if( o.imgWidth != 'none'
                            || (o.height == 'fixed' && (o.imgHeight != 'none' || o.img != 'none')) )
                                IMAGE._fit($el);
                            
                            // Truong hop con lai, image update size theo ti le
                            else IMAGE.updateSize($el);
                        }
                    });
                }
            },




            /* Layout line: image background fix auto hide
               fixed for firefox
            ---------------------------------------------- */
            autoShow : function(id) {
                var img = $s.eq(id ? id : cs.idCur).data('$')['imgback'];
                if( !!img ) {

                    img.css('position', 'static');
                    setTimeout(function() { img.css('position', '') }, 2);
                }
            }
        },






        /* Pagination function
        ================================================== */
        PAG = {

            // Thumbnail: tao wraper + iconloader truoc,...
            preThumb : function($sl, $pItem) {

                // Thumbnail tag
                var $thumb = $(divdiv, {'class' : o.ns + o.thumbWrap});
                $pItem.append($thumb);

                // Thumbnail luu tru vao slide
                $sl.data('$')['thumb'] = $thumb;
                // $thumbItem = $thumbItem.add($thumb);


                // Add icon loader vao thumbnail
                RENDER.icon.add($sl, $pItem, 'thumbLoader');


                // Thumbnail src: tim kiem
                var src  = $sl.data('imgthumb');
                if( !src ) src = $sl.find('[data-imgthumb]').data('imgthumb');      // Tiep tuc tim kiem ben trong slide



                // Neu src ton tai --> tao image thumb
                if( !!src ) {
                    var iThumb = new Image();

                    iThumb.onload = function() {
                        var $i = $('<img></img>', {'src': src}).data('rate', iThumb.width/iThumb.height);
                        PAG.addThumb($i, $thumb, $sl);
                    }
                    iThumb.onerror = function() {
                        is.e && console.warn('[ codetabs: thumb load fail ] -> ('+ src +')');
                    }

                    // Image thumbnail: set src
                    iThumb.src = src;
                }

                // Neu src khong ton tai --> luu tru vao slider --> tao thumb bang imgback khi slider bat dau load
                else $sl.data('is')['addThumb'] = 1;
            },


            // Function create thumbnail
            addThumb : function($i, $thumb, $sl) {

                // Lay kich thuoc thumbnail --> neu khong co trong option thi get tren style
                var wPag   = o.pag.width,
                    hPag   = o.pag.height,
                    w      = typeof wPag == 'number' ? wPag : $thumb.width(),       // shortcut width thumbnail container
                    h      = typeof hPag == 'number' ? hPag : $thumb.height(),      // shortcut height thumbnail container
                    rThumb = w / h,                                                 // shortcut rate thumbnail container
                    rImg   = $i.data('rate');                                       // shortcut rate thumbnail image
                    


                // Setup image thumb o vi tri chinh giua va fill trong wrapper
                var c = '',                                 // Them class Thumbnail setup fit width/height
                    s = {'width': '', 'height': ''};        // Loai bo css thuoc tinh width/height neu co

                if( w && h ) {
                    if( rImg > rThumb ) {
                        // c = o.fitHeight;
                        c = o.ns + 'hfit';
                        s.left = -m.r( (rImg*h - w)/2 );
                    }
                    else {
                        // c = o.fitWidth;
                        c = o.ns + 'wfit';
                        s.top = -m.r( (w/rImg - h)/2 );
                    }
                }


                // Chen style moi setup vao image
                $i.css(s);

                // Thumbnail: append image
                $thumb.addClass(c).append($i);

                // Loai bo thumb loader o giai doan cuoi cung
                RENDER.icon.remove($sl, 'thumbLoader');
            },



            // Lay kich thuoc cua pagItem
            sizeItem : function() {

                /* Truoc tien phai loai bo width tren pagInner
                    --> lay dung gia tri width/height cua pagItem
                    --> toggle class 'wfit' va 'hfit' de lay dung kich thuoc */
                var p  = va.pag,
                    op = o.pag,
                    wFirst = typeof op.width == 'number' ? op.width : '',
                    hFirst = typeof op.height == 'number' ? op.height : '';

                $pagInner.css({ 'width'         : wFirst,
                                'height'        : hFirst,
                                'margin-right'  : '',
                                'margin-bottom' : '' });

                var wfit = o.ns + 'wfit',
                    hfit = o.ns + 'hfit';
                if( wFirst == '' ) $pagInner.removeClass(wfit);
                else               $pagInner.addClass(wfit);

                if( hFirst == '' ) $pagInner.removeClass(hfit);
                else               $pagInner.addClass(hfit);




                /* Lay gia kich thuoc width/height gia tri lon nhat
                    --> tim khoang chenh lech giua width va outerWidth */
                var els  = $pagItem,
                    _max = M.valueMax,
                    wEle = wFirst != '' ? wFirst : _max(els, 'width'),
                    hEle = hFirst != '' ? hFirst : _max(els, 'height');

                // Kich thuoc outerWidth va outerHeight lon nhat cua pagitem
                p.wCon = _max(els, 'outerWidth', true);
                p.hCon = _max(els, 'outerHeight', true);
                // console.log(p.wCon);





                /* Setup kich thuoc va khoang cach [padding-border-margin] len PagInner
                    Khoang cach tong duoc tinh bang margin-right va margin-bottom
                    --> khong anh huong toi size 100% va vi tri pagination
                    --> Kiem tra TAB VERTICAL OUTSIDE --> loai bo width tren paginner */
                var maRight  = p.maRight  = p.wCon - wEle,
                    maBottom = p.maBottom = p.hCon - hEle;

                $pagInner.css({ 'width'         : (is.pagOutside && p.dirs == 'ver') ? '' : wEle,
                                'height'        : hEle,
                                'margin-right'  : maRight,
                                'margin-bottom' : maBottom });

                // Fixed cho type 'tab' --> pagitem luon luon co kich thuoc bang nhau khi ko co w/h opts
                op.type == 'tab' && $pagInner.addClass(wfit +' '+ hfit);





                /* Lay padding va border cua VIEWPORT
                    --> ho tro pag-tab voi opt SIZEAUTO-FULL */
                var pad     = 'padding-',
                    border  = 'border-',

                    spaceFn = function(aProp) {
                        var sizeView = 0, sizePag  = 0;

                        for( i = aProp.length-1; i >= 0; i-- ) {
                            sizeView += M.pInt($viewport.css(aProp[i]));
                            sizePag  += M.pInt($pag.css(aProp[i]));
                        }
                        return sizeView - sizePag;
                    };

                va.viewSpace = {
                    'hor': spaceFn([pad +'left', pad +'right', border +'left-width', border +'right-width']),
                    'ver': spaceFn([pad +'top', pad +'bottom', border +'top-width', border +'bottom-width'])
                };
            },



            // Lay gia tri cac thuoc tinh cua pagination lien quan den kich thuoc/size
            prop : function(isUpdatePos) {

                // Bien shortcut va khoi tao ban dau
                var p        = va.pag,
                    isPagHor = p.dirs == 'hor',
                    wRemain;

                p.width  = $pag.width();
                p.height = $pag.height();



                /* Chieu dai cua Pagination thay doi theo huong swipeCur
                   Thay doi theo option sizeAuto [null, 'full', 'self']
                        + Chuyen doi sizeAuto khi pagination co markup outside
                        + null : khong setup gi ca
                        + full : width/height pag == width/height slider
                        + self : width/height pag = tong cua width/height pagitem cong lai
                */
                var sAuto = is.pagOutside && !isPagHor ? 'self' : o.pag.sizeAuto,
                    pSize = { 'width': '', 'height': '' },
                    wSwap;

                if( sAuto == null ) {
                    wSwap = isPagHor ? p.width : p.height;
                }
                else if( sAuto == 'full' ) {
                    if( isPagHor ) wSwap = pSize.width  = wViewport + va.viewSpace.hor;
                    else           wSwap = pSize.height = hCode     + va.viewSpace.ver;
                }
                else if( sAuto == 'self' ) {
                    if( isPagHor ) wSwap = pSize.width  = p.wCon * num;
                    else           wSwap = pSize.height = p.hCon * num;
                }

                // Setup size auto len pagination
                p.wSwap = wSwap;
                $pag.css(pSize);




                /* Kiem tra va setup PULL JUSTIFIED
                    + Justified: opts sizeAuto la null || full, markup inside va Huong pag la 'hor' */
                if( isPagHor && !is.pagOutside && o.pag.align == 'justified' && (sAuto == null || sAuto == 'full') ) {

                    // Bien Shortcut va khoi tao ban dau
                    var wJustify  = M.r(wSwap / num),       // Kich thuoc cua tung pagItem justified
                        isJustify = 0,
                        wEle, hEle;

                    // Kiem tra dieu kien tiep theo va lay kich thuoc cua pagItem
                    if     ( isPagHor  && p.wCon < wJustify ) { p.wCon = wJustify; wEle = wJustify - p.maRight;  isJustify = 1; }
                    else if( !isPagHor && p.hCon < wJustify ) { p.hCon = wJustify; hEle = wJustify - p.maBottom; isJustify = 1; }

                    // Update  kich thuoc cua pagInner --> Muc dich cuoi cung
                    isJustify && $pagInner.css({ 'width' : wEle, 'height': hEle });
                }




                p.wTranslate = isPagHor ? p.wCon : p.hCon;
                wRemain      = p.wSwap - (p.wTranslate * num);      // Do dai lai con cua wViewport so voi tong width pagItem --> multi use
                p.xMin       = 0;
                p.xMax       = wRemain < 0 ? wRemain : 0;           // Vi tri toi da cua pagInner --> ho tro giam ti swipe
                p.wEdge      = (p.wSwap - p.wTranslate) / 2;        // Chieu dai cua canh so voi slide nam o giua --> ho tro pag center
                p.xCanvas    = 0;


                // Kiem tra cho phep pagItem co center
                // --> width Viewport phai lon hon tong width pagItem cong lai
                p.isViewLarge = wRemain > 0;
                
                // Setup PULL cua pagination
                // PULL se tro ve mac dinh la 'begin' --> neu do dai cua pagination lon hon viewport
                p.align = o.pag.align;
                if( p.align == 'justified' || (!p.isViewLarge && p.align != 'begin') ) p.align = 'begin';


                // Update gia tri khi pagInner o chinh giua
                if( p.align == 'center' ) {
                    p.xPull = p.xCanvas = M.r(wRemain / 2);
                    p.xMax  = p.xPull + (p.wTranslate * num);

                    // Setup vi tri pagInner luc bat dau
                    POSITION.translateX($pagInner, p.xCanvas, 1, 1);
                }
                else if( p.align == 'end' ) {
                    p.xPull = p.xCanvas = wRemain;
                    p.xMax  = p.wSwap;

                    // Setup vi tri pagInner luc bat dau
                    POSITION.translateX($pagInner, p.xCanvas, 1, 1);
                }



                // Update lai vi tri cua pag khi height slider thay doi --> ho tro type 'tab' voi huong 'ver'
                isUpdatePos && POSITION.translateX($pagInner, p.xCanvas, 0, 1);
            },



            // Setup size, sap xep tung pagitem trong phuong thuc SIZE.translateW()
            size : function() {

                // Bien shortcut va khoi tao ban dau
                var pag   = va.pag,
                    isHor = pag.dirs == 'hor';


                // Width translate thumbnail --> tuong tu nhu wTranslate cho canvas
                // pBegin --> vi tri cua slide duoc luu theo thu tu sap xep tren slider
                pag.pBegin = [];
                for (i = 0; i < num; i++) { pag.pBegin[i] = pag.wTranslate * i }


                // Slides: sap xep vi tri co san trong mang pBegin da setup o tren
                var tl = isHor ? 'tlx' : 'tly', tf = {};
                for (i = 0; i < num; i++) {

                    tf[pag.cssTf] = M[tl](pag.pBegin[i]);
                    $pagItem.eq(i).css(tf);
                }
            },



            // Setup pagItem current o vi tri chinh giua
            itemCenter : function() {

                // Kiem tra co duoc phep item center
                var p = va.pag;
                if( !p.isViewLarge ) {

                    // Tim vi tri chinh giua can dem
                    var xTarget = -(cs.idCur * p.wTranslate) + p.wEdge;

                    // Truong hop o ria viewport thi di chuyen toi ria
                    if( xTarget > 0 ) xTarget = 0;
                    else if( xTarget < p.xMax ) xTarget = p.xMax;

                    

                    // PagInner setup transition --> di chuyen
                    // Khac vi tri xCanvas thi moi setup --> tiet kiem Memory
                    // Thiet lap bang tay, khong dung translateX() --> canvas va pagination cung transition
                    // Ho tro pagitem0 o vi tri chinh giua sau khi resize nho dan --> van phuc hoi vi tri pagitem0
                    if( xTarget != p.xCanvas || xTarget == 0 ) {

                        // Setup gia tri transform
                        var tf = {},
                            sp = o.pag.speed,
                            es = o.pag.ease,
                            tl = (p.dirs == 'hor') ? 'tlx' : 'tly';
                        tf[p.cssTf] = M[tl]( M.r(xTarget) );


                        // Setup transition len pagInner, ho tro browser fallback
                        if( is.ts ) {
                            var ts = M.ts(cssTf, sp, M.easeName(es));

                            // Can phai co delay > 1ms (cho trinh duyen detach transition)
                            $pagInner.css(ts);
                            setTimeout(function() { $pagInner.css(tf) }, 2);


                            // Loai bo transition --> sach se
                            M.tc(ti.pagCenter);
                            ti.pagCenter = setTimeout(function() { M.tsRemove($pagInner) }, sp);
                        }
                        else {
                            $pagInner.animate(tf, {duration: sp, queue: false, easing: es});
                        }
                            

                        // Update xCanvas cua pagination
                        p.xCanvas = xTarget;
                    }
                }
            },



            // TAB VERTICAL --> them margin vao viewport de lay wViewport chinh xac
            tabVer : function() {
                if( is.pag ) {
                    // console.log(is.tabVer, va.pag.wCon);

                    if     ( is.tabVer == 'top' )    $viewport.css('margin-left', va.pag.wCon);
                    else if( is.tabVer == 'bottom' ) $viewport.css('margin-right', va.pag.wCon);
                }
            },



            // TAB VERTICAL tu dong chuyen thanh HORIZONTAL
            toHor : function() {

                // Kiem tra co thay doi huong cua tab hay khong
                var oPag = o.pag, dirs = null;
                if( oPag.type == 'tab' && oPag.dirs == 'ver' ) {

                    // Bien shortcut va khoi tao ban dau
                    var pag       = va.pag,
                        isHor     = pag.dirs == 'hor',
                        wMinToHor = oPag.wMinToHor,
                        wCODE     = $cs.width();


                    if( pag.dirs == 'ver' && wCODE < wMinToHor ) {
                        dirs = pag.dirs = 'hor';

                        // Clear Height tren pag dom
                        // Ngan can setup height tren pag trong sliderHeight()
                        !!$pag && $pag.stop(true).css('height', '');
                    }
                    else if( pag.dirs == 'hor' && wCODE >= wMinToHor ) {
                        dirs = pag.dirs = 'ver';
                    }
                }


                // Update code neu co thay doi huong
                // Loai bo width-inline truoc de lay kich width dung khi update
                if( !!dirs ) {
                    $canvas.css('width', '');
                    cs.prop({}, 0, { 'pagDirs': dirs });
                }
            }
        },







        /* APIs: more function
        ================================================== */
        API = {

            // Kiem tra va convert thanh number cho index
            indexParse : function(index, isAddSlide) {

                // Kiem tra co phai number
                if( /^\-?\d+/g.test(index) ) index = parseInt(index);

                // Kiem tra index, neu gia tri index khong hop le --> index se la id slide cuoi
                // Slide cuoi cua addSlide khac voi removeSlide
                if( !(typeof index == 'number' && (index >= 0 && index < num)) )
                    index = isAddSlide ? num : num-1;

                return index;
            },



            /* Add new slide & remove slide with index
             * Slide va pagitem co cung chung func --> toi uu code later!!!
            ---------------------------------------------- */
            addSlide : function(str, index) {

                // Check str --> convert to jquery selector
                var $sl = $('');
                if     ( typeof str == 'string' && str != '' ) $sl = $(str);
                else if( typeof str == 'object' )              $sl = str;

                if( $sl.length ) {

                    // Slide setup markup, va return slide da setup
                    // va setup pagitem trong slide
                    // Convert chi so index thanh number
                    $sl   = RENDER.slide($sl);
                    index = API.indexParse(index, 1);


                    // SLIDE SETUP: append to slider with index
                    var isIDEnd = index == num;
                    if( isIDEnd ) { $canvas.append($sl) }
                    else {
                        // Them slide moi vao phia truoc slide index
                        $s.eq(index).before($sl);

                        // Varible $s reset thu tu
                        $s = $canvas.children('.' + o.ns + o.slideName);
                    }


                    // PAGITEM SETUP
                    if( is.pag ) {

                        // Lay noi dung ben trong cua capitem va pagitem
                        RENDER.capPagHTML($sl);

                        // Them pagitem vao pagination
                        var pagAdd = RENDER.pagitem($sl);

                        // Add pagitem vao pagination
                        if( isIDEnd ) { $pagInner.append(pagAdd) }
                        else {
                            // Mac dinh them pagitem moi phia truoc pagitem index
                            $pagItem.eq(index).before(pagAdd);

                            // Varible $pagItem reset thu tuong
                            $pagItem = $pagInner.children('.' + o.ns + 'pagitem');
                        }

                        // Them event click vao pagitem
                        EVENTS.pag();
                    }



                    // ID toggle class active --> Ho tro khi index trung voi idCur
                    if( index == cs.idCur ) cs.idLast = cs.idCur + 1;

                    // Properties slider & slide: resetup
                    is.apiAdd = 1;      // De cac func khac biet update slider bang apiAdd
                    PROP.slider();      // Setup prop truoc --> trong khi load image
                    PROP.slide();

                    // Slide setup to load image
                    LOAD.slideBegin($sl, 1);
                }
            },


            removeSlide : function(index) {

                // Dieu kien remove: phai co it nhat 1 slide
                if( num > 1 ) {

                    // Convert index thanh number
                    index = API.indexParse(index, 0);

                    // Kiem tra index, neu gia tri index khong hop le --> index se la id slide cuoi
                    // if( !(typeof index == 'number' && (index >= 0 || index < num)) ) index = num-1;

                    // Setup inCur: idCur cuoi, remove se lay bot --> idCur chuyen sang id phia truoc
                    if( cs.idCur == num-1 ) cs.idCur = num-2;


                    // Remove slide from slider va setup lai var $s
                    $s.eq(index).remove();
                    $s = $canvas.children('.'+ o.ns + o.slideName);

                    // Remove pagitem form pagination va setup lai var $pagItem
                    if( is.pag ) {
                        $pagItem.eq(index).remove();
                        $pagItem = $pag.find('.' + o.ns + 'pagitem');
                    }

                    // Repress other setting in slider
                    is.apiRemove = 1;   // Bien de cac func khac nhan biet loai bo slide
                    cs.prop();
                    is.apiRemove = 0;
                }
            }
        },






        /* Position
        ================================================== */
        POSITION = {

            /* Canvas: css translate
            ---------------------------------------------- */
            translateX : function($obj, nx, isNoAnim, isPosFixed, _speed, _ease) {

                // Value: get
                // Doi tuong translate la $obj --> neu khong co chon doi tuong swipeCur
                var $swipe = ($obj != null) ? $obj : va.swipeCur,
                    p = $canvas.is($swipe) ? va.can : va.pag,

                // Vi tri can di chuyen toi
                    x = isPosFixed ? nx : (- nx * p.wTranslate + p.xCanvas),

                // Toc do va easing khi transition
                    sp = _speed ? _speed : speed[cs.idCur],
                    es = _ease ? M.easeName(_ease) : va.ease;


                // xCanvas: set current value
                p.xCanvas = x;          // --> co can thiet --> xem lai boi vi func nearX() da setup roi
                

                // Transition danh cho browser support transition css
                var tf = {};
                if( is.ts ) {

                    // Clear timeout thuoc tinh transition
                    M.tc($swipe.data('timer'));

                    // Them thuoc tinh transition css vao canvas
                    if( !isNoAnim ) M.tsAdd($swipe, sp, es);

                    
                    // Canvas: set transform - important
                    // Ho tro transition theo huong swipe
                    var translate = (p.dirs == 'hor') ? 'tlx' : 'tly';
                    tf[p.cssTf] = va[translate + 0] + x + 'px' + va[translate + 1];        // Faster than M.tlx();
                    $swipe.css(tf);


                    // Clear thuoc tinh transition --> kiem soat tot hon
                    $swipe.data('timer', setTimeout(function() { M.tsRemove($swipe) }, sp + 10));
                }

                // Transition danh cho old brower --> su dung jQuery animate
                else {
                    tf[p.cssTf] = x;

                    if( isNoAnim ) $swipe.stop(true, true).css(tf);
                    else           $swipe.animate(tf, {duration: sp, queue: false, easing: es});
                }



                // Translate Complete, support for animRebound()
                // M.tc(ti.translateX);
                // ti.translateX = setTimeout(function() { o.idEnd = cs.idCur }, sp);
            },




            /* CSS: Object translate --> khong co transition
            ---------------------------------------------- */
            objTranslateX : function($obj, nx, isPosFixed, xPlus) {

                // Position: init
                var x;
                if( isPosFixed ) x = nx;
                else             x = (o.layout == 'dash') ? ds.pBegin[nx] : nx * va.can.wTranslate;


                // Transform: add _xPlus
                if( typeof xPlus == 'number' ) x += xPlus;


                // Object: set transform
                var tf = {},
                    tlName = (va.can.dirs == 'hor') ? 'tlx' : 'tly';

                tf[cssTf] = is.ts ? M[tlName](x) : x;
                $obj.css(tf);
            },




            /* Canvas: buffer translate
            ---------------------------------------------- */
            bufferX : function(xCur) {

                // Bien shortcut va khoi tao ban dau
                var layout   = o.layout,
                    idCur    = cs.idCur,
                    swipeNav = is.swipeNav,
                    isRight  = swipeNav == 'right',
                    isLeft   = swipeNav == 'left',

                    isCanvas = $canvas.is(va.swipeCur),
                    p        = isCanvas ? va.can : va.pag,
                    w        = p.wTranslate,
                    x        = p.xCanvas,

                // Thuoc tinh luu tru su khac nhau khi di chuyen 'next' hay 'prev'
                    sign = px.offset < 0 ? 1 : -1,

                // Khoang xe dich khi swipe move
                    pageX = px.pageX1 - px.pageX0;



                // Giam ti le gia tri di chuyen --> swipe out viewport
                // Chi ap dung cho canvas co isLoop-0 va pagination
                if( (isCanvas && !o.isLoop && (layout == 'line' || layout == 'dash')) || !isCanvas ) {

                    // Swipe limit chi ap dung khi swipe phai va swipe trai canvas o ngoai viewport
                    if( (px.buffer > p.xMin && isRight)
                    ||  (px.buffer < p.xMax && isLeft) ) {

                        // Giam ti le xuong 8 lan cho desktop, mobile thi nho hon
                        var nRate1 = is.mobile ? 4 : 8;
                        pageX /= nRate1;
                    }


                    // Grab stop view
                    // Can phai rut gon
                    if( o.isViewGrabStop ) {

                        if     ( px.buffer > 0 && isRight )     M.toggleClass('stop', 1);
                        else if( px.buffer < p.xMax && isLeft ) M.toggleClass('stop', 0);
                    }
                }
                // Tuong tu o tren --> danh cho layout dot va free
                else if( isCanvas && (layout == 'dot' || layout == 'free')) {

                    // Giam ti le di chuyen mac dinh tren layout dot va free
                    var nRate2 = is.mobile ? 2 : 4;
                    pageX /= nRate2;

                    // Tiep tuc giam ti le neu isloop false
                    if( !o.isLoop
                    &&  (  (idCur <= 0 && isRight)
                        || (idCur >= num-1 && isLeft) ) ) {

                        pageX /= 4;
                    }
                }



                // Layout [line - dot -dash]
                if( layout != 'free' ) {

                    // Transform di chuyen tam thoi
                    if( isCanvas && (o.view == 'coverflow' || o.view == 'scale') ) { px.buffer += pageX * w / va.wCon }
                    else                                                           { px.buffer += pageX }

                    // Di chuyen canvas tam thoi
                    // Di chuyen x/y tuy theo huong swipe
                    var tl = (p.dirs == 'hor') ? 'tlx' : 'tly';
                    var tf = {}; tf[p.cssTf] = M[tl](px.buffer.toFixed(1));
                    va.swipeCur.css(tf);


                    // UPDATE TRANSFORM CAC SLIDE CHINH GIUA
                    // Truyen tham so a: phan biet swipe 'next'/'prev'
                    isCanvas && VIEW.buffer[o.view](sign);
                }





                // Slider center
                // Next/prev cung cong thuc voi nhau nhung khac bien so 'a.s'
                // Vi so sanh cua 'next' la '>', con so sanh cua 'prev' la '<' nen nhan hai ve cho -1 de dao nguoc lai tu '<' sang '>'
                if( isCanvas && layout == 'line' ) {
                    var pNext = x - (w * sign);


                    // Swipe 'next' slide (so am) --> Swipe 'prev' tuong tu nhu 'next'
                    if( px.buffer * sign < pNext * sign ) {

                        // Reset action chi thuc hien 1 lan trong luc drag lien tuc
                        is.swipeBegin = 1;

                        // Update px.x0 --> su dung cho event dragmove --> de khi dragOut thi canvas chi di chuyen toi da 1 slide nua
                        px.x0 = xCur;

                        // Update xCanvas
                        p.xCanvas -= w * sign;

                        // Update so slide di chuyen duoc khi bat dau swipe --> reset khi swipe end
                        va.nSwipe += sign;

                        /* Update cac thanh phan khac khi next 1 slide
                            Them option isLive --> ngan can setup 1 so options, trong do co POSITION.translateX
                            --> boi vi xCanvas da update o tren */ 
                        SLIDETO.run(sign, 0, 1);
                    }
                }


                // Setup other value
                // is.swipeBegin --> voi muc dich function chi chay 1 lan trong di drag move
                if( is.swipeBegin ) is.swipeBegin = 0;
            },




            /* Canvas: move to nearly position
            ---------------------------------------------- */
            nearX : function() {

                // Vi tri va kich thuoc doi tuong dang swipe
                var isCanvas = $canvas.is(va.swipeCur),
                    p = isCanvas ? va.can : va.pag;

                // x: Drag how many px
                var x = px.offset;      // Shortcut offset
                is.ease = 'touch';


                if( isCanvas ) {        // Khoa tam thoi

                    // Layout dash
                    if( o.layout == 'dash' ) {

                        // Drag: x > 0 -> prev, x < 0 -> next
                        var nBegin = ds.nBegin;
                        if( x < 0 ) {
                            while( ds.pBegin[nBegin] < -px.buffer ) { nBegin++; }
                        }
                        else if( x > 0 ) {
                            while( ds.pBegin[nBegin] > -px.buffer ) { nBegin--; }
                        }

                        var isID = nBegin != ds.nBegin;
                        SLIDETO.run(nBegin, isID);
                    }

                    // Layout line - dot - free
                    else {

                        // Width in fullwidth and touch screen
                        // var wTran = !!va.pa.left ? p.wTranslate - (va.pa.left*2) : p.wTranslate,
                        var wCon   = !!va.pa.left ? va.wCon - (va.pa.left*2) : va.wCon,
                            tFast  = is.mobile ? 400 : 200,
                            isFast = va.tDrag1 - va.tDrag0 < tFast;


                        // Width drag: select
                        // Xac dinh di chuyen nhanh/cham cua 1 slide
                        var w2  = M.r(wCon/2),
                            w4  = M.r(wCon/4),
                            w10 = M.r(wCon/10),
                            w   = isFast ? w10 : (o.layout == 'dot' ? w4 : w2);


                        // Xac dinh co duoc cong di chuyen them/bot 1 slide nua khong
                        var tGo      = 100,                     // Thoi gian layout dot phuc hoi vi tri cu khi di chuyen sang slide moi
                            tRestore = speed[cs.idCur]/2;       // Thoi gian khi slide phuc hoi lai vi tri cu = 1/2 thoi gian goc


                        // SETUP TAM THOI
                        // var isPlus = isFast && MATH.abs(va.nSwipe) > 0,
                        //     nMovePlus = isPlus ? 0 : va.nSwipe ;
                        var isPlus = false,
                            nMovePlus = 0;


                        // Canvas: next slide
                        if( x < -w && (o.isLoop || (!o.isLoop && cs.idCur < num-1)) && (num-1) ) {

                            (o.layout == 'dot') && POSITION.translateX(null, 0, 0, 0, tGo);
                            SLIDETO.run(nMovePlus + 1, 0);
                        }

                        // Canvas: prev slide
                        else if( x > w && (o.isLoop || (!o.isLoop && cs.idCur > 0)) && (num-1) ) {

                            (o.layout == 'dot') && POSITION.translateX(null, 0, 0, 0, tGo);
                            SLIDETO.run(nMovePlus - 1);
                        }

                        // Canvas: restore position
                        else {

                            // // Phuc hoi lai vi tri va transform sau khi di chuyen tam thoi
                            if( isPlus ) SLIDETO.run(nMovePlus);
                            else {

                                POSITION.translateX($canvas, 0, 0, 0, tRestore);

                                // Phuc hoi lai vi tri va transform sau khi di chuyen tam thoi
                                is.center && VIEW.restore[o.view]();
                            }
                        }

                        
                        // Slideshow: setup bien --> reset timer khi di chuyen next/prev toi slide khac
                        if( (x < -w || x > w) && o.isSlideshow ) is.hoverAction = 1;
                    }
                }


                // Tam thoi: Pagination setup
                else {
                    if( x != 0 ) {

                        // Update gia tri xCanvas
                        p.xCanvas = px.buffer;

                        // Phuc hoi lai vi tri chinh giua cho pagInner
                        var sp = o.pag.speed;
                        // if( p.isPagCenter ) {
                        if( p.align == 'center' || p.align == 'end' ) {
                            p.xCanvas != p.xPull && POSITION.translateX($pagInner, p.xPull, 0, 1, sp);
                        }

                        // Phuc hoi lai vi tri dau/cuoi neu Canvas o ngoai viewport
                        else {
                            if( p.xCanvas > 0 )           { POSITION.translateX($pagInner, 0, 0, 1, sp) }
                            else if( p.xCanvas < p.xMax ) { POSITION.translateX($pagInner, p.xMax, 0, 1, sp) }
                        }
                    }
                }



                // Test flywheel (banh da): tiep tuc di chuyen
                POSITION.flywheel();
            },




            /* Thu tu id slide xuat hien duoc luu tru trong mang
             * Qua trinh:
                    Tim kiem id-slide bat dau trong mang
                    --> thu tu con lai trong mang chi viec +1
                    neu thu tu lon hon num --> bat dau lai == 0
            ---------------------------------------------- */
            centerIdMap : function() {
                var map = [];

                // Setup idBegin
                // Uu tien slide xuat hien ben phai neu tong slide la so chan
                var idBegin = M.c(num/2) + cs.idCur;
                if( va.center.isNum == 'even' ) idBegin++;

                // idBegin bat dau lai bang so nho, neu lon hon num
                if( idBegin >= num ) idBegin -= num;


                // Func loop: add id to map
                for (i = 0; i < num; i++) {

                    // Id begin tro ve 0, neu lon hon num
                    if( idBegin >= num ) idBegin = 0;

                    // Map: add value
                    map[i] = idBegin++;
                }

                // Luu vao bien
                va.idMap = map;
            },




            /* Center slide balance
             + Muc dich:
                Di chuyen slide o vi tri edge
                --> slides luon can bang so luong 2 ben sau khi canvas move

             + Thuc hien:
                Xac dinh bao nhieu slide can di chuyen --> vong lap di chuyen tung slide
                Di chuyen tung slide: xac dinh id slide can di chuyen, vi tri(p) se di chuyen toi
                --> thuc hien di chuyen bang objTranslateX()
            ---------------------------------------------- */
            balance : function(isLive, isOne, _speed) {

                // Kiem tra di chuyen 'next' hay 'prev' slide
                // Di chuyen 'next' va 'prev' co cung cach thuc nhu nhau --> chi khac doi so
                var isNext = va.nMove > 0,

                // Thuoc tinh luu tru su khac nhau khi di chuyen 'next' hay 'prev'
                    a = isNext ? { is : 1, s : 1, id0 : 0, idN : num-1 }
                               : { is : 0, s :-1, id0 : num-1, idN : 0 },

                // So luong slide di chuyen duoc ket hop voi options 'isOne', mac dinh la va.nMove
                    nMove = isOne ? 1 : M.a(va.nMove);


                // Toc do khi translate --> cang nhieu slide thi toc do cang nho
                a.sp = (_speed == UNDEFINED) ? speed[cs.idCur] : _speed;

                // Chen nhung option khac vao namespace
                a.isLive = isLive;


                // Swap slide to balace
                var w = va.can.wTranslate,
                    id, p, tf;

                for (i = 0; i < nMove; i++) {

                    // GIA TRI CUA SLIDE RIA --> dich chuyen varible trong array
                    // id: lay id slide of first array
                    // p : Lay vi tri of last array + wslide
                    // tf: vi tri thanh tranform
                    id = va.idMap[a.id0];
                    p  = va.pBegin[a.idN] + (w * a.s);


                    // Gia tri Transform
                    var tf = {};
                    if( o.view == 'basic' || o.view == 'mask' ) {
                        var tl = (va.can.dirs == 'hor') ? 'tlx' : 'tly';            // Translate bang css3
                        tf[cssTf] = M[tl](p);
                    }
                    else if( o.view == 'coverflow' ) {

                        // Setup transform cua slide ria
                        tf = VIEW.tf1(p, - o.coverflow.rotate * a.s);
                        tf['z-index'] = va.zMap[a.idN]-1;
                    }
                    else if( o.view == 'scale' ) {

                        // Setup transform cua slide ria
                        tf = VIEW.tf2(p, o.scale.intensity/100);
                    }



                    // Update gia tri trong namespace
                    var aIS = a.is;
                    M.shift(va.idMap, aIS);
                    M.push(va.idMap, id, aIS);

                    M.shift(va.pBegin, aIS);
                    M.push(va.pBegin, p, aIS);

                    M.shift(va.tfMap, aIS);
                    M.push(va.tfMap, tf, aIS);



                    // Setup transition cua slide ria khi slider chi co 3 SLIDES
                    // Neu khong thi loai bo transtion
                    var ts = {}, slEdge = $s.eq(id);
                    if( o.view != 'basic' && num == 3 ) {

                        // Xoa bo timer clear transition(neu co) truoc khi assign transition
                        M.tc(slEdge.data('timer'));

                        ts = M.ts(cssTf, a.sp, va.ease);
                        slEdge.data('timer', setTimeout(function() { M.tsRemove(slEdge) }, a.sp));
                    }
                    else ts[cssTs] = '';


                    // Assign transition va transform moi vua setup vao slide can di chuyen
                    slEdge.css(ts).css(tf);

                    // Fixed khong chiu hien image trong firefox -> firefox version moi da fixed
                    // is.firefox && IMAGE.autoShow(id)



                    // UPDATE SLIDE CHINH GIUA VA CANVAS
                    VIEW.balance[o.view](a);
                }
            },




            /* Center fill hold when move by pagination
             + Muc dich:
                Khi di chuyen bang pagination --> slide o vi tri edge tu dong di chuyen de tat ca slide can bang
                Khi do xuat hien khoang trang do slide edge di chuyen --> copy slide edge giu nguyen vi tri
                --> sau time translate thi loai bo slide da copy.
            ---------------------------------------------- */
            fillHole : function() {
                if( o.view == 'basic' ) {

                    // Kiem tra slideClone - remove
                    va.sClone.length && va.sClone.remove();


                    // Kiem tra clone slide hay ko
                    // Khi pagination ma chi di chuyen slide an sau viewport thi khong can thiet clone slide.
                    var n    = va.center.n,
                        nMin = (va.nMove > 0) ? n.left : n.right;
                        nMin -= n.edge;

                    var nMoveAbs = M.a(va.nMove);       // Shortcut nMove luon luon duong
                    if( nMoveAbs > nMin ) {

                        // clone slide - chi clone slide nhin thay
                        // -> id get tu nMin
                        for (i = nMin; i < nMoveAbs; i++) {

                            // Copy slide roi append vao canvas
                            var id = (va.nMove > 0) ? va.idMap[i] : va.idMap[num-1-i],
                                sl = $s.eq(id).clone().appendTo($canvas);

                            // Add slide vua moi clone vao bien --> remove toan bo slide clone sau khi di chuyen xong
                            va.sClone = va.sClone.add(sl);
                        }

                        // Xoa bo tat ca slide clone sau khi transition ket thuc
                        M.tc(ti.fillHole);
                        ti.fillHole = setTimeout(function() {

                            // Them hieu ung fade neu wSlide total < wViewport
                            if( va.wSlide * num < wViewport ) {
                                va.sClone.animate({ opacity : 0 }, {
                                    duration : 200,
                                    complete : function() { va.sClone.remove() }
                                });
                            }
                            else va.sClone.remove();
                        }, speed[cs.idCur]);
                    }
                }
            },




            /* Chuyen dong rebound khi click vao navigation khong cho di chuyen
            ---------------------------------------------- */
            animRebound : function(dirs) {
                if( o.isAnimRebound ) {

                    // Bien shortcut va khoi tao ban dau
                    var p      = va.can,
                        layout = o.layout,
                        isNext = dirs == 'next',
                        sign   = isNext ? -1 : 1,

                        tSpeed = 150,                           // Thoi gian chuyen dong
                        plus   = 30,                            // x plus value, unit px
                        xBack  = isNext ? p.xMax : p.xMin,      // Vi tri ban dau cua canvas
                        xLimit = 130 * sign + xBack;            // Vi tri gio han de canvas quay tro lai --> +/-130px



                    // Vi tri hien tai --> ho tro lay vi canvas di chuyen
                    var xCur = $canvas.css(cssTf);
                    if( is.ts ) xCur = (xCur == 'none') ? xBack : M.valueX(xCur);
                    else        xCur = (xCur == 'auto') ? xBack : parseInt(xCur);
                    
                    // Vi tri canvas di chuyen duoc
                    var xGo = plus * sign + xCur,

                    // Function chuyen dong go va back
                        fnGo   = function() { POSITION.translateX(null, xGo, 0, 1, tSpeed) },
                        fnBack = function() { POSITION.translateX(null, xBack, 0, 1) };



                    /* xGo: limited value
                        --> khi canvas di chuyen vuot qua gioi han cho phep
                        --> canvas di chuyen ve vi tri ban dau */
                    if( xGo/sign > xLimit/sign ) fnBack();

                    // /* Animate run
                    //     --> Se cho canvas di 1 doan --> setup timer de quay tro lai */
                    else {

                        // if( (layout != 'line' && layout != 'dash') 
                        //     // ||  (  (layout == 'line' || layout == 'dash')
                        //     //     && (  (dirs == 'next' && o.idEnd == num-1) || (dirs == 'prev' && !o.idEnd)  ))) {}
                        fnGo();
                        M.tc(ti.rebound);
                        ti.rebound = setTimeout(fnBack, tSpeed);
                    }
                }
            },



            /* Flywheel (banh da): tiep tuc di chuyen khi ngung swipe
            ---------------------------------------------- */
            flywheel : function() {

                var $swipe = va.swipeCur;
                var isCanvas = $canvas.is(va.swipeCur),
                    p = isCanvas ? va.can : va.pag;

                // Di chuyen cho pagination truoc
                if( !isCanvas ) {

                    /* Dieu kien de banh da di chuyen:
                        + o trong pham vi viewport
                        + Thoi gian swipe nho hon 200ms
                        + Di chuyen tam thoi phai lon hon 1 wTranslate --> truong hop slide chinh
                    */
                    var tDrag = va.tDrag1 - va.tDrag0,
                        // isRun = (px.buffer < 0 && px.buffer > p.xMax) && (tDrag < 200) && (MATH.abs(px.offset) > p.wTranslate);
                        isRun = (px.buffer < 0 && px.buffer > p.xMax) && (tDrag < 200) && (M.a(px.offset) > 10);

                    if( isRun ) {

                        var xOff    = px.pageX1 - px.x0Fix,     // khoang cach swipe duoc --> lay dung gia tri thay vi xOffset
                            xTarget = px.buffer + xOff,
                            sp      = o.pag.speed;              // Thoi gian chuyen dong
                        

                        // Kiem tra gia tri co xTarget co o ngoai pham vi viewport --> ++ di chuyen 2 lan nua
                        if( xTarget > 0 || xTarget < p.xMax ) {

                            var x = [];
                            if     ( xTarget > 0 )      x[0] = 0;
                            else if( xTarget < p.xMax ) x[0] = p.xMax;

                            x[1] = (xTarget - x[0])/8 + x[0];
                            x[2] = x[0];

                            /* Setup chuyen dong rebound lai voi easing linear
                                + Chuyen dong 1: toi vi tri ria` 0 hay xMax
                                + Chuyen dong 2: di chuyen canvas ra ngoai viewport
                                + Chuyen dong 3: phuc hoi canvas tro lai vi tri ria`
                            */
                            var es = o.pag.ease;
                            POSITION.translateX($pagInner, x[0], 0, 1, sp/4, 'linear');

                            M.tc(ti.flywheel1); M.tc(ti.flywheel2);
                            ti.flywheel1 = setTimeout(function() { POSITION.translateX($pagInner, x[1], 0, 1, sp/2, es) }, sp/4);
                            ti.flywheel2 = setTimeout(function() { POSITION.translateX($pagInner, x[2], 0, 1, sp, es) }, (sp/4) + (sp/2));

                            // console.log('flywheel rebound');
                        }

                        // Chuyen dong banh da binh thuong --> o trong pham vi viewport
                        else { POSITION.translateX($pagInner, xTarget, 0, 1, sp) }
                    }
                }



                else {

                    /* Dieu kien de banh da di chuyen:
                        + Thoi gian swipe nho hon 200ms
                        + So slide di chuyen duoc lon hon 0
                    */

                    var tDrag = va.tDrag1 - va.tDrag0;
                        isRun = (tDrag < 200) && (M.a(va.nSwipe) > 0);

                    if( isRun ) {}
                }
            }
        },






        /* Sizes
         * Da so la function upate gia tri khi resize
        ================================================== */
        SIZE = {

            /* Codeslide: setup size others
            ---------------------------------------------- */
            general : function() {

                // Margin: get
                // if have responsive -> got in res func()
                !is.res && SIZE.margin();


                // Canvas: set width
                if( o.layout == 'line' || o.layout == 'dot' || o.layout == 'free' ) {
                    var wSlide;

                    // wCanvas center position
                    if( is.center ) {

                        // width: get from media
                        // wSlide = RES.valueGet(va.center, 'width');
                        wSlide = RES.valueGet(va.wRange, 'width');

                        // Chuyen doi unit percent sang px, don vi percent trong khang [0, 1]
                        if( wSlide > 0 && wSlide <= 1 ) wSlide *= va.wSwap;
                    }

                    // wCanvas default: always == viewport
                    else wSlide = wViewport;


                    // Setup gia tri wSlide
                    va.wSlide = parseInt(wSlide);

                    // Setup chieu rong cua canvas theo huong swipe
                    va.wCanvas = (va.can.dirs == 'hor') ? va.wSlide : wViewport;
                    $canvas.css('width', va.wCanvas);
                }


                // ImageBack: dat vi tri CENTER horizontal
                is.loadAll && IMAGE.backCenterHor();


                // TranslateW: get
                SIZE.translateW();


                // Slide: other setting
                if( o.layout == 'line' ) SIZE._lineWidth();
                if( o.layout == 'dash' ) SIZE._dashWidth();


                // Pagination: update gia tri va vi tri center
                if( is.pag && !is.pagList ) {
                    PAG.size();
                    PAG.itemCenter();
                }
            },


            _lineWidth : function() {

                // Xac dinh number-slide o ben canh thay duoc so voi slide giua
                if( is.cenLoop ) {

                    var wAll = 0, i = 0;
                    while (wAll < wViewport) {
                        wAll = (va.wSlide + va.ma[0] + va.ma[1]) * (i*2 + 1);       // So 1: cho slide giua, so 2 cho 2 slide ben canh
                        i++;
                    }
                    var nEdge = i-1;
                    if( nEdge*2 >= num ) nEdge = ~~((num-1)/2);

                    // Luu ket qua vao namespace va.center.n
                    va.center.n.edge = nEdge;
                }



                // Tinh toan vi tri pBegin, transfrom tung slide
                VIEW.size[o.view]();


                // Canvas: di chuyen toi vi tri ban dau
                // Slider center: xCanvas da co gia tri --> func() chi de update gia tri tren canvas
                va.swipeCur = $canvas;
                M.tsRemove($canvas);        // Loai bo transition khi update

                if( o.isLoop ) POSITION.translateX(null, va.can.xCanvas, 1, 1);
                else           POSITION.translateX(null, cs.idCur, 1);
            },


            _dashWidth : function() {

                // Slide: set properties
                ds.pBegin    = [];
                ds.pEnd      = [];
                ds.width     = [];
                ds.mCanvas   = parseInt($canvas.css('margin-left'));
                is.canvasEnd = 0;

                var _snum = $s.length
                  , _x    = 0;

                // Width slide not true, waiting image loaded, fix later!
                for (i = 0; i < _snum; i++) {
                    var $el = $s.eq(i);

                    // Dash: width check & set
                    // width slide auto resize == wViewport when width-slide < wViewport 
                    var _str = $el.attr('style');
                    if( _str != UNDEFINED && !!_str.indexOf('width') ) $el.css('width', '');

                    var _w = $el.outerWidth(true);
                    if( _w > wViewport ) {
                        $el.css('width', wViewport);

                        // Margin: add to width
                        var _ml = parseInt($el.css('margin-left'))
                          , _mr = parseInt($el.css('margin-right'));
                        _w = wViewport + _ml + _mr;
                    }
                    ds.width[i] = _w;


                    // Dash: position
                    ds.pBegin[i] = _x; _x += _w;
                    ds.pEnd[i]   = _x;

                    // Slide: reset position
                    POSITION.objTranslateX($el, i);
                }


                // Slide: reGet height
                if( is.loadAll ) {

                    ds.height = [];
                    for (i = 0; i < _snum; i++) ds.height[i] = $s.eq(i).outerHeight(true);
                }



                // Pag Item update
                if( is.pag ) {
                    ds.pagID = [0];

                    // Loai bo toan pagitem khoi dom, va add pagitem dau tien vao
                    // --> update pagitem when resize
                    $pagItem.detach();
                    $pag.append( $pagItem.eq(0) );


                    // Tim kiem pagitem bat dau hien thi trong viewport tung doan
                    for (var i = 0, _wSlide = 0; i < _snum; i++) {
                        _wSlide += ds.width[i];
                        if( _wSlide > wViewport - ds.mCanvas && i != 0 ) {

                            $pag.append( $pagItem.eq(i) );
                            ds.pagID.push(i);
                            _wSlide = ds.width[i];
                        }
                    }

                    // Update varible and events
                    ds.pagNum = ds.pagID.length;
                    EVENTS.pag();
                }



                // Pos Max: get
                // margin right will replace by value options
                var _slEndMaRi = parseInt($s.eq(num-1).css('margin-right'));    // slide end margin right
                ds.pMax = -(ds.pEnd[_snum-1] - _slEndMaRi  - wViewport + ds.mCanvas);

                
                // Canvas: goto current number begin
                ds.lastBegin = ds.nBegin;
                ds.nBegin = 0;
                SLIDETO.run(ds.lastBegin, 0);



                // Slide: clone if o.isLoop == true
                // Fix later
                // if( o.isLoop ) {

                //     console.log(ds.pagID);

                //     var _nCloneBegin = ds.pagID[1] - ds.pagID[0];
                //     for (i = 0; i < _nCloneBegin; i++ ) {

                //         var $_clone = $s.eq(i).clone();
                //         $_clone.data({'id': -1}).appendTo($canvas);
                //     }
                // }

                // console.log(ds.pBegin, ds.pEnd, va.can.x, ds.pMax);
                // console.log(ds.width);
                // console.log(ds.pagID);
                // console.log(ds.pBegin, ds.pEnd);
            },





            /* Translate: width
            ---------------------------------------------- */
            translateW : function() {

                /* CANVAS - SETUP */
                // Width container of slide include margin
                // Margin: tao shortcut de khoi so sanh nhieu lan
                var ma0 = va.ma[0],
                    ma1 = va.ma[1];

                // Tu dong lay margin khi Viewport co padding --> ho tro tab styled
                if( !va.maRange && wViewport != $viewport.innerWidth() ) {
                    ma0 = va.ma[0] = M.pInt($viewport.css('padding-left'));
                    ma1 = va.ma[0] = M.pInt($viewport.css('padding-right'));
                }

                // Assign value
                va.wCon = va.wSlide + ma0 + ma1;




                // Sau khi resize --> Canvas va slide deu reset lai position --> xCanvas cung reset lai
                // Slider center --> xCanvas: tinh toan vi tri lui` lai cua canvas
                var xBegin,
                    layout = o.layout;
                if( layout == 'line' && is.center ) xBegin = M.r( (va.wSwap - va.wSlide)/2 );
                else                                xBegin = 0;

                // Canvas luu tru nguoc lai cac gia tri
                var vaCan = va.can;
                vaCan.wTranslate = va.wCon;                         // Mac dinh la wCon --> cac view khac se update gia tri sau
                vaCan.xCanvas = xBegin;                             // Update vi tri bat dau cua canvas
                
                // Ho tro cho swipe bi gioi han --> px.buffet giam ti le
                if( layout == 'line' ) {
                    vaCan.xMin = xBegin;
                    vaCan.xMax = -(va.wCon*num - wViewport) - xBegin;
                }
                else if( layout == 'dot' ) {
                    vaCan.xMin = vaCan.xMax = 0;
                }



                /* PAGINATION - SETUP */
                // update gia tri cac thuoc tinh cua pagination
                is.pag && !is.pagList && PAG.prop();
            },





            /* Margin: get
             * Type margin is array: gia tri dau tien la 'left', thu 2 la 'right'
            ---------------------------------------------- */
            margin : function() {

                // Margin not available when no media
                var isMargin = 0;
                if( !!va.maRange ) {

                    var ma   = va.maRange,     // Shortcut margin
                        wDoc = $doc.width();
                    for (i = ma.num-1; i >= 0; i--) {

                        // Margin: find
                        if( ma[i].from <= wDoc && wDoc <= ma[i].to ) {

                            va.ma = [ma[i].left, ma[i].right];
                            isMargin = 1;
                        }
                    }
                }

                // Update resize: reset
                if( !isMargin ) va.ma = [0, 0];
            },




            /* Code: reset height
            ---------------------------------------------- */
            sliderHeight : function(isUpdateResize) {

                // Ho tro smoothHeight cho doi tuong canvas & paginner
                var smoothHeightFn = function(height, $obj, isViewport) {

                    // Bien khoi tao ban dau --> neu resize thi khong can animate
                    var vaDuration = isUpdateResize ? 0 : o.speedHeight - 10;

                    // Assign value chieu cao cua slider
                    if( isViewport ) hCode = height;


                    // HIEU UNG HEIGHT
                    if( is.mobile ) $obj.css('height', height);
                    else
                        $obj.delay(2).animate({ 'height' : height }, {
                            duration: vaDuration,
                            queue: false,
                            complete: function() {

                                $obj.css('overflow', '');

                                // UPDATE LAI SLIDER NEU TOGGLE SCROLLBAR
                                isViewport && EVENTS.reCheck();
                            }
                        });
                };


                // Layout line - dot - free
                var layoutOtherFn = function() {

                    // Lay chieu cao hien tai cua slide current
                    var hCur = $s.eq(cs.idCur).outerHeight(true);


                    // Smooth resize height slider when move to near slide
                    // Them options isUpdateResize de luon luon run smoothHeightFn()
                    // Tranh truong hop khi update, hCode == hCur --> khong chay smoothHeight()
                    if( o.height == 'auto' && ((hCode != hCur && hCur > 0) || !!isUpdateResize) ) {
                        smoothHeightFn(hCur, $viewport, 1);


                        // Smooth height cho pagination chieu huong vertical
                        if( is.pag && !is.pagList && va.pag.dirs == 'ver' && !is.pagOutside && o.pag.sizeAuto == 'full' ) {

                            // Update cac gia tri cua pagination va UPDATE VI TRI CUA PAGINATION
                            PAG.prop(1);
                            // console.log('pag height', va.pag.dirs, hCur + va.viewSpace.ver);
                            smoothHeightFn(hCur + va.viewSpace.ver, $pag, 0);
                        }
                    }
                };


                // Layout dash
                var layoutDashFn = function() {

                    // Height : get
                    var _hMax = 0, _num = ds.nEnd - ds.nBegin;
                    for (i = ds.nBegin; i <= ds.nEnd; i++) {

                        var _h = ds.height[i];
                        if( _h != UNDEFINED && _h > _hMax ) _hMax = _h;
                    }

                    // Slider: smooth height
                    if( _hMax > 0 && _hMax != hCode ) smoothHeightFn(_hMax, $viewport, 1);
                };


                // Function select
                // Setup timer cho sliderHeight --> THAY DOI CHIEU CAO SAU CUNG
                // >= 30 ms --> layout DOT khi toggle hNative can delay cho old browser
                // console.log(+new Date());
                M.tc(ti.sliderHeight);
                ti.sliderHeight = setTimeout(function() {
                    o.layout == 'dash' ? layoutDashFn() : layoutOtherFn();
                }, 30);
            },





            /* Height slider cho 'height-fixed' option
            ---------------------------------------------- */
            sliderHeightFix : function() {

                // Setup chieu cao cho viewport
                var viewHeightFn = function(hView) {
                    console.log('set height viewport');
                    $viewport.css('height', hView);
                };


                // Fullscreen setup
                if( o.isFullscreen ) {

                    // Get height page & assign to viewport
                    var h = $w.height();

                    // Height slider will subtract by height container if have offsetBy
                    if( o.offsetBy != null ) {
                        var hTop  = 0,
                            hBot  = 0,
                            isImg = 0;


                        // Get height of container top & bottom
                        var $top = $(va.offsetBy.top);
                        if( $top.length ) {
                            $top.each(function() { hTop += $(this).outerHeight(true) });

                            if( $top.find('img').length ) isImg = 1;
                        }

                        var $bot = (va.offsetBy.bottom == null) ? $('') : $(va.offsetBy.bottom);
                        if( $bot.length ) {
                            $bot.each(function() { hBot += $(this).outerHeight(true) });

                            if( $bot.find('img').length ) isImg = 1;
                        }

                        // Height slider will substract by height offsetBy container
                        h -= hTop + hBot;


                        // Reupdate when offsetBy container have image, when loaded -> reupdate
                        if( isImg ) $w.load(function() { cs.refresh(); });
                    }

                    hCode = h;
                    viewHeightFn(hCode);
                }

                // Default setup
                else {

                    // Muc do uu tien cua height slider: va.hRes > height css > o.hCode
                    // Assign height viewport when have height repsonsive
                    if( va.hRes ) {
                        hCode = M.r(va.hRes*va.rate);
                        viewHeightFn(hCode);
                    }
                    else {

                        // Height value in css
                        var h = $viewport.height();

                        // Kiem tra neu set Chieu cao tu option khac chieu cao trong css
                        if( o.hCode != null && h != o.hCode ) {
                           h = o.hCode;
                           viewHeightFn(h);
                        }

                        if( !h ) h = 0;
                        hCode = h;
                    }
                }
                
                // Lay chieu rong cua slider khi khong co responsive
                SIZE.wCode();
                rCanvas = wViewport/hCode;
            },





            /* Slides: setup all slide at time
            ---------------------------------------------- */
            slideHeight : function() {
                
                // Slide setup
                $s.each(function() {
                    var $sl = $(this);

                    // Image background: reset center by remove css 'top'
                    IMAGE.backCenter.reset($sl);

                    // slide: store height when image background fit/fill, or update size
                    // Get height cua slide phai co delay (phuc vu cho slide co text) --> setup trong bay h khong chinh xac.
                    // $sl.data('height', $sl.outerHeight(true));

                    // Image background: set vertical center
                    (o.height == 'fixed') && IMAGE.backCenter.setup($sl);
                });
            },




            /* Lay chieu rong cua slider --> ho tro lay wSwap phuc vu huong swipe
            ---------------------------------------------- */
            wCode : function() {

                // TAB VERTICAL --> them margin vao viewport de lay wViewport chinh xac
                // console.log(va.pag.wCon);
                is.pag && PAG.tabVer();

                // Bien chieu rong cua slider --> co dinh
                wViewport = $viewport.width();

                // Bien chieu rong cua slider thay doi theo huong swipe
                va.wSwap = (va.can.dirs == 'hor') ? wViewport : hCode;
            }
        },






        /* View
        ================================================== */
        VIEW = {

            /* Nhung func nho
            ---------------------------------------------- */
            // Bao gom prefix transition
            tf1 : function(x, ndeg) {

                var con = 'translate3d('+ x.toFixed(1) +'px, 0, 0)';
                if( ndeg != UNDEFINED ) con += ' rotateY('+ ndeg.toFixed(1) +'deg)';

                var tf = {}; tf[cssTf] = con;
                return tf;
            },

            // Tuong tu tf() o tren
            tf2 : function(x, nScale) {

                var con = 'translate3d('+ x.toFixed(1) +'px, 0, 0)';
                if( nScale != UNDEFINED ) con += ' scale('+ nScale +')';

                var tf = {}; tf[cssTf] = con;
                return tf;
            },




            /* Setup thuoc tinh khi resize trong func size
            ---------------------------------------------- */
            size : {
                basic : function() {

                    // Shortcut bien va khoi tai luc dau
                    va.pBegin  = [];
                    var pBegin = va.pBegin,
                        nBegin = is.cenLoop ? va.center.n.left : 0,
                        vaCan  = va.can;


                    // pBegin --> vi tri cua slide duoc luu theo thu tu sap xep tren slider
                    for (i = 0; i < num; i++) { pBegin[i] = vaCan.wTranslate * (- nBegin + i) }


                    // Slides: sap xep vi tri co san trong mang pBegin da setup o tren
                    // Update gia tri transform va namespace --> dinh dang giong voi cac view khac
                    var isHor     = vaCan.dirs == 'hor',
                        translate = isHor ? 'tlx' : 'tly',
                        tf        = {},
                        id;

                    va.tfMap = [];
                    for (i = 0; i < num; i++) {
                        id = is.cenLoop ? va.idMap[i] : i;

                        tf[vaCan.cssTf] = M[translate](pBegin[i]);

                        va.tfMap.push(tf);      // add vao namespace transform
                        $s.eq(id).css(tf);      // Dat slide o vi tri dinh san
                    }
                }
            },



            /* Setup cac slide khi di chuyen tam thoi --> de quan ly
            ---------------------------------------------- */
            buffer : {
                basic : function() { /* empty */ }
            },



            /* Setup cac slide de can bang
            ---------------------------------------------- */
            balance : {
                basic : function() { /* empty */ }
            },




            /* Phuc hoi lai VI TRI va TRANSFORM cac slide sau khi di chuyen tam thoi
            ---------------------------------------------- */
            restore : {
                basic : function() { /*empty*/ }
            }
        },






        /* Slide to
        ================================================== */
        SLIDETO = {

            /* Layout
            ---------------------------------------------- */
            line : function(st) {

                is.ts && M.tsRemove(va.swipeCur);
                SLIDETO.idCur(st);
                // is.firefox && IMAGE.autoShow();  // Fixed khong chiu hien image trong firefox -> firefox version moi da fixed
                SIZE.sliderHeight();

                // Setup khi slide chay xong effect --> dat vi tri dau cho giong nhau
                M.tc(ti.lineEnd);
                ti.lineEnd = setTimeout(SLIDETO.end, speed[cs.idCur]);


                // Slider center
                if( is.cenLoop ) {

                    // Fill hole when move by pagination
                    st.isID && POSITION.fillHole();

                    // Thiet lap timer cho fillHole() khi slide balance --> roi moi di chuyen canvas
                    setTimeout(function() { SLIDETO.lineTrans(st) }, st.isID ? 10 : 0);
                }

                // Default setup
                // TranslateX next item
                else { setTimeout(function() { !st.isLive && POSITION.translateX($canvas, st.num) }, 0) }
            },

            /* Muc dich:
                + View 'coverflow' --> tach chuyen dong cua pagination thanh nhieu traslate nho */
            lineTrans : function(st) {

                var n = M.a(st.num);
                if( o.view != 'basic' && n > 1 ) {

                    var tOne = M.r(speed[cs.idCur]/n),      // Thoi gian di chuyen tung slide
                        t    = 0,
                        sign = st.num > 0 ? 1 : -1;         // Phan biet 'next' hay 'prev'

                    // Function setup transform tung slide
                    var _transOne = function(_time, _es) {
                        setTimeout(function() {

                            // Easing rieng cho tach chuyen dong nay
                            va.ease = M.easeName(_es);
                            is.easeLast = 'multi';

                            POSITION.balance(st.isLive, sign, tOne + 100);
                            !st.isLive && POSITION.translateX($canvas, sign, 0, 0, tOne + 100);
                        }, _time-100);
                    };

                    // Tang thoi gian sau khi set timer
                    for (i = 0; i < n; i++, t += tOne) {

                        var es = (i == n-1) ? o.easeMove : 'linear';
                        _transOne(t, es);
                    }

                    // Setup lock swipe va lock SLIDETO.run() khi thuc function multi run
                    is.lockSwipe = is.lockNav = 1;
                    setTimeout(function() { is.lockSwipe = is.lockNav = 0; }, speed[cs.idCur]);
                }

                // View basic hoac view khac di chuyen chi 1 slide
                else {
                    POSITION.balance(st.isLive);                // Slider balance

                    if( !st.isLive ) {
                        POSITION.translateX($canvas, st.num);
                    }
                }
            },




            dash : function(st) {

                // Position: reset when drag immediately
                is.ts && M.tsRemove(va.swipeCur);


                // Number: setup
                SLIDETO.idCurDash(st);
                M.toggleDash();


                // Goto end position
                if( !o.isLoop && st.num > 0 && ds.nEnd == num-1 ) {
                    POSITION.translateX(null, ds.pMax, 0, 1);
                    is.canvasEnd = 1;
                }
                // prev after end position
                else if( !o.isLoop && st.num < 0 && is.canvasEnd ) {
                    POSITION.translateX(null, -ds.pBegin[ds.nBegin], 0, 1 );
                    is.canvasEnd = 0;
                }
                // Other position
                else POSITION.translateX(null, -ds.pBegin[ds.nBegin], 0, 1);



                // Slider: update height
                SIZE.sliderHeight();


                // Transition End: set properties
                setTimeout( SLIDETO.end, speed[0] + 10 );
            },



            dot : function(st) {

                // Toggle id current
                SLIDETO.idCur(st);

                // Bien namespace va khoi tao ban dau
                var f     = {};
                f.$sCur   = $s.eq(cs.idCur);
                f.$sLast  = $s.eq(cs.idLast);
                f.direct  = (st.num > 0) ? 'in' : 'out';

                // FxFunc run setup
                FX.init(f);

                // Slider: setup height
                SIZE.sliderHeight();
            },



            free : function(st) {
                SLIDETO.idCur(st);
                M.toggleFree();
            },




            /* Number Current: setup
            ---------------------------------------------- */
            idCur : function(st) {

                // Bien shortcut va khoi tao ban dau
                var idCur = cs.idCur,
                    nMove = va.nMove = st.num;      // Slider center: store nMove

                // Luu tru idLast va cap nhat id current
                cs.idLast2 = cs.idLast;             // Ho tro loai fx css khi swap slide lien tiep
                cs.idLast  = idCur;


                // idCur return value when out range [0, num]
                idCur += nMove;
                if( o.isLoop ) {
                    if(      nMove < 0 && idCur < 0 )    idCur = num-1;
                    else if( nMove > 0 && idCur >= num ) idCur = 0;
                }

                cs.idCur = idCur;
                M.toggle();
                // Add delay: layout dot in chrome running effect -> slide shake.
                // Neu them delay thi trong M.toggle() --> su dung phuong phap cu: $s.not($slCur).removeClass(current)
                // --> vi trong may yeu', setTimeout co the bi bo? qua neu click lien tuc
                // if( o.layout == 'dot' ) setTimeout(M.toggle, 30);
                // else                    M.toggle();


                // Pagination (thumbnail) setup cho id current nam o vi tri chinh giua
                // Setup timer de update lai gia tri cua pag neu la huong 'ver' va type 'tab'
                // Chi di chuyen chinh giua ki swipe tren body code
                is.pag && !is.pagList && st.isPagCenter && is.ease == 'touch'
                && setTimeout(PAG.itemCenter, 10);
            },


            idCurDash : function(st) {

                // Number Begin: get
                var nBegin = ds.nBegin,
                    sum    = nBegin + st.num;

                // console.log(ds.nBegin, st.num, sum);

                if( !o.isLoop && st.num < 0 && sum < 0 ) {
                    st.num = - nBegin;
                    ds.nBegin = 0;
                }
                else if( !o.isLoop && st.num > 0 && sum >= num ) {
                    st.num = num - 1 - nBegin;
                    ds.nBegin = num-1;
                }
                else ds.nBegin += st.num;



                // Position Begin: get
                // var _xBegin = 0, i = 0;
                // if( st.num > 0 ) for (; i < st.num; i++) _xBegin += ds.width[nBegin + 1];
                // else             for (; i > st.num; i--) _xBegin -= ds.width[nBegin - 1];

                // console.log(_xBegin);



                // Number End: find
                // var j     = ds.nBegin
                //   , xEnd = -va.can.x - ds.mCanvas + wViewport + _xBegin;

                var j = ds.nBegin,
                    xEnd = ds.pBegin[j] + wViewport;

                while ( ds.pEnd[j] < xEnd ) { j++ }

                if( ds.pEnd[j] > xEnd ) j--;       // If pEnd > xEnd -> giam di 1
                if( j >= num ) j = num-1;           // > num -> j = num
                if( j < ds.nBegin ) j = ds.nBegin;  // for width slide > wViewport
                ds.nEnd = j;



                // Number begin: update
                if( ds.nEnd == num-1 ) {
                    var _x = ds.pEnd[num-1] - wViewport + ds.mCanvas,
                        _j = num-1;

                    while ( ds.pBegin[_j] >= _x ) { _j-- }
                    ds.nBegin = _j+1;
                }


                // id current: update
                cs.idCur = (st.num > 0) ? ds.nEnd : ds.nBegin;
            },





            /* Run: goto slide
            ---------------------------------------------- */
            run : function(nSlide, isID, isLive, isPagCenter) {
                
                // Layout: swape cs.idCur value
                var nCur = (o.layout == 'dash') ? ds.nBegin : cs.idCur;


                // Check action
                if( !is.lockNav && (!isID || (isID && nCur != nSlide)) ) {

                    /* Bien luu tru hieu ung swap slide bat dau
                        --> Ho tro cho SLIDESHOW va setup TAB VERTICAL khi body resize */
                    is.fxRun = 1;
                    $cs.addClass(o.ns + 'fxRun');
                    cs.ev.trigger('fxBegin');


                    // slideTo: store properties
                    var st = {
                        num         : nSlide,
                        isID        : !!isID,        // Cho biet bien nSlide co phai id truc tiep hay so slide can di chuyen
                        isLive      : isLive,

                        // Mac dinh khong co lam center
                        isPagCenter : (isPagCenter == UNDEFINED) ? 1 : !!isPagCenter
                    };

                    var core = function() {

                        // Callback func: start && before
                        st.isID ? (st.num == 0) && cs.ev.trigger('start')
                                : (nCur + st.num == 0 || nCur + st.num - num == 0 ) && cs.ev.trigger('start');
                        cs.ev.trigger('before');


                        // ID: convert to st.num
                        if( st.isID ) st.num -= nCur;


                        // Canvas: set Transition timing function
                        if( o.layout != 'free' ) {

                            var es;
                            if     ( is.ease == 'touch' && is.easeLast != 'touch' ) es = o.easeTouch;
                            else if( is.ease == 'move' && is.easeLast != 'move' )   es = o.easeMove;

                            if( es ) {
                                va.ease = M.easeName(es);
                                is.easeLast = is.ease;
                            }
                        }

                        // Set swipe current mac dinh la canvas --> translate chi tren canvas
                        // Loai bo vi xung dong voi swipe event
                        // va.swipeCur = $canvas;


                        // Layout: select
                        switch (o.layout) {
                            case 'dot'  : SLIDETO.dot(st); break;
                            case 'line' : SLIDETO.line(st); break;
                            case 'dash' : SLIDETO.dash(st); break;
                            case 'free' : SLIDETO.free(st); break;
                        }
                    };



                    // Other setup, dieu kien khac layout dash va slide current da loading
                    if( o.layout != 'dash' && $s.eq(nCur).data('is') ) {

                        // layer.slidePause(nCur);        // Layer current pause
                        // video.slideClose(nCur);        // Video current close
                        // map.slideClose(nCur);          // Map current close
                    }

                    // Slideshow: setup stop timer khi chay hieu ung chuyen slide
                    o.isSlideshow && SLIDESHOW.go();

                    // Core func
                    core();
                }
            },




            /* End of effect
            ---------------------------------------------- */
            end : function() {

                // Setup thong bao ket thuc hieu ung swap slide
                is.fxRun = 0;
                $cs.removeClass(o.ns + 'fxRun');
                cs.ev.trigger('fxEnd');


                // Other setup
                if( o.layout != 'dash' ) {

                    // layer.slideStart(cs.idCur);                 // Layer begin start
                    cs.ev.trigger('after');                     // Event after()
                    cs.idCur == num-1 && cs.ev.trigger('end');  // Event end()
                }


                // Playauto: reset when click nav, pag, drag
                if( o.isSlideshow ) {
                    is.hoverAction = 1;
                    SLIDESHOW.go();
                }
            },



            swapHNative : function() {

                // Toggle class height Native
                // --> thay doi chieu cao cua slide theo bien doi ben trong slide
                var idCur    = cs.idCur,
                    hNative  = o.ns + 'hNative',
                    speedCur = (!is.ts && va.fxType[idCur] == 'css') ? 400 : speed[idCur];

                // Truoc tien loai bo class 'hNative'
                $viewport.hasClass(hNative) && $viewport.removeClass(hNative);

                // Setup timer de add class 'hNative' tro lai
                M.tc(ti.dotHNative);
                ti.dotHNative = setTimeout(function() { $viewport.addClass(hNative) }, speedCur + 10);
            }
        },






        /* Events
        ================================================== */
        EVENTS = {

            prevFn : function(step) {
                is.ease = 'move';
                if( o.isLoop || (!o.isLoop && cs.idCur > 0) ) SLIDETO.run(-step);
                else                                          POSITION.animRebound('prev');
            },

            nextFn : function(step) {
                is.ease = 'move';
                if( o.isLoop || (!o.isLoop && cs.idCur < num-1) ) SLIDETO.run(step);
                else                                              POSITION.animRebound('next');
            },

            prev : function() {
                var step;

                // Layout DASH
                if( o.stepNav == 'visible' && o.layout == 'dash' ) {
                    var nBegin = ds.nBegin-1,
                        wView = wViewport - ds.mCanvas,
                        sum  = 0;

                    if( ds.pEnd[nBegin] < wView ) step = ds.nBegin;
                    else {

                        while ( sum <= wView ) { sum += ds.width[nBegin--] }
                        step = ds.nBegin - (nBegin+2);
                    }
                }

                // Layout OTHERS
                else step = o.stepNav;


                EVENTS.prevFn(step);
                return false;
            },

            next : function(isSlideshow) {

                // Setup bao nhieu buoc
                var oStep = isSlideshow ? o.stepPlay : o.stepNav,
                    step  = (oStep == 'visible' && o.layout == 'dash') ? (ds.nEnd - ds.nBegin + 1) : oStep;

                EVENTS.nextFn(step);
                return false;
            },


            nav : function() {

                var evName = va.ev.touch.end +' '+ va.ev.click;
                $prev.add($next).off(evName);
                $prev.on(evName, function(e) { EVENTS.prev(); e.preventDefault(); });
                $next.on(evName, function(e) { EVENTS.next(); e.preventDefault(); });
            },

            pag : function() {

                var evName = va.ev.touch.end +' '+ va.ev.click;
                $pagItem.off(evName);
                $pagItem.on(evName, function(e) {

                    // Goto slide selected
                    if( is.click ) {
                        is.ease = 'move';
                        SLIDETO.run($(this).data('id'), 1, 0, 1);
                    }

                    // Loai bo touchend hoac mouseup --> chi 1 events dc hanh dong
                    // 'preventDefault' khac voi 'return false'
                    e.preventDefault();
                });
            },




            /* Swipe gesture
            ---------------------------------------------- */
            swipe : function() {
                is.move = 0;
                is.swipeTypeCur = null;

                // Bat dau loai bo het event swipe tren cac doi tuong
                EVENTS.swipeDocOFF();
                EVENTS.swipeOFF($viewport);
                is.pag          && EVENTS.swipeOFF($pag);
                is.nestedParent && EVENTS.swipeOFF(va.$nestedChild);


                // Dang ki lai event tren cac doi tuong
                var touchSupport = is.touchSupport,
                    evMouse      = va.ev.mouse,
                    evTouch      = va.ev.touch;

                if( is.swipeBody || is.swipePag ) {
                    EVENTS.swipeDocON(evMouse);
                    touchSupport && EVENTS.swipeDocON(evTouch);
                }
                if( is.swipeBody ) {
                    EVENTS.swipeON($viewport, $canvas, evMouse);
                    touchSupport && EVENTS.swipeON($viewport, $canvas, evTouch);

                    // Loai bo hanh dong tren cac doi tuong nested
                    if( is.nestedParent ) {
                        var evNested = touchSupport ? (evTouch.start +' '+ evMouse.start) : evMouse.start; 
                        va.$nestedChild.on(evNested, function(e) { is.nestedInner = 1 });
                    }
                }
                if( is.swipePag && is.pag ) {
                    EVENTS.swipeON($pag, $pagInner, evMouse);
                    touchSupport && EVENTS.swipeON($pag, $pagInner, evTouch);
                }
            },



            // Function swipe co tham so --> nhieu doi tuong su dung cung function nhu 'viewport', 'pagination'
            swipeON: function($swipe, $sCanvas, evName) {

                /* Elements: stop drag
                ---------------------------------------------- */
                $swipe.off(va.ev.drag);
                $swipe.on(va.ev.drag, function(e) { return false });        /* For Other browser */
                $swipe[0].onselectstart = function() { return false };      /* For IE7-8 */



                /* Swipe start -> drag begin
                 * swipeType --> ho tro swipe gestures cung luc event 'touch' va 'mouse'
                 * Touchmouse dung de phan bien swipe 'code' hay scroll 'page'
                ---------------------------------------------- */
                $swipe.on(evName.start, { 'swipeType': evName.type }, function(e) {

                    va.touchmove = null;
                    var evSwipeType = e.data.swipeType;
                    if( is.swipeTypeCur == null ) is.swipeTypeCur = evSwipeType;

                    // var $console = $('#console');
                    // $console.html( $console.html() +' START event '+ is.swipeTypeCur +' '+ evSwipeType + '<br/>');

                    // Them class 'swiping' de nhan bien 'touch' event
                    // evSwipeType == 'touch' && M.toggleClass('swipe', 0);


                    
                    if( !is.move && !is.lockSwipe && is.swipeTypeCur == evSwipeType ) {
                        // var $console = $('#console');
                        // $console.html( $console.html() +' START event '+ is.swipeTypeCur +' '+ evSwipeType + '<br/>');
                        // console.log('start event', evSwipeType);

                        // Get Thoi gian luc bat dau drag
                        va.tDrag0 = va.tDrag1 = +new Date();

                        // Luu thuoc tinh doi tuong nao dang swipe --> chi duoc 1 doi tuong duy nhat hoat dong
                        va.swipeCur = $sCanvas;

                        // Canvas: loai bo thuoc tinh transition --> di chuyen bang mouseMove
                        M.tsRemove($sCanvas);

                        // Function sSwap tra lai va.can hay va.pag
                        var p = M.sSwap();
                        

                        // X0: get value --> lay vi tri ban dau, khi di chuyen lay vi tri hien tai tru` di vi tri goc
                        // Tren desktop: mac dinh ho tro event.pageX
                        // Tren mobile: ie su dung 'pointer' --> event.pageX. Browser khac su dung 'touches' --> event.touches[0].pageX

                        // x0Fix --> vi tri ban dau khi swipe, khong thay doi khi chuyen sang slide khac
                        // pageX1 --> ho tro khi swipe 'move' moi bat dau --> pageX0 lay gia tri pageX o day
                        var i = evSwipeType == 'mouse' ? e : (is.msGesture ? e.originalEvent : e.originalEvent.touches[0]);
                        px.x0 = px.x0Fix = px.pageX1 = i[p.pageX];


                        // Y0 get value --> su dung de nhan biet swipe slider hay swipe page
                        px.y0 = i.pageY;

                        // xOffset, xBuffer : reset value
                        px.offset = px.buffer = 0;

                        // xBuffer bat dau bang xCanvas --> khi di chuyen chi viec +/- gia thi hien thoi
                        px.buffer = p.xCanvas;

                        // Bien voi muc dich chi cho phep bat dau drag o viewport
                        is.move = 1;

                        // Bien reset lai dragBegin --> bien voi muc dich thuc hien 1 lan ban dau trong luc 'mouseMove'
                        is.swipeBegin = 1;

                        // Reset gia tri so slide di chuyen duoc khi swipe --> ho tro cho flywheel
                        va.nSwipe = 0;

                        // Reset gia tri so luong event move swipe thuc thi --> ho tro cho event trigger 'swipeBegin'
                        va.nMoveEvent = 0;

                        // Canvas grabbing cursor
                        $canvas.is(va.swipeCur) && M.toggleClass('grab', 0);

                        // Fixed loi cursor hien thi lai 'default' sau khi click
                        // Khong thuc hien trong mobile --> khong scroll page duoc
                        evSwipeType == 'mouse' && e.preventDefault();
                    }
                });
            },



            // Function swipe cho document --> co the swipe o ngoai slider
            swipeDocON : function(evName) {

                var _swipeEnd = function(e, evSwipeType, isScrollPage) {
                    // var $console = $('#console');
                    // $console.html( $console.html() +' END event '+ is.swipeTypeCur +' '+ evSwipeType +' '+ isScrollPage +'<br/>');
                    // console.log('end event', is.swipeTypeCur, swipeType, is.swipeTypeCur == swipeType);

                    // Loai bo class Swiping trong 'touch' event
                    // evSwipeType == 'touch' && M.toggleClass('swipe', -1);
                    // $('body').css('-ms-touch-action', '');
                    !!isScrollPage && $('html').removeClass('ct-swiping');


                    if( is.move && !is.lockSwipe && is.swipeTypeCur == evSwipeType ) {
                        // var $console = $('#console');
                        // $console.html( $console.html() +' END event '+ is.swipeTypeCur +' '+ evSwipeType + '<br/>');

                        // Callback event end swipe
                        !is.swipeBegin && cs.ev.trigger('swipeEnd');

                        // Get thoi gian luc swipe out --> tinh toan nhanh hay cham
                        va.tDrag1 = +new Date();

                        // Khoa di chuyen
                        is.move = 0;

                        // Mo khoa click event --> fixed for firefox
                        setTimeout(function() { is.click = 1; }, 10);


                        // Tinh toan vi tri di chuyen sau khi swipe
                        POSITION.nearX();


                        /* + Canvas --> phuc hoi lai cursor swipe
                           + Pagination --> xoa bo class cursor */
                        $canvas.is(va.swipeCur) ? M.toggleClass('grab', 1)
                                                : M.toggleClass('grab', -1);

                        // Loai bo class Grabstop khi swipe leave
                        o.isViewGrabStop && M.toggleClass('stop', -1);

                        // Ngan can event mouseUp o tren thiet bi ho tro touch event
                        // Neu la scrollpage trong androidNative khong cho prevent --> khong scrollpage dc
                        if( evSwipeType == 'touch' && !isScrollPage ) e.preventDefault();
                    }

                    // Setup code Nested
                    if( is.nestedParent ) is.nestedInner = 0;

                    // Other setup
                    // Reset lai gia tri swipeTypeCur o cuoi event
                    // Phai co so sanh --> boi vi co 2 events mouse va touch trong mobile
                    if( is.swipeTypeCur == evSwipeType ) is.swipeTypeCur = null;
                };



                /* Swipe move
                ---------------------------------------------- */
                $doc.on(evName.move, { 'swipeType': evName.type }, function(e) {

                    // Bien shortcut va khoi tao ban dau
                    var evSwipeType = e.data.swipeType;
                    // var $console = $('#console');
                    // $console.html( $console.html() +' MOVE event '+ is.swipeTypeCur +' '+ evSwipeType + '<br/>');
                    // console.log(e);



                    // Kiem tra de di chuyen tam thoi
                    if( is.move && !is.lockSwipe && !(is.nestedParent && is.nestedInner) && is.swipeTypeCur == evSwipeType ) {
                        // var $console = $('#console');
                        // $console.html( $console.html() +' MOVE event '+ evSwipeType + '<br/>');
                        // console.log('move event', evSwipeType);

                        // Callback event Begin swipe
                        !va.nMoveEvent && cs.ev.trigger('swipeBegin');
                        va.nMoveEvent++;

                        // Lay event --> tuong tu nhu event start
                        var i = evSwipeType == 'mouse' ? e : (is.msGesture ? e.originalEvent : e.originalEvent.touches[0]);

                        // Luu tru pageX cu va lay pageX moi --> de tim dang swipe 'trai' hay 'phai'
                        var p = M.sSwap();
                        px.pageX0 = px.pageX1;
                        px.pageX1 = i[p.pageX];


                        // Chi tinh toan khi pageX0 khac pageX1 --> tiet kiem CPU
                        if( px.pageX0 != px.pageX1 ) {

                           // Gia tri di chuyen offset tam thoi
                            px.offset = px.pageX1 - px.x0;

                            // Phan biet swipe sang trai hay phai --> su dung cho swipe limit
                            is.swipeNav = (px.pageX1 > px.pageX0) ? 'right' : 'left';


                            // Di chuyen tam thoi
                            // Tren Mobile: Phan biet scroll page hay la swipe slider
                            // Scroll page: vi khong co e.preventDefault() o trong touchstart va touchmove
                            // --> chi thuc hien 1 lan touchmove va ko co touchend
                            if( evSwipeType == 'touch' ) {

                                px.y = M.a(px.y0 - i.pageY);
                                if( va.touchmove == null && M.a(px.offset) >= px.y ) va.touchmove = 'tabs';
                                if( va.touchmove == null && px.y > 5 )               va.touchmove = 'page';


                                // Truong hop swipe code
                                if( va.touchmove == null || va.touchmove == 'tabs' ) {

                                    // Fix scrollpage for Android
                                    e.preventDefault();

                                    // evSwipeType == 'touch' && M.toggleClass('swipe', 0);
                                    $('html').addClass('ct-swiping');

                                    // Di chuyen tam thoi
                                    POSITION.bufferX(px.pageX1);
                                }

                                // Fixed androiNative khong ho tro 'touchend'
                                // khi ko co 'e.preventDefault' trong 'touchstart' va 'touchmove'
                                else {
                                    (is.androidNative || is.ie) && _swipeEnd(e, evSwipeType, 1);
                                }
                            }

                            // Mac dinh 'desktop'
                            else POSITION.bufferX(px.pageX1);
                        }

                        // Pagination Grabbing Cursor: toggle class
                        !$canvas.is(va.swipeCur) && M.toggleClass('grab', 0);

                        // Khoa click event, kiem offset de ho tro click de dang neu swipe it
                        if( M.a(px.offset) > 10 && is.click ) is.click = 0;
                    }
                });



                /* Swipe end
                 * loai bo 'mouseleave' --> vi khong can thiet va giup code don gian
                ---------------------------------------------- */
                $doc.on(evName.end, { 'swipeType': evName.type }, function(e) { _swipeEnd(e, e.data.swipeType, 0) });
            },



            // Loai bo event bao gom san mouse va` touch event --> khoi can so sanh
            swipeOFF    : function($swipe) { $swipe.off(va.ev.mouse.start +' '+ va.ev.touch.start) },
            swipeDocOFF : function()       { $doc.off(va.ev.mouse.move +' '+ va.ev.mouse.end +' '+ va.ev.touch.move +' '+ va.ev.touch.end) },




            /* Click
            ---------------------------------------------- */
            click : function() {

                // Position report console event
                if( o.isPosReport && o.layout != 'dash' ) {
                    $viewport.on(va.ev.click + 'Dev', function(e) {

                        // x/y: get pos relative with page
                        var xPage = e.pageX,
                            yPage = e.pageY,
                            slCur = $s.eq(cs.idCur),
                            left  = slCur.offset().left,
                            top   = slCur.offset().top;

                        // Padding: get
                        var pa = !!va.pa.left ? va.pa.left : 0;

                        // Pos x/y: on viewport
                        var x = xPage - left,
                            y = yPage - top;

                        // Pos x/y: when have responsive
                        var xRes = (x - pa) / va.rate,
                            yRes = y / va.rate;

                        // Output on console
                        var out = '[codetabs: x/y position (' + parseInt(x-pa) +' ,'+ parseInt(y) + ')';
                        if( va.rate < 1 ) out += ' | x/y responsive (' + parseInt(xRes) +' ,'+ parseInt(yRes) + ')]';
                        else              out += ']';
                        is.e && console.log(out);
                    });
                }

                // Remove position report event report
                else $viewport.off(va.ev.click + 'Dev');
            },






            /* Keyboard navigation
            ---------------------------------------------- */
            keyboard : function() {
                $doc.off('keyup.code'+ codekey);

                if( o.isKeyboard ) {
                    $doc.on('keyup.code'+ codekey, function(e) {

                        // Check slideInto
                        M.scroll.check(1);
                        if( is.into ) {

                            var keycode = e.keyCode;
                            if     ( keycode == 37 ) EVENTS.prevFn(1);
                            else if( keycode == 39 ) EVENTS.nextFn(1);
                        }
                    });
                }
            },




            /* Mousewheel navigation
                + Ho tro giam ti le khi mousewheel
            ---------------------------------------------- */
            mousewheel : function() {

                // Bien shortcut va khoi tao ban dau
                var wheelName = 'mousewheel.code'+ codekey,
                    wheelDelta;

                // Loai bo event va reset wheelDelta
                $viewport.off(wheelName);
                va.wheelDelta = 0;
                

                // Kiem tra co add event mousewheel
                if( !!$.fn.mousewheel && o.isMousewheel ) {
                    $viewport.on(wheelName, function(e, delta) {

                        // Bien shortcut va khoi tao ban dau
                        wheelDelta = va.wheelDelta;

                        // Giam ti le mousewheel
                        wheelDelta += delta/2;

                        // console.log(delta);
                        if     ( delta == -1 && wheelDelta <= -1 ) { EVENTS.prevFn(1); wheelDelta = 0; }
                        else if( delta == 1  && wheelDelta >= 1 )  { EVENTS.nextFn(1); wheelDelta = 0; }

                        // Assign bien wheelDelta vao namespace
                        va.wheelDelta = wheelDelta;
                        return false;
                    });
                }
            },




            /* Resize
            ---------------------------------------------- */
            resize : function() {

                var tCheck = 100;           // Thoi gian de update value va function sau khi resize
                va.hWin = $w.height();      // Chieu cao cua window --> khoi tao ban dau, ho tro slideshow into


                var checkFn = function() {
                    M.tc(ti.resize);
                    ti.resize = setTimeout(function() {
                        // console.log('events resize');

                        // Event trigger 'resize'
                        cs.ev.trigger('resize');

                        // Code: toggle showFrom
                        !!o.showFrom && SHOW.toggle();

                        // Fullscreen: find height page first
                        if( o.isFullscreen ) hCode = $w.height();


                        // console.log(($viewport.height() != hCode) +' '+ $viewport.height() +' '+ hCode);
                        // Reupdate slider: when show/hide scroll-bar browser
                        if( is.showFrom && (($viewport.width() != wViewport) || ($viewport.height() != hCode)) ) {
                            UPDATE.resize();

                            // Lay chieu cao cua trang web --> ho tro REUPDATE slider khi scrollbar toggle
                            va.hWin = $w.height();
                            // if( o.layout == 'line' || (o.layout == 'dot' && !is.tabVerFixWidth) ) reCheckFn();
                            reCheckFn();
                        }
                    }, tCheck);
                };

                var reCheckFn = function() {

                    M.tc(ti.resize);
                    ti.resize = setTimeout(EVENTS.reCheck, o.speedHeight + 100);
                };


                // Resize: event
                var resizeEvent = 'resize.code'+ codekey;
                $w.off(resizeEvent);
                $w.on(resizeEvent, function() { checkFn() });
            },


            /* Kiem tra trang web co toggle scrollbar hay ko --> update slider slider
               TOI UU HOA LAI FUNC --> van con nhieu function goi toi khi resize */
            reCheck : function() {

                // So sanh chieu cao cua trang web luc truoc va bay gio co toggle scrollbar hay khong
                var hBodyCur = $('body').height();
                is.bodyLast  = va.hBodyLast > va.hWin ? 'large' : 'small';
                is.bodyCur   = hBodyCur     > va.hWin ? 'large' : 'small';

                // Update gia tri chieu cao trang web hien tai
                va.hBodyLast = hBodyCur;


                // Update slider
                // console.log('reCheck', is.bodyLast, is.bodyCur);
                // console.log('reCheck' +' '+ (is.bodyLast !== is.bodyCur) +' '+ is.bodyLast +' '+ is.bodyCur);
                // if(is.bodyLast !== is.bodyCur) console.log('update recheck');
                is.bodyLast !== is.bodyCur && UPDATE.resize();
            },




            /* update slider khi trang web loaded xong
             * Ho tro update lai khi code la nestedChild
             * Ho tro lay kich thuoc pagination tab trong luc dau
             * --> font chua tai xong --> lay kich thuoc khong chinh xac
            ---------------------------------------------- */
            pageLoaded : function() {
                $w.load(function() {

                    is.pageLoaded = 1;
                    (o.pag.type == 'tab' || is.nestedChild) && cs.refresh();

                    // Lay chieu cao cua body luc dau --> ho tro REUPDATE slider khi scrollbar toggle
                    setTimeout(function() { va.hBodyLast = $('body').height() }, o.speedHeight);
                });
            },


            codeLoaded : function() {

                // Refresh lai khi thuc te page da load xong ma chua chay fn pageLoaded()
                // Refresh lai code khi code la nestedChild
                !is.pageLoaded && ((o.pag.type == 'tab' && o.pag.dirs == 'ver') || is.nestedChild)
                               && cs.refresh();
            }
        },
        





        /* Update
        ================================================== */
        UPDATE = {

            // Function toggle class
            // Phai kiem tra $pag co ton tai hay khong, boi vi slider bat dau setup, setup prop truoc khi khi RENDER.pag
            // Class tren pagination nhu the nao --> tren code cung tuong tu
            pagToggleFn : function( isAdd, oAdd ) {
                if( is.pag && !!$pag ) {

                    // Bien shortcut va khoi tao ban dau
                    var opt      = isAdd ? o : oo,
                        pag      = opt.pag,
                        ns       = ' '+ o.ns,
                        nsPag    = ns + 'pag-',
                        dirsCur  = '',
                        pagClass = '',
                        classes  = '';


                    /* PHAN KIEM TRA */
                    // Kiem tra more class them vao
                    if( o.pag.moreClass != oo.pag.moreClass ) pagClass += ' '+ pag.moreClass;

                    // Kiem tra type class
                    if( o.pag.type != oo.pag.type ) pagClass += ns + pag.type;

                    // Kiem tra vi tri class
                    if( o.pag.pos != oo.pag.pos ) classes += nsPag + pag.pos;

                    // Kiem tra dirs class
                    if( o.pag.dirs != oo.pag.dirs && pag.dirs ) classes += nsPag + pag.dirs;
                    else if( !!oAdd )                           classes += oAdd.pagDirs == 'hor' ? (isAdd ? nsPag + 'hor' : nsPag +'ver')
                                                                                                 : (isAdd ? nsPag + 'ver' : nsPag +'hor');


                    /* PHAN ADD CLASS VAO DOI TUONG */
                    // Setup class cho pagination
                    pagClass += ' '+ classes;
                    isAdd ? $pag.addClass(pagClass) : $pag.removeClass(pagClass);

                    // Setup class cho CODE --> ho tro TAB style
                    if( pag.type == 'tab' ) { isAdd ? $cs.addClass(classes) : $cs.removeClass(classes) }
                }
            },

            // Loai bo class hien co tren slider --> su dung cho update properties
            removeClass : function(oAdd) {

                // Code: remove exist class
                var ns        = ' '+ o.ns,
                    classCode = ns + 'one' + ns + 'multi';

                classCode += ns + 'line' + ns + 'dot' + ns + 'dash';        // Layout type
                classCode += ns + 'height-auto'+ ns + 'height-fixed';       // Height type

                // Kiem tra huong cua pagination
                $cs.removeClass(classCode);


                // Pagination loai bo class them vao
                UPDATE.pagToggleFn(0, oAdd);
            },

            // Add class vao slider sau khi update
            addClass : function(oAdd) {

                // code: class layout & height type
                var ns        = ' '+ o.ns,
                    classCode = ns + o.layout + ns +'height-'+ o.height;

                // Class browser old && showFrom
                if( !is.ts )       classCode += ns + 'old';
                if( !is.showFrom ) classCode += ns + 'hide';

                // Add Class vao codetabs sau khi setup
                $cs.addClass(classCode);


                // Pagination add type class
                UPDATE.pagToggleFn(1, oAdd);
            },


            // Reset other when update options
            reset : function() {

                // Layout dot: remove translate
                if( o.layout == 'dot' ) {
                    var _tf = {}; _tf[cssTf] = '';
                    $s.css(_tf);

                    POSITION.translateX($canvas, 0, 1, 1);
                }


                // Image background: reset centerVertical by remove css top
                if( o.height0 != o.height ) {
                    $s.each(function() { IMAGE.backCenter.reset($(this)) });
                }
            },



            // Update when resize slider
            // Thu tu function rat quan trong!!!!
            resize : function() {
                // console.log('resize');

                // Setup size cua pagItem --> tim kiem gia tri wItem/hItem
                // Boi vi trong template TAB VERTICAL --> can phai reset kich thuoc pagination truoc
                is.pag && !is.pagList && PAG.sizeItem();

                // Update width slider first --> use for function below
                // Bo sung tuy chon delay --> ho tro template TAB VERTICAL khi doi tuong pagitem thay doi
                SIZE.wCode();



                is.res && RES.varible();                            // Responsive: calculation padding & va.rate
                (o.height == 'fixed') && SIZE.sliderHeightFix();    // Get width/height slider first -> for image autofit/autofill
                is.res && o.isFullscreen && FULLSCREEN.varible();   // Fullscreen: re calculation padding & va.rate, nead hCode first --> below sliderHeightFix()
                IMAGE.backUpdate();                                 // Image background: update size to fit/fill

                SIZE.slideHeight();
                SIZE.general();


                /* Phan setup can DELAY */
                SIZE.sliderHeight(1);                               // sliderHeight: update make image shake --> delay co san trong fn (30ms)
                setTimeout(PAG.toHor, 40);                      // Tab VERTICAL tu dong chuyen sang HORIZONTAL --> o vi tri cuoi
            }
        },






        /* Show slider in range[min, max]
        ================================================== */
        SHOW = {

            /* Get value show/showFrom
            ---------------------------------------------- */
            get : function() {

                if( !!o.showFrom ) {

                    // Convert to array again
                    if( typeof o.showFrom == 'string' )      { var _temp = o.showFrom; o.showFrom = [_temp]; }
                    else if( typeof o.showFrom == 'number' ) { var _temp = o.showFrom; o.showFrom = [_temp+'-100000']; }

                    va.showFrom = {};
                    va.showFrom.num = o.showFrom.length;

                    for (i = va.showFrom.num-1; i >= 0; i--) {
                        var a = [], _n = o.showFrom[i];      // Shortcut number o.showFrom

                        // Auto and to-value
                        if( typeof _n == 'number' ) _n += '-100000';

                        a = _n.split('-');
                        va.showFrom[i] = {
                            'from' : parseInt(a[0]),
                            'to'   : parseInt(a[1])
                        };
                    }
                    SHOW.check();
                }

                // Default setup: if no showFrom value
                else { is.showFrom = 1; is.awake = 1; }


                // Kiem tra thuoc tinh show tren 'desktop' va 'mobile'
                if( (is.mobile && o.show == 'desktop')
                ||  (!is.mobile && o.show == 'mobile') ) is.show = 0;
                else                                     is.show = 1;
            },




            /* Codeslide: kiem tra show/hide --> gan vao bien is.awake
            ---------------------------------------------- */
            check : function() {
                var _show = va.showFrom, _wWin = $w.width();
                is.showFrom = 0;

                // Check is.showFrom
                for (i = _show.num-1; i >= 0; i--) {
                    if( _wWin >= _show[i].from && _wWin <= _show[i].to ) { is.showFrom = 1; break; }
                }

                // Check slider awake
                // Slider is sleep --> slider not init, not setup
                if( is.awake == UNDEFINED && is.showFrom ) is.awake = 1;
            },




            /* Slider: toggle show/hide sau khi slider da setup luc dau
            ---------------------------------------------- */
            toggle : function() {

                // Show: check
                SHOW.check();
                var c = o.ns + 'hide';      // Shortcut class showhide

                if( !is.showFrom && !$cs.hasClass(c) ) $cs.addClass(c);
                if( is.showFrom && $cs.hasClass(c) )   $cs.removeClass(c);
            },




            /* Resize event: khi slider chua bat dau setup
            ---------------------------------------------- */
            resizeON : function() {

                var _t = 200;
                $cs.addClass(o.ns +'hide');
                $w.on('resize.csShow'+ codekey, function() {

                    M.tc(ti.showResize);
                    ti.showResize = setTimeout(function() {

                        SHOW.check();
                        is.awake && SHOW.resizeOFF();
                    }, _t);
                });
            },

            resizeOFF : function() {
                $w.off('resize.csShow'+ codekey);
                $cs.removeClass(o.ns +'hide');

                // Init ready when slider awake
                INIT.ready();
            }
        },





        /* Captions
        ================================================== */
        CAPTION = {

            toggle : function($slCur, $slLast) {

                // Bien shortcut va khoi tao ban dau
                var capCur    = $slCur.data('html')['cap'],
                    capLast   = $slLast.length ? $slLast.data('html')['cap'] : '',

                    animEnd   = { duration : o.speedHeight,
                                  complete : function() {
                                        var $self = $(this);

                                        if     ( $self.is(va.$capLast) )  $self.css('visibility', '');
                                        else if( $self.is(va.$capInner) ) $self.css('height', '');
                                  }};

                // Thay doi noi dung giua caption current
                va.$capCur.html(capCur);
                
                // Lay height cua caption
                var hCur  = va.$capCur.outerHeight(true),
                    hLast = va.$capLast.outerHeight(true) || hCur;      // Fixed luc dau = 0


                // Hieu ung khong chay tren mobile --> khong can thiet
                // HIEU UNG FADE giua caption current va last
                // HIEU UNG HEIGHT cho caption
                if( !is.mobile && !is.ie7 ) {

                    // NOI DUNG capton Last
                    va.$capLast.html(capLast);

                    // HIEU UNG
                    va.$capCur
                        .stop(true)
                        .css('opacity', 0)
                        .animate({ 'opacity' : 1 }, animEnd);

                    va.$capLast
                        .stop(true)
                        .css({ 'opacity': 1, 'visibility': 'visible' })
                        .animate({ 'opacity' : 0 }, animEnd);
                    
                    hCur != hLast &&
                    va.$capInner
                        .stop(true)
                        .css('height', hLast)
                        .animate({ 'height' : hCur }, animEnd);
                }
            }
        },






        /* Effects
        ================================================== */
        FX = {

            // Chon lua ten hieu ung trong mang --> chon lua ngau nhien
            array : function(fxArray) {

                return (typeof fxArray == 'object') ? fxArray[ M.r(M.rm(0, fxArray.length-1)) ]
                                                    : fxArray;
            },

            init : function(f) {

                // Them class effect hien tai vao codetabs --> chua can toi
                // // Tao bien shortcut va luu tru ten hieu ung vua roi
                // var fxCur  = va.fxCur,
                //     fxLast = fxCur,
                //     idCur  = cs.idCur;

                // // Effect setup bang javascript
                // if( va.fxType[idCur] == 'js' ) {

                //     // Lay ten hieu ung hien tai
                //     fxCur = (va.fx[idCur] == 'random') ? o.fxName[ M.r(M.rm(1,va.fxNum-1)) ]
                //                                        : va.fx[idCur];

                //     // Setup neu fxCur la mang --> random ten trong mang
                //     if( typeof fxCur == 'object' ) fxCur = fxCur[ M.r(M.rm(0, fxCur.length-1)) ];

                //     // Loai bo class fxLast va Them class hien tai vao code
                //     (fxLast != fxCur) && $cs.removeClass('fx-'+ fxLast).addClass('fx-'+ fxCur);

                //     // Tao hieu ung
                //     FX[fxCur](f);

                //     // Luu nguoc tro lai namespace
                //     va.fxCur = fxCur;
                // }



                var idCur   = cs.idCur,
                    fxIdCur = va.fx[idCur],
                    fxType  = va.fxType[idCur],
                    fxCur;


                // Effect setup bang javascript
                if( fxType == 'js' ) {

                    // Lay ten hieu ung hien tai
                    fxCur = (fxIdCur == 'random') ? o.fxName[ M.r(M.rm(1,va.fxNum-1)) ]
                                                  : fxIdCur;

                    // Setup neu fxCur la mang --> random ten trong mang
                    fxCur = FX.array(fxCur);


                    // Hieu ung fade khong dap ung het moi truong hop
                    if( fxCur == 'fade' ) FX[fxCur](f);
                    else {

                        // Setup truong hop co image background
                        f.$tar = f.$sCur.find('.'+ o.ns +'imgback');
                        if( f.$tar.length ) {
                            FX[fxCur](f);
                        }
                        else FX.end();
                    }
                }

                // Effect setup bang css
                else if( is.ts ) FX.css();
                else             FX.jFade(0);
            },



            /* Effect Core function
            ---------------------------------------------- */
            end : function(f) {

                M.tc(ti.fxEnd);
                ti.fxEnd = setTimeout(function() {

                    // Image-background: restore visible
                    if( !!f ) {
                        f.d.is && f.$tar.find('img').css('visibility', '');
                        f.$fxOver.remove();
                    }

                    SLIDETO.end();
                }, speed[cs.idCur]);
            },





            /* Effect bang css
            ---------------------------------------------- */
            /* Ket hop voi CSS va CSSOne. Setup chia lam 3 phan:
                + Toggle class tren idLast va idCurrent --> bao gom ten hieu ung + speed
                + Toggle class tren Viewport --> Them class NoClip + ten hieu ung combine (neu co)
                + Toggle class tren ID last cua last --> loai bo ten class --> ho tro swap nav lien tiep
                + Setup moi thu con lai khi chay xong hieu ung bang css
            */
            css : function() {

                // Bien shortcut va khoi tao dau tien
                var prefix     = 'code',        // Prefix chung cho file 'code.animate.css'
                    idCur      = cs.idCur,
                    idLast     = cs.idLast,

                    isCSSOne   = va.fxType[idCur] == 'cssOne',
                    classAnim  = ' '+ prefix +'-animated',
                    classClip  = o.ns + 'noclip',
                    dataTimer  = isCSSOne ? 'tiRemoveCSSOne' : 'tiRemoveCSS',
                    dataFxAdd  = 'fxAdded',
                    speedCur   = speed[idCur],
                    speedCSS   = {},
                    cssReset   = {},
                    easeCur    = va.fxEase[idCur],
                    easeName   = va.prefix +'animation-timing-function',
                    fxEasing   = {},

                    // Bien va setup danh cho cssOne
                    ns         = prefix +'-slide',
                    IN         = ns + 'In',
                    OUT        = ns + 'Out';

                // Setup thoi gian hieu ung hoat dong
                speedCSS[cssAD] = speedCur + 'ms';

                // Setup easing animation
                fxEasing[easeName] = !!easeCur ? easeCur : '';

                // CSS Reset style khi swap slide ket thuc
                cssReset[cssAD]    = '';
                cssReset[easeName] = '';



                /* Toggle hieu ung bang css tren slide current
                   --> slide last nghich dao va tuong tu
                */
                var slideToggleCSS = function(id, isCur) {

                    var $slide = $s.eq(id),
                        fxAdd, fxDel;

                    // Setup class Add va Delete
                    if( isCSSOne ) {
                        fxAdd = isCur ? IN : OUT;
                        fxDel = isCur ? OUT : IN;

                    } else {
                        fxAdd = va.fx[id][isCur ? 0 : 1] || '';
                        fxDel = $slide.data(dataFxAdd) || '';

                        // Kiem tra co phai mang hieu ung hay khong
                        fxAdd = FX.array(fxAdd);

                        // Luu tru hieu ung hien tai vao data slide
                        $slide.data('fxAdded', fxAdd);
                    }


                    // Loai bo timer remove class cua slide
                    M.tc($slide.data(dataTimer));

                    // Them thoi gian chuyen dong vao slide
                    $slide.css(speedCSS);

                    // Toggle class vao idCurrent va idLast
                    $slide.removeClass(fxDel).css(fxEasing).addClass(fxAdd + classAnim);

                    // Loai bo class effect tren slide
                    $slide.data(dataTimer, setTimeout(function() {
                        $slide.removeClass(fxAdd + classAnim).css(cssReset)
                    }, speedCur));
                }

                slideToggleCSS(idLast, 0);
                slideToggleCSS(idCur, 1);



                /* Loai bo class tren viewport
                    + Loai bo timer remove class
                    + Them class noclip de hien hieu ung css khong bi cat
                    + Setup timer loai bo class no clip
                */
                var fxViewAdd, fxViewDel;
                if( isCSSOne ) {
                    var fxPrefix  = prefix +'fx-',
                        fxViewAdd = fxPrefix + FX.array( va.fx[idCur] ),
                        fxViewDel = $viewport.data(dataFxAdd),
                        isNext    = va.nMove > 0,
                        navCur    = isNext ? ns +'Next' : ns +'Prev',
                        navLast   = isNext ? ns +'Prev' : ns +'Next';

                    fxViewAdd = classClip +' '+ fxViewAdd +' '+ navCur;
                    fxViewDel = fxViewDel +' '+ navLast;

                    // Luu tru Hieu ung hien tai vao data viewport
                    $viewport.data('fxAdded', fxViewAdd);
                }
                else {
                    fxViewAdd = classClip;
                    fxViewDel = '';
                }
                M.tc($viewport.data(dataTimer));
                $viewport.removeClass(fxViewDel).addClass(fxViewAdd);

                $viewport.data(dataTimer, setTimeout(function() {
                    $viewport.removeClass(fxViewAdd)
                }, speedCur));



                /* Loai bo hieu ung tren slide last cua last
                   Voi dieu kien phai co idLast2 va phai khac voi idCurrent
                */
                var idLast2 = cs.idLast2;
                if( idLast2 != UNDEFINED && idLast2 != idCur ) {

                    var $slLast2   = $s.eq(idLast2),
                        fxLast2Del = isCSSOne ? OUT
                                              : $slLast2.data(dataFxAdd) || '';

                    M.tc($slLast2.data(dataTimer));
                    $slLast2.removeClass(fxLast2Del + classAnim).css(cssReset);
                }



                /* Setup cac thu con lai khi chay xong effect bang css */
                FX.end();
            },




            /* Hieu ung fade bang jQuery --> ho tro cho hieu ung custom cho old browser
            ---------------------------------------------- */
            jFade : function(isDuration) {

                // Bien shortcut va khoi tao dau tien
                var idCur = cs.idCur,
                    fxEnd = { 'visibility': '' };

                // Setup hieu ung fade cho slide dong thoi loai bo hieu ung neu swap slide lien tiep
                var slideFade = function(id, isCur) {

                    var $slide   = $s.eq(id),
                        opaBegin = isCur ? 0 : 1,
                        opaEnd   = isCur ? 1 : 0;

                    $slide
                        .stop(true)
                        .css({'opacity': opaBegin, 'visibility': 'visible'})
                        .animate({
                            'opacity': opaEnd
                        },{
                            // Hieu ung css fallback thi 400, con hieu ung fade chi dinh thi lay thoi gian cua slide
                            duration : isDuration ? speed[idCur] : 250,
                            // easing   : 'linear',
                            complete : function() { $slide.css(fxEnd) }
                        });
                };
                slideFade(cs.idLast, 0);
                slideFade(idCur, 1);


                // Loai bo hieu ung tren slide last cua last
                // Voi dieu kien phai co idLast2 va phai khac voi idCurrent
                var idLast2 = cs.idLast2;
                if( idLast2 != UNDEFINED && idLast2 != idCur ) {

                    $s.eq(idLast2).stop(true).css(fxEnd);
                }


                // Setup end
                FX.end();
            },





            /* Effect basic
             * Su dung hieu ung fade bang jQuery
            ---------------------------------------------- */
            fade : function() { FX.jFade(1) }
        },






        /* Responsive
        ================================================== */
        RES = {

            // Value: search value in range object
            valueGet : function(v, vName) {

                var wDoc = $doc.width()
                  , name = !!vName ? vName : 'value';

                // Bo sung: cho phep media mac dinh, va` lay value gia tri nho nhat
                var wMin = 1e5, id = -1;
                for (i = v.num-1; i >= 0; i--) {

                    // From & to so sanh voi width-document
                    if( v[i].from <= wDoc && wDoc <= v[i].to ) {
                        
                        if( wMin >= v[i].to ) {
                            wMin = v[i].to;
                            id = i;
                        }
                    }
                }

                // Return value
                if( id > -1 ) return v[id][name];
                else          return null;
            },




            // va.pa & va.rate: control all relative function!
            varible : function() {

                // padding: get value in va.paRange object
                var paddingGet = function() {
                    var pa;
                    if( !!va.paRange ) {

                        pa = RES.valueGet(va.paRange);
                        if( pa == null ) pa = 0;
                    }
                    else pa = 0;
                    return pa;
                };



                /* padding: get */
                // Padding only working when wViewport smaller width Responsive
                if( o.media ) {

                    // Condition
                    // Case 1: wMax < va.wRes -> often for small width in media
                    // Case 2: wMax > va.wRes -> uu tien cho width trong media
                    var _wMax = va.media.wMax
                      , cond  = (_wMax > va.wRes) ? (_wMax >= wViewport) : (va.wRes > wViewport);

                    if( cond ) {
                        var _m = va.media               // Shortcut va.media
                          , _w = RES.valueGet(_m);      // Width value in media: find


                        // if return == null -> padding get va.paRange
                        // else -> calculator from wViewport
                        va.pa.left = (_w == null) ? paddingGet() : (wViewport-_w)/2;
                    }
                    else va.pa.left = (wViewport-va.wRes)/2;
                }

                // No media
                else va.pa.left = (va.wRes > wViewport) ? paddingGet() : (wViewport-va.wRes)/2;
                va.pa.left = ~~(va.pa.left);   // Padding left: round number



                /* Margin: get */
                SIZE.margin();



                /* Rate: get */
                va.rateLast = va.rate;

                // Vi padding left luon luon co gia tri nen luc nao cung lay dc ratio widthContent / widthResponsive
                var rate0 = (wViewport-(va.pa.left*2)) / va.wRes;
                va.rate = (rate0 > 1) ? 1 : rate0;
            }
        },






        /* fullscreen
        ================================================== */
        FULLSCREEN = {

            varible : function() {

                // width/height content: de biet ti le
                va.wContent = wViewport - (va.pa.left*2);
                va.hContent = M.r(va.wContent / va.rRes);

                // Truong hop: content nho hon page
                if( va.hContent < hCode ) {

                    va.pa.top = M.r( (hCode-va.hContent)/2 );
                }

                // Truong hop nguoc lai: content lon hon page
                // --> setup hContent = height page, tinh toan lai va.rate va padding
                else {
                    va.pa.top = 0;
                    va.hContent = hCode;
                    va.wContent = M.r(va.hContent * va.rRes);

                    va.rate = va.wContent / va.wRes;
                    va.pa.left = M.r( (wViewport-va.wContent)/2 );
                }
            }
        },






        /* Deep-linking
        ================================================== */
        DEEPLINKING = {

            // Kiem tra HASH tren dia chi trang web
            check : function(isIDReturn) {

                var codeName  = o.deeplinking.prefix[0] + va.codeID + o.deeplinking.prefix[1],
                    reStr     = codeName +'\\d+',
                    re        = new RegExp(reStr, 'g'),
                    hash      = window.location.hash,
                    linkCheck = hash.match(re);


                // Tra lai ket qua ID nhan duoc tu hash
                if( isIDReturn ) {
                    if( linkCheck != null ) {

                        var idCur = M.pInt( linkCheck[0].substr(codeName.length) );

                        // Dat lai idBegin neu nho hon idCur < num
                        if( idCur < num ) return idCur;
                    }
                    // Tra lai gia tri null neu khong hop le
                    return null;
                }

                // Kiem tra va tra lai gia tri Hash moi' thay the cho hash hien tai
                else {
                    var hashNew = codeName + cs.idCur;

                    // Neu ton tai hash thi thay the bang hash moi
                    if( linkCheck != null ) {
                        var strRest = hash.split(linkCheck[0]);
                        hash = strRest[0] + hashNew + strRest[1];
                    }
                    else {
                        // Neu khong co hash --> cong them dau '#'
                        // Neu co hash --> cong vao tiep theo
                        // Them dau '-' cho multi hash --> de doc hon
                        if( hash === '' )           hash = '#'+ hashNew;
                        else if( /\-$/.test(hash) ) hash += hashNew;
                        else                        hash += '-'+ hashNew;
                    }

                    // Cuoi cung tra lai gia tri Hash
                    return hash;
                }
            },


            // Doc id tu link trang page --> di toi slide do
            read : function() {
                var idCur = DEEPLINKING.check(1);
                if( idCur != null ) cs.idCur = o.idBegin = idCur;
            },


            // Toggle link on browser
            write : function() {

                // Lay gia tri Hash moi tu dia chi trang web
                var hashNew   = DEEPLINKING.check(0),
                    hashReset = function() {

                        M.tc(ti.hashReset);
                        ti.hashReset = setTimeout(function() { codeVar.stopHashChange = 0 }, 100);
                    };


                // Thay doi hash hien tai
                // Ngan hanh dong event 'hashchange' --> tranh lap lai
                M.tc(ti.hashChange);
                ti.hashChange = setTimeout(function() {

                    codeVar.stopHashChange = 1;
                    window.location.hash = hashNew;

                    // Phuc hoi lai event hashchange
                    hashReset();
                }, speed[cs.idCur]);
            },



            // Event khi hashChange
            events : function() {

                // Loai bo event roi dang ki lai --> ho tro update
                $w.off(va.ev.hash);
                o.isDeeplinking && $w.on(va.ev.hash, function(e) {

                    // Ngan browser load lai trang
                    e.preventDefault();
                    if( !codeVar.stopHashChange ) {

                        // Kiem tra hash change co phai la 'code' hien tai
                        // --> neu phai thi di toi id cua slide
                        var idCur = DEEPLINKING.check(1);
                        if( idCur != null ) SLIDETO.run(idCur, 1, 0, 1);
                    }
                });
            }
        },





        /* Cookie
        ================================================== */
        COOKIE = {

            write : function() {

                // Bien shortcut va khoi tao ban dau
                var date = new Date(),
                    name = 'code'+ va.codeID + o.cookie.name + window.location.host;

                // Cong them so ngay luu tru va convert theo gio GTM chuan
                date.setTime( date.getTime() + (o.cookie.days*24*60*60*1000) );
                var expires = '; expires='+ date.toGMTString();

                // Ghi hoac update cookie gia tri moi
                document.cookie = name +'='+ cs.idCur + expires +'; path=/';
            },
            

            read : function() {

                var aCookie = document.cookie.replace(/\s+/g, '').split(';'),
                    name    = 'code'+ va.codeID + o.cookie.name + window.location.host +'=',
                    idCur   = null;

                // Kiem tra tat ca cookie
                for( i = 0; i < aCookie.length; i++ ) {
                    if( aCookie[i].indexOf(name) == 0 ) idCur = M.pInt( aCookie[i].substr(name.length) );
                }

                // Setup idCur neu cookie co luu tru gia tri trong qua khu
                if( idCur != null ) cs.idCur = o.idBegin = idCur;
            }
        },





        /* Class add features
        ================================================== */
        CLASSADD = {

            // Kiem tra va luu tru classAdd cua tung slide
            filter : function(opt) {

                var classAdd = '';
                if( opt.classAdd != UNDEFINED ) {

                    // Dam bao chuyen doi sang chuoi
                    classAdd = opt.classAdd.toString();
                }
                return classAdd;
            },


            // Toggle class tren code khi switch slide
            toggle : function() {

                var classLast = va.classAdd[cs.idLast],
                    classCur  = va.classAdd[cs.idCur];

                // Loai bo class cu va add class moi
                if( classLast != UNDEFINED && classLast != '' ) $cs.removeClass(classLast);
                if( classCur  != UNDEFINED && classCur  != '' ) $cs.addClass(classCur);
            }
        },





        /* Slideshow
         * tDelay: control all relative function
        ================================================== */
        SLIDESHOW = {

            init : function() {

                // Number of slide > 0, it mean at least 2 slides
                if( num > 1 ) {
                    is.hoverAction = 0;

                    SLIDESHOW.focus();
                    M.scroll.setup();
                    SLIDESHOW.hover();
                    o.slideshow.isPlayPause && SLIDESHOW.toggle();

                    is.stop = 0; // for button stop
                    SLIDESHOW.go();
                }
            },

            go : function() {
                // console.log('go', is.stop, is.focus, is.into, is.hover, is.playing, is.fxRun, is.hoverAction, is.pauseActive);
                // console.log('go' +' '+ is.stop +' '+ is.focus +' '+ is.into +' '+ is.hover +' '+ is.playing +' '+ is.fxRun +' '+ is.hoverAction +' '+ is.pauseActive);
                // console.log(is.into, is.fxRun);

                // Choose action
                // va.nVideoOpen/va.nMapOpen use for video/map open
                // !o.isHoverPause && is.fxRun --> isHoverPause-off, sua loi khi chuyen qua slide khac, timer van chay
                // is.playing --> the hien timer co chay hay khong
                if( !is.stop ) {

                    // if( is.pauseActive ) is.playing && SLIDESHOW.pause();
                    if( is.pauseActive ) SLIDESHOW.pause();

                    else {
                        if( !is.focus || !is.into || is.hover || va.nVideoOpen || va.nMapOpen || (!o.slideshow.isHoverPause && is.fxRun) ) {
                            is.playing && SLIDESHOW.pause();
                        }

                        else if( !is.fxRun ) {
                            is.hoverAction ? SLIDESHOW.reset() : SLIDESHOW.play();
                        }
                    }
                }
            },



            /* APIs: update properties
            ---------------------------------------------- */
            update : function() {

                // Timer toggle markup
                var auto0 = oo.slideshow,
                    auto1 = o.slideshow;

                if( auto0.timer != auto1.timer ) {
                    clearInterval(ti.timer);
                    RENDER.timer();
                    !!va.tTimer0 && SLIDESHOW.pause();      // no check if first auto SLIDESHOW.
                    SLIDESHOW.play();
                }


                // Timer arc update properties
                is.timer && (va.timer == 'arc') && TIMER.arcProp();


                // Slideshow toggle --> after timer update
                if( oo.isSlideshow != o.isSlideshow ) {

                    if( o.isSlideshow ) SLIDESHOW.init();
                    else {
                        SLIDESHOW.pause(1);

                        var winEvents = ' focus.code' + codekey
                                      + ' blur.code'  + codekey
                                      + ' scroll.code'+ codekey;
                        $w.off(winEvents);
                        $cs.off('mouseenter.code mouseleave.code');
                    }
                }

                // Hoverstop toggle
                (auto0.isHoverPause != auto1.isHoverPause) && SLIDESHOW.hover();
            },





            /* Play - pause
               tDelay: important!
            ---------------------------------------------- */
            play : function() {
                // console.log('play');

                // Next play function:
                var nextPlayFn = function() {
                    M.tc(ti.play);
                    ti.play = setTimeout(SLIDESHOW.reset, speed[cs.idCur] + 10);
                };


                is.playing = 1;
                va.tTimer0 = +new Date();
                is.timer && TIMER.update[va.timer]();

                // Setup di chuyen toi slide ke tiep
                M.tc(ti.play);
                ti.play = setTimeout(function() {

                    // Bien shortcut va khoi tao ban dau
                    var isRandom = o.slideshow.isRandom && num > 2,
                        idCur    = (o.layout == 'dash') ? ds.nEnd : cs.idCur,
                        idNext   = isRandom ? M.raExcept(0, num-1, idCur)
                                            : (idCur >= num-1 ? 0 : idCur + 1),

                        $slNext  = $s.eq(idNext);

                    // var nRandom = M.raExcept(0, num-1, idCur);
                    // console.log(nRandom);

                    // SLIDE da load xong --> di chuyen toi slide
                    if( $slNext.data('is')['loaded'] ) {
                        if     ( isRandom )                    SLIDETO.run(idNext, 1);
                        else if( !o.isLoop && idCur == num-1 ) SLIDETO.run(0, 1);
                        else                                   EVENTS.next(1);

                        // Next play
                        nextPlayFn();
                    }

                    // SLIDE chua load xong --> cho` load xong
                    else {
                        $slNext.data({'isPlayNext': 1});
                        cs.stop();
                    }

                }, tDelay);
            },

            reset : function() {
                if( tDelay != delay[cs.idCur] ) tDelay = delay[cs.idCur];

                if     ( va.timer == 'bar' && xTimer != 100 )  xTimer = 100;
                else if( va.timer == 'number' && xTimer != 0 ) xTimer = 0;
                else if( va.timer == 'arc' )                   va.arc.angCur = 0;
                

                // console.log('reset', tDelay, is.hoverAction);
                SLIDESHOW.play();
            },

            pause : function(_isStop) {
                // console.log('pause');
                is.playing = 0;
                is.hoverAction = 0;


                if( _isStop ) {
                    tDelay = delay[cs.idCur];
                    TIMER.setup[va.timer]();
                }

                // pause: tDelay get
                else {

                    var t0  = tDelay;
                    va.tTimer1 = +new Date();
                    tDelay  = delay[cs.idCur] - (va.tTimer1 - va.tTimer0);

                    if( delay[cs.idCur] != t0 ) tDelay -= delay[cs.idCur] - t0;
                    if( tDelay < 0 )          { tDelay = 0; is.hoverAction = 1; }    // is.hoverAction = 1 -> !important to solve hover slideshow when fxrunning
                }
                
                is.timer && TIMER.stop();           // Timer: stop
                M.tc(ti.play);                      // PlayAuto: clears
            },





            /* Slideshow events
            ---------------------------------------------- */
            focus : function() {
                is.focus = 1;

                $w.on('focus.code'+ codekey, function(e) { if( !is.focus ) { is.focus = 1; SLIDESHOW.go(); } })
                  .on('blur.code'+  codekey, function(e) { if( is.focus )  { is.focus = 0; SLIDESHOW.go(); } });
            },

            
            scroll : {

                setup : function() {

                    // Truong hop options slideshow chi run khi o trong vung nhin thay
                    if( o.slideshow.isRunInto ) {
                        is.into = 0;
                        M.scroll.check();

                        var t = 200;
                        $w.on('scroll.code'+ codekey, function() {
                            M.tc(ti.scroll);
                            ti.scroll = setTimeout(function() { !is.pauseActive && M.scroll.check() }, t);
                        });
                    }
                    
                    // Truong hop slideshow run khong can trong vung nhin thay
                    else { is.into = 1; SLIDESHOW.go(); }
                },

                check : function(isNoGo) {
                    // console.log('scroll check', va.topW, va.topCS, va.botW, va.botCS);
                    M.scroll.position();

                    // Code: into window with vary 100px
                    // Voi chieu cao CODE lon hon hWindow --> headache!! xem lai
                    var isInto = (va.topW <= va.topCS + 100 && va.botW >= va.botCS - 100)
                              || ((hCode >= va.hWin) && (va.botW - 50 >= va.topCS && va.topW - 50 <= va.botCS));

                    if( isInto ) {
                        if( !is.into ) { is.into = 1; !isNoGo && SLIDESHOW.go(); }
                    }
                    else {
                        if( is.into ) { is.into = 0; !isNoGo && SLIDESHOW.go(); }
                    }
                },

                position : function() {

                    // Lay Vi tri top/bottom cua Window
                    va.topW = $w.scrollTop();
                    va.botW = va.hWin + va.topW;


                    // Slider offset
                    va.topCS = $cs.offset().top;
                    va.botCS = va.topCS + hCode;
                }
            },


            hover : function() {
                if( o.slideshow.isHoverPause ) {
                    is.hover = 0;

                    $cs .off('mouseenter.code mouseleave.code')
                        .on('mouseenter.code', function() { SLIDESHOW.hover1() })
                        .on('mouseleave.code', function() { SLIDESHOW.hover0() });
                }
                else $cs.off('mouseenter.code mouseleave.code');
            },

            hover0 : function() { is.hover = 0; SLIDESHOW.go(); },
            hover1 : function() { is.hover = 1; SLIDESHOW.go(); },


            toggle : function() {

                var actived = o.ns + o.actived,
                    evName  = va.ev.touch.end +' '+ va.ev.click;

                $playpause.off(evName);
                $playpause.on(evName, function(e) {

                    // var isActived 
                    if( $playpause.hasClass(actived) ) {
                        is.pauseActive = 0;
                        $playpause.removeClass(actived);

                    } else {
                        is.pauseActive = 1;
                        $playpause.addClass(actived);
                    }

                    SLIDESHOW.go();
                    return false;
                });
            }
        },






        /* Timer for slideshow
        ================================================== */
        TIMER = {

            /* Timer setup
            ---------------------------------------------- */
            setup : {
                bar : function() {

                    var _tf = {}; _tf[cssTf] = M.tlx(-100, '%');
                    if( is.ts ) {
                        var _ts = {}; _ts = M.ts(cssTf, 0, 'linear');
                        $timerItem.css(_ts).css(_tf);
                    }
                    else $timerItem.css(_tf);
                },


                arc : function() {
                    var c = va.tContext;
                },


                number : function() {
                    $timerItem.attr('data-num', 0).text(0);
                },

                none : function() {}
            },



            /* Timer update properties
            ---------------------------------------------- */
            update : {

                bar : function() {
                    var _reTimer = function() {

                        var tf = {}; tf[cssTf] = M.tlx(-xTimer, '%');
                        if( is.ts ) {

                            $timerItem.hide().show();   // Fixed IE: refresh obj
                            $timerItem.css(cssD0).css(tf);
                        }
                        else $timerItem.css(tf);
                    }

                    , _beginTimer = function() {

                        var tf = {}; tf[cssTf] = M.tlx(0);
                        if( is.ts ) {

                            var ts = {}; ts[cssD] = tDelay + 'ms';

                            $timerItem.hide().show();   // Fixed IE: refresh obj
                            $timerItem.css(ts).css(tf);
                        }
                        else $timerItem.animate(tf, {duration: tDelay, easing: 'linear'});
                    };

                    _reTimer();                    // Timer: remove transition
                    setTimeout(_beginTimer, 20);   // Timer: set transition, need delay > 50
                },


                arc : function() {

                    var angPlus = M.c( va.arc.speed*360/delay[cs.idCur] ),
                        arcDraw = function() {

                        // Bien shortcut va khoi tao ban dau
                        var ctx    = va.tContext,
                            ARC    = va.arc,
                            inFill = M.c((ARC.radius - ARC.weight) / 2);

                        // Clear canvas first
                        ctx.clearRect(-ARC.width/2, -ARC.height/2, ARC.width, ARC.height);
                        
                        // OUT circle
                        ctx.beginPath();
                        ctx.arc(0, 0, ARC.oRadius, 0, ARC.pi*360, false);
                        ctx.lineWidth   = ARC.oWeight;
                        ctx.strokeStyle = ARC.oStroke;
                        ctx.fillStyle   = ARC.oFill;
                        
                        ctx.stroke();
                        ctx.fill();
                        // ctx.closePath();

                        // IN FILL circle
                        ctx.beginPath();
                        ctx.arc(0, 0, inFill + 1, 0, ARC.pi * M.c(ARC.angCur*10)/10, false);
                        ctx.lineWidth   = inFill * 2 + 2;
                        ctx.strokeStyle = ARC.fill;
                        ctx.stroke();


                        // IN STROKE circle
                        ctx.beginPath();
                        ctx.arc(0, 0, ARC.radius, 0, ARC.pi * ARC.angCur, false);
                        ctx.lineWidth   = ARC.weight;
                        ctx.strokeStyle = ARC.stroke;
                        ctx.stroke();

                        // csReqAnimFrame(arcDraw);


                        // Update VONG TRON HIEN TAI
                        va.arc.angCur += angPlus;
                        if( va.arc.angCur > 370 ) clearInterval(ti.timer);
                    };

                    // Fx update VE LAI DUA TREN VONG TRON HIEN TAI
                    arcDraw();
                    clearInterval(ti.timer);
                    ti.timer = setInterval(arcDraw, va.arc.speed);


                    // Fx update VONG TRON HIEN TAI
                    // clearInterval(ti.timer);
                    // ti.timer = setInterval(function() {

                    //     va.arc.angCur += 360/delay[cs.idCur]*va.arc.speed;
                    //     if( va.arc.angCur > 360 ) clearInterval(ti.timer);
                        
                    // }, va.arc.speed);
                },



                number : function() {
                    var tRefresh = 100;         // Thoi gian update update len DOM

                    var _setup = function() {
                        tDelay -= tRefresh;

                        // Luu tru xTimer --> Chi update len DOM khi xTimer thay doi
                        va.xTimer0 = xTimer;

                        // Setup xTimer hien tai
                        xTimer = 100 - (tDelay / delay[cs.idCur] * 100);
                        xTimer = M.r(xTimer);
                        if( xTimer > 100 ) xTimer = 0;

                        // Update DOM
                        (va.xTimer0 != xTimer) && $timerItem.attr('data-num', xTimer).text(xTimer);
                    };

                    clearInterval(ti.timer);
                    ti.timer = setInterval(_setup, tRefresh);
                }
            },



            stop : function() {

                if( va.timer == 'bar' ) {
                    xTimer = tDelay/delay[cs.idCur] * 100;

                    var tf = {}; tf[cssTf] = M.tlx(-xTimer, '%');

                    if( is.ts ) $timerItem.css(cssD0).css(tf);
                    else        $timerItem.stop(true).css(tf);
                }

                else if( va.timer == 'arc' ) {
                    va.arc.angCur = 360 - (tDelay/delay[cs.idCur] * 360);
                    clearInterval(ti.timer);
                }

                else if( va.timer == 'number' ) {
                    clearInterval(ti.timer);
                }
            },



            /* Timer arc setup properties
            ---------------------------------------------- */
            arcProp : function() {

                // Arc setup properties
                var _arcOther = {
                    angCur : (!!va.arc && !!va.arc.angCur) ? va.arc.angCur : 0,     // Angle Current, get angle last if update by api
                    pi     : MATH.PI/180,
                    width  : (o.arc.width == null)  ? $timer.width()  : o.arc.width,
                    height : (o.arc.height == null) ? $timer.height() : o.arc.height,
                    speed  : ~~(1000/o.arc.fps)
                };

                // API update: all properties extend to va.arc
                va.arc = $.extend(o.arc, _arcOther);

                // Arc size
                $timerItem.attr({'width' : va.arc.width, 'height' : va.arc.height});
                

                // Arc: style draw
                va.tContext = $timerItem[0].getContext('2d');
                var arcSet = function() {
                    var c = va.tContext;
                    c.setTransform(1,0,0,1,0,0);
                    c.translate(va.arc.width/2, va.arc.height/2);
                    c.rotate(-va.arc.pi*(90-va.arc.rotate));

                    c.strokeStyle = va.arc.stroke;
                    c.fillStyle   = va.arc.fill;
                    c.lineWidth   = va.arc.weight;
                };
                arcSet();

                // window.csReqAnimFrame = (function() {
                //     return  window.requestAnimationFrame ||
                //             window.webkitRequestAnimationFrame ||
                //             window.mozRequestAnimationFrame ||
                //             function(callback) { return setTimeout(callback, va.arc.speed) };
                // })();
            }
        };







        /* APIs
        ================================================== */
        var cs = {

            /* Public methods simple
            ---------------------------------------------- */

            // Method navigation
            prev : function() { EVENTS.prev() },
            next : function() { EVENTS.next() },
            first: function() { SLIDETO.run(0, 1) },
            last : function() { SLIDETO.run(num-1, 1) },
            goTo : function(id) { if( id >= 0 && id < num ) SLIDETO.run(id, 1) },


            // Method slideshow
            play : function() {
                if( o.isSlideshow ) {
                    if( is.stop && !is.playing && !is.fxRun ) { is.stop = 0; SLIDESHOW.reset(); }
                    else                                      { SLIDESHOW.play() }
                }
                else { o.isSlideshow = 1; SLIDESHOW.init(); }
            },

            // pause : function() { is.playing && SLIDESHOW.pause() },
            pause : function() {
                SLIDESHOW.pause();
                o.isPlayPause && $playpause.addClass(o.ns + o.actived);
            },

            // Fix Late - not working correct
            // when translateX call, not working!
            stop : function() {
                if( !is.stop ) { is.stop = 1; SLIDESHOW.pause(1); }
                if( o.isSlideshow ) o.isSlideshow = 0;
            },



            // Method update properties
            prop : function(options, isNoRefresh, oAdd) {

                // Luu tru option cu va Cap nhat option voi deep level
                oo = $.extend(true, {}, o);
                o  = $.extend(true, o, options);

                // Kiem tra slider co toggle show hay khong
                !!is.awake && !isNoRefresh && cs.refresh(oAdd);
            },
            refresh : function(oAdd) {
                // console.log('refresh');
                UPDATE.removeClass(oAdd);

                PROP.slider(oAdd);
                PROP.slide();
                M.toggle();

                UPDATE.reset();
                UPDATE.resize();


                //Other
                RENDER.refresh();
                EVENTS.swipe();
                EVENTS.keyboard();
                EVENTS.mousewheel();
                DEEPLINKING.events();
                SLIDESHOW.update();
            },



            /* Public properties
            ---------------------------------------------- */
            width       : function() { return wViewport },
            height      : function() { return hCode },
            curId       : function() { return cs.idCur },
            slideLength : function() { return num },
            slideCur    : function() { return $s.eq(cs.idCur) },
            slideAll    : function() { return $s },
            opts        : function() { return o },
            varible     : function() { return va },
            is          : function() { return is },
            browser     : function() { return is.browser },
            isMobile    : function() { return is.mobile },




            /* Events
               ['start', 'end', 'before', 'after', 'loadAll', 'loadSlide.id', 'resize',
                'init', 'selectID', 'deselectID', 'swipeBegin', 'swipeEnd', 'fxBegin', 'fxEnd',
                'slideshowPause', 'slideshowPlay', 'loadBegin', 'loadEnd']
            ---------------------------------------------- */
            ev : $(divdiv, {'class': 'code-event'})
        };

        // Extend more complex function to api
        cs = $.extend(cs, API);
        $.data(element[0], 'codetabs', cs);






        /* Init: above pulic methods
        ================================================== */
        INIT.check();
    };




    /* Khoi tao Codeslider - begin
    =======================================================*/
    $.fn.codetabs = function() {
        var args = arguments;       // args[0] : options, args[1]: value

        return $(this).each(function() {

            // Cac bien ban dau
            var self = $(this), code = self.data('codetabs');

            // Tham so thu nhat luon luon la object --> de dang kiem tra
            if( args[0] === undefined ) args[0] = {};


            // Truong hop la object: khoi tao slider moi hoac update properties
            if( typeof args[0] === 'object' ) {

                if( !code ) {
                    var opts = $.extend(true, {}, $.fn.codetabs.defaults, args[0]);
                    new $.codetabs(self, opts);
                }
                else code.prop(args[0]);    // Update properties
            }

            // Truong hop con lai: goi truc tiep function --> neu khong co thi bao error
            else {
                try      { code[args[0]](args[1]) }
                catch(e) { typeof console === 'object' && console.warn('[ codetabs: function not exist! ]'); }
            }
        });
    };


    // Khoi tao slider bang cach khac
    // Function tuong tu nhu o tren
    window.codetabs = function() {
        var args = arguments;       // args[0] : selector, args[1] : options

        var self = $(args[0]);
        if( self.length === 1 ) {

            if( args[1] === UNDEFINED ) args[1] = {};
            var opts = $.extend(true, {}, $.fn.codetabs.defaults, args[1]);

            // Init slider, neu co data 'codetabs' roi --> khong can khoi tao
            // Rut gon bien khong duoc
            if( !self.data('codetabs') ) new $.codetabs(self, opts);
            return self.data('codetabs');
        }
    };


    // Slider auto run
    $(document).ready(function() {

        var code = $('.ct');
        if( code.length ) {

            code.each(function() {
                var self = $(this),
                    data = self.data('tabs'),
                    text = 'isAutoRun-';

                // Kiem tra bien data ton tai hoac khac empty string
                // --> kiem tra tiep data co chuoi 'isAutoRun-true'
                // --> kiem tra tiep co ton tai data 'codetabs' khong
                   (data != undefined && data !== '')
                && (data.indexOf(text+'true') != -1 || data.indexOf(text+'on') != -1 || data.indexOf(text+'1') != -1)
                && !self.data('codetabs')
                && self.codetabs();
            });
        }
    });

    /* Khoi tao Codeslider - end
    =======================================================*/






    /* Codeslider default options - begin
    =======================================================*/
    $.fn.codetabs.defaults = {

        // Name custom for code
        ns              : 'ct-',
        canvasName      : 'canvas',
        canvasTag       : 'div',
        viewportName    : 'viewport',
        slideName       : 'slide',
        navName         : 'nav',
        nextName        : 'next',
        prevName        : 'prev',
        playName        : 'playpause',
        pagName         : 'pag',
        capName         : 'cap',
        timerName       : 'timer',
        layerName       : 'layer',
        overlayName     : 'overlay',
        shadowName      : 'shadow',
        imgName         : 'img',
        lazyName        : 'src',

        name            : null,             // Use for search DOM outer code
        dataSlider      : 'tabs',
        dataSlide       : 'slide',
        current         : 'cur',
        thumbWrap       : 'thumbitem',
        actived         : 'actived',
        inActived       : 'inactived',

        // Setting type of elements
        layout          : 'dot',            // line, dot, dash, free
        view            : 'basic',          // basic, coverflow, ...
        fx              : null,             // fade, move, rectMove...
        fxDefault       : 'rectRun',        // Slider will use this effect if without setup 'fx'
        fxOne           : null,             // Effect both fxIn & fxOut by css
        fxIn            : null,             // Effect by css
        fxOut           : null,             // Effect by css
        fxEasing        : null,             // Default easing depend on css
        fxMobile        : null,
        height          : 'auto',           // auto, fixed
        imgWidth        : 'none',           // none, autofit, largefit, smallfit -> Width type
        imgHeight       : 'none',           // none, autofit, largefit, smallfit -> Height type
        img             : 'none',           // none, autofit, autofill
        dirs            : 'hor',            // Swipe direction, defalut is horizontal, value ['hor', 'ver']
        easeTouch       : 'easeOutQuint',
        easeMove        : 'easeOutCubic',

        // Setting with number or mix value
        speed           : 400,
        speedHeight     : 400,
        speedMobile     : null,
        layerSpeed      : 400,
        layerStart      : 400,
        perspective     : 800,              // Support for layer
        slot            : 'auto',           // 'auto' || number
        stepNav         : 'visible',        // 'visible' || number 1 -> n
        stepPlay        : 1,
        responsive      : null,             // Default responsive-off
        media           : null,             // media-748-768-920 -> media-width-wMin-wMax
        padding         : 0,                // padding default: 0, included padding-left, padding-right, padding media
        margin          : 0,                // margin default: 0, included margin-left, margin-right, margin media
        hCode           : null,             // Height Slider in height-type fixed
        wSlide          : 1,                // Width value of slide, included media, unit -> width-unit-from-to
        idBegin         : 0,                // ID slide to show first in slider ready
        preload         : 1,                // Number slide preload -> show cs; ['all']
        loadAmount      : 2,
        show            : 'all',            // ['all', 'desktop', 'mobile']
        showFrom        : 0,
        offsetBy        : null,             // Fullscreen options: offset by container, included offset-top & offset-bottom

        // Setting with boolean value
        isCenter        : 1,
        isNav           : 0,
        isPag           : 1,
        isCap           : 0,
        isLayerRaUp     : 1,
        isSlideshow     : 0,
        isSwipe         : 1,
        isMousewheel    : 0,
        isLoop          : 1,
        isAnimRebound   : 1,
        isKeyboard      : 0,
        isOverlay       : 0,
        isShadow        : 0,
        isViewGrabStop  : 0,
        isMarginCut     : 1,                // Remove margin-left at begin, margin-right at end slide
        isPagSmooth     : 1,                // Slider center move by pagination, it will clone slide to fill hole
        isFullscreen    : 0,
        isDeeplinking   : 0,
        isCookie        : 0,

        // Setting with object value
        combine         : {},

        swipe           : { isMobile       : 1,             // Turn swipe on mobile devices
                            isBody         : 0,             // Turn off swipe on body
                            isBodyOnMobile : 0 },           // Auto turn on swipe body when option isBody = false

        className       : { grab  : ['grab', 'grabbing'],
                            swipe : ['', 'swiping'],
                            stop  : ['left', 'right'] },

        fxName          : ['random',
                           'fade', 'move',
                           'rectMove', 'rectRun', 'rectSlice',
                           'rubyFade', 'rubyMove', 'rubyRun', 'rubyScale',
                           'zigzagRun'],

        pag             : { type     : 'tab',               // ['bullet', 'thumb', 'tab', 'list'] - codeslider: bullet
                            width    : null,
                            height   : null,
                            dirs     : 'hor',               // ['hor', 'ver']
                            pos      : 'top',               // ['top', 'bottom'] - codeslider: bottom
                            align    : 'begin',             // ['begin', 'center', 'end', 'justified']
                            speed    : 300,
                            ease     : 'easeOutCubic',

                            /* Support options: 
                                + null   --> none setup, width or height pagination depend on css
                                + 'full' --> width or height == slider. depend on direction 'hor' or 'ver'
                                + 'self' --> width or height == all width/height pagitems */
                            sizeAuto : null,
                            moreClass: null,                // Adding class into pagination
                            isActivedCenter  : 0,           // Swap slide bang pag --> update pagitem vi tri center
                            wMinToHor        : 0,           // Tu dong chuyen sang huong 'hor' khi chieu rong cua wiewport nho hon gia tri
                            mediaToHor       : null },

        slideshow       : { delay        : 8000,
                            timer        : 'arc',           // bar, arc, number 
                            isAutoRun    : 1,               // only active false when have playpause button
                            isPlayPause  : 1,
                            isTimer      : 1,               // Timer only turn on when slideshow on
                            isHoverPause : 0,
                            isRunInto    : 1,
                            isRandom     : 0 },

        arc             : { width    : null,
                            height   : null,
                            fps      : 24,
                            rotate   : 0,

                            radius   : 14,
                            weight   : 2,
                            stroke   : '#fff',
                            fill     : 'transparent',

                            oRadius  : 14,
                            oWeight  : 2,
                            oStroke  : 'hsla(0,0%,0%,.2)',
                            oFill    : 'transparent' },

        markup          : { timerInto : 'media',            // ['none', 'nav', 'media']
                            playInto  : 'media'},

        deeplinking     : { prefix : ['code', 'slide'] },

        cookie          : { name     : '',
                            days     : 7  },

        
        // Setting only for layout-free
        layoutFall      : 'line',           // For browser not support css3
        fName           : 'sl',             // Custom namespace on slide
        fLoop           : 1,                // Loop class effect
        isInOutBegin    : 0,                // Add class 'in' 'out' at begin
        isClassRandom   : 0,                // Random class effect
        isSlAsPag       : 0,                // Toggle click slide

        // Tool for developer
        isPosReport     : 0,
        rev             : ['erp']           // ['omed', 'moc.oidutsyht'], 'eerf'
    };

    /* Codeslider default options - end
    =======================================================*/


    /* jQuery Easing */
    $.extend(jQuery.easing, {
        easeOutQuad: function (x, t, b, c, d) {
            return -c *(t/=d)*(t-2) + b;
        },
        easeOutQuint: function (x, t, b, c, d) {
            return c*((t=t/d-1)*t*t*t*t + 1) + b;
        },
        easeInCubic: function (x, t, b, c, d) {
            return c*(t/=d)*t*t + b;
        },
        easeOutCubic: function (x, t, b, c, d) {
            return c*((t=t/d-1)*t*t + 1) + b;
        }
    });

})(jQuery);