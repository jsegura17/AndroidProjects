/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tfia.dto;

import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author David Zamora
 */
@XmlRootElement(name = "Rol" )
public class CuentaDTO {
    private Integer idCuenta;
    private String nombre;
    private String descripcion;
    private boolean tipo;

    public CuentaDTO() {
    }

    public Integer getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(Integer idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isTipo() {
        return tipo;
    }

    public void setTipo(boolean tipo) {
        this.tipo = tipo;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.idCuenta);
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
        final CuentaDTO other = (CuentaDTO) obj;
        if (!Objects.equals(this.idCuenta, other.idCuenta)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CuentaDTO{" + "idCuenta=" + idCuenta + ", nombre=" + nombre + ", descripcion=" + descripcion + ", tipo=" + tipo + '}';
    }
    
}
