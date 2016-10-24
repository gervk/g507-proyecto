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
        Double totalAntesImpuestos = 100.0;//valor asignado solo para efectuar las pruebas

        if(consumoActual < consumoAnterior*0.8){
            totalAntesImpuestos = dias*( totalAntesImpuestos - 20);
            //usar tarifa de costo fijo y variable de ahorro mayor al 20% tope de 2000 de ahorro
            //que abajo aparezca una aclaracion "durante este período el consumo fue menor que en el mismo período del año pasado"
        }else{
            if(consumoActual < consumoAnterior*0.9 ){
                totalAntesImpuestos =  dias*(totalAntesImpuestos - 10);
                //usar tarifa de costo fijo y variable de ahorro mayor al 20% tope de 2000 de ahorro
                //que abajo aparezca una aclaracion "durante este período el consumo fue menor que en el mismo período del año pasado"
            }else{
                if(consumoActual< 300){
                    if(consumoActual >= consumoAnterior*1.1){
                        //penalizacion por exceso de consumo
                        totalAntesImpuestos = dias*(totalAntesImpuestos + 20);
                        //excedente por cargo variable
                        //resto calculo normal factura
                        //que abajo aparezca una aclaracion "durante este período el consumo fue mayor que en el mismo período del año pasado"
                    }else{
                        totalAntesImpuestos = totalAntesImpuestos*dias;
                        //calculo con valores normales consumo*costo fijo y consumo*costo variable
                        //calcular para cada tipo de tarifa y empresa, teniendo en cuenta la tarifa normal (sin descuento)
                    }
                }
            }
        }
        return efectuarImpuestos(totalAntesImpuestos);
    }

    private Double efectuarImpuestos(Double valorAntesImpuestos){
        Double valorDespImpuestos = 0.0;
        //TODO ver impuestos que aplican
        //para edesur 10% imp al serv deelectricidad, 5,5% fondo obras publicas, 21% iva, 0,6% sta cruz, 6,4% cont municipal, 0,6424 cont provincial
        valorDespImpuestos = valorAntesImpuestos*1.21;
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

}
