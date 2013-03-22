package com.tintuna.stockfx.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tintuna.stockfx.application.MainApplication;
import com.tintuna.stockfx.persistence.Portfolio;

/**
 * @Entity Portfolios have @Entity Stocks (1+). There is one model of @Entity Portfolios - PortfoliosModel; and there is
 *         one StocksModel for each Portfolio. This class maintains the map mapping each Portfolio to a StocksModel
 * 
 * @author bsmith
 * 
 */
public class PortfolioAssociatedStocks {
	private static final Logger log = LoggerFactory.getLogger(PortfolioAssociatedStocks.class);

	private static Map<String, StocksModel> portfolioStocks = new HashMap<>();

	public static Map<String, StocksModel> getPortfolioStocks() {
		return portfolioStocks;
	}

	public static void setPortfolioStocks(Map<String, StocksModel> passedPortfolioStocks) {
		portfolioStocks = passedPortfolioStocks;
	}

	// TODO - this will probably fail when stock are added to portfolio in some other way
	public static StocksModel getStockModelForAssociatedPortfolio(Portfolio portfolio) {
		if (portfolioStocks.containsKey(portfolio.getName().toLowerCase())) {
			log.debug("Return Stocks model for existing mapped portfolio: " + portfolio.getName());
			return portfolioStocks.get(portfolio.getName().toLowerCase());
		} else {
			log.debug("Return NEW Stocks model for new mapped portfolio: " + portfolio.getName());
			StocksModel s = new StocksModel(portfolio);
			portfolioStocks.put(portfolio.getName().toLowerCase(), s);
			return s;
		}
	}
}
