/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tfia.dto;

import edu.tfia.entidades.CuentasPorPersona;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author David Zamora
 */
@XmlRootElement(name = "GastosPorPersona" )
public class GastosPorCuentaDTO implements Serializable {
    private Integer idGastosPorCuenta;
    private BigDecimal gasto;
    private Date fechaGasto;
    private String lugarGasto;
    private char formaPago;
    private String comentario;
    private CuentasPorPersona idCuentasPorPersona;

    public GastosPorCuentaDTO() {
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
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.idGastosPorCuenta);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GastosPorCuentaDTO other = (GastosPorCuentaDTO) obj;
        if (!Objects.equals(this.idGastosPorCuenta, other.idGastosPorCuenta)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "GastosPorCuentaDTO{" + "idGastosPorCuenta=" + idGastosPorCuenta + ", gasto=" + gasto + ", fechaGasto=" + fechaGasto + ", lugarGasto=" + lugarGasto + ", formaPago=" + formaPago + ", comentario=" + comentario + ", idCuentasPorPersona=" + idCuentasPorPersona + '}';
    }
    
    
}
