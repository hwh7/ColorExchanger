package com.example.colorexchanger;

public class NativeCall {
	public native String add();
	
	static {
		System.loadLibrary("test");
	}
}
