
package com.rainbowagri.uaservercommunicator;


@SuppressWarnings("hiding")
public interface AsyncTaskCompleteListener<XMLRequestAndResponseData> {
	public void onTaskComplete(XMLRequestAndResponseData result);
	public void onTaskCompleteForDownloadAudio(XMLRequestAndResponseData result);
	 
}
