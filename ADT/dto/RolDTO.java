/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tfia.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author David Zamora
 */
@XmlRootElement(name = "Rol" )
public class RolDTO implements Serializable {
    private Integer idRol;
    private String nombre;
    private String descripcion;

    public RolDTO() {
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
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.idRol);
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
        final RolDTO other = (RolDTO) obj;
        if (!Objects.equals(this.idRol, other.idRol)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RolDTO{" + "idRol=" + idRol + ", nombre=" + nombre + ", descripcion=" + descripcion + '}';
    }
    
    
}
