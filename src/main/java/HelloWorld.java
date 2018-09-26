import lala.*;

public class HelloWorld {

    private String nombre;
    private String ultimoSaludo;

    public HelloWorld(){
        this.nombre = "Mundo";
    }

    public String salude(Hola nombre, String lala){
        return "hola";
    }

    public String servidorEstampillaDeTiempo(){
        return (new Long(System.currentTimeMillis()/1000L)).toString();
    }

    public String UltimoSaludo(Hola a){
        return "Saludo guardado:  " + this.ultimoSaludo;
    }

    public void prueba(){
        return;
    }
}
