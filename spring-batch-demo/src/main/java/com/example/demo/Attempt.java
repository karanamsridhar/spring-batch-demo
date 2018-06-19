package com.example.demo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Attempt {

	private File file;
	private boolean success;
	private boolean systemError;
	private List<String> errors;
	
	public Attempt(File file) {
		this.file = file;
		this.success = Boolean.TRUE;
		this.systemError = Boolean.FALSE;
		this.errors = new ArrayList<String>();
	}
	
	
	public boolean isSuccess() {
		return this.success;
	}
	
	public boolean hasSystemError() {
		return this.systemError;
	}
	
	public List<String> getErrors() {
		return this.errors;
	}
	
	public File getFile() {
		return this.file;
	}
	
	public void addError(String error) {
		this.success = Boolean.FALSE;
		this.errors.add(error);
	}
	
	public void addSystemError(String systemError) {
		addError(systemError);
		this.systemError = Boolean.FALSE;
	}
	
	@Override
	public String toString() {
		return "Attempt["+this.file.getName()+ ", " + this.success+"]";
	}
}
