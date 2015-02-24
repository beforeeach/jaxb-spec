/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004-2015 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package javax.xml.bind.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Maps a factory method to a XML element.
 *
 * <p> <b>Usage</b> </p>
 *
 * The annotation creates a mapping between an XML schema element
 * declaration and a <i> element factory method </i> that returns a
 * JAXBElement instance representing the element
 * declaration. Typically, the element factory method is generated
 * (and annotated) from a schema into the ObjectFactory class in a
 * Java package that represents the binding of the element
 * declaration's target namespace. Thus, while the annotation syntax 
 * allows &#64;XmlElementDecl to be used on any method, semantically
 * its use is restricted to annotation of element factory method. 
 *
 * The usage is subject to the following constraints:
 * 
 * <ul>
 *   <li> The class containing the element factory method annotated
 *        with &#64;XmlElementDecl must be marked with {@link
 *        XmlRegistry}. </li> 
 *   <li> The element factory method must take one parameter
 *        assignable to {@link Object}.</li> 
 * </ul>
 *
 * <p><b>Example 1: </b>Annotation on a factory method
 * <pre>
 *     // Example: code fragment
 *     &#64;XmlRegistry
 *     class ObjectFactory {
 *         &#64;XmlElementDecl(name="foo")
 *         JAXBElement&lt;String&gt; createFoo(String s) { ... }
 *     }
 * </pre>
 * <pre> {@code
 * 
 *     <!-- XML input -->
 *     <foo>string</foo>
 *
 *     // Example: code fragment corresponding to XML input
 *     JAXBElement<String> o =
 *     (JAXBElement<String>)unmarshaller.unmarshal(aboveDocument);
 *     // print JAXBElement instance to show values
 *     System.out.println(o.getName());   // prints  "{}foo"
 *     System.out.println(o.getValue());  // prints  "string"
 *     System.out.println(o.getValue().getClass()); // prints "java.lang.String"
 *
 *     <!-- Example: XML schema definition -->
 *     <xs:element name="foo" type="xs:string"/>
 * }</pre>
 *
 * <p><b>Example 2: </b> Element declaration with non local scope
 * <p>
 * The following example illustrates the use of scope annotation
 * parameter in binding of element declaration in schema derived
 * code. 
 * <p>
 * The following example may be replaced in a future revision of
 * this javadoc.
 * 
 * <pre>{@code
 *     <!-- Example: XML schema definition -->
 *     <xs:schema>
 *       <xs:complexType name="pea">
 *         <xs:choice maxOccurs="unbounded">
 *           <xs:element name="foo" type="xs:string"/>
 *           <xs:element name="bar" type="xs:string"/>
 *         </xs:choice>
 *       </xs:complexType>
 *       <xs:element name="foo" type="xs:int"/>
 *     </xs:schema>
 * }</pre> 
 * <pre>
 *     // Example: expected default binding
 *     class Pea {
 *         &#64;XmlElementRefs({
 *             &#64;XmlElementRef(name="foo",type=JAXBElement.class)
 *             &#64;XmlElementRef(name="bar",type=JAXBElement.class)
 *         })
 *         List&lt;JAXBElement&lt;String&gt;&gt; fooOrBar;
 *     }
 * 
 *     &#64;XmlRegistry
 *     class ObjectFactory {
 *         &#64;XmlElementDecl(scope=Pea.class,name="foo")
 *         JAXBElement&lt;String&gt; createPeaFoo(String s);
 * 
 *         &#64;XmlElementDecl(scope=Pea.class,name="bar")
 *         JAXBElement&lt;String&gt; createPeaBar(String s);
 * 
 *         &#64;XmlElementDecl(name="foo")
 *         JAXBElement&lt;Integer&gt; createFoo(Integer i);
 *     }
 * 
 * </pre>
 * Without scope createFoo and createPeaFoo would become ambiguous
 * since both of them map to a XML schema element with the same local
 * name "foo". 
 *
 * @see XmlRegistry
 * @since 1.6, JAXB 2.0
 */
@Retention(RUNTIME)
@Target({METHOD})
public @interface XmlElementDecl {
    /**
     * scope of the mapping.
     *
     * <p>
     * If this is not {@link XmlElementDecl.GLOBAL}, then this element
     * declaration mapping is only active within the specified class.
     */
    Class scope() default GLOBAL.class;

    /**
     * namespace name of the XML element.
     * <p>
     * If the value is "##default", then the value is the namespace
     * name for the package of the class containing this factory method.
     *
     * @see #name()
     */
    String namespace() default "##default";

    /**
     * local name of the XML element.
     *
     * <p>
     * <b> Note to reviewers: </b> There is no default name; since
     * the annotation is on a factory method, it is not clear that the
     * method name can be derived from the factory method name.
     * @see #namespace()
     */
    String name();

    /**
     * namespace name of a substitution group's head XML element.
     * <p>
     * This specifies the namespace name of the XML element whose local
     * name is specified by <tt>substitutionHeadName()</tt>.
     * <p> 
     * If <tt>susbtitutionHeadName()</tt> is "", then this
     * value can only be "##default". But the value is ignored since
     * since this element is not part of susbtitution group when the
     * value of <tt>susbstitutionHeadName()</tt> is "".
     * <p>
     * If <tt>susbtitutionHeadName()</tt> is not "" and the value is
     * "##default", then the namespace name is the namespace name to 
     * which the package of the containing class, marked with {@link
     * XmlRegistry }, is mapped.
     * <p>
     * If <tt>susbtitutionHeadName()</tt> is not "" and the value is
     * not "##default", then the value is the namespace name.
     *
     * @see #substitutionHeadName()
     */
    String substitutionHeadNamespace() default "##default";

    /**
     * XML local name of a substitution group's head element.
     * <p>
     * If the value is "", then this element is not part of any
     * substitution group.
     *
     * @see #substitutionHeadNamespace()
     */
    String substitutionHeadName() default "";

    /**
     * Default value of this element.
     *
     * <p>
     * The <pre>'\u0000'</pre> value specified as a default of this annotation element
     * is used as a poor-man's substitute for null to allow implementations
     * to recognize the 'no default value' state.
     */
    String defaultValue() default "\u0000";
    
    /**
     * Used in {@link XmlElementDecl#scope()} to
     * signal that the declaration is in the global scope.
     */
    public final class GLOBAL {}
}
