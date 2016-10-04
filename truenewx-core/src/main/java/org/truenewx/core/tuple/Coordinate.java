package org.truenewx.core.tuple;

/**
 * 坐标
 * 
 * @author jianglei
 * @since JDK 1.8
 */
public class Coordinate {
    /**
     * x轴坐标
     */
    private double x;

    /**
     * y轴坐标
     */
    private double y;

    public Coordinate() {
    }

    /**
     * @param x
     *            x轴坐标
     * @param y
     *            y轴坐标
     */
    public Coordinate(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return x轴坐标
     *
     * @author jianglei
     */
    public double getX() {
        return this.x;
    }

    /**
     * @return y轴坐标
     *
     * @author jianglei
     */
    public double getY() {
        return this.y;
    }

    /**
     * @param x
     *            x轴坐标
     *
     * @author jianglei
     */
    public void setX(final double x) {
        this.x = x;
    }

    /**
     * @param y
     *            y轴坐标
     *
     * @author jianglei
     */
    public void setY(final double y) {
        this.y = y;
    }

}
