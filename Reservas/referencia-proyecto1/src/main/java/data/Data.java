package data;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import logic.Categoria;
import logic.Funcionario;
import logic.Recurso;
import logic.Reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Data {

    @XmlTransient
    private static Data instancia;

    @XmlElementWrapper(name = "funcionarios")
    @XmlElement(name = "funcionario")
    private List<Funcionario> funcionarios;

    @XmlElementWrapper(name = "categorias")
    @XmlElement(name = "categoria")
    private List<Categoria> categorias;

    @XmlElementWrapper(name = "recursos")
    @XmlElement(name = "recurso")
    private List<Recurso> recursos;

    @XmlElementWrapper(name = "reservas")
    @XmlElement(name = "reserva")
    private List<Reserva> reservas;

    public Data() {
        funcionarios = new ArrayList<>();
        reservas = new ArrayList<>();
        recursos = new ArrayList<>();
        categorias = new ArrayList<>();
    }

    public static Data getInstancia() {
        if (instancia == null) {
            try {
                instancia = XmlPersister.instance().load();
            } catch (Exception e) {
                instancia = new Data();
            }
            instancia.asegurarListas();
            instancia.sembrarUsuariosSiFalta();
            instancia.sembrarCatalogoSiFalta();
            Runtime.getRuntime().addShutdownHook(new Thread(instancia::guardarTodo));
        }
        return instancia;
    }

    public List<Funcionario> getFuncionarios() { return funcionarios; }
    public List<Reserva> getReservas() { return reservas; }
    public List<Recurso> getRecursos() { return recursos; }
    public List<Categoria> getCategorias() { return categorias; }

    public void setFuncionarios(List<Funcionario> funcionarios) { this.funcionarios = funcionarios; }
    public void setReservas(List<Reserva> reservas) { this.reservas = reservas; }
    public void setRecursos(List<Recurso> recursos) { this.recursos = recursos; }
    public void setCategorias(List<Categoria> categorias) { this.categorias = categorias; }

    public void guardarTodo() {
        try {
            XmlPersister.instance().store(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void guardarFuncionarios() { guardarTodo(); }
    public void guardarCategorias() { guardarTodo(); }
    public void guardarRecursos() { guardarTodo(); }
    public void guardarReservas() { guardarTodo(); }

    public Funcionario buscarFuncionario(String id) {
        if (id == null) return null;
        for (Funcionario f : funcionarios) {
            if (id.equals(f.getId())) return f;
        }
        return null;
    }

    public Categoria buscarCategoria(String id) {
        if (id == null) return null;
        for (Categoria c : categorias) {
            if (id.equals(c.getId())) return c;
        }
        return null;
    }

    public Recurso buscarRecurso(String id) {
        if (id == null) return null;
        for (Recurso r : recursos) {
            if (id.equals(r.getId())) return r;
        }
        return null;
    }

    private void asegurarListas() {
        if (funcionarios == null) funcionarios = new ArrayList<>();
        if (categorias == null) categorias = new ArrayList<>();
        if (recursos == null) recursos = new ArrayList<>();
        if (reservas == null) reservas = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.getRecursosAsignados() == null) r.setRecursosAsignados(new ArrayList<>());
            if (r.getCategoriasRequeridas() == null) r.setCategoriasRequeridas(new ArrayList<>());
        }
    }

    private void sembrarUsuariosSiFalta() {
        if (buscarFuncionario("admin") == null) {
            funcionarios.add(new Funcionario("admin", "222", "ADMIN", "Administrador", "2222-0000"));
            guardarFuncionarios();
        }
        if (buscarFuncionario("111") == null) {
            funcionarios.add(new Funcionario("111", "111", "FUNCIONARIO", "Funcionario Test", "8888-1234"));
            guardarFuncionarios();
        }
    }

    public static void establecerInstanciaParaPruebas(Data data) {
        instancia = data;
    }

    private void sembrarCatalogoSiFalta() {
        boolean sembrar = false;
        if (categorias.isEmpty()) {
            Categoria salas = new Categoria("CAT-001", "Sala para 10 personas", "Sala para 10 personas");
            Categoria laptops = new Categoria("CAT-002", "Laptop windows 11", "Laptop windows 11");
            categorias.add(salas);
            categorias.add(laptops);
            sembrar = true;
        }
        if (recursos.isEmpty()) {
            Categoria salas = buscarCategoria("CAT-001");
            Categoria laptops = buscarCategoria("CAT-002");
            if (salas == null && !categorias.isEmpty()) salas = categorias.get(0);
            if (laptops == null && categorias.size() > 1) laptops = categorias.get(1);
            recursos.add(new Recurso("SALA-1", "Sala 1 primer piso", salas));
            recursos.add(new Recurso("SALA-2", "Sala 2 segundo piso", salas));
            recursos.add(new Recurso("238715", "Laptop #238715", laptops));
            sembrar = true;
        }
        if (sembrar) {
            guardarCategorias();
            guardarRecursos();
        }
        if (reservas.isEmpty()) {
            Funcionario func = buscarFuncionario("111");
            Recurso sala1 = buscarRecurso("SALA-1");
            Categoria catSala = buscarCategoria("CAT-001");
            if (func != null && sala1 != null) {
                LocalDate fecha = LocalDate.now().plusDays(1);
                Reserva demo = new Reserva("RES-DEMO", "Reunión de coordinación",
                        fecha, LocalTime.of(10, 0), LocalTime.of(12, 0),
                        List.of(sala1),
                        catSala != null ? List.of(catSala) : new ArrayList<>(),
                        func);
                reservas.add(demo);
                guardarReservas();
            }
        }
    }
}