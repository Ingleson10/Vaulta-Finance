package br.com.vaultfinance.api.domain.exception;

public class NotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public NotFoundException(String message) {
    super(message);
  }
}
