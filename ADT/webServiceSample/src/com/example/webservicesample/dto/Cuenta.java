/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.webservicesample.dto;

/**
 *
 * @author David Zamora
 */

public class Cuenta {
    private Integer idCuenta;
    private String nombre;
    private String descripcion;
    private boolean tipo;

    public Cuenta() {
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
    public String toString() {
        return "CuentaDTO{" + "idCuenta=" + idCuenta + ", nombre=" + nombre + ", descripcion=" + descripcion + ", tipo=" + tipo + '}';
    }
    
}
