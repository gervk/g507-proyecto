package g507.controldeconsumo.modelo;


import java.util.List;

public class ServicioElectricidad {
    private int id;
    private EmpresaElec empresa;
    private Double cargo_fijo;
    private Double cargo_variable;
    private Double a_10_20_fijo ;
    private Double a_10_20_variable;
    private Double a_mas_20_fijo;
    private Double a_mas_20_variable;
    private String fecUltFact;
    private String fecPrimerConsumo;
    private Double primerConsumo;
    private boolean ahorro = false;
    private boolean penalizado = false;

    public ServicioElectricidad (){}

    public ServicioElectricidad(Double cargo_fijo, Double cargo_variable, Double a_10_20_fijo, Double a_10_20_variable, Double
            a_mas_20_fijo, Double a_mas_20_variable){

        this.cargo_fijo = cargo_fijo;
        this.cargo_variable = cargo_variable;
        this.a_10_20_fijo = a_10_20_fijo;
        this.a_10_20_variable = a_10_20_variable;
        this.a_mas_20_fijo = a_mas_20_fijo;
        this.a_mas_20_variable = a_mas_20_variable;
    }

    public double calcularCosto(Double consumoActual, Double consumoAnterior, Integer dias){
        Double totalAntesImpuestos = 0.0;

        if(consumoAnterior == -1.00){
            totalAntesImpuestos = this.getCargo_fijo()+this.getCargo_variable()*dias;
        }else{
            if(consumoActual < consumoAnterior*0.8){
                totalAntesImpuestos = this.getA_mas_20_fijo() + this.getA_mas_20_variable()*dias;
                this.setAhorro(true);
            }else{
                if(consumoActual < consumoAnterior*0.9 ){
                    totalAntesImpuestos =  this.getA_10_20_fijo() + this.getA_10_20_variable()*dias;
                    this.setAhorro(true);
                }else{
                    if(consumoActual< 300){
                        if(consumoActual > consumoAnterior*1.9){
                            totalAntesImpuestos = this.getCargo_fijo()+this.getCargo_variable()*dias*(consumoActual - (consumoAnterior*1.9));
                            this.setPenalizado(true);
                        }else {
                            totalAntesImpuestos = this.getCargo_fijo()+this.getCargo_variable()*dias;
                        }
                    }
                }
            }
        }

        Double totalAPagar = efectuarImpuestos(totalAntesImpuestos);
        return totalAPagar;
    }

    private Double efectuarImpuestos(Double valorAntesImpuestos){
        Double valorDespImpuestos;
        Double IVA = 0.21;
        Double contMunicipal = 0.064;
        Double fondoObras = 0.055;
        Double impServElec = 0.1;

        double impuestos = valorAntesImpuestos * IVA + valorAntesImpuestos * contMunicipal +
                valorAntesImpuestos * fondoObras + valorAntesImpuestos * impServElec;

        valorDespImpuestos = valorAntesImpuestos + impuestos;

        return valorDespImpuestos;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EmpresaElec getEmpresa() {
        return empresa;
    }

    public void setEmpresa(EmpresaElec empresa) {
        this.empresa = empresa;
    }

    public Double getCargo_fijo() {
        return cargo_fijo;
    }

    public void setCargo_fijo(Double cargo_fijo) {
        this.cargo_fijo = cargo_fijo;
    }

    public Double getCargo_variable() {
        return cargo_variable;
    }

    public void setCargo_variable(Double cargo_variable) {
        this.cargo_variable = cargo_variable;
    }

    public Double getA_10_20_fijo() {
        return a_10_20_fijo;
    }

    public void setA_10_20_fijo(Double a_10_20_fijo) {
        this.a_10_20_fijo = a_10_20_fijo;
    }

    public Double getA_10_20_variable() {
        return a_10_20_variable;
    }

    public void setA_10_20_variable(Double a_10_20_variable) {
        this.a_10_20_variable = a_10_20_variable;
    }

    public Double getA_mas_20_fijo() {
        return a_mas_20_fijo;
    }

    public void setA_mas_20_fijo(Double a_mas_20_fijo) {
        this.a_mas_20_fijo = a_mas_20_fijo;
    }

    public Double getA_mas_20_variable() {
        return a_mas_20_variable;
    }

    public void setA_mas_20_variable(Double a_mas_20_variable) {
        this.a_mas_20_variable = a_mas_20_variable;
    }

    public String getFecUltFact() {
        return fecUltFact;
    }

    public void setFecUltFact(String fecUltFact) {
        this.fecUltFact = fecUltFact;
    }

    public String getFecPrimerConsumo() {
        return fecPrimerConsumo;
    }

    public void setFecPrimerConsumo(String fecPrimerConsumo) {
        this.fecPrimerConsumo = fecPrimerConsumo;
    }

    public boolean isAhorro() {
        return ahorro;
    }

    public void setAhorro(boolean ahorro) {
        this.ahorro = ahorro;
    }

    public boolean isPenalizado() {
        return penalizado;
    }

    public void setPenalizado(boolean penalizado) {
        this.penalizado = penalizado;
    }

    public Double getPrimerConsumo() {
        return primerConsumo;
    }

    public void setPrimerConsumo(Double primerConsumo) {
        this.primerConsumo = primerConsumo;
    }
}
