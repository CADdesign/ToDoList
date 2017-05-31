package com.runaway.ryan.todolist;

import com.runaway.ryan.todolist.datamodel.TodoData;
import com.runaway.ryan.todolist.datamodel.TodoItem;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * This is the controller of the main display
 */
public class Controller {
	@FXML
	private ListView<TodoItem> todoListView;
	@FXML
	private TextArea todoDetail;
	@FXML
	private Label todoDeadline;
	@FXML
	private BorderPane mainBorderPane;
	@FXML
	private ContextMenu listContextMenu;
	@FXML
	private ToggleButton filterToogleButton;

	private FilteredList<TodoItem> filteredList;
	private Predicate<TodoItem> wantAllItems;
	private Predicate<TodoItem> wantTodayItems;

	public void initialize(){

		listContextMenu = new ContextMenu();
		MenuItem deleteMenuItem = new MenuItem("Delete");
		deleteMenuItem.setOnAction(event -> {
			TodoItem item = todoListView.getSelectionModel().getSelectedItem();
			deleteItem(item);
		});

		listContextMenu.getItems().add(deleteMenuItem);
		todoListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue != null){
				TodoItem item = todoListView.getSelectionModel().getSelectedItem();
				todoDetail.setText(item.getDetails());
				LocalDate itemDeadline = item.getDeadline();
				String formattedDeadline = item.formatDeadlineAmerican(itemDeadline);
				todoDetail.setText(item.getDetails());
				todoDeadline.setText(formattedDeadline);
			}
		});

		wantAllItems = todoItem -> true;

		wantTodayItems = todoItem -> (todoItem.getDeadline().equals(LocalDate.now()));
		filteredList = new FilteredList<>(TodoData.getInstance().getTodoItems(), wantAllItems);

		SortedList<TodoItem> sortedList = new SortedList<>(filteredList,
				Comparator.comparing(TodoItem::getDeadline));

		todoListView.setItems(sortedList);
		todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		todoListView.getSelectionModel().selectFirst();

		todoListView.setCellFactory(new Callback<ListView<TodoItem>, ListCell<TodoItem>>() {
			@Override
			public ListCell<TodoItem> call(ListView<TodoItem> param) {
				ListCell<TodoItem> cell = new ListCell<TodoItem>(){
					@Override
					protected void updateItem(TodoItem item, boolean empty) {
						super.updateItem(item, empty);
						if(empty){
							setText(null);
						} else{
							setText(item.getShortDescription());
							if(item.getDeadline().isBefore(LocalDate.now().plusDays(1))){
								//I added the setFont..wanted it to stand-out more.
								setFont(Font.font("Times New Roman", FontWeight.BOLD, 16));
								setTextFill(Color.RED);
							}
							else if(item.getDeadline().equals(LocalDate.now().plusDays(1))){

								setFont(Font.font("Times New Roman", FontWeight.NORMAL, 16));
								setTextFill(Color.PURPLE.brighter());
							}
						}
					}
				};

				cell.emptyProperty().addListener(
						(ObservableValue<? extends Boolean> obs, Boolean wasEmpty, Boolean isNowEmpty) ->
						{
							if(isNowEmpty){
								cell.setContextMenu(null);
							}
							else{
								cell.setContextMenu(listContextMenu);
							}
						}
				);
				return cell;
			}
		});

	}

	private void deleteItem(TodoItem item) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Delete Todo Item");
		alert.setHeaderText("Delete Item: " + item.getShortDescription());
		alert.setContentText("Are you sure?, Press OK to confirm, or cancel to back out).");
		Optional<ButtonType> result = alert.showAndWait();

		if(result.isPresent() && (result.get() == ButtonType.OK)){
			TodoData.getInstance().deleteTodoItem(item);
		}
	}

	@FXML
	public void showNewItemDialog(){
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.initOwner(mainBorderPane.getScene().getWindow());
		dialog.setTitle("Add New Todo Item");
		dialog.setHeaderText("Use this dialog to create a new item");
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));
		try{
			dialog.getDialogPane().setContent(fxmlLoader.load());
		} catch(IOException e){
			System.out.println("Couldn't load the dialog");
			e.printStackTrace();
		}

		dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

		Optional<ButtonType> result = dialog.showAndWait();
		if(result.isPresent() && result.get()==ButtonType.OK){
			DialogController controller = fxmlLoader.getController();
			TodoItem newItem = controller.processResults();
			todoListView.getSelectionModel().select(newItem);
		}
	}


	@FXML
	public void handleKeyPressed(KeyEvent keyEvent) {
			TodoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
			if(selectedItem != null){
				if(keyEvent.getCode().equals(KeyCode.DELETE)){
					deleteItem(selectedItem);
				}
			}
	}
	@FXML
	public void handleFilterButton() {
		TodoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
		if(filterToogleButton.isSelected()){
			filteredList.setPredicate(wantTodayItems);
			if(filteredList.isEmpty()){
				todoDetail.clear();
				todoDeadline.setText("");
			} else if(filteredList.contains(selectedItem)){
				todoListView.getSelectionModel().select(selectedItem);
			} else{
				todoListView.getSelectionModel().selectFirst();
			}
		}
		else{
			filteredList.setPredicate(wantAllItems);
			todoListView.getSelectionModel().select(selectedItem);
		}

	}

	@FXML
	public void exitApplication() {
		Platform.exit();

	}
}
