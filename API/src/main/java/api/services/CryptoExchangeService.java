package api.services;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface CryptoExchangeService {
    @GetMapping("/crypto-exchange/from/{from}/to/{to}")
    public ResponseEntity<?> getExchange(@PathVariable String from, @PathVariable String to);
}
