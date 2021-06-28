package com.mmorpg.qx.common.exception;

/**
 * 业务验证错误
 * @author wang ke
 * @since v1.0 2018年3月7日
 *
 */
public class ManagedException extends RuntimeException {

	private static final long serialVersionUID = -5566075318388205571L;

	/** 错误代码 */
	private final int code;
	private String[] params;
	public ManagedException(int code, String... params) {
		super();
		this.code = code;
		this.params = params;
	}

	public ManagedException(int code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	public ManagedException(int code, String message) {
		super(message);
		this.code = code;
	}

	public ManagedException(int code, Throwable cause) {
		super(cause);
		this.code = code;
	}

	/**
	 * 获取错误代码
	 * @return
	 */
	public int getCode() {
		return code;
	}

	public String[] getParams() {
		return params;
	}
}
