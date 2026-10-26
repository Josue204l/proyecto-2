package data;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import logic.Categoria;
import logic.Funcionario;
import logic.Recurso;
import logic.Reserva;
import logic.Usuario;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class XmlPersister {

    private static XmlPersister theInstance;
    private final String path;

    public static void establecerInstanciaParaPruebas(XmlPersister persister) {
        theInstance = persister;
    }

    public static XmlPersister instance() {
        if (theInstance == null) {
            theInstance = new XmlPersister("datos/data.xml");
        }
        return theInstance;
    }

    public XmlPersister(String p) {
        path = p;
    }

    public Data load() throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(
                Data.class,
                Funcionario.class,
                Usuario.class,
                Reserva.class,
                Recurso.class,
                Categoria.class
        );
        FileInputStream is = new FileInputStream(path);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Data result = (Data) unmarshaller.unmarshal(is);
        is.close();
        return result;
    }

    public void store(Data d) throws Exception {
        File archivo = new File(path);
        File directorio = archivo.getParentFile();
        if (directorio != null) {
            directorio.mkdirs();
        }
        JAXBContext jaxbContext = JAXBContext.newInstance(
                Data.class,
                Funcionario.class,
                Usuario.class,
                Reserva.class,
                Recurso.class,
                Categoria.class
        );
        FileOutputStream os = new FileOutputStream(path);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(d, os);
        os.flush();
        os.close();
    }
}