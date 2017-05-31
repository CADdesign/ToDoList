package com.runaway.ryan.todolist;

import com.runaway.ryan.todolist.datamodel.TodoData;
import com.runaway.ryan.todolist.datamodel.TodoItem;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

/**
 * This controls the dialog that displays
 * Created by Ryan on 5/29/2017.
 */
public class DialogController {
	@FXML
	private TextField shortDescriptionField;
	@FXML
	private TextArea detailDescriptionField;
	@FXML
	private DatePicker deadlinePicker;

	TodoItem processResults(){
		String shortDescription = shortDescriptionField.getText().trim();
		String detailDescription = detailDescriptionField.getText().trim();
		LocalDate deadlineValue = deadlinePicker.getValue();
		TodoItem newItem = new TodoItem(shortDescription, detailDescription, deadlineValue);
		TodoData.getInstance().addTodoItem(newItem);
		return newItem;

	}
}
