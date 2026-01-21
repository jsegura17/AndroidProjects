/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.webservicesample.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author David Zamora
 */
public class GastosPorCuenta{
    private Integer idGastosPorCuenta;
    private BigDecimal gasto;
    private Date fechaGasto;
    private String lugarGasto;
    private char formaPago;
    private String comentario;
    private CuentasPorPersona idCuentasPorPersona;

    public GastosPorCuenta() {
    }

    public Integer getIdGastosPorCuenta() {
        return idGastosPorCuenta;
    }

    public void setIdGastosPorCuenta(Integer idGastosPorCuenta) {
        this.idGastosPorCuenta = idGastosPorCuenta;
    }

    public BigDecimal getGasto() {
        return gasto;
    }

    public void setGasto(BigDecimal gasto) {
        this.gasto = gasto;
    }

    public Date getFechaGasto() {
        return fechaGasto;
    }

    public void setFechaGasto(Date fechaGasto) {
        this.fechaGasto = fechaGasto;
    }

    public String getLugarGasto() {
        return lugarGasto;
    }

    public void setLugarGasto(String lugarGasto) {
        this.lugarGasto = lugarGasto;
    }

    public char getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(char formaPago) {
        this.formaPago = formaPago;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public CuentasPorPersona getIdCuentasPorPersona() {
        return idCuentasPorPersona;
    }

    public void setIdCuentasPorPersona(CuentasPorPersona idCuentasPorPersona) {
        this.idCuentasPorPersona = idCuentasPorPersona;
    }
    
    @Override
    public String toString() {
        return "GastosPorCuentaDTO{" + "idGastosPorCuenta=" + idGastosPorCuenta + ", gasto=" + gasto + ", fechaGasto=" + fechaGasto + ", lugarGasto=" + lugarGasto + ", formaPago=" + formaPago + ", comentario=" + comentario + ", idCuentasPorPersona=" + idCuentasPorPersona + '}';
    }
    
    
}
