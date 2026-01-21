/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.webservicesample.dto;


import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author David Zamora
 */
public class CuentasPorPersona{
    private Integer idCuentasPorPersona;
    private BigDecimal montoFijado;
    private boolean activa;
    private boolean recurrente;
    private Persona idPersona;
    private Cuenta idCuenta;
    private List<GastosPorCuenta> gastosPorCuentaList;

    public CuentasPorPersona() {
    }

    public Integer getIdCuentasPorPersona() {
        return idCuentasPorPersona;
    }

    public void setIdCuentasPorPersona(Integer idCuentasPorPersona) {
        this.idCuentasPorPersona = idCuentasPorPersona;
    }

    public BigDecimal getMontoFijado() {
        return montoFijado;
    }

    public void setMontoFijado(BigDecimal montoFijado) {
        this.montoFijado = montoFijado;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public boolean isRecurrente() {
        return recurrente;
    }

    public void setRecurrente(boolean recurrente) {
        this.recurrente = recurrente;
    }

    public Persona getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Persona idPersona) {
        this.idPersona = idPersona;
    }

    public Cuenta getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(Cuenta idCuenta) {
        this.idCuenta = idCuenta;
    }

    public List<GastosPorCuenta> getGastosPorCuentaList() {
        return gastosPorCuentaList;
    }

    public void setGastosPorCuentaList(List<GastosPorCuenta> gastosPorCuentaList) {
        this.gastosPorCuentaList = gastosPorCuentaList;
    }

    @Override
    public String toString() {
        return "CuentasPorPersonaDTO{" + "idCuentasPorPersona=" + idCuentasPorPersona + ", montoFijado=" + montoFijado + ", activa=" + activa + ", recurrente=" + recurrente + ", idPersona=" + idPersona + ", idCuenta=" + idCuenta + ", gastosPorCuentaList=" + gastosPorCuentaList + '}';
    }
    
}
