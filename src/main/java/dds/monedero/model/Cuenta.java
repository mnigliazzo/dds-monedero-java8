package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private BigDecimal saldo ;
  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta() {
    this.saldo= new BigDecimal(0);
  }


  public void poner(BigDecimal cuanto) {
    esMontoNegativo(cuanto);

    if (getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }
    this.agregateA(new Movimiento(LocalDate.now(), cuanto, true));
  }

  private void esMontoNegativo(BigDecimal cuanto) {
    if (isNegativo(cuanto) || isCero(cuanto)) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }
  }

  private boolean isNegativo(BigDecimal cuanto) {
    return cuanto.compareTo(new BigDecimal(0)) == -1;
  }
  private boolean isPositivo(BigDecimal cuanto) {
    return cuanto.compareTo(new BigDecimal(0)) == 1;
  }
  private boolean isCero(BigDecimal cuanto) {
    return cuanto.compareTo(new BigDecimal(0)) == 0;
  }

  public void sacar(BigDecimal cuanto) {
    esMontoNegativo(cuanto);
    validarQueNoSaqueMasDeLoQueTiene(cuanto);
    BigDecimal montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    BigDecimal limite = new BigDecimal(1000).subtract(montoExtraidoHoy);
    validarQueNoExcedaLimiteDiario(cuanto, limite);
    this.agregateA(new Movimiento(LocalDate.now(), cuanto, false));
  }

  private void validarQueNoExcedaLimiteDiario(BigDecimal cuanto, BigDecimal limite) {
    if (isPositivo(cuanto.subtract(limite))) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + limite);
    }
  }

  private void validarQueNoSaqueMasDeLoQueTiene(BigDecimal cuanto) {
    if (isNegativo(getSaldo().subtract(cuanto))) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
  }

  public void agregarMovimiento(Movimiento movimiento) {
    movimientos.add(movimiento);
  }

  public BigDecimal getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> movimiento.fueExtraido(fecha))
        .map(Movimiento::getMonto)
        .reduce(BigDecimal.ZERO,BigDecimal::add);
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public BigDecimal getSaldo() {
    return saldo;
  }

  public void setSaldo(BigDecimal saldo) {
    this.saldo = saldo;
  }

  private void agregateA(Movimiento movimiento) {
    this.actualizarSaldo(movimiento);
    this.agregarMovimiento(movimiento);
  }

  private void actualizarSaldo(Movimiento movimiento) {
    if (movimiento.isDeposito()) {
      this.setSaldo(this.getSaldo().add(movimiento.getMonto()));
    } else {
      this.setSaldo(this.getSaldo().subtract(movimiento.getMonto()));
    }
  }

}
