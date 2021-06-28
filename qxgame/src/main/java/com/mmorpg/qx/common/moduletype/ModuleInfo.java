package com.mmorpg.qx.common.moduletype;

public class ModuleInfo {

	private int module;
	private int moduleSub;
	private String data;

	public static ModuleInfo valueOf(ModuleType module, SubModuleType type) {
		ModuleInfo info = new ModuleInfo();
		info.module = module.getId();
		info.moduleSub = type.getId();
		return info;
	}

	public static ModuleInfo valueOf(ModuleType module, SubModuleType type, String data) {
		ModuleInfo info = new ModuleInfo();
		info.module = module.getId();
		info.data = data;
		info.moduleSub = type.getId();
		return info;
	}

	public int getModule() {
		return module;
	}

	public void setModule(int module) {
		this.module = module;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getModuleSub() {
		return moduleSub;
	}

	public void setModuleSub(int moduleSub) {
		this.moduleSub = moduleSub;
	}

}
