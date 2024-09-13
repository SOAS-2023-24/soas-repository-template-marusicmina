package api.services;

import api.dto.CryptoConversion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

public interface CryptoConversionService {
    @GetMapping("/crypto-conversion?from={from}&to={to}&quantity={quantity}")
    public CryptoConversion getConversion
            (@PathVariable String from, @PathVariable String to, @PathVariable double quantity);

    @GetMapping("/crypto-conversion-feign")
    public ResponseEntity<?> getConversionFeign(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam double quantity,
            @RequestHeader("Authorization") String authorizationHeader);
}
