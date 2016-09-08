package g507.controldeconsumo.modelo;


import java.util.List;

public class ServicioElectricidad {
    private int id;
    private EmpresaElec empresa;
    private Tarifa tarifa;

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

    public EmpresaElec getEmpresa() {
        return empresa;
    }

    public void setEmpresa(EmpresaElec empresa) {
        this.empresa = empresa;
    }

    public Tarifa getTarifa() {
        return tarifa;
    }

    public void setTarifa(Tarifa tarifa) {
        this.tarifa = tarifa;
    }
}
