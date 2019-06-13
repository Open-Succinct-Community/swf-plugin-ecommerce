package in.succinct.plugins.ecommerce.integration.fedex;

/**
 * This is a utility class for working with Axis generated objects.
 *
 * @author Bob Withers  7/6/06
 */

import org.apache.axis.MessageContext;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.AttributeSerializationContextImpl;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.ser.BeanSerializer;
import org.apache.axis.server.AxisServer;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.namespace.QName;
import java.io.StringWriter;
import java.lang.reflect.Method;

public class AxisObjectUtil {

    /**
     * Return the Axis TypeDesc object for the passed Axis JavaBean.
     *
     * @param obj The Axis JavaBean object.
     * @return The Axis TypeDesc for the JavaBean.
     */
    public static TypeDesc getAxisTypeDesc(final Object obj) {
        final Class objClass = obj.getClass();
        try {
            final Method methodGetTypeDesc = objClass.getMethod("getTypeDesc", new Class[]{});
            final TypeDesc typeDesc = (TypeDesc) methodGetTypeDesc.invoke(obj, new Object[]{});
            return (typeDesc);
        } catch (final Exception e) {
            throw new RuntimeException("Unable to get Axis TypeDesc for "
                    + objClass.getName(), e);
        }
    }


    public static String serializeAxisObject(Object obj) {
        if (obj == null) {
            return null;
        }
        StringWriter outStr = new StringWriter();
        TypeDesc typeDesc = getAxisTypeDesc(obj);
        QName qname = typeDesc.getXmlType();
        String lname = qname.getLocalPart();
        if (lname.startsWith(">") && lname.length() > 1)
            lname = lname.substring(1);

        qname = new QName(qname.getNamespaceURI(), lname);
        AxisServer server = new AxisServer();
        BeanSerializer ser = new BeanSerializer(obj.getClass(), qname, typeDesc);
        SerializationContext ctx = new SerializationContext(outStr,
                new MessageContext(server));
        ctx.setSendDecl(false);
        ctx.setDoMultiRefs(false);
        ctx.setPretty(true);
        try {
            ser.serialize(qname, new AttributesImpl(), obj, ctx);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        String xml = outStr.toString();
        return xml;
    }
}