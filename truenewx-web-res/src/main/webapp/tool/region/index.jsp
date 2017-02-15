<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/truenewx-tags" prefix="tnx"%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<title>行政区划数据调整工具</title>
<link href="${context}/vendor/treeview/1.2.0/css/bootstrap-treeview.css" rel="stylesheet" type="text/css" />
<script src="${context}/vendor/treeview/1.2.0/js/bootstrap-treeview.js" type="text/javascript"></script>
<script type="text/javascript">
$(function() {
    selectMenu(0);
    
    var nationCaption = $("#nation option:selected").text();
    $("#nationCaption").text(nationCaption);
});

function analyze() {
    $("#errorBlock").hide();
    
    var sourceObj = $("#source");
    var source = sourceObj.val();
    if (source == "") {
        sourceObj.focus();
        return;
    }
    
    var nation = $("#nation option:selected").val();
    var rpc = $.tnx.rpc.imports("RegionToolController");
    rpc.analyze(nation, source, function(regions) {
        if (regions == null) {
            $("#errorBlock").show();
            sourceObj.select();
        } else {
            $("#form1").hide();
            buildRegionTreeView(regions);
            $("#form2").show();
        }
    });
}

function buildRegionTreeView(regions) {
    var data = [];
    regions.each(function(region) {
        data.push(regionToNode(region));
    });
    $("#regionContainer").treeview({
        data : data,
        showTags : true
    });
    collapseAll();
    showNodeNum();
}

function regionToNode(region) {
    var node = {
        id : region.code,
        text : region.caption,
        level : region.level,
        selectable : false
    };
    node.tags = [node.id];
    if (region.subs && region.subs.length) {
        node.nodes = [];
        region.subs.each(function(subRegion) {
            node.nodes.push(regionToNode(subRegion));
        });
    }
    return node;
}

function processing(callback) {
    var imgObj = $("<img/>").attr("src", $.tnx.domain.site.path.context + "/assets/image/processing.gif");
    var content = $("<div></div>").addClass("text-center").append(imgObj);
    $.tnx.dialog(undefined, content, undefined, {
        backdrop : true,
        events : {
            shown : function() {
                var dialog = this;
                setTimeout(function() {
                    callback();
                    dialog.close();
                }, 1000);
            }
        }
    });
}

function expandAll() {
    processing(function() {
        $("#regionContainer").treeview("expandAll");
    });
}

function collapseAll() {
    $("#regionContainer").treeview("collapseAll");
}

function getNodeNum(nodes) {
    var count = 0;
    if (nodes) {
        nodes.each(function(node){
            count += 1 + getNodeNum(node.nodes);
        });
    }
    return count;
}

function showNodeNum() {
    var data = $("#regionContainer").treeview("getData");
    var count = getNodeNum(data);
    $("#regionNumContainer").text("各级行政区划共" + count + "个");
}

function ensureLevel3(btnObj) {
    var nodes = getNodesWith2Levels();
    var content = $("<table></table>").addClass("table table-bordered bottom-block");
    content.append("<thead><tr>" +
                       "<th nowrap='nowrap'>直辖市名称</th>" +
                       "<th nowrap='nowrap'>直辖市代码</th>" +
                       "<th nowrap='nowrap'>二级行政区划名称</th>" +
                       "<th nowrap='nowrap'>二级行政区划代码</th>" +
                  "</tr></thead>");
    var tbody = $("<tbody></tbody>");
    nodes.each(function(node) {
        var tr = $("<tr></tr>");
        tr.attr("tree-index", node.index);
        tr.append("<td class='text-center'>" + node.text + "</td>");
        tr.append("<td class='text-center'><span class='badge'>" + node.id + "</span></td>");
        var captionInputObj = $("<input type='text' />").addClass("form-control");
        captionInputObj.val(node.text);
        tr.append($("<td></td>").append(captionInputObj));
        var codeInputObj = $("<input type='text' />").addClass("form-control");
        codeInputObj.val(getLevel2NodeId(node.id));
        tr.append($("<td></td>").append(codeInputObj));
        tbody.append(tr);
    });
    content.append(tbody);
    
    $.tnx.confirm(content, function(yes) {
        if (yes) {
            collapseAll(); // 先收起所有条目，以提高速度
            
            processing(function() {
                var regionContainer = $("#regionContainer");
                $("tr", tbody).each(function(index, tr){
                    tr = $(tr);
                    var level1Index = parseInt(tr.attr("tree-index"));
                    var level1Node = regionContainer.treeview("getNode", [level1Index]);
                    var level3Nodes = level1Node.nodes; // 新的三级节点集
                    var level2Node = {
                        text : $("input:eq(0)", tr).val(),
                        id : $("input:eq(1)", tr).val(),
                        selectable : false
                    };
                    level2Node.tags = [level2Node.id];
                    regionContainer.treeview("addNode", [level2Node, level1Index]);
                    // 移动原二级节点到新的二级节点上，成为新的三级节点
                    level3Nodes.each(function(level3Node) {
                        regionContainer.treeview("moveNode", [level3Node.index, level2Node.index]);
                    });
                });
                
                $(btnObj).attr("title", "已设置，不可再次设置").disable();
                showNodeNum();
            });
        }
    }, {
        title : "设置直辖市的二级行政区划"
    });
}

// 获取只有两级的节点集合
function getNodesWith2Levels() {
    var nodes = [];
    var level1Nodes = $("#regionContainer").treeview("getData");
    level1Nodes.each(function(node1) {
        if (node1.nodes && node1.nodes.length) {
            var node2 = node1.nodes[0]; // 第一个二级节点
            if (!node2.nodes || node2.nodes.length == 0) { // 二级节点没有下级节点，则添加到结果集中
                nodes.push(node1);
            }
        }
    });
    return nodes;
}

function getLevel2NodeId(level1NodeId) {
    if (level1NodeId.endsWith("0000")) {
        var length = level1NodeId.length;
        return level1NodeId.substr(0, length - 3) + "1" + level1NodeId.substr(length - 2);
    }
    return undefined;
}

function removeLevel1Suffix(btnObj) {
    var content = $("<div></div>").addClass("row table-responsive");
    var regionContainer = $("#regionContainer");
    var level1Nodes = regionContainer.treeview("getData");
    var tableContainer = $("<div class='col-md-4'><table class='table bottom-block'></table></div>");
    for (var i = 0; i < level1Nodes.length; i++){
        if (i > 10 && i % 10 == 1) {
            content.append(tableContainer);
            tableContainer = $("<div class='col-md-4'><table class='table'></table></div>");
        }
        var tr = $("<tr></tr>");
        var caption = level1Nodes[i].text;
        tr.append($("<td class='text-right' nowrap='nowrap'></td>").css("padding-top", "15px").text(caption));
        tr.append($("<td><span class='glyphicon glyphicon-arrow-right'></span></td>").css("padding-top", "15px"));
        var inputObj = $("<input type='text'/>").addClass("form-control");
        caption = getLevel1CaptionWithoutSuffix(caption);
        inputObj.attr("data-code", level1Nodes[i].id).val(caption);
        tr.append($("<td></td>").append(inputObj));
        $("table", tableContainer).append(tr);
    }
    content.append(tableContainer);
    
    $.tnx.confirm(content, function(yes) {
        if (yes) {
            collapseAll(); // 先收起所有条目，以提高速度
            
            processing(function() {
                var codeCaptionMap = getCodeCaptionMap(content);
                level1Nodes.each(function(level1Node) {
                    var code = level1Node.id;
                    var caption = codeCaptionMap[code];
                    if (caption) {
                        level1Node.text = caption;
                    }
                });
                // 用新的数据集重新构建树
                regionContainer.treeview({
                    data : level1Nodes,
                    showTags : true
                });
                collapseAll();
                
                $(btnObj).attr("title", "已设置，不可再次设置").disable();
            });
        }
    }, {
        title : "设置省级行政区划简称"
    });
}

function getLevel1CaptionWithoutSuffix(caption) {
    if (caption.endsWith("省") || caption.endsWith("市")) {
        caption = caption.substr(0, caption.length - 1);
    } else if (caption.endsWith("自治区")) {
        caption = caption.substr(0, caption.length - 3);
        if (caption.endsWith("壮族") || caption.endsWith("回族")) {
            caption = caption.substr(0, caption.length - 2);
        } else if (caption.endsWith("维吾尔")) {
            caption = caption.substr(0, caption.length - 3);
        }
    }
    return caption;
}

function getCodeCaptionMap(container) {
    var map = {};
    $("input[data-code]", container).each(function(i, inputObj) {
        inputObj = $(inputObj);
        var caption = inputObj.val();
        if (caption) { // 忽略未输入新名称的
            var code = inputObj.attr("data-code");
            map[code] = caption;
        }
    });
    return map;
}

function back() {
    window.location.reload(true);
}

function nodeToRegion(node) {
    var region = {
        code : node.id,
        caption : node.text
    };
    if (node.nodes && node.nodes.length) {
        region.subs = [];
        node.nodes.each(function(subNode) {
            region.subs.push(nodeToRegion(subNode));
        });
    }
    return region;
}

function generate() {
    $("#form2").hide();
    
    processing(function() {
        var regions = [];
        var data = $("#regionContainer").treeview("getData");
        data.each(function(node) {
            regions.push(nodeToRegion(node));
        });
        
        var json = "";
        var national = $("#national").is(":checked");
        if (national) {
            var nationOption = $("#nation option:selected");
            var nationalRegion = {
                code : nationOption.val(),
                caption : nationOption.text(),
                subs : regions
            };
            json = $.toJSON(nationalRegion);
        } else {
            json = $.toJSON(regions);
        }
        $("#jsonContainer").text(json);
    });
    
    $("#form3").show();
}
</script>
</head>
<body>
<form class="form-horizontal" id="form1" action="${context}/tool/region" role="form" method="get">
    <div class="form-group">
        <div class="col-md-2">
            <select class="form-control" id="nation">
                <option value="CN">中国</option>
            </select>
        </div>
    </div>
    <div class="form-group">
        <div class="col-md-10">
            <div class="alert alert-info bottom-block">请在下框中填入<span id="nationCaption"></span>国家统计局行政区划代码页原始数据 (
            <a href="http://www.stats.gov.cn/tjsj/tjbz/xzqhdm/" target="_blank">点此查看</a> )</div>
        </div>
    </div>
    <div class="form-group">
        <div class="col-md-10">
            <textarea class="form-control" id="source" rows="20" placeholder="请确保以110000开头"></textarea>
        </div>
    </div>
    <div class="form-group hidden" id="errorBlock">
        <div class="col-md-10">
            <div class="alert alert-danger bottom-block">数据格式不正确，请确保以110000开头</div>
        </div>
    </div>
    <div class="form-group">
        <div class="col-md-10">
            <button type="button" class="btn btn-primary" onclick="analyze()">分析</button>
        </div>
    </div>
</form>

<form class="form-horizontal hidden" id="form2" action="${context}/tool/region" role="form" method="get">
    <div class="form-group">
        <div class="col-md-6">
            <button type="button" class="btn btn-default" title="设置直辖市具有三级行政区划，与其它省份的层次结构保持一致"
                onclick="ensureLevel3(this)">设置直辖市为三级</button>
            <button type="button" class="btn btn-default" title="去掉省级行政区划的省、市、自治区后缀"
                onclick="removeLevel1Suffix(this)">去掉省级后缀</button>
            <button type="button" class="btn btn-default" title="为省级行政区划分组"
                onclick="groupLevel1(this)">省级分组</button>
        </div>
    </div>
    <div class="form-group">
        <div class="col-md-6">
            <span class="text-muted" id="regionNumContainer"></span>
            <a class="pull-right" href="javascript:collapseAll()" title="全部收缩">
                <span class="glyphicon glyphicon-minus" style="margin-right: 8px;"></span>
            <a class="pull-right" href="javascript:expandAll()" title="全部展开">
                <span class="glyphicon glyphicon-plus" style="margin-right: 8px;"></span>
            </a>
        </div>
    </div>
    <div class="form-group">
        <div class="col-md-6">
            <div id="regionContainer"></div>
        </div>
    </div>
    <div class="form-group">
        <div class="col-md-6">
            <div class="checkbox">
                <label>
                    <input type="checkbox" id="national" checked="checked"> 将国家作为顶级行政区划
                </label>
            </div>
        </div>
    </div>
    <div class="form-group">
        <div class="col-md-6">
            <button type="button" class="btn btn-primary" onclick="generate()">生成</button>
            <button type="button" class="btn btn-default" onclick="back()">返回</button>
        </div>
    </div>
</form>

<form class="form-horizontal hidden" id="form3" action="${context}/tool/region" role="form" method="get">
    <div class="form-group">
        <div class="col-md-10">
            <textarea class="form-control" id="jsonContainer" rows="20" readonly="readonly"></textarea>
        </div>
    </div>
    <div class="form-group">
        <div class="col-md-10">
            <button type="button" class="btn btn-default" onclick="back()">完成</button>
        </div>
    </div>
</form>
</body>
</html>
