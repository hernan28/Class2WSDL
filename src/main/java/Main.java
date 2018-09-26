public class Main {

    public static void main(String[] args) {

        ClassLoader classLoader = Main.class.getClassLoader();
        try {
            new WSDLGenerator(classLoader.loadClass(args[0])) ;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
