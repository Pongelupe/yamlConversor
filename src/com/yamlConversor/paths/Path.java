package com.yamlConversor.paths;

public class Path {

	private String path;
	private String requestType;
	private String objDefinitionRequest;
	private String objDefinitionResponse;

	public Path(String path, String objRequest, String objResponse) {
		this.path = path;
		this.objDefinitionRequest = objRequest;
		this.objDefinitionResponse = objResponse;
		this.requestType = "post";
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getObjDefinitionRequest() {
		return objDefinitionRequest;
	}

	public void setObjDefinitionRequest(String objDefinitionRequest) {
		this.objDefinitionRequest = objDefinitionRequest;
	}

	public String getObjDefinitionResponse() {
		return objDefinitionResponse;
	}

	public void setObjDefinitionResponse(String objDefinitionResponse) {
		this.objDefinitionResponse = objDefinitionResponse;
	}

}
