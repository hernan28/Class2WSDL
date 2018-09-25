import java.io.*;
import java.lang.reflect.*;

public class WSDLGenerator {

    private Class aClass;
    private String WSDL;

    public WSDLGenerator(Class aclass) {
        this.aClass = aclass;
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
        String definition = "<definitions name=\"ECCI_" + aClass.getName() + "\"\n";
        definition += "             targetNamespace=\"urn:ECCI_" + aClass.getName() + "\"\n";
        definition += "             xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\"\n";
        definition += "             xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\"\n";
        definition += "             xmlns:tns=\"urn:ECCI_" + aClass.getName() + "\"\n";
        definition += "             xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n";
        definition += "             xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\"\n";
        definition += "             xmlns=\"http://schemas.xmlsoap.org/wsdl/\">\n\n";
        return definition;
    }

    private String createComplexTypes(Class aClass){
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
        String port = " <portType name=\"ECCI_" + aClass.getName() + "Port\">\n";
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
        String binding = " <binding name=\"ECCI_" + aClass.getName() + "Binding\" type=\"tns:ECCI_" + aClass.getName() + "Port\">\n" +
                "   <soap:binding style=\"document\" transport=\"http://schemas.xmlsoap.org/soap/http\" />\n\n";
        for(Method method: aClass.getDeclaredMethods()){
            binding += "    <operation name=\"" + method.getName() + "\">\n";
            binding += "        <soap:operation soapAction=\"urn:ECCI_" + aClass.getName() + "#" + aClass.getName() + "#" + method.getName() + "\" style=\"document\" />\n";
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
        String service = " <service name=\"ECCI_" + aClass.getName() +"\">\n";
        service += "    <documentation />\n";
        service += "    <port name=\"ECCI_" + aClass.getName() + "Port\" binding=\"tns:ECCI_" + aClass.getName() + "Binding\">\n";
        service += "        <soap:address location=\"http://localhost:8080/\" />\n";
        service += "    </port>\n";
        service += " </service>\n";
        service += "</definitions>";
        return service;
    }
}
