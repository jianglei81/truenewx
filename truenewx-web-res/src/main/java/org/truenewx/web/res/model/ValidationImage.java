package org.truenewx.web.res.model;

import javax.validation.constraints.Max;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.truenewx.data.model.TransportModel;
import org.truenewx.data.validation.config.annotation.InheritConstraint;
import org.truenewx.data.validation.constraint.NotContainsAngleBracket;
import org.truenewx.data.validation.constraint.NotContainsHtmlChars;
import org.truenewx.data.validation.constraint.NotContainsSqlChars;
import org.truenewx.data.validation.constraint.TagLimit;

/**
 * 校验测试用的图片档案
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class ValidationImage implements TransportModel<Image> {
    @NotEmpty
    @Length(max = 16)
    @NotContainsSqlChars
    private String name;

    @Length(max = 8)
    @NotContainsHtmlChars
    private String md5;

    @InheritConstraint("dir")
    @NotContainsAngleBracket
    private String path;

    @TagLimit(forbidden = { "A", "div" })
    private String extension;

    @Max(500)
    private int capacity;

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getMd5() {
        return this.md5;
    }

    public void setMd5(final String md5) {
        this.md5 = md5;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public String getExtension() {
        return this.extension;
    }

    public void setExtension(final String extension) {
        this.extension = extension;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public void setCapacity(final int capacity) {
        this.capacity = capacity;
    }

}
