package br.com.vaultfinance.api.domain.lancamento;

import java.time.LocalDate;

public class PeriodoUtils {

  private PeriodoUtils() {}

  public static LocalDate[] resolver(PeriodoPreset periodo) {
    LocalDate hoje = LocalDate.now();

    if (periodo == null || periodo == PeriodoPreset.ALL) {
      return new LocalDate[]{null, null};
    }

    return switch (periodo) {
      case MONTH -> new LocalDate[]{
        hoje.withDayOfMonth(1),
        hoje.withDayOfMonth(hoje.lengthOfMonth())
      };
      case LAST_30_DAYS -> new LocalDate[]{hoje.minusDays(30), hoje};
      case YEAR -> new LocalDate[]{
        hoje.withDayOfYear(1),
        hoje.withDayOfYear(hoje.lengthOfYear())
      };
      case ALL -> new LocalDate[]{null, null};
    };
  }
}
