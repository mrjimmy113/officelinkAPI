package com.fpt.officelink.entity;


import java.io.Serializable;
import java.sql.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Workplace implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;

    @Column(updatable = false)
    private Date dateCreated;

    @Column
    private Date dateModified;

    @Column
    private boolean isDeleted;

    @OneToMany(mappedBy = "workplace")
    private Set<Configuration> configurations;

    @OneToMany(mappedBy = "workplace")
    private Set<Account> accounts;

    @OneToMany(mappedBy = "workplace")
    private Set<Department> departments;

    @OneToMany(mappedBy = "workplace")
    private Set<Survey> surveys;

    @OneToMany(mappedBy = "workplace")
    private Set<Location> location;

    @OneToMany(mappedBy = "workplace")
    private Set<News> news;
    
    @OneToMany(mappedBy = "workplace")
    private Set<WordCloudFilter> filters;

    //Getter setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Set<Configuration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(Set<Configuration> configurations) {
        this.configurations = configurations;
    }

    public Set<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<Account> accounts) {
        this.accounts = accounts;
    }

    public Set<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(Set<Department> departments) {
        this.departments = departments;
    }

    public Set<Survey> getSurveys() {
        return surveys;
    }

    public void setSurveys(Set<Survey> surveys) {
        this.surveys = surveys;
    }

    public Set<Location> getLocation() {
        return location;
    }

    public void setLocation(Set<Location> location) {
        this.location = location;
    }

    public Set<News> getNews() {
        return news;
    }

    public void setNews(Set<News> news) {
        this.news = news;
    }

	public Set<WordCloudFilter> getFilters() {
		return filters;
	}

	public void setFilters(Set<WordCloudFilter> filters) {
		this.filters = filters;
	}
    
    

}
