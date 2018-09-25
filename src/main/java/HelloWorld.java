package lala;

public class HelloWorld {

    private String nombre;
    private String ultimoSaludo;

    public HelloWorld(){
        this.nombre = "Mundo";
    }

    public String salude(String nombre){
        if(!nombre.equals("")){
            this.nombre = nombre;
        }
        this.ultimoSaludo = "Hola " + nombre + ".";
        return this.ultimoSaludo;
    }

    public String servidorEstampillaDeTiempo(){
        return (new Long(System.currentTimeMillis()/1000L)).toString();
    }

    public String UltimoSaludo(){
        return "Saludo guardado:  " + this.ultimoSaludo;
    }
}
