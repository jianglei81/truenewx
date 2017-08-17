/**
 * 基于bootstrap v2的扩展 truenewx-bs2.js v1.0.0
 *
 * Depends on: bootstrap-2.*.*.js, truenewx.js
 */
$
        .extend(
                $.tnx,
                {
                    dialogHtml : "<div class=\"dialog modal hide fade\" tabindex=\"-1\" style=\"min-width: 300px;\">"
                            + "<div class=\"modal-header\">"
                            + "<button type=\"button\" class=\"close\" data-dismiss=\"modal\" "
                            + "aria-hidden=\"true\">&times;</button>"
                            + "<h4 class=\"modal-title\" style=\"white-space: nowrap; margin: 1px 20px 1px 1px;\">&nbsp;</h4>"
                            + "</div>"
                            + "<div class=\"modal-body\">&nbsp;</div>"
                            + "<div class=\"modal-footer\">&nbsp;</div>" + "</div>",
                    processHtml : "<div class=\"processing modal hide fade\" tabindex=\"-1\" style=\"text-align: center;\">"
                            + "<div class=\"progress progress-striped active\" style=\"margin-bottom: 0px;\">"
                            + "<div class=\"bar\" style=\"width: 100%;\"></div>"
                            + "</div>"
                            + "<div class=\"image\">"
                            + "<img />"
                            + "<div class=\"bar\" style=\"color: #FFFFFF\"></div>"
                            + "</div>"
                            + "</div>"
                });

$.extend($.tnx, {
    processing : function(timeout, text, imageUrl) {
        var html = this.processHtml;
        if (html) {
            var zIndex = this.minTopZIndex(20);
            var processingObj = $(html); // 每次全新构建DOM元素
            $("body").append(processingObj);

            if (typeof timeout == "string") {
                imageUrl = text;
                text = timeout;
                timeout = 0;
            }
            if (imageUrl && imageUrl.length) {
                processingObj.find(".progress").remove();
                processingObj.find("img").attr("src", imageUrl);
                processingObj.css({
                    backgroundColor : "transparent",
                    border : "none",
                    boxShadow : "none"
                });
            } else {
                processingObj.find(".image").remove();
            }
            if (text) {
                processingObj.find(".bar").text(text);
            }
            processingObj.close = function() {
                processingObj.modal("hide");
            };
            processingObj.on("hidden", function() {
                processingObj.remove(); // 隐藏后移除DOM元素
            });
            processingObj.center();

            processingObj.modal({
                backdrop : "static",
                keyboard : true
            });
            processingObj.css("zIndex", zIndex);
            processingObj.next(".modal-backdrop").css("zIndex", zIndex - 10);
            if (typeof timeout == "number" && timeout > 0) {
                setTimeout(processingObj.close, timeout);
            }
            return processingObj;
        }
        return undefined;
    },
    /**
     * 弹出模态对话框显示指定内容
     *
     * @param title
     *            标题
     * @param content
     *            内容
     * @param buttons
     *            按钮集，数组，每个按钮的选项形如： { text : "按钮上的显示文本", "class" : "除btn外的其它样式名", style : "自定义样式",
     *            focus: false //是否默认获得焦点, click: function(){
     *            按钮单击处理事件，其中this为整个对话框jquery对象，调用this.close()可关闭对话框} }
     * @param options
     *            选项，形如：{width: "200px", events: 事件映射集，形如：{show: function(){ }, shown: function(){ },
     *            close: function(){ }, hide: function(){ }, hidden: function(){ } } }
     */
    dialog : function(title, content, buttons, options) {
        var html = this.dialogHtml;// this.loadTemplate("../bs2/dialog.html");
        if (html) {
            var zIndex = this.minTopZIndex(20);
            var dialogObj = $(html);
            $("body").append(dialogObj);

            dialogObj.close = function() {
                dialogObj.modal("hide");
            };
            dialogObj.setTitle = function(title) {
                if (typeof title == "string") {
                    dialogObj.find(".modal-title").text(title);
                } else if (title instanceof jQuery) {
                    title.replaceAll(dialogObj.find(".modal-title"));
                    title.addClass(".modal-title");
                } else {
                    dialogObj.find(".modal-header").remove();
                }
            };
            dialogObj.setTitle(title);
            var dialogBodyObj = dialogObj.find(".modal-body");
            if (content instanceof jQuery) {
                content.show();
            }
            dialogBodyObj.html(content);

            var focusBtnObj = undefined;
            var footerObj = dialogObj.find(".modal-footer");
            if ($.isArray(buttons)) { // buttons必须为数组形式，否则不会生成按钮
                footerObj.html(""); // 先清空可能已有的按钮
                buttons.each(function(button) {
                    if (button) {
                        var btnObj = $("<button></button>");
                        btnObj.attr("type", "button");
                        btnObj.html(button.text);
                        btnObj.addClass("btn");
                        btnObj.addClass(button["class"]);
                        if (button.style) {
                            btnObj.attr("style", button.style);
                        }
                        if (button.click) {
                            btnObj.click(function() {
                                button.click.apply(dialogObj);
                            });
                        } else {
                            btnObj.click(function() {
                                dialogObj.close();
                            });
                        }
                        if (button.focus === true) {
                            focusBtnObj = btnObj;
                        }
                        btnObj.appendTo(footerObj);
                    }
                });
            } else {
                footerObj.remove();
            }
            dialogObj.resetWidth = function() {
                var width = dialogBodyObj.outerWidth();
                dialogObj.css("width", width + "px");
            };

            options = options || {};
            var events = options.events;
            // 注册事件
            dialogObj.on("shown", function() { // shown事件特殊处理
                if (focusBtnObj) {
                    focusBtnObj.focus();
                }
                if (events && typeof events.shown == "function") {
                    events.shown.apply(dialogObj);
                }
                dialogObj.off("shown");
                dialogObj.resetWidth();
                dialogObj.center();
            });
            dialogObj.on("hidden", function() { // hidden事件特殊处理
                if (events && typeof events.hidden == "function") {
                    events.hidden.apply(dialogObj);
                }
                dialogObj.remove(); // 对话框关闭后清除对话框DOM元素
            });
            if (events) {
                if (typeof events.close == "function") {
                    var btnClose = $(".close", dialogObj);
                    btnClose.click(function() {
                        events.close.apply(dialogObj);
                    });
                }
                if (typeof events.show == "function") {
                    dialogObj.on("show", function() {
                        events.show.apply(dialogObj);
                    });
                }
                if (typeof events.hide == "function") {
                    dialogObj.on("hide", function() {
                        events.hide.apply(dialogObj);
                    });
                }
            }

            if (options.width) {
                dialogObj.css("width", options.width);
            } else {
                dialogObj.css("width", "auto");
                // 未指定宽度时才需要设置最大宽度
                if (options.maxWidth) {
                    dialogObj.css("max-width", options.maxWidth);
                } else { // 设置默认的最大宽度为最小宽度的2倍
                    var minWidth = parseInt(dialogObj.find(".modal-dialog").css("min-width"));
                    dialogObj.css("max-width", minWidth * 2 + "px");
                }
            }

            // 更改内容框最大高度
            var headerHeight = dialogObj.find(".modal-header").height();
            var footerHeight = dialogObj.find(".modal-footer").height();
            var windowHeight = $(window).height();
            var bodyMaxHeight = windowHeight - headerHeight - footerHeight - 20;
            dialogBodyObj.css("maxHeight", bodyMaxHeight + "px");
            dialogBodyObj.css("overflowY", "visible");

            dialogObj.center();

            var backdrop = options.backdrop == undefined ? "static" : options.backdrop;
            dialogObj.modal({
                backdrop : backdrop,
                keyboard : true
            });
            dialogObj.css("zIndex", zIndex);
            dialogObj.next(".modal-backdrop").css("zIndex", zIndex - 10);
        }
    }
});

// 扩展弹出提示
if (typeof $.fn.popover == "function") {
    var fn_popover_applyPlacement = $.fn.popover.Constructor.prototype.applyPlacement;
    $.extend($.fn.popover.defaults, {
        arrow : "default", // 箭头相对于框体的位置
        closeable : false
    });
    $.extend($.fn.popover.Constructor.prototype, {
        tip : function() {
            if (!this.$tip) {
                this.$tip = $(this.options.template);
                if (!this.options.content) {
                    $(".popover-content", this.$tip).remove();
                    $(".popover-title", this.$tip).css({
                        borderRadius : "5px",
                        borderBottom : "none"
                    });
                }
                this.$tip.css({
                    padding : "0px",
                    minWidth : this.options.minWidth,
                    maxWidth : this.options.maxWidth,
                    width : this.options.width
                });
            }
            return this.$tip;
        },
        applyPlacement : function(offset, placement) {
            if (this.options.closeable) {
                this.addCloseButton();
            }
            var $tip = this.tip();
            $tip.attr("arrow", this.options.arrow);
            if (this.options.arrow != "default") {
                var $arrow = $(".arrow", $tip);
                var arrowMargin = -parseInt($arrow.css("border-" + this.options.arrow + "-width"));
                switch (this.options.arrow) {
                case "top":
                    if (placement == "left" || placement == "right") {
                        var diff;
                        var titleHeight = $(".popover-title:visible", $tip).outerHeight();
                        if (titleHeight) {
                            diff = ($tip.outerHeight() - titleHeight) / 2 - 1;
                        } else {
                            diff = $tip.outerHeight() / 2 + arrowMargin - 8;
                        }
                        $arrow.css("marginTop", arrowMargin - diff);
                        offset.top += diff;
                        this.applyArrowColor(placement);
                    }
                    break;
                case "bottom":
                    if (placement == "left" || placement == "right") {
                        var diff;
                        var titleHeight = $(".popover-title:visible", $tip).outerHeight();
                        if (titleHeight) {
                            diff = ($tip.outerHeight() - titleHeight) / 2 - 1;
                        } else {
                            diff = $tip.outerHeight() / 2 + arrowMargin - 8;
                        }
                        $arrow.css("marginTop", arrowMargin + diff);
                        offset.top -= diff;
                    }
                    break;
                case "left":
                    if (placement == "top" || placement == "bottom") {
                        var diff = $tip.outerWidth() / 2 + arrowMargin - 8;
                        $arrow.css("marginLeft", arrowMargin - diff);
                        offset.left += diff;
                    }
                    break;
                case "right":
                    if (placement == "top" || placement == "bottom") {
                        var diff = $tip.outerWidth() / 2 + arrowMargin - 8;
                        $arrow.css("marginLeft", arrowMargin + diff);
                        offset.left -= diff;
                    }
                    break;
                default:
                    this.options.arrow = "default";
                    $tip.attr("arrow", this.options.arrow);
                    break;
                }
            }
            if (placement == "bottom" || !this.options.content) {
                // 如果弹出气泡在下方，则箭头颜色一定需要设置
                this.applyArrowColor(placement);
            }
            fn_popover_applyPlacement.call(this, offset, placement);
        },
        applyArrowColor : function(placement) {
            var $tip = this.tip();
            $("style", $tip).remove(); // 先移除可能已有的
            var arrowColor = $(".popover-title:visible", $tip).css("backgroundColor");
            if (arrowColor) {
                $tip.prepend("<style>.popover." + placement + "[arrow='" + this.options.arrow
                        + "'] .arrow:after{border-" + placement + "-color:" + arrowColor
                        + "}</style>");
            }
        },
        addCloseButton : function() {
            var $tip = this.tip();
            var $title = $(".popover-title:visible", $tip);
            var top;
            if ($title.length) { // 有标题则放置在标题栏中
                var paddingRight = parseInt($title.css("paddingRight"));
                if (paddingRight == parseInt($title.css("paddingLeft"))) {
                    $title.css("paddingRight", paddingRight * 2);
                }
                top = parseInt($title.css("paddingTop")) - 1;
            } else { // 否则放置在内容框中
                var $content = $(".popover-content", $tip);
                var paddingRight = parseInt($content.css("paddingRight"));
                if (paddingRight == parseInt($content.css("paddingLeft"))) {
                    $content.css("paddingRight", paddingRight * 2);
                }
                top = parseInt($content.css("paddingTop"));
            }
            var btnClose = $("<button></button>").attr("type", "button");
            btnClose.addClass("close").html("&times;");
            btnClose.css({
                position : "absolute",
                right : "10px",
                top : top + "px"
            });
            var _this = this;
            btnClose.click(function() {
                _this.hide();
            });
            $tip.append(btnClose);
        }
    });
}
