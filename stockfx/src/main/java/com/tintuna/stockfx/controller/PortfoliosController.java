package com.tintuna.stockfx.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import com.tintuna.stockfx.application.AppFactory;
import com.tintuna.stockfx.application.MainApplication;
import com.tintuna.stockfx.application.TabStandardNames;
import com.tintuna.stockfx.persistence.Portfolio;
import com.tintuna.stockfx.persistence.Stock;
import com.tintuna.stockfx.util.TabManagerParameters;

public class PortfoliosController extends BorderPane implements Initializable, Controller {
	private AppFactory appFactory;
	@FXML
	private Parent root;
	@FXML
	private VBox portfolioList;
	@FXML
	private TableView<Portfolio> portfolioTable;
	@FXML
	private TableView<Stock> stockTable;

	@FXML
	private GridPane gridPane;
	@FXML
	private TabPane tabPane;
	@FXML
	private Button newPortfolioButton;
	@FXML
	private Button newStockButton;
	@FXML
	private Label messageBox;

	public PortfoliosController(AppFactory controllerFactory) {
		this.appFactory = controllerFactory;
		FXMLLoader loader = null;
		try {
			loader = new FXMLLoader(getClass().getResource("/fxml/Portfolios.fxml"));
			loader.setRoot(this);
			loader.setController(this);
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException("Unable to load Main.fxml", e);
		}
	}

	public Parent getRoot() {
		return root;
	}

	public VBox getPortfolioList() {
		return portfolioList;
	}

	public void setPortfolioList(VBox portfolioList) {
		this.portfolioList = portfolioList;
	}

	public TableView<Portfolio> getPortfolioTable() {
		return portfolioTable;
	}

	public void setPortfolioTable(TableView<Portfolio> portfolioTable) {
		this.portfolioTable = portfolioTable;
	}

	public GridPane getGridPane() {
		return gridPane;
	}

	public void setGridPane(GridPane gridPane) {
		this.gridPane = gridPane;
	}

	public TabPane getTabPane() {
		return tabPane;
	}

	public void setTabPane(TabPane tabPane) {
		this.tabPane = tabPane;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializePortfolioTable();
		initializeStockTable();
		initializeNewPortfolioButton();
		initializeNewStockButton();
	}

	private void initializePortfolioTable() {
		TableColumn<Portfolio, String> col = new TableColumn<>("Portfolio");
		portfolioTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		col.setCellValueFactory(new PropertyValueFactory<Portfolio, String>("Name"));
		col.setText("Name");
		initializePortfolioTableData();
		portfolioTable.getColumns().clear();
		portfolioTable.getColumns().add(col);
		portfolioTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Portfolio>() {
			@Override
			public void changed(ObservableValue<? extends Portfolio> arg0, Portfolio arg1, Portfolio arg2) {
				if (arg2 != null && arg2 instanceof Portfolio) {
					MainApplication.getModelFactory().getPortfoliosModel().setSelected(arg2);
					newStockButton.setDisable(false);
					setStockItems();
				}
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initializePortfolioTableData() {
		MainApplication.getModelFactory().getPortfoliosModel().addPortfoliosListener(new ListChangeListener() {

			@SuppressWarnings("rawtypes")
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change c) {
				portfolioTable.setItems(MainApplication.getModelFactory().getPortfoliosModel().getPortfolios());
			}

		});
		portfolioTable.setItems(MainApplication.getModelFactory().getPortfoliosModel().getPortfolios());
	}

	private void initializeStockTable() {
		// adding something to see
		stockTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		TableColumn<Stock, String> colSymbol = new TableColumn<>("Symbol");
		colSymbol.setCellValueFactory(new PropertyValueFactory<Stock, String>("Symbol"));
		colSymbol.setText("Symbol");
		TableColumn<Stock, String> colCompany = new TableColumn<>("CompanyName");
		colCompany.setCellValueFactory(new PropertyValueFactory<Stock, String>("CompanyName"));
		colCompany.setText("CompanyName");
		initializeStockTableData();
		stockTable.getColumns().clear();
		stockTable.getColumns().addAll(colSymbol, colCompany);
		stockTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Stock>() {
			@Override
			public void changed(ObservableValue<? extends Stock> arg0, Stock arg1, Stock arg2) {
				if (arg2 != null) {
					System.out.println("Stock CHANGED...to:" + arg2.getId() + " = " + arg2.getSymbol());
				} else {
					System.out.println("Stock CHANGED...to: well arg2 is null");
				}
				if (arg2 != null && arg2 instanceof Stock) {
					MainApplication.getModelFactory()
							.getStocksModel(MainApplication.getModelFactory().getPortfoliosModel().getSelected())
							.setSelected(arg2);
				}
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initializeStockTableData() {
		MainApplication.getModelFactory().getPortfoliosModel().addPortfoliosListener(new ListChangeListener() {

			@SuppressWarnings("rawtypes")
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change c) {
				System.out.println("onChanged table data");
				setStockItems();
			}
		});
		setStockItems();
	}

	private void setStockItems() {
		if (MainApplication.getModelFactory().getPortfoliosModel().getSelected() != null) {
			stockTable
					.setItems(MainApplication.getModelFactory().getPortfoliosModel().getPortfoliosStocksForSelected());
		}
	}

	private void initializeNewPortfolioButton() {
		newPortfolioButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				// MainApplication.getModelFactory().getPortfolios().newPortfolio();
				newPortfolio();
			}
		});
	}

	protected void newPortfolio() {
		appFactory.getTabManager().addTabWithNode(TabStandardNames.Portfolio.name(),
				appFactory.getPortfolioController(),
				TabManagerParameters.startParams().insertAfter(TabStandardNames.Portfolios.name()).openNotAdd(true));
	}

	private void initializeNewStockButton() {
		newStockButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				newStockForPortfolio();
			}
		});
		newStockButton.setDisable(true);
	}

	protected void newStockForPortfolio() {
		appFactory.getTabManager().addTabWithNode(TabStandardNames.Stock.name(), appFactory.getStockController(),
				TabManagerParameters.startParams().insertAfter(TabStandardNames.Portfolios.name()).openNotAdd(true));
	}
}
