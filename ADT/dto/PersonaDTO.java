/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tfia.dto;

import edu.tfia.entidades.CuentasPorPersona;
import edu.tfia.entidades.Rol;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author David Zamora
 */
@XmlRootElement(name = "Persona" )
public class PersonaDTO implements Serializable {
    private Integer idPersona;
    private String nombre;
    private String apellidos;
    private String correo;
    private String clave;
    private char tipoPlanificador;
    private Date fechaInicio;
    private Date fechaUltimoReporte;
    private Rol idRol;
    private List<CuentasPorPersona> cuentasPorPersonaList;

    public PersonaDTO() {
    }

    public Integer getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Integer idPersona) {
        this.idPersona = idPersona;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public char getTipoPlanificador() {
        return tipoPlanificador;
    }

    public void setTipoPlanificador(char tipoPlanificador) {
        this.tipoPlanificador = tipoPlanificador;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaUltimoReporte() {
        return fechaUltimoReporte;
    }

    public void setFechaUltimoReporte(Date fechaUltimoReporte) {
        this.fechaUltimoReporte = fechaUltimoReporte;
    }
    
    public Rol getIdRol() {
        return this.idRol;
    }
    
    public void setIdRol(Rol rol) {
        this.idRol = rol;
    }
    
    @XmlTransient
    public List<CuentasPorPersona> getCuentasPorPersonaList() {
        return cuentasPorPersonaList;
    }

    public void setCuentasPorPersonaList(List<CuentasPorPersona> cuentasPorPersonaList) {
        this.cuentasPorPersonaList = cuentasPorPersonaList;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + Objects.hashCode(this.idPersona);
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
        final PersonaDTO other = (PersonaDTO) obj;
        if (!Objects.equals(this.idPersona, other.idPersona)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PersonaDTO{" + "idPersona=" + idPersona + ", nombre=" + nombre + ", apellidos=" + apellidos + ", correo=" + correo + ", clave=" + clave + ", tipoPlanificador=" + tipoPlanificador + ", fechaInicio=" + fechaInicio + ", fechaUltimoReporte=" + fechaUltimoReporte + '}';
    }
    
    
}
