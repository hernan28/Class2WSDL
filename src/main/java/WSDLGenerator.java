import java.io.*;
import java.lang.reflect.*;
import java.util.*;

public class WSDLGenerator {

    private Class aClass;
    private String WSDL;
    private List<String> types;
    ClassLoader classLoader;

    public WSDLGenerator(Class aclass) {
        this.classLoader = this.getClass().getClassLoader();
        this.aClass = aclass;
        this.types = new ArrayList<>();
        this.createWSLD();
        this.printWSDL(this.WSDL);
    }

    private void printWSDL(String finalWSDL){
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(aClass.getSimpleName() + ".wsdl", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.println(finalWSDL);
        writer.close();
    }

    private void createWSLD(){
        this.WSDL = this.createDefinition(this.aClass) +
        this.createComplexTypes(this.aClass) +
        this.createMessages(this.aClass) +
        this.createPort(this.aClass) +
        this.createBinding(this.aClass) +
        this.createService(this.aClass);
    }

    private String createDefinition(Class aClass){
        String definition = "<?xml version=\"1.0\"?>\n\n" +
                "<definitions name=\"ECCI_" + aClass.getSimpleName() + "\"\n";
        definition += "             targetNamespace=\"urn:ECCI_" + aClass.getSimpleName() + "\"\n";
        definition += "             xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\"\n";
        definition += "             xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\"\n";
        definition += "             xmlns:tns=\"urn:ECCI_" + aClass.getSimpleName() + "\"\n";
        definition += "             xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n";
        definition += "             xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\"\n";
        definition += "             xmlns=\"http://schemas.xmlsoap.org/wsdl/\">\n\n";
        return definition;
    }

    private String createComplexTypes(Class aClass){
        String complexTypes = " <types xmlns=\"http://schemas.xmlsoap.org/wsdl/\"> \n" +
                "   <xsd:schema targetNamespace=\"urn:ECCI_" + aClass.getSimpleName() + "\"> \n";
        for (Method method: aClass.getDeclaredMethods()) {
            complexTypes += "       <xsd:element name=\"" + method.getName() + "\"> \n";
            if(method.getParameters().length > 0){
                complexTypes += "           <xsd:complexType>\n" +
                        "               <xsd:sequence>\n";
                for (Parameter parameter: method.getParameters()) {
                    if (!parameter.getType().isPrimitive() && !parameter.getType().getSimpleName().equals("String")){
                        if(!this.types.contains(parameter.getType().getName())){
                            this.types.add(parameter.getType().getName());
                        }
                        complexTypes += "                   <xsd:element name=\"" + parameter.getName() + "\" " +
                                "type=\"tns:" + parameter.getType().getSimpleName() +  "\" />\n";
                    }
                    else{
                        complexTypes += "                   <xsd:element name=\"" + parameter.getName() + "\" " +
                                "type=\"xsd:" + parameter.getType().getSimpleName().toLowerCase() +  "\" />\n";
                    }
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
            if(!method.getReturnType().getSimpleName().equals("void")){
                complexTypes += "               <xsd:complexType>\n" +
                        "                   <xsd:sequence>\n";

                complexTypes += "                       <xsd:element name=\"" + method.getName() + "Result\" " +
                        "type=\"xsd:" + method.getReturnType().getSimpleName().toLowerCase() +  "\" />\n";

                complexTypes += "                   </xsd:sequence>\n" +
                        "               </xsd:complexType>\n" +
                        "           </xsd:element> \n";
            }
            else{
                complexTypes += "           <xsd:complexType />\n" +
                        "       </xsd:element> \n";
            }

        }
        for (String type : this.types) {
            Class parameterClass = null;
            try {
                parameterClass = this.classLoader.loadClass(type);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            complexTypes += "       <xsd:element name=\"" + parameterClass.getSimpleName() + "\"> \n";
            if(parameterClass.getFields().length > 0){
                complexTypes += "           <xsd:complexType>\n" +
                        "               <xsd:sequence>\n";
                for (Field field: parameterClass.getFields()) {
                    complexTypes += "                   <xsd:element name=\"" + field.getName() + "\" " +
                            "type=\"xsd:" + field.getType().getSimpleName().toLowerCase() +  "\" />\n";
                }
                complexTypes += "                   </xsd:sequence>\n" +
                        "               </xsd:complexType>\n" +
                        "           </xsd:element> \n";
            }
            else{
                complexTypes += "           <xsd:complexType />\n" +
                        "       </xsd:element> \n";
            }

        }
        complexTypes += "   </xsd:schema>\n" +
                " </types> \n";
        return complexTypes;

    }

    private String createMessages(Class aClass){
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

        return messages;
    }

    private String createPort(Class aClass){
        String port = " <portType name=\"ECCI_" + aClass.getSimpleName() + "Port\">\n";
        for(Method method: aClass.getDeclaredMethods()){
            port += "   <operation name=\"" + method.getName() + "\">\n";
            port += "       <input message=\"tns:" + method.getName() + "Request\" />\n";
            port += "       <output message=\"tns:" + method.getName() + "Response\" />\n";
            port += "   </operation>\n";
        }
        port += "</portType>\n";
        return port;
    }

    private String createBinding(Class aClass){
        String binding = " <binding name=\"ECCI_" + aClass.getSimpleName() + "Binding\" type=\"tns:ECCI_" + aClass.getSimpleName() + "Port\">\n" +
                "   <soap:binding style=\"document\" transport=\"http://schemas.xmlsoap.org/soap/http\" />\n\n";
        for(Method method: aClass.getDeclaredMethods()){
            binding += "    <operation name=\"" + method.getName() + "\">\n";
            binding += "        <soap:operation soapAction=\"urn:ECCI_" + aClass.getSimpleName() + "#" + aClass.getSimpleName() + "#" + method.getName() + "\" style=\"document\" />\n";
            binding += "        <input>\n";
            binding += "            <soap:body use=\"literal\" />\n";
            binding += "        </input>\n";
            binding += "        <output>\n";
            binding += "            <soap:body use=\"literal\" />\n";
            binding += "        </output>\n";
            binding += "    </operation>\n";
        }
        binding += " </binding>\n";
        return binding;
    }

    private String createService(Class aClass){
        String service = " <service name=\"ECCI_" + aClass.getSimpleName() +"\">\n";
        service += "    <documentation />\n";
        service += "    <port name=\"ECCI_" + aClass.getSimpleName() + "Port\" binding=\"tns:ECCI_" + aClass.getSimpleName() + "Binding\">\n";
        service += "        <soap:address location=\"http://localhost:8080/\" />\n";
        service += "    </port>\n";
        service += " </service>\n";
        service += "</definitions>";
        return service;
    }
}
