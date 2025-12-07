package ec.edu.istq.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * MODELO DE DOMINIO - ESTUDIANTE
 * Representa la entidad Estudiante en el sistema.
 * Implementa Serializable para poder ser transmitida por la red.
 * Sigue el patrón JavaBean (atributos privados, getters/setters, constructor vacío)
 */
public class Estudiante implements Serializable {

    // ==============================================================
    // ATRIBUTOS DE LA CLASE (MAPEO DE COLUMNAS DE LA TABLA)
    // ==============================================================

    private Long id;                    // Identificador único (Primary Key)
    private String cedula;              // Cédula del estudiante (Unique)
    private String nombres;             // Nombres del estudiante
    private String apellidos;           // Apellidos del estudiante
    private String email;               // Correo electrónico institucional
    private String telefono;            // Número de teléfono
    private String carrera;             // Carrera que estudia
    private Integer nivel;              // Nivel académico (1-10)
    private Boolean activo = true;      // Estado activo/inactivo (default: true)
    private Timestamp fechaCreacion;    // Fecha de creación del registro
    private Timestamp fechaActualizacion; // Fecha de última actualización

    // ==============================================================
    // CONSTRUCTORES
    // ==============================================================

    /**
     * CONSTRUCTOR VACÍO (Requerido por JavaBean y frameworks)
     */
    public Estudiante() {
    }

    /**
     * CONSTRUCTOR CON PARÁMETROS BÁSICOS
     * @param id Identificador único
     * @param cedula Cédula del estudiante
     * @param nombres Nombres del estudiante
     * @param apellidos Apellidos del estudiante
     * @param email Correo electrónico
     * @param telefono Número de teléfono
     * @param carrera Carrera de estudio
     * @param nivel Nivel académico
     */
    public Estudiante(Long id, String cedula, String nombres, String apellidos,
                      String email, String telefono, String carrera, Integer nivel) {
        this.id = id;
        this.cedula = cedula;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
        this.carrera = carrera;
        this.nivel = nivel;
        this.activo = true; // Por defecto activo
    }

    // ==============================================================
    // MÉTODOS GETTER Y SETTER (ACCESORES Y MUTADORES)
    // ==============================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Timestamp getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Timestamp fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    // ==============================================================
    // MÉTODOS DE UTILIDAD
    // ==============================================================

    /**
     * Representación en String del objeto Estudiante
     * Útil para logging y debugging
     */
    @Override
    public String toString() {
        return "Estudiante{" +
                "id=" + id +
                ", cedula='" + cedula + '\'' +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", carrera='" + carrera + '\'' +
                ", nivel=" + nivel +
                ", activo=" + activo +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaActualizacion=" + fechaActualizacion +
                '}';
    }
}