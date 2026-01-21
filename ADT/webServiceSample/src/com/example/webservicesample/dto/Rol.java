/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.webservicesample.dto;

/**
 *
 * @author David Zamora
 */
public class Rol {
    private Integer idRol;
    private String nombre;
    private String descripcion;

    public Rol() {
    }

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
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

    @Override
    public String toString() {
        return "RolDTO{" + "idRol=" + idRol + ", nombre=" + nombre + ", descripcion=" + descripcion + '}';
    }
    
    
}
