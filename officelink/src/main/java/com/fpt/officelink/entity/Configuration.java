package com.fpt.officelink.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Configuration implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private int  id;

	// cron expression
    @NotNull
    @Column
    private String scheduleTime;
    
    @Column
	private boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "workplace_id", nullable = false)
    private Workplace workplace;

    //Getter setter
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getScheduleTime() {
		return scheduleTime;
	}

	public void setScheduleTime(String scheduleTime) {
		this.scheduleTime = scheduleTime;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Workplace getWorkplace() {
		return workplace;
	}

	public void setWorkplace(Workplace workplace) {
		this.workplace = workplace;
	}
}
