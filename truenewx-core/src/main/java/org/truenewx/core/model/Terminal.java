package org.truenewx.core.model;

import org.apache.commons.lang3.EnumUtils;
import org.truenewx.core.Strings;
import org.truenewx.core.enums.Device;
import org.truenewx.core.enums.OS;
import org.truenewx.core.enums.Program;
import org.truenewx.core.util.ArrayUtil;

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

    public Terminal(final Device device, final OS os, final Program program) {
        this.device = device;
        this.os = os;
        this.program = program;
    }

    public Terminal(final String s) {
        final String[] array = s.split(Strings.DOT);
        this.device = EnumUtils.getEnum(Device.class, ArrayUtil.get(array, 0));
        this.os = EnumUtils.getEnum(OS.class, ArrayUtil.get(array, 1));
        this.program = EnumUtils.getEnum(Program.class, ArrayUtil.get(array, 2));
    }

    /**
     * @return 设备类型
     */
    public Device getDevice() {
        return this.device;
    }

    /**
     *
     * @return 操作系统
     */
    public OS getOs() {
        return this.os;
    }

    /**
     * @return 程序类型
     */
    public Program getProgram() {
        return this.program;
    }

    public boolean supports(final Terminal terminal) {
        // 设备属性为null，视为支持所有设备
        if (this.device != null && this.device != terminal.device) {
            return false;
        }
        // 操作系统属性为null，视为支持所有操作系统
        if (this.os != null && this.os != terminal.os) {
            return false;
        }
        // 程序类型属性为null，视为支持所有程序类型
        if (this.program != null && this.program != terminal.program) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.device != null) {
            sb.append(this.device.name());
        }
        sb.append(Strings.DOT);
        if (this.os != null) {
            sb.append(this.os.name());
        }
        sb.append(Strings.DOT);
        if (this.program != null) {
            sb.append(this.program.name());
        }
        return sb.toString();
    }

}
