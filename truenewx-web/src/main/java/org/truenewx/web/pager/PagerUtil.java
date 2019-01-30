package org.truenewx.web.pager;

import java.io.File;
import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.truenewx.core.Strings;
import org.truenewx.core.util.IOUtil;
import org.truenewx.core.util.MathUtil;
import org.truenewx.data.query.Paging;
import org.truenewx.web.spring.context.SpringWebContext;
import org.truenewx.web.util.WebUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 分页工具类
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class PagerUtil {

    private PagerUtil() {
    }

    public static int getPageSize(HttpServletRequest request, int defaultPageSize) {
        String url = WebUtil.getRelativeRequestUrl(request);
        String cookieName = url.replace('/', '_') + "_pageSize";
        String value = WebUtil.getCookieValue(request, cookieName);
        return MathUtil.parseInt(value, defaultPageSize);
    }

    public static int getPageSize(int defaultPageSize) {
        return getPageSize(SpringWebContext.getRequest(), defaultPageSize);
    }

    public static void output(HttpServletRequest request, Writer out, Map<String, Object> params) {
        Paging paging = null;
        if (params.get("paging") != null) {
            paging = (Paging) params.get("paging");
        } else if (params.get("total") != null && params.get("pageSize") != null
                && params.get("pageNo") != null) {
            paging = new Paging(MathUtil.parseInt(params.get("pageSize").toString()),
                    MathUtil.parseInt(params.get("pageNo").toString()),
                    MathUtil.parseInt(params.get("total").toString()));
        }
        if (paging != null && paging.isPageable()) {
            boolean pageNoInputtable = false;
            if (params.get("pageNoInputtable") != null) {
                pageNoInputtable = BooleanUtils
                        .toBoolean(params.get("pageNoInputtable").toString());
            }
            String goText = "";
            if (params.get("goText") != null) {
                goText = params.get("goText").toString();
            }
            String align = "";
            if (params.get("align") != null) {
                align = params.get("align").toString();
            }
            String pageSizeOptions = "";
            if (params.get("pageSizeOptions") != null) {
                pageSizeOptions = params.get("pageSizeOptions").toString();
            }
            int pageNoSpan = 3;
            if (params.get("pageNoSpan") != null) {
                pageNoSpan = Integer.parseInt(params.get("pageNoSpan").toString());
            }

            params.put("align", align);
            params.put("pageNoInputtable", pageNoInputtable);
            params.put("goText", goText);
            params.put("pageSizeOptions", pageSizeOptions.split(","));
            params.put("total", paging.getTotal());
            params.put("pageCount", paging.getPageCount());
            params.put("pageNo", paging.getPageNo());
            params.put("pageSize", paging.getPageSize());
            params.put("previousPage", paging.getPreviousPage());
            params.put("nextPage", paging.getNextPage());
            params.put("isMorePage", paging.isMorePage());
            params.put("isCountable", paging.isCountable());
            params.put("startPage", getStartPage(paging, pageNoSpan));
            params.put("endPage", getEndPage(paging, pageNoSpan));

            Configuration config = new Configuration();
            try {
                // 在pager目录中找文件
                String baseDir = request.getSession().getServletContext().getRealPath("pager");
                File templateFile = IOUtil.findI18nFileByDir(baseDir, "pager", "ftl",
                        request.getLocale());
                if (templateFile != null) {
                    config.setDirectoryForTemplateLoading(templateFile.getParentFile());
                    Template t = config.getTemplate(templateFile.getName(), Strings.ENCODING_UTF8);
                    t.process(params, out);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 获得开始页码
     *
     * @return 开始页码
     */
    private static int getStartPage(Paging paging, int pageNoSpan) {
        int total = paging.getTotal();
        int pageNo = paging.getPageNo();
        if (total < 0) {
            if (pageNo <= pageNoSpan) {
                return 1;
            }
            return pageNo - pageNoSpan;
        } else {
            int pageCount = paging.getPageCount();
            if (pageCount <= 0 || pageCount <= pageNoSpan * 2 + 1 || pageNo - pageNoSpan <= 0) {
                return 1;
            } else if (pageCount - pageNoSpan <= pageNo && pageNo - pageNoSpan - 1 <= 0) {
                return 1;
            }
            return pageNo - pageNoSpan;
        }
    }

    /**
     * 获得结束页码
     *
     * @return 结束页码
     */
    private static int getEndPage(Paging paging, int pageNoSpan) {
        int total = paging.getTotal();
        int pageNo = paging.getPageNo();
        int pageCount = paging.getPageCount();
        int endPage = pageNo + pageNoSpan;
        if (!paging.isMorePage()) {
            return pageNo;
        } else if (total > 0 && endPage > pageCount) {
            return pageCount;
        } else if (total == 0) {
            return 1;
        }
        return endPage;
    }
}
