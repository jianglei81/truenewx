/**
 * 基于zui的扩展 truenewx-zui.js v1.0.0
 *
 * Depends on: bootstrap-3.*.*.js, truenewx.js
 */
$
        .extend(
                $.tnx,
                {
                    dialogHtml : "<div class=\"modal fade\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"_modal_title\" aria-hidden=\"true\">"
                            + "<div class=\"modal-dialog\" style=\"min-width: 300px;\">"
                            + "<div class=\"modal-content\">"
                            + "<div class=\"modal-header\" style=\"padding: 7px 15px;\">"
                            + "<button type=\"button\" class=\"close\" style=\"margin-top: 4px;\" data-dismiss=\"modal\" aria-hidden=\"true\">&times;</button>"
                            + "<h4 class=\"modal-title\" id=\"_modal_title\" style=\"white-space: nowrap; margin: 1px 20px 1px 1px;\">&nbsp;</h4>"
                            + "</div>"
                            + "<div class=\"modal-body\">&nbsp;</div>"
                            + "<div class=\"modal-footer\" style=\"margin-top: 0px;\">&nbsp;</div>"
                            + "</div>" + "</div>" + "</div>"
                });

$.extend($.tnx, {
    dialog : function(title, content, buttons, options) {
        var html = this.dialogHtml;// this.loadTemplate("../bs3/dialog.html");
        if (html) {
            var zIndex = this.minTopZIndex(20);
            var dialogObj = $(html);
            $("body").append(dialogObj);

            dialogObj.close = function() {
                dialogObj.modal("hide");
            };
            if (typeof title == "string") {
                dialogObj.find(".modal-title").text(title);
            } else if (title instanceof jQuery) {
                title.replaceAll(dialogObj.find(".modal-title"));
                title.addClass(".modal-title");
            } else {
                dialogObj.find(".modal-header").remove();
            }
            var dialogBodyObj = dialogObj.find(".modal-body");
            dialogBodyObj.html(content);

            var focusBtnObj = undefined;
            var footerObj = dialogObj.find(".modal-footer");
            if ($.isArray(buttons)) { // buttons必须为数组形式，否则不会生成按钮
                footerObj.html(""); // 先清空可能已有的按钮
                buttons.each(function(button) {
                    if (button) {
                        var btnObj = $("<button></button>");
                        btnObj.attr("type", "button");
                        btnObj.text(button.text);
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
                dialogObj.find(".modal-dialog").css("width", width + "px");
            };
            var events = options ? options.events : undefined;
            // 注册事件
            dialogObj.on("shown.zui.modal", function() { // shown事件特殊处理
                if (focusBtnObj) {
                    focusBtnObj.focus();
                }
                if (events && typeof events.shown == "function") {
                    events.shown.apply(dialogObj);
                }
                dialogObj.off("shown.zui.modal");
                dialogObj.resetWidth();
                dialogObj.find(".modal-dialog").center();
            });
            dialogObj.on("hidden.zui.modal", function() { // hidden事件特殊处理
                alert("this");
                if (events && typeof events.hidden == "function") {
                    events.hidden.apply(dialogObj);
                }
                dialogObj.remove(); // 对话框关闭后清除对话框DOM元素
            });
            if (events) {
                if (typeof events.close == "function") {
                    var btnClose = $(".close", dialogObj);
                    btnClose.click(function() {
                        alert("123");
                        events.close.apply(dialogObj);
                    });
                } else if (typeof events.show == "function") {
                    dialogObj.on("show.zui.modal", function() {
                        events.show.apply(dialogObj);
                    });
                } else if (typeof events.hide == "function") {
                    dialogObj.on("hide.zui.modal", function() {
                        events.hide.apply(dialogObj);
                    });
                }
            }

            if (options && options.width) {
                dialogObj.find(".modal-dialog").css("width", options.width);
            } else {
                dialogObj.find(".modal-dialog").css("width", "auto");
            }
            // 更改内容框最大高度
            var headerHeight = dialogObj.find(".modal-header").height();
            var footerHeight = dialogObj.find(".modal-footer").height();
            var windowHeight = $(window).height();
            var bodyMaxHeight = windowHeight - headerHeight - footerHeight - 20;
            dialogBodyObj.css("maxHeight", bodyMaxHeight + "px");
            dialogBodyObj.css("overflowY", "visible");

            dialogObj.modal({
                backdrop : (!options || options.backdrop == undefined) ? "static"
                        : options.backdrop,
                keyboard : true
            });
            dialogObj.find(".modal-dialog").center();
            dialogObj.css("zIndex", zIndex);
            dialogObj.next(".modal-backdrop").css("zIndex", zIndex - 10);
        }
    }
});
