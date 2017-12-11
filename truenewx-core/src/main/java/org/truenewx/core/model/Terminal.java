package org.truenewx.core.model;

import org.truenewx.core.enums.Device;
import org.truenewx.core.enums.OS;
import org.truenewx.core.enums.Program;

/**
 * 终端类型
 *
 * @author liuzhiyi
 * @since JDK 1.8
 */
public class Terminal {

    /**
     * 设备类型
     */
    private Device device;

    /**
     * 操作系统
     */
    private OS os;

    /**
     * 程序类型
     */
    private Program program;

    /**
     *
     * @return 操作系统
     */
    public OS getOs() {
        return this.os;
    }

    /**
     *
     * @param os
     *            操作系统
     */
    public void setOs(final OS os) {
        this.os = os;
    }

    /**
     * @return 设备类型
     */
    public Device getDevice() {
        return this.device;
    }

    /**
     * @param device
     *            设备类型
     */
    public void setDevice(final Device device) {
        this.device = device;
    }

    /**
     * @return 程序类型
     */
    public Program getProgram() {
        return this.program;
    }

    /**
     * @param program
     *            程序类型
     */
    public void setProgram(final Program program) {
        this.program = program;
    }
}
