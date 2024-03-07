package currencyExchange.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import currencyExchange.model.CurrencyExchangeModel;

public interface CurrencyExchangeReposytory extends JpaRepository<CurrencyExchangeModel, Integer> {

	CurrencyExchangeModel findByFromAndTo(String from, String to);
}
