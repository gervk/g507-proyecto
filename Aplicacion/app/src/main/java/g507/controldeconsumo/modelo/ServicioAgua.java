package g507.controldeconsumo.modelo;

import java.util.List;

public class ServicioAgua {
    private int id;
    private double k;
    private double zf;
    private double tgdf;
    private double sc;
    private double ef;
    private double st;
    private double aud;
    private double fs;
    private double cl;
    private String fecUltFact;

    public ServicioAgua(){

    }

    public ServicioAgua(int id, double k, double zf, double tgdf, double sc, double ef, double st,
                        double aud, double fs, double cl){
        this.id = id;
        this.k = k;
        this.zf = zf;
        this.tgdf = tgdf;
        this.sc = sc;
        this.ef = ef;
        this.st = st;
        this.aud = aud;
        this.fs = fs;
        this.cl = cl;
    }

    public double calcularCosto(Long dias, Double consumoRegistrado){
        Double cargoFijo = (k*(zf/1000)*(sc*ef+(st/10))+(aud*k*fs)*dias.intValue());
        Double precio;
        if(fs ==1){
            precio = 0.3288;
        }else{
            precio = 0.6566;
        }
        Double cargoVariable = (consumoRegistrado - cl)* precio* k*fs;
        return efectuarImpuestos(cargoFijo+cargoVariable);
    }

    private Double efectuarImpuestos(Double valorAntesImpuestos){
        Double valorDespImpuestos = 0.0;
        Double eras = 0.0155;
        Double apla= 0.0112;
        Double IVA = 0.21;

        valorDespImpuestos = valorAntesImpuestos*eras*apla*IVA;
        return valorDespImpuestos;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getK(){return k;}

    public void setK(double k){this.k = k;}

    public double getZf() {
        return zf;
    }

    public void setZf(double zf) {
        this.zf = zf;
    }

    public double getTgdf() {
        return tgdf;
    }

    public void setTgdf(double tgdf) {
        this.tgdf = tgdf;
    }

    public double getSc() {
        return sc;
    }

    public void setSc(double sc) {
        this.sc = sc;
    }

    public double getEf() {
        return ef;
    }

    public void setEf(double ef) {
        this.ef = ef;
    }

    public double getSt() {
        return st;
    }

    public void setSt(double st) {
        this.st = st;
    }

    public double getAud() {
        return aud;
    }

    public void setAud(double aud) {
        this.aud = aud;
    }

    public double getFs() {
        return fs;
    }

    public void setFs(double fs) {
        this.fs = fs;
    }

    public double getCl() {
        return cl;
    }

    public void setCl(double cl) {
        this.cl = cl;
    }

    public String getFecFact(){return fecUltFact;}

    public void setFecFact(String fecFact){this.fecUltFact = fecFact;}
}
