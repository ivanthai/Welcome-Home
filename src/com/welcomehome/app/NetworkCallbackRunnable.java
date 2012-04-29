package com.welcomehome.app;

public interface NetworkCallbackRunnable<InputType> {
	public void onSuccess(InputType input);
	public void onFailure(Throwable e);
}
