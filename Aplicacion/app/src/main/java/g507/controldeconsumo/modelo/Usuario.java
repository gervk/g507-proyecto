package g507.controldeconsumo.modelo;

import java.util.List;

public class Usuario {
    private String username;
    private String password;
    private String mail;
    private String ciudad;
    private int idPregSeguridad;
    private String respPregSeguridad;
    private ServicioAgua servicioAgua;
    private ServicioElectricidad servicioElectricidad;
    private int codigoArduino;
    private List<Consumo> consumos;
    private List<Estadistica> estadisticas;

    public Usuario() {

    }

    public Usuario(String username, String pass, String ciudad, int idPregSeguridad, String respPreg,
                   ServicioAgua servAgua, ServicioElectricidad servElec) {
        this.username = username;
        this.password = pass;
        this.ciudad = ciudad;
        this.idPregSeguridad = idPregSeguridad;
        this.respPregSeguridad = respPreg;
        this.servicioAgua = servAgua;
        this.servicioElectricidad = servElec;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public int getIdPregSeguridad() {
        return idPregSeguridad;
    }

    public void setIdPregSeguridad(int idPregSeguridad) {
        this.idPregSeguridad = idPregSeguridad;
    }

    public String getRespPregSeguridad() {
        return respPregSeguridad;
    }

    public void setRespPregSeguridad(String respPregSeguridad) {
        this.respPregSeguridad = respPregSeguridad;
    }

    public ServicioAgua getServicioAgua() {
        return servicioAgua;
    }

    public void setServicioAgua(ServicioAgua servicioAgua) {
        this.servicioAgua = servicioAgua;
    }

    public ServicioElectricidad getServicioElectricidad() {
        return servicioElectricidad;
    }

    public void setServicioElectricidad(ServicioElectricidad servicioElectricidad) {
        this.servicioElectricidad = servicioElectricidad;
    }

    public int getCodigoArduino() {
        return codigoArduino;
    }

    public void setCodigoArduino(int codigoArduino) {
        this.codigoArduino = codigoArduino;
    }

    public List<Consumo> getConsumos() {
        return consumos;
    }

    public void setConsumos(List<Consumo> consumos) {
        this.consumos = consumos;
    }

    public List<Estadistica> getEstadisticas() {
        return estadisticas;
    }

    public void setEstadisticas(List<Estadistica> estadisticas) {
        this.estadisticas = estadisticas;
    }
}