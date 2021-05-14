package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class MonederoTest {
  private Cuenta cuenta;

  @BeforeEach
  void init() {
    cuenta = new Cuenta();
  }

  @Test
  void ponerValorEnCuentaValido() {
    assertDoesNotThrow(()->cuenta.poner(new BigDecimal(1500)));
  }

  @Test
  void obtenerMovimientosDeCuenta() {
    cuenta.poner(new BigDecimal(1500));
    assertEquals(1,cuenta.getMovimientos().size());
  }
  @Test
  void obtenerMontoExtraido(){
    cuenta.poner(new BigDecimal(100));
    cuenta.sacar(new BigDecimal(50));
    assertEquals(new BigDecimal(50),cuenta.getMontoExtraidoA(LocalDate.now()));
  }

  @Test
  void ponerMontoNegativoException() {
    assertThrows(MontoNegativoException.class, () -> cuenta.poner(new BigDecimal(-1500)));
  }

  @Test
  void esValidoTresDepositos() {
    assertDoesNotThrow(()->{cuenta.poner(new BigDecimal(1500));
    cuenta.poner(new BigDecimal(456));
    cuenta.poner(new BigDecimal(1900));});

  }

  @Test
  void masDeTresDepositosEsInvalido() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
          cuenta.poner(new BigDecimal(1500));
          cuenta.poner(new BigDecimal(456));
          cuenta.poner(new BigDecimal(1900));
          cuenta.poner(new BigDecimal(245));
    });
  }

  @Test
  void extraerMontoValido(){
    cuenta.poner(new BigDecimal(100));
    assertDoesNotThrow(()->cuenta.sacar(new BigDecimal(50)));
  }
  @Test
  void extraerMasQueElSaldoEsInvalido() {
    assertThrows(SaldoMenorException.class, () -> {
          cuenta.setSaldo(new BigDecimal(90));
          cuenta.sacar(new BigDecimal(1001));
    });
  }

  @Test
  public void extraerMasDelLimeteDiarioEsInvalido() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.setSaldo(new BigDecimal(5000));
      cuenta.sacar(new BigDecimal(1001));
    });
  }

  @Test
  public void extraerMontoNegativoEsInvalido() {
    assertThrows(MontoNegativoException.class, () -> cuenta.sacar(new BigDecimal(-500)));
  }

}