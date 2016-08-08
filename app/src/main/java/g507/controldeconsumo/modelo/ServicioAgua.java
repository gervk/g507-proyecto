package g507.controldeconsumo.modelo;

import java.util.List;

public class ServicioAgua {
    private int id;
    private double zf;
    private double tgdf;
    private double sc;
    private double ef;
    private double st;
    private double aud;
    private double fs;
    private double cl;

    public ServicioAgua(){

    }

    public ServicioAgua(int id, double zf, double tgdf, double sc, double ef, double st,
                        double aud, double fs, double cl){
        this.id = id;
        this.zf = zf;
        this.tgdf = tgdf;
        this.sc = sc;
        this.ef = ef;
        this.st = st;
        this.aud = aud;
        this.fs = fs;
        this.cl = cl;
    }

    private double calcularCosto(List<Consumo> consumos){
        //TODO
        return 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
}
