/**
 * 对原生javascript的扩展
 */

/**
 * 扩展String
 */
$.extend(String.prototype, {
    format : function() {
        var args = arguments;
        return this.replace(/{(\d+)}/gm, function(match, name) {
            return args[~~name];
        });
    },
    firstToLowerCase : function() {
        return this.substring(0, 1).toLowerCase() + this.substring(1);
    },
    firstToUpperCase : function() {
        return this.substring(0, 1).toUpperCase() + this.substring(1);
    },
    replaceAll : function(findText, replaceText) {
        var regex = new RegExp(findText, "g");
        return this.replace(regex, replaceText);
    }
});

/**
 * 扩展Date
 */
$.extend(Date.prototype, {
    /**
     * 将当前Date转换为指定格式的字符串 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 年(y)可以用 1-4个占位符，毫秒(S)只能用 1
     * 个占位符(是 1-3 位的数字) 例子： (new Date()).format("yyyy-MM-dd hh:mm:ss.S") ==> 2013-07-02 08:09:04.423
     * (new Date()).format("yyyy-M-d h:m:s.S") ==> 2013-7-2 8:9:4.18
     *
     * @param pattern
     *            格式
     * @returns 当前Date的指定格式的字符串
     * @author meizz
     */
    format : function(pattern) {
        var o = {
            "M+" : this.getMonth() + 1, // 月份
            "d+" : this.getDate(), // 日
            "h+" : this.getHours(), // 小时
            "m+" : this.getMinutes(), // 分
            "s+" : this.getSeconds(), // 秒
            "q+" : Math.floor((this.getMonth() + 3) / 3), // 季度
            "S" : this.getMilliseconds()
        // 毫秒
        };
        if (/(y+)/.test(pattern)) {
            pattern = pattern.replace(RegExp.$1, (this.getFullYear() + "")
                    .substr(4 - RegExp.$1.length));
        }
        for ( var k in o) {
            if (new RegExp("(" + k + ")").test(pattern)) {
                pattern = pattern.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k])
                        : (("00" + o[k]).substr(("" + o[k]).length)));
            }
        }
        return pattern;
    }
});
