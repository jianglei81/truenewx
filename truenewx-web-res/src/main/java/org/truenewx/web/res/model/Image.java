package org.truenewx.web.res.model;

import java.util.Date;

import org.truenewx.data.model.unity.AbstractUnity;

/**
 * 图片
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class Image extends AbstractUnity<Long> {
    /**
     * md5值
     */
    private String md5;

    /**
     * 路径
     */
    private String dir;

    /**
     * 扩展名
     */
    private String extension;

    /**
     * 宽度
     */
    private int width;

    /**
     * 高度
     */
    private int height;

    /**
     * 图片大小
     */
    private int capacity;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * @return md5值
     */
    public String getMd5() {
        return this.md5;
    }

    /**
     * @param md5
     *            md5值
     */
    public void setMd5(final String md5) {
        this.md5 = md5;
    }

    /**
     * @return 路径
     */
    public String getDir() {
        return this.dir;
    }

    /**
     * @param dir
     *            路径
     */
    public void setDir(final String dir) {
        this.dir = dir;
    }

    /**
     * @return 扩展名
     */
    public String getExtension() {
        return this.extension;
    }

    /**
     * @param extension
     *            扩展名
     */
    public void setExtension(final String extension) {
        this.extension = extension;
    }

    /**
     * @return 宽度
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * @param width
     *            宽度
     */
    public void setWidth(final int width) {
        this.width = width;
    }

    /**
     * @return 高度
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * @param height
     *            高度
     */
    public void setHeight(final int height) {
        this.height = height;
    }

    /**
     * @return 图片大小
     */
    public int getCapacity() {
        return this.capacity;
    }

    /**
     * @param capacity
     *            图片大小
     */
    public void setCapacity(final int capacity) {
        this.capacity = capacity;
    }

    /**
     * @return 创建时间
     */
    public Date getCreateTime() {
        return this.createTime;
    }

    /**
     * @param createTime
     *            创建时间
     */
    public void setCreateTime(final Date createTime) {
        this.createTime = createTime;
    }
}
