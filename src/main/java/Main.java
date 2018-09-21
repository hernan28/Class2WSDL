import java.lang.reflect.*;

public class Main {

    public static void main(String[] args) {

        ClassLoader classLoader = Main.class.getClassLoader();
        Class aClass = null;
        try {
            aClass = classLoader.loadClass("HelloWorld");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
//        System.out.println("aClass.getName() = " + aClass.getName());
//
//        for (Method method: aClass.getDeclaredMethods()) {
//            System.out.println(method.getName());
//        }

        createComplexTypes(aClass);
        createMessages(aClass);
    }

    public static void createComplexTypes(Class aClass){
        String complexTypes = " <types xmlns=\"http://schemas.xmlsoap.org/wsdl/\"> \n" +
                "   <xsd:schema targetNamespace=\"urn:ECCI_" + aClass.getName() + "\"> \n";
        for (Method method: aClass.getDeclaredMethods()) {
            complexTypes += "       <xsd:element name=\"" + method.getName() + "\"> \n";
            if(method.getParameters().length > 0){
                complexTypes += "           <xsd:complexType>\n" +
                        "               <xsd:sequence>\n";
                for (Parameter parameter: method.getParameters()) {
                    complexTypes += "                   <xsd:element name=\"" + parameter.getName() + "\" " +
                            "type=\"xsd:" + parameter.getType().getSimpleName().toLowerCase() +  "\" />\n";
                }
                complexTypes += "               </xsd:sequence>\n" +
                        "           </xsd:complexType>\n" +
                        "       </xsd:element> \n";
            }
            else{
                complexTypes += "           <xsd:complexType />\n" +
                        "       </xsd:element> \n";
            }
            //return part
            complexTypes += "           <xsd:element name=\"" + method.getName() + "Return\"> \n";
            complexTypes += "               <xsd:complexType>\n" +
                    "                   <xsd:sequence>\n";

            complexTypes += "                       <xsd:element name=\"" + method.getName() + "Result\" " +
                        "type=\"xsd:" + method.getReturnType().getSimpleName().toLowerCase() +  "\" />\n";

            complexTypes += "                   </xsd:sequence>\n" +
                    "               </xsd:complexType>\n" +
                    "           </xsd:element> \n";


        }
        complexTypes += "   </xsd:schema>\n" +
                " </types> \n";

        System.out.println(complexTypes);

    }

    public static void createMessages(Class aClass){
        String messages = "";
        for (Method method: aClass.getDeclaredMethods()) {
            messages += " <message name =\"" + method.getName() + "Request\">\n";
            messages += "   <part name =\"parameters\" element=\"tns:" + method.getName() + "\" />\n";
            messages += " </message>\n\n";
            //return part
            messages += " <message name =\"" + method.getName() + "Response\">\n";
            messages += "   <part name =\"parameters\" element=\"tns:" + method.getName() + "Return\" />\n";
            messages += " </message>\n\n";
        }

        System.out.println(messages);
    }

    public static void createPort(Class aClass){
        String port = "";
        
    }
}
