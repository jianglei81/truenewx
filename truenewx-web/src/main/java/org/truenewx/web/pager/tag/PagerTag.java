package org.truenewx.web.pager.tag;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.truenewx.data.query.Paging;
import org.truenewx.web.pager.functor.AlgoPagerOutput;

/**
 * 分页标签
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class PagerTag extends TagSupport {

    private static final long serialVersionUID = -8236304660577964951L;

    /**
     * 分页信息
     */
    private Paging value;

    /**
     * 按钮个数
     */
    private int pageNoSpan = 3;

    /**
     * 对齐方式
     */
    private String align = "";

    /**
     * 每页显示数选项集
     */
    private String pageSizeOptions = "";

    /**
     * 跳转按钮文本
     */
    private String goText = "";

    /**
     * href跳转模板
     */
    private String tempHref = "";

    /**
     * 是否显示页码输入框
     */
    private boolean pageNoInputtable = false;

    /**
     * 附加样式
     */
    private String className = "";

    /**
     * 是否显示总记录条数
     */
    private boolean showCount = true;

    /**
     * @param pageNoSpan
     *            按钮个数
     */
    public void setPageNoSpan(final int pageNoSpan) {
        this.pageNoSpan = pageNoSpan;
    }

    /**
     * @param pageNoInputtable
     *            是否显示页码输入框
     */
    public void setPageNoInputtable(final boolean pageNoInputtable) {
        this.pageNoInputtable = pageNoInputtable;
    }

    /**
     * @param goText
     *            跳转按钮文本
     */
    public void setGoText(final String goText) {
        this.goText = goText;
    }

    /**
     * @param value
     *            分页信息
     */
    public void setValue(final Paging value) {
        this.value = value;
    }

    /**
     * @param align
     *            对齐方式
     */
    public void setAlign(final String align) {
        this.align = align;
    }

    /**
     * @param pageSizeOptions
     *            每页显示数选项集
     */
    public void setPageSizeOptions(final String pageSizeOptions) {
        this.pageSizeOptions = pageSizeOptions;
    }

    /**
     * @param tempHref
     *            连接模板
     */
    public void setTempHref(final String tempHref) {
        this.tempHref = tempHref;
    }

    /**
     * @return 附加样式
     * 
     * @author jianglei
     */
    public String getClassName() {
        return this.className;
    }

    /**
     * @param className
     *            附加样式
     * 
     * @author jianglei
     */
    public void setClassName(final String className) {
        this.className = className;
    }

    /**
     * @return 是否显示总记录条数
     * 
     * @author jianglei
     */
    public boolean isShowCount() {
        return this.showCount;
    }

    /**
     * @param showCount
     *            是否显示总记录条数
     * 
     * @author jianglei
     */
    public void setShowCount(final boolean showCount) {
        this.showCount = showCount;
    }

    @Override
    public int doEndTag() throws JspException {
        final JspWriter out = this.pageContext.getOut();
        final Map<String, Object> params = new HashMap<>();
        params.put("align", this.align);
        params.put("goText", this.goText);
        params.put("tempHref", this.tempHref);
        params.put("pageNoInputtable", this.pageNoInputtable);
        params.put("pageNoSpan", this.pageNoSpan);
        params.put("pageSizeOptions", this.pageSizeOptions);
        params.put("paging", this.value);
        params.put("className", this.className);
        params.put("showCount", this.showCount);
        AlgoPagerOutput.visit((HttpServletRequest) this.pageContext.getRequest(), out, params);
        return Tag.EVAL_PAGE;
    }

}
