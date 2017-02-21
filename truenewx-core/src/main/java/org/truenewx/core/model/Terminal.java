package org.truenewx.core.model;

import org.truenewx.core.enums.Device;
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
	 * 程序类型
	 */
	private Program program;

	/**
	 * @return 设备类型
	 */
	public Device getDevice() {
		return device;
	}

	/**
	 * @param device 设备类型
	 */
	public void setDevice(Device device) {
		this.device = device;
	}

	/**
	 * @return 程序类型
	 */
	public Program getProgram() {
		return program;
	}

	/**
	 * @param program 程序类型
	 */
	public void setProgram(Program program) {
		this.program = program;
	}
}
