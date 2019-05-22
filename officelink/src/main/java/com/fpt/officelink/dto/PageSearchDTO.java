package com.fpt.officelink.dto;

import java.util.List;

public class PageSearchDTO<T> {
	
	private int maxPage;
	
	private List<T> objList;

	public int getMaxPage() {
		return maxPage;
	}

	public void setMaxPage(int maxPage) {
		this.maxPage = maxPage;
	}

	public List<T> getObjList() {
		return objList;
	}

	public void setObjList(List<T> objList) {
		this.objList = objList;
	}
	
	
}
