package com.runaway.ryan.todolist.datamodel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by Ryan on 5/29/2017.
 * The actual item.
 */
public class TodoItem {
	private final String shortDescription;
	private final String details;
	private final LocalDate deadline;

	public TodoItem(String shortDescription, String details, LocalDate deadline) {
		this.shortDescription = shortDescription;
		this.details = details;
		this.deadline = deadline;
	}

	public String getShortDescription() {
		return shortDescription;
	}

//	public void setShortDescription(String shortDescription) {
//		this.shortDescription = shortDescription;
//	}

	public String getDetails() {
		return details;
	}

//	public void setDetails(String details) {
//		this.details = details;
//	}

	public LocalDate getDeadline() {
		return deadline;
	}

//	public void setDeadline(LocalDate deadline) {
//		this.deadline = deadline;
//	}

	//The ability to format the date to US style.
	public String formatDeadlineAmerican(LocalDate deadline){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
		return formatter.format(deadline);
	}
}
