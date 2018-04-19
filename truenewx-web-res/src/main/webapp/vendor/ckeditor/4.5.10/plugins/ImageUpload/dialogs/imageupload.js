(function() {
    function ImageUploadDialog(editor) {
        var maxHeight = $(window).height() - 100;
        var maxWidth = editor.config.imageMaxWidth;
        return {
            title : '添加图片',
            Width : 800,
            minHeight : 400,
            buttons : [ CKEDITOR.dialog.okButton, CKEDITOR.dialog.cancelButton ],
            contents : [
                    {
                        id : "localImage",
                        label : "本地上传",
                        title : "本地上传",
                        elements : [ {
                            type : 'html',
                            html : "<div class='btn' style='position: relative;overflow:hidden;border: 1px solid #ccc;cursor: pointer;font-size: 14px;font-weight: normal;line-height: 1.42857;margin-bottom: 0;padding: 6px 12px;text-align: center;vertical-align: middle;white-space: nowrap;'>"
                                    + "<span style='cursor: pointer'>上传图片</span>"
                                    + "<input id='ckImageUpload' type='file' style='cursor: pointer;display: block;opacity: 0;top: 0px;right: 0px;position: absolute;direction:ltr;'/></div>"
                                    + "<div id='ckViewLocalImage' style='width:"
                                    + maxWidth
                                    + ";margin-top:10px;text-align:center;border:1px #c9cccf solid;padding:5px;'></div>"
                        } ]
                    },
                    {
                        id : "linkImage",
                        label : "网络图片",
                        title : "网络图片",
                        elements : [
                                {
                                    id : "ckImageUrl",
                                    type : 'text',
                                    label : "URL",
                                    onBlur : function() {
                                        if (this.getValue() != "") {
                                            var $image = $("<img/>");
                                            $image.bind("load", function() {
                                                var hRatio;
                                                var wRatio;
                                                var ratio = 1;
                                                var w = $image.width();
                                                var h = $image.height();
                                                var originalHeight = $image.height();
                                                wRatio = maxWidth / w;
                                                hRatio = maxHeight / h;
                                                if (maxWidth == 0 && maxHeight == 0) {
                                                    ratio = 1;
                                                } else if (maxWidth == 0) {
                                                    if (hRatio < 1) {
                                                        ratio = hRatio;
                                                    }
                                                } else if (maxHeight == 0) {
                                                    if (wRatio < 1) {
                                                        ratio = wRatio;
                                                    }
                                                } else if (wRatio < 1 || hRatio < 1) {
                                                    ratio = (wRatio <= hRatio ? wRatio : hRatio);

                                                }
                                                ratio = ratio.toFixed(2);
                                                if (ratio < 1) {
                                                    w = parseInt(w * ratio);
                                                    h = parseInt(h * ratio);
                                                }
                                                $image.height(h);
                                                $image.width(w);
                                                $image.attr("ratio", wRatio >= 1 ? 0 : (Math
                                                        .floor(wRatio * 100) / 100));
                                                $image.attr("original-height", originalHeight);
                                                $image.show();
                                            });
                                            $image.attr("src", this.getValue());
                                            $image.hide();
                                            $("#ckViewLinkImage").html($image);
                                        } else {
                                            $("#ckViewLinkImage").html("");
                                        }
                                    }
                                },
                                {

                                    type : "vbox",
                                    align : 'center',
                                    width : '702px',
                                    children : [ {
                                        type : "html",
                                        html : "<div id='ckViewLinkImage' style='text-align:center;border:1px #c9cccf solid;padding:5px;'></div>"
                                    } ]
                                } ]
                    } ],
            onOk : function() {
                var $seletedTab = $(".cke_dialog_tab.cke_dialog_tab_selected");
                var $img;
                if ($seletedTab.attr("title") == "本地上传") {
                    $img = $("#ckViewLocalImage").find("img[insert='true']");
                } else {
                    $img = $("#ckViewLinkImage").find("img");
                }
                if ($img == null || $img.length == 0) {
                    site.alert("请上传图片");
                    return false;
                }
                var ratio = $img.attr("ratio");
                var image = "<img src='" + $img.attr("src") + "'";
                if (ratio > 0) {
                    image += " width='" + maxWidth + "px'";
                    var originalHeight = $img.attr("original-height");
                    var height = (originalHeight * ratio).toFixed(0);
                    image += " height='" + height + "px'";
                }
                image += " />";
                editor.insertHtml("<p>" + image + "</p>");
                if ($seletedTab.attr("title") == "本地上传") {
                    $("#ckImageUpload").val("");
                    $("#ckViewLocalImage").html("");
                } else {
                    $("#ckViewLinkImage").html("");
                }
            },
            onCancel : function() {
                var $seletedTab = $(".cke_dialog_tab.cke_dialog_tab_selected");
                if ($seletedTab.attr("title") == "本地上传") {
                    $("#ckImageUpload").val("");
                    $("#ckViewLocalImage").html("");
                } else {
                    $("#ckViewLinkImage").html("");
                }
            },
            onShow : function() {
                $(".cke_dialog").css("top", "0px");
                $(".cke_dialog_tab_disabled").removeClass("cke_dialog_tab_disabled");
            },
            onLoad : function() {
                $("input[type='file']")
                        .imageUpload(
                                {
                                    authorizeType : "ARTICLE_IMAGE",
                                    crop : {
                                        disabled : true
                                    },
                                    beginCallback : function(ele) {
                                        $("#ckViewLocalImage")
                                                .html(
                                                        "<img src='"
                                                                + $.tnx.context
                                                                + "/vendor/ckeditor/4.5.10/plugins/ImageUpload/images/Loading6.gif' loding='true'/>")
                                    },
                                    successCallback : function(result) {
                                        if (result != null && result.length > 0) {
                                            var $image = $("<img insert='true'/>");
                                            $image.bind("load", function() {
                                                var hRatio;
                                                var wRatio;
                                                var ratio = 1;
                                                var w = $image.width();
                                                var h = $image.height();
                                                var originalHeight = $image.height();
                                                wRatio = maxWidth / w;
                                                hRatio = maxHeight / h;
                                                if (maxWidth == 0 && maxHeight == 0) {
                                                    ratio = 1;
                                                } else if (maxWidth == 0) {
                                                    if (hRatio < 1) {
                                                        ratio = hRatio;
                                                    }
                                                } else if (maxHeight == 0) {
                                                    if (wRatio < 1) {
                                                        ratio = wRatio;
                                                    }
                                                } else if (wRatio < 1 || hRatio < 1) {
                                                    ratio = (wRatio <= hRatio ? wRatio : hRatio);

                                                }
                                                ratio = ratio.toFixed(2);
                                                if (ratio < 1) {
                                                    w = parseInt(w * ratio);
                                                    h = parseInt(h * ratio);
                                                }
                                                $image.height(h);
                                                $image.width(w);
                                                $image.attr("ratio", wRatio >= 1 ? 0 : (Math
                                                        .floor(wRatio * 100) / 100));
                                                $image.attr("original-height", originalHeight);
                                                $("#ckViewLocalImage").find("img[loding='true']")
                                                        .remove();
                                                $image.show();
                                            });
                                            $image.attr("src", result[0].outerUrl);
                                            $image.hide();
                                            $("#ckViewLocalImage").append($image);
                                        }
                                    }
                                });
            }
        };
    }
    CKEDITOR.dialog.add('ImageUpload', function(editor) {
        return ImageUploadDialog(editor);
    });
})();
