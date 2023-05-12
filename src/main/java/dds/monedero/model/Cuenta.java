package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo = 0;
  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta() {
    saldo = 0;
  }

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }

  public void poner(double cuanto) {
    this.montoNegativo(cuanto);
    if (getMovimientos().stream().filter(movimiento -> movimiento.esDelTipoYFecha(new Deposito(), LocalDate.now())).count() >= 3) { 
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }

    new Movimiento(LocalDate.now(), cuanto, true).agregateA(this);
  }

  public void sacar(double cuanto) {
    this.montoNegativo(cuanto);
    if (getSaldo() < cuanto) {
      throw new SaldoMenorException("No puede sacar mas de " + this.getSaldo() + " $");
    }
    if (cuanto > limiteDiario()) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000 
          + " diarios, límite: " + limiteDiario());
    }
    new Movimiento(LocalDate.now(), cuanto, false).agregateA(this);
  }

  public void agregarMovimiento(LocalDate fecha, double cuanto, boolean esDeposito) {
    Movimiento movimiento = new Movimiento(fecha, cuanto, esDeposito);
    movimientos.add(movimiento);
  }

  public double getMontoExtraidoA(LocalDate fecha) { 
    return movimientos.stream()
    .filter(movimiento -> movimiento.esDelTipoYFecha(new Extraccion(), fecha))
    .mapToDouble(Movimiento::getMonto)
    .sum();
  }

  private void montoNegativo(double monto){
    if (monto <= 0) {
      throw new MontoNegativoException(monto + ": el monto a ingresar debe ser un valor positivo");
    }
  }

  private double limiteDiario(){
    return 1000 - getMontoExtraidoA(LocalDate.now());
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }

}
