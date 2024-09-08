package api.dto;

import java.math.BigDecimal;

public class CurrencyExchangeDto {

	private String from;
	private String to;
	private BigDecimal conversionMultiple;
	private String instancePort;

	public CurrencyExchangeDto() {
		super();
	}

	public CurrencyExchangeDto(String from, String to, BigDecimal exchangeValue) {
		super();
		this.from = from;
		this.to = to;
		this.conversionMultiple = exchangeValue;
	}

	public String getInstancePort() {
		return instancePort;
	}

	public void setInstancePort(String instancePort) {
		this.instancePort = instancePort;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public BigDecimal getConversionMultiple() {
		return conversionMultiple;
	}

	public void setConversionMultiple(BigDecimal conversionMultiple) {
		this.conversionMultiple = conversionMultiple;
	}

}
