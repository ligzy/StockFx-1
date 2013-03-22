package com.tintuna.stockfx.application;

import java.util.List;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tintuna.stockfx.exception.StockFxPersistenceException;
import com.tintuna.stockfx.model.ModelFactory;
import com.tintuna.stockfx.persistence.Portfolio;
import com.tintuna.stockfx.persistence.PortfolioStock;
import com.tintuna.stockfx.persistence.Stock;

public class MainApplication extends Application {

	private static final Logger log = LoggerFactory.getLogger(MainApplication.class);
	private static final String PERSISTENCE_UNIT_NAME = "stockFxPU";
	// private static EntityManagerFactory factory;
	// private static EntityManager em;

	private static AppFactory appFactory = new AppFactory();
	private static ModelFactory modelFactory = new ModelFactory();
	private static ServiceFactory serviceFactory = new ServiceFactory();

	public static AppFactory getAppFactory() {
		return appFactory;
	}

	public static ModelFactory getModelFactory() {
		return modelFactory;
	}

	public static ServiceFactory getServiceFactory() {
		return serviceFactory;
	}

	public static void main(String[] args) throws Exception {
		launch(args);
	}

	public void start(Stage stage) throws Exception {
		setDependencies();
		Scene scene = new Scene(appFactory.getMainController().getRoot(), 1000, 700);
		scene.getStylesheets().add("/styles/styles.css");
		stage.setTitle("StockFx");
		stage.setScene(scene);
		stage.show();

		appFactory.getTabManager().setTabPane(appFactory.getMainController().getTabPane());

		databaseDebugPrintout();
		if (getAppFactory().getCrudService().isDatabaseReady()) {
			appFactory.getTabManager().addTabWithNode(TabStandardNames.Portfolios.name(), appFactory.getPortfoliosController());
		}
		// else the only tab open will be the preferences one not allowing users to go any 'deeper'
		clearMessagesHandler();
	}

	/**
	 * As soon as the user 'moves' after a message is displayed it will be cleared. Twould be good to add the message to
	 * an Audit log so its permanently recorded.
	 */
	private void clearMessagesHandler() {
		appFactory.getMainController().getRoot().addEventHandler(Event.ANY, new EventHandler<Event>() {

			@Override
			public void handle(Event arg0) {
				setMessage("");
			}

		});
	}

	private void setDependencies() {
	}

	public static void setMessage(String msg) {
		getAppFactory().getMainController().setMessage(msg);
	}

	// public static EntityManager openTransaction() {
	// factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
	// EntityManager em = factory.createEntityManager();
	// return em;
	// }

	// public static void endTransaction(EntityManager localEm) {
	// localEm.getTransaction().commit();
	// localEm.close();
	// }

	public static void databaseDebugPrintout() {
		// EntityManager em = openTransaction();
		// em.getTransaction().begin();
		// Read the existing entries and write to console
		// Query q = em.createQuery("select t from Portfolio t");
		List<Portfolio> portfolioList = null;
		List<PortfolioStock> stockList = null;
		try {
			portfolioList = getServiceFactory().getPortfolioService().findAll();
			stockList = getServiceFactory().getPortfoliostockService().findAll();
		} catch (StockFxPersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Portfolio todo : portfolioList) {
			System.out.println(todo);
		}
		System.out.println("Size: " + portfolioList.size());

		// Query q2 = em.createQuery("select s from Stock s");
		for (PortfolioStock stock : stockList) {
			System.out.println(stock);
		}
		System.out.println("Size: " + stockList.size());
		// endTransaction(em);
	}
}
