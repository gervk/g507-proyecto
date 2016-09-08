package g507.controldeconsumo.modelo;

public class Tarifa {
    private int id;
    private String descripcion;
    private EmpresaElec empresa;
    private double cargoFijo;
    private double cargoVariable;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public EmpresaElec getEmpresa() {
        return empresa;
    }

    public void setEmpresa(EmpresaElec empresa) {
        this.empresa = empresa;
    }

    public double getCargoFijo() {
        return cargoFijo;
    }

    public void setCargoFijo(double cargoFijo) {
        this.cargoFijo = cargoFijo;
    }

    public double getCargoVariable() {
        return cargoVariable;
    }

    public void setCargoVariable(double cargoVariable) {
        this.cargoVariable = cargoVariable;
    }
}
