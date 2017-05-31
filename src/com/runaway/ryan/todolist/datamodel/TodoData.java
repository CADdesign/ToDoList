package com.runaway.ryan.todolist.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Singleton Class
 * Created by Ryan on 5/29/2017.
 */
public class TodoData {
	private static final TodoData OUR_INSTANCE = new TodoData();
	private static final String fileName = "TodoListItems.txt";
	private ObservableList<TodoItem> todoItems;
	private final DateTimeFormatter formatter;


	public static synchronized TodoData getInstance() {
		return TodoData.OUR_INSTANCE;
	}

	private TodoData() {
		formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
	}

	public ObservableList<TodoItem> getTodoItems() {
		return todoItems;
	}

	public void addTodoItem(TodoItem todoItem) {
		todoItems.add(todoItem);
	}

	public void loadTodoItems() throws IOException{
		todoItems = FXCollections.observableArrayList();
		Path path = Paths.get(fileName);
		try (BufferedReader br = Files.newBufferedReader(path)) {
			String input;

			try {
				while ((input = br.readLine()) != null) {
					String[] itemPieces = input.split("\t");

					String shortDescription = itemPieces[0];
					String details = itemPieces[1];
					String dateString = itemPieces[2];
					LocalDate date = LocalDate.parse(dateString, formatter);

					TodoItem todoItem = new TodoItem(shortDescription, details, date);
					todoItems.add(todoItem);
				}
			} finally {
				if (br != null) {
					br.close();
				}
			}
		}
	}

	public void storeTodoItems() throws IOException{
		Path path = Paths.get(fileName);
		BufferedWriter bw = Files.newBufferedWriter(path);

		try{
			for (TodoItem item : todoItems) {
				bw.write(String.format("%s\t%s\t%s", item.getShortDescription(),
						item.getDetails(),
						item.getDeadline().format(formatter)));
				bw.newLine();
			}

		} finally{
			if((bw!= null)){
				bw.close();
			}

		}
	}

	public void deleteTodoItem(TodoItem item) {
		todoItems.remove(item);
	}

}
