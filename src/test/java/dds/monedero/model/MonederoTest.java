package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

public class MonederoTest {
  private Cuenta cuenta;

  @BeforeEach
  void init() {
    cuenta = new Cuenta();
  }

  @Test
  void AgregarMovimiento() {
    Movimiento movimiento = new Movimiento(LocalDate.now(), 200, true);
    movimiento.agregateA(cuenta);
    assertTrue(cuenta.getMovimientos().contains(movimiento));
  }

  @Test
  void Poner() {
    cuenta.poner(1500);
    assertEquals(1500.00, cuenta.getSaldo());
  }

  @Test
  void MasDeTresDepositos() {
          cuenta.poner(1500);
          cuenta.poner(456);
          cuenta.poner(1900);
          assertThrows(MaximaCantidadDepositosException.class, () ->cuenta.poner(245));
  
  }
  @Test
  void MontoExtraido(){
    cuenta.setSaldo(1000);
    cuenta.sacar(300);
    cuenta.sacar(300);
    cuenta.sacar(300);
    assertEquals(900.00, cuenta.getMontoExtraidoA(LocalDate.now()));  
  }
  @Test
  void ExtraerMasQueElSaldo() {
          cuenta.setSaldo(90);
          assertThrows(SaldoMenorException.class, () ->cuenta.sacar(91));
  }

  @Test
  public void ExtraerMasDe1000() {
    cuenta.setSaldo(5000);
    assertThrows(MaximoExtraccionDiarioException.class, () ->cuenta.sacar(1001));
  }

  @Test
  public void ExtraerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.sacar(-500));
  }
  @Test
  void PonerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.poner(-1500));
  }

}