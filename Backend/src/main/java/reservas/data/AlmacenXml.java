package reservas.data;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import reservas.logic.Categoria;
import reservas.logic.Funcionario;
import reservas.logic.Recurso;
import reservas.logic.Reserva;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Guarda funcionarios, categorías, recursos y reservas en un solo archivo XML.
 * Al cargar, resuelve las referencias por id en memoria.
 */
public class AlmacenXml {
    private final Path archivo;
    private final List<Funcionario> funcionarios = new ArrayList<>();
    private final List<Categoria> categorias = new ArrayList<>();
    private final List<Recurso> recursos = new ArrayList<>();
    private final List<Reserva> reservas = new ArrayList<>();

    public AlmacenXml(Path archivo) throws Exception {
        this.archivo = archivo;
        if (Files.exists(archivo)) cargar();
    }

    public List<Funcionario> getFuncionarios() { return funcionarios; }
    public List<Categoria> getCategorias() { return categorias; }
    public List<Recurso> getRecursos() { return recursos; }
    public List<Reserva> getReservas() { return reservas; }

    public void guardar() throws Exception {
        if (archivo.getParent() != null) Files.createDirectories(archivo.getParent());
        DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
        fabrica.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        Document doc = fabrica.newDocumentBuilder().newDocument();
        Element raiz = doc.createElement("reservas");
        doc.appendChild(raiz);

        Element nodoFuncionarios = doc.createElement("funcionarios");
        raiz.appendChild(nodoFuncionarios);
        for (Funcionario funcionario : funcionarios) {
            Element n = doc.createElement("funcionario");
            n.setAttribute("id", texto(funcionario.getId()));
            n.setAttribute("clave", texto(funcionario.getClave()));
            n.setAttribute("rol", texto(funcionario.getRol()));
            n.setAttribute("nombre", texto(funcionario.getNombre()));
            n.setAttribute("telefono", texto(funcionario.getTelefono()));
            nodoFuncionarios.appendChild(n);
        }

        Element nodoCategorias = doc.createElement("categorias");
        raiz.appendChild(nodoCategorias);
        for (Categoria categoria : categorias) {
            Element n = doc.createElement("categoria");
            n.setAttribute("id", texto(categoria.getId()));
            n.setAttribute("descripcion", texto(categoria.getDescripcion()));
            nodoCategorias.appendChild(n);
        }

        Element nodoRecursos = doc.createElement("recursos");
        raiz.appendChild(nodoRecursos);
        for (Recurso recurso : recursos) {
            Element n = doc.createElement("recurso");
            n.setAttribute("id", texto(recurso.getId()));
            n.setAttribute("descripcion", texto(recurso.getDescripcion()));
            n.setAttribute("categoria", recurso.getCategoria() == null ? "" : texto(recurso.getCategoria().getId()));
            nodoRecursos.appendChild(n);
        }

        Element nodoReservas = doc.createElement("reservas");
        raiz.appendChild(nodoReservas);
        for (Reserva reserva : reservas) {
            Element n = doc.createElement("reserva");
            n.setAttribute("id", texto(reserva.getId()));
            n.setAttribute("titulo", texto(reserva.getTitulo()));
            n.setAttribute("fecha", reserva.getFecha() == null ? "" : reserva.getFecha().toString());
            n.setAttribute("horaInicio", reserva.getHoraInicio() == null ? "" : reserva.getHoraInicio().toString());
            n.setAttribute("horaFin", reserva.getHoraFin() == null ? "" : reserva.getHoraFin().toString());
            n.setAttribute("funcionario", reserva.getSolicitante() == null ? "" : texto(reserva.getSolicitante().getId()));
            n.setAttribute("estado", texto(reserva.getEstado()));
            for (Categoria categoria : reserva.getCategoriasRequeridas()) {
                Element hijo = doc.createElement("categoria");
                hijo.setTextContent(categoria.getId());
                n.appendChild(hijo);
            }
            for (Recurso recurso : reserva.getRecursosAsignados()) {
                Element hijo = doc.createElement("recurso");
                hijo.setTextContent(recurso.getId());
                n.appendChild(hijo);
            }
            nodoReservas.appendChild(n);
        }

        Path temporal = archivo.resolveSibling(archivo.getFileName() + ".tmp");
        TransformerFactory transformers = TransformerFactory.newInstance();
        var transformer = transformers.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.transform(new DOMSource(doc), new StreamResult(temporal.toFile()));
        Files.move(temporal, archivo, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    private void cargar() throws Exception {
        DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
        fabrica.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        Document doc = fabrica.newDocumentBuilder().parse(archivo.toFile());
        doc.getDocumentElement().normalize();

        Map<String, Funcionario> porFuncionario = new LinkedHashMap<>();
        NodeList nodosFuncionario = doc.getElementsByTagName("funcionario");
        for (int i = 0; i < nodosFuncionario.getLength(); i++) {
            if (nodosFuncionario.item(i).getParentNode() == null
                    || !"funcionarios".equals(nodosFuncionario.item(i).getParentNode().getNodeName())) continue;
            Element n = (Element) nodosFuncionario.item(i);
            Funcionario funcionario = new Funcionario(
                    n.getAttribute("id"), n.getAttribute("clave"), n.getAttribute("rol"),
                    n.getAttribute("nombre"), n.getAttribute("telefono"));
            porFuncionario.put(funcionario.getId(), funcionario);
            funcionarios.add(funcionario);
        }

        Map<String, Categoria> porCategoria = new LinkedHashMap<>();
        NodeList nodosCategoria = doc.getElementsByTagName("categoria");
        for (int i = 0; i < nodosCategoria.getLength(); i++) {
            Node padre = nodosCategoria.item(i).getParentNode();
            if (padre == null || !"categorias".equals(padre.getNodeName())) continue;
            Element n = (Element) nodosCategoria.item(i);
            Categoria categoria = new Categoria(n.getAttribute("id"), n.getAttribute("descripcion"));
            porCategoria.put(categoria.getId(), categoria);
            categorias.add(categoria);
        }

        Map<String, Recurso> porRecurso = new LinkedHashMap<>();
        NodeList nodosRecurso = doc.getElementsByTagName("recurso");
        for (int i = 0; i < nodosRecurso.getLength(); i++) {
            Node padre = nodosRecurso.item(i).getParentNode();
            if (padre == null || !"recursos".equals(padre.getNodeName())) continue;
            Element n = (Element) nodosRecurso.item(i);
            Recurso recurso = new Recurso(n.getAttribute("id"), n.getAttribute("descripcion"),
                    porCategoria.get(n.getAttribute("categoria")));
            porRecurso.put(recurso.getId(), recurso);
            recursos.add(recurso);
        }

        NodeList nodosReserva = doc.getElementsByTagName("reserva");
        for (int i = 0; i < nodosReserva.getLength(); i++) {
            Element n = (Element) nodosReserva.item(i);
            Reserva reserva = new Reserva();
            reserva.setId(n.getAttribute("id"));
            reserva.setTitulo(n.getAttribute("titulo"));
            if (!n.getAttribute("fecha").isBlank()) reserva.setFecha(LocalDate.parse(n.getAttribute("fecha")));
            if (!n.getAttribute("horaInicio").isBlank()) reserva.setHoraInicio(LocalTime.parse(n.getAttribute("horaInicio")));
            if (!n.getAttribute("horaFin").isBlank()) reserva.setHoraFin(LocalTime.parse(n.getAttribute("horaFin")));
            reserva.setEstado(n.getAttribute("estado"));
            reserva.setSolicitante(porFuncionario.get(n.getAttribute("funcionario")));
            List<Categoria> cats = new ArrayList<>();
            List<Recurso> recs = new ArrayList<>();
            NodeList hijos = n.getChildNodes();
            for (int h = 0; h < hijos.getLength(); h++) {
                if (hijos.item(h).getNodeType() != Node.ELEMENT_NODE) continue;
                Element hijo = (Element) hijos.item(h);
                if ("categoria".equals(hijo.getNodeName())) {
                    Categoria categoria = porCategoria.get(hijo.getTextContent().trim());
                    if (categoria != null) cats.add(categoria);
                } else if ("recurso".equals(hijo.getNodeName())) {
                    Recurso recurso = porRecurso.get(hijo.getTextContent().trim());
                    if (recurso != null) recs.add(recurso);
                }
            }
            reserva.setCategoriasRequeridas(cats);
            reserva.setRecursosAsignados(recs);
            reservas.add(reserva);
        }
    }

    private static String texto(String valor) {
        return valor == null ? "" : valor;
    }
}
