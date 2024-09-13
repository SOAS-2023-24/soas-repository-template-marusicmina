package tradeService;

import org.springframework.data.jpa.repository.JpaRepository;

import tradeService.model.Trade;

public interface CustomTradeServiceRepository extends JpaRepository<Trade, Long>{
	
	Trade findByFromAndToIgnoreCase(String from, String to);
	
}
