/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.xml.bind;

import java.util.Collections;
import java.util.Map;

/**
 * <p>
 * The <tt>JAXBContext</tt> class provides the client's entry point to the 
 * JAXB API. It provides an abstraction for managing the XML/Java binding 
 * information necessary to implement the JAXB binding framework operations: 
 * unmarshal, marshal and validate.
 *
 * <p>A client application normally obtains new instances of this class via the
 * newInstance methods, although there are other specialized forms of the method
 * available:
 *
 * <ul>
 *   <li>{@link #newInstance(String) JAXBContext.newInstance( "com.acme.foo:com.acme.bar" )} </li>
 *   <li>{@link #newInstance(Class[]) JAXBContext.newInstance( com.acme.foo.Foo.class )} </li>
 * </ul>
 *
 * <p>
 * The <tt>contextPath</tt> contains a list of Java package names that contain
 * JAXB mapped classes. Alternatively, you may initialize the <tt>JAXBContext</tt> by
 * passing Class objects.  In either case, the values of these parameters initialize
 * the <tt>JAXBContext</tt> object so that it is capable of managing the JAXB mapped
 * classes.
 *
 * <p>
 * <blockquote>
 * <i><B>SPEC REQUIREMENT:</B> the provider must supply an implementation
 * class containing the following method signatures:</i>
 *
 * <pre>
 * public static JAXBContext createContext( String contextPath, ClassLoader classLoader, Map<String,Object> properties ) throws JAXBException
 * public static JAXBContext createContext( Class[] classes, Map<String,Object> properties ) throws JAXBException
 * </pre>
 *
 * <p><i>
 * Each package in the contextPath is expected to contain either a
 * ObjectFactory class and/or a jaxb.index file that designates the
 * classes in the package that are registered with the JAXBContext.
 * 
 * The following JAXB 1.0 requirement is only required for schema to 
 * java interface/implementation binding. It does not apply to JAXB annotated
 * classes. JAXB Providers must generate a <tt>jaxb.properties</tt> file in 
 * each package containing schema derived classes.  The property file must 
 * contain a property named <tt>javax.xml.bind.context.factory</tt> whose 
 * value is the name of the class that implements the <tt>createContext</tt> 
 * APIs.</i>
 * 
 * <p><i>
 * The class supplied by the provider does not have to be assignable to 
 * <tt>javax.xml.bind.JAXBContext</tt>, it simply has to provide a class that
 * implements the <tt>createContext</tt> APIs.</i>
 * 
 * <p><i>
 * In addition, the provider must call the 
 * {@link DatatypeConverter#setDatatypeConverter(DatatypeConverterInterface) 
 * DatatypeConverter.setDatatypeConverter} api prior to any client 
 * invocations of the marshal and unmarshal methods.  This is necessary to 
 * configure the datatype converter that will be used during these operations.</i>
 * </blockquote>
 *
 * <p>
 * <b>Format for jaxb.index</b>
 * <p>
 * <blockquote>
 * The file should contain a newline-separated list of class names. Space and 
 * tab characters, as well as blank lines, are ignored. The comment character 
 * is '#' (0x23); on each line all characters following the first comment 
 * character are ignored. The file must be encoded in UTF-8. 
 * </blockquote>
 *
 * <p>
 * <b>Unmarshalling</b>
 * <p>
 * <blockquote>
 * The {@link Unmarshaller} class provides the client application the ability
 * to convert XML data into a tree of Java content objects.
 * The unmarshal method for a schema (within a namespace) allows for
 * any global XML element declared in the schema to be unmarshalled as
 * the root of an instance document. The <tt>JAXBContext</tt> object 
 * allows the merging of global elements across a set of schemas (listed
 * in the <tt>contextPath</tt>). Since each schema in the schema set can belong
 * to distinct namespaces, the unification of schemas to an unmarshalling 
 * context should be namespace independent.  This means that a client 
 * application is able to unmarshal XML documents that are instances of
 * any of the schemas listed in the <tt>contextPath</tt>.  For example:
 *
 * <pre>
 *        JAXBContext jc = JAXBContext.newInstance( "com.acme.foo:com.acme.bar" );
 *        Unmarshaller u = jc.createUnmarshaller();
 *        FooObject fooObj = (FooObject)u.unmarshal( new File( "foo.xml" ) ); // ok
 *        BarObject barObj = (BarObject)u.unmarshal( new File( "bar.xml" ) ); // ok
 *        BazObject bazObj = (BazObject)u.unmarshal( new File( "baz.xml" ) ); // error, "com.acme.baz" not in contextPath
 * </pre>
 *
 * <p>
 * The client application may also generate Java content trees explicitly rather
 * than unmarshalling existing XML data.  To do so, the application needs to 
 * have access and knowledge about each of the schema derived <tt>
 * ObjectFactory</tt> classes that exist in each of java packages contained 
 * in the <tt>contextPath</tt>.  For each schema derived java class, there will 
 * be a static factory method that produces objects of that type.  For example, 
 * assume that after compiling a schema, you have a package <tt>com.acme.foo</tt> 
 * that contains a schema derived interface named <tt>PurchaseOrder</tt>.  In 
 * order to create objects of that type, the client application would use the 
 * factory method like this:
 *
 * <pre>
 *       com.acme.foo.PurchaseOrder po = 
 *           com.acme.foo.ObjectFactory.createPurchaseOrder();
 * </pre>
 *
 * <p>
 * Once the client application has an instance of the the schema derived object,
 * it can use the mutator methods to set content on it.
 *
 * <p>
 * For more information on the generated <tt>ObjectFactory</tt> classes, see
 * Section 4.2 <i>Java Package</i> of the specification.
 *
 * <p>
 * <i><B>SPEC REQUIREMENT:</B> the provider must generate a class in each
 * package that contains all of the necessary object factory methods for that 
 * package named ObjectFactory as well as the static 
 * <tt>newInstance( javaContentInterface )</tt> method</i>  
 * </blockquote>
 *
 * <p>
 * <b>Marshalling</b>
 * <p>
 * <blockquote>
 * The {@link Marshaller} class provides the client application the ability
 * to convert a Java content tree back into XML data.  There is no difference
 * between marshalling a content tree that is created manually using the factory
 * methods and marshalling a content tree that is the result an <tt>unmarshal
 * </tt> operation.  Clients can marshal a java content tree back to XML data
 * to a <tt>java.io.OutputStream</tt> or a <tt>java.io.Writer</tt>.  The 
 * marshalling process can alternatively produce SAX2 event streams to a 
 * registered <tt>ContentHandler</tt> or produce a DOM Node object.  
 * <!-- don't expose fragment support yet 
 * Client applications 
 * have control over the output encoding as well as whether or not to marshal 
 * the XML data as a complete document or as a fragment.
 * -->
 *
 * <p>
 * Here is a simple example that unmarshals an XML document and then marshals
 * it back out:
 *
 * <pre>
 *        JAXBContext jc = JAXBContext.newInstance( "com.acme.foo" );
 *
 *        // unmarshal from foo.xml
 *        Unmarshaller u = jc.createUnmarshaller();
 *        FooObject fooObj = (FooObject)u.unmarshal( new File( "foo.xml" ) );
 *
 *        // marshal to System.out
 *        Marshaller m = jc.createMarshaller();
 *        m.marshal( fooObj, System.out );
 * </pre>
 * </blockquote>
 *
 * <p>
 * <b>Validation</b>
 * <p>
 * <blockquote>
 * Validation has been changed significantly since JAXB 1.0.  The {@link Validator}
 * class has been deprecated and made optional.  This means that you are advised
 * not to use this class and, in fact, it may not even be available depending on
 * your JAXB provider.  JAXB 1.0 client applications that rely on <tt>Validator</tt>
 * will still work properly when deployed with the JAXB 1.0 runtime system.
 *
 * In JAXB 2.0, the {@link Unmarshaller} has included convenince methods that expose
 * the JAXP 1.3 {@link javax.xml.validation} framework.  Please refer to the
 * {@link Unmarshaller#setSchema(javax.xml.validation.Schema)} API for more
 * information.
 * </blockquote>
 *
 * <p>
 * <b>JAXB Runtime Binding Framework Compatibility</b><br>
 * <blockquote>
 * The following JAXB 1.0 restriction only applies to binding schema to 
 * interfaces/implementation classes.
 * Since this binding does not require a common runtime system, a JAXB 
 * client application must not attempt to mix runtime objects (<tt>JAXBContext,
 * Marshaller</tt>, etc. ) from different providers.  This does not 
 * mean that the client application isn't portable, it simply means that a 
 * client has to use a runtime system provided by the same provider that was 
 * used to compile the schema.
 * </blockquote>
 *
 * @author <ul><li>Ryan Shoemaker, Sun Microsystems, Inc.</li><li>Kohsuke Kawaguchi, Sun Microsystems, Inc.</li><li>Joe Fialli, Sun Microsystems, Inc.</li></ul>
 * @version $Revision: 1.3 $ $Date: 2005-02-17 21:06:39 $
 * @see Marshaller
 * @see Unmarshaller
 * @since JAXB1.0
 */
public abstract class JAXBContext {
    
    /**
     * The name of the property that contains the name of the class capable
     * of creating new <tt>JAXBContext</tt> objects.
     */
    public static final String JAXB_CONTEXT_FACTORY = 
        "javax.xml.bind.context.factory";
       

    protected JAXBContext() {
    }

    
    /**
     * <p>
     * Obtain a new instance of a <tt>JAXBContext</tt> class.
     *
     * <p>
     * This is a convenience method for the 
     * {@link #newInstance(String,ClassLoader) newInstance} method.  It uses
     * the context class loader of the current thread.  To specify the use of
     * a different class loader, either set it via the 
     * <tt>Thread.setContextClassLoader()</tt> api or use the 
     * {@link #newInstance(String,ClassLoader) newInstance} method.
     */
    public static JAXBContext newInstance( String contextPath ) 
        throws JAXBException {
            
        //return newInstance( contextPath, JAXBContext.class.getClassLoader() );
        return newInstance( contextPath, Thread.currentThread().getContextClassLoader() );
    }
    
    /**
     * <p>
     * Obtain a new instance of a <tt>JAXBContext</tt> class.
     *
     * <p>
     * The client application must supply a context path which is a list of 
     * colon (':', \u005Cu003A) separated java package names that contain schema 
     * derived classes.
     *
     * <p>
     * In the case of schema to java interface/implementation binding,
     * the JAXB provider will ensure that each package on the context path
     * has a <tt>jaxb.properties</tt> file which contains a value for the 
     * <tt>javax.xml.bind.context.factory</tt> property and that all values
     * resolve to the same provider.  This requirement does not apply to
     * JAXB annotated classes.
     *
     * <p>
     * If there are any global XML element name collisions across the various 
     * packages listed on the <tt>contextPath</tt>, a <tt>JAXBException</tt> 
     * will be thrown.
     *
     * <p>
     * Mixing generated interface/impl bindings from multiple JAXB Providers
     * in the same context path may result in a <tt>JAXBException</tt>
     * being thrown.
     *  
     * @param contextPath list of java package names that contain schema 
     *                     derived classes
     * @param classLoader
     *      This class loader will be used to locate the implementation
     *      classes.
     *
     * @return a new instance of a <tt>JAXBContext</tt>
     * @throws JAXBException if an error was encountered while creating the
     *                       <tt>JAXBContext</tt>, such as an ambiguity among
     *                       global elements contained in the contextPath,
     *                       failure to locate a value for the context factory
     *                       property, or mixing schema derived packages from
     *                       different providers on the same contextPath.
     */
    public static JAXBContext newInstance( String contextPath, ClassLoader classLoader ) throws JAXBException {

        return newInstance(contextPath,classLoader,Collections.<String,Object>emptyMap());
    }

    /**
     * <p>
     * Obtain a new instance of a <tt>JAXBContext</tt> class.
     *
     * <p>
     * This is mostly the same as {@link JAXBContext#newInstance(String, ClassLoader)},
     * but this version allows you to pass in provider-specific properties to configure
     * the instanciation of {@link JAXBContext}.
     *
     * <p>
     * The interpretation of properties is up to implementations.
     *
     * @param contextPath list of java package names that contain schema derived classes
     * @param classLoader
     *      This class loader will be used to locate the implementation classes.
     * @param properties
     *      provider-specific properties
     *
     * @return a new instance of a <tt>JAXBContext</tt>
     * @throws JAXBException if an error was encountered while creating the
     *                       <tt>JAXBContext</tt>, such as an ambiguity among
     *                       global elements contained in the contextPath,
     *                       failure to locate a value for the context factory
     *                       property, or mixing schema derived packages from
     *                       different providers on the same contextPath.
     *
     * @since JAXB 2.0
     */
    public static JAXBContext newInstance( String contextPath, ClassLoader classLoader, Map<String,?>  properties  )
        throws JAXBException {

        return ContextFinder.find(
                        /* The default property name according to the JAXB spec */
                        JAXB_CONTEXT_FACTORY,

                        /* the context path supplied by the client app */
                        contextPath,

                        /* class loader to be used */
                        classLoader,
                        properties );
    }

// TODO: resurrect this once we introduce external annotations
//    /**
//     * <p>
//     * Obtain a new instance of a <tt>JAXBContext</tt> class.
//     *
//     * <p>
//     * The client application must supply a list of classes that the new
//     * context object needs to recognize.
//     *
//     * Not only the new context will recognize all the classes specified,
//     * but it will also recognize any classes that are directly/indirectly
//     * referenced statically from the specified classes.
//     *
//     * For example, in the following Java code, if you do
//     * <tt>newInstance(Foo.class)</tt>, the newly created {@link JAXBContext}
//     * will recognize both <tt>Foo</tt> and <tt>Bar</tt>, but not <tt>Zot</tt>:
//     * <pre><xmp>
//     * class Foo {
//     *      Bar b;
//     * }
//     * class Bar { int x; }
//     * class Zot extends Bar { int y; }
//     * </xmp></pre>
//     *
//     * Therefore, a typical client application only needs to specify the
//     * top-level classes, but it needs to be careful.
//     *
//     * TODO: if we are to define other mechanisms, refer to them.
//     *
//     * @param externalBindings
//     *      list of external binding files. Can be null or empty if none is used.
//     *      when specified, those files determine how the classes are bound.
//     *
//     * @param classesToBeBound
//     *      list of java classes to be recognized by the new {@link JAXBContext}.
//     *      Can be empty, in which case a {@link JAXBContext} that only knows about
//     *      spec-defined classes will be returned.
//     *
//     * @return
//     *      A new instance of a <tt>JAXBContext</tt>. Always non-null valid object.
//     *
//     * @throws JAXBException
//     *      if an error was encountered while creating the
//     *      <tt>JAXBContext</tt>, such as (but not limited to):
//     * <ol>
//     *  <li>No JAXB implementation was discovered
//     *  <li>Classes use JAXB annotations incorrectly
//     *  <li>Classes have colliding annotations (i.e., two classes with the same type name)
//     *  <li>Specified external bindings are incorrect
//     *  <li>The JAXB implementation was unable to locate
//     *      provider-specific out-of-band information (such as additional
//     *      files generated at the development time.)
//     * </ol>
//     *
//     * @throws IllegalArgumentException
//     *      if the parameter contains {@code null} (i.e., {@code newInstance(null);})
//     *
//     * @since 2.0
//     */
//    public static JAXBContext newInstance( Source[] externalBindings, Class... classesToBeBound )
//        throws JAXBException {
//
//        // empty class list is not an error, because the context will still include
//        // spec-specified classes like String and Integer.
//        // if(classesToBeBound.length==0)
//        //    throw new IllegalArgumentException();
//
//        // but it is an error to have nulls in it.
//        for( int i=classesToBeBound.length-1; i>=0; i-- )
//            if(classesToBeBound[i]==null)
//                throw new IllegalArgumentException();
//
//        return ContextFinder.find(externalBindings,classesToBeBound);
//    }

    /**
     * <p>
     * Obtain a new instance of a <tt>JAXBContext</tt> class.
     *
     * <p>
     * The client application must supply a list of classes that the new
     * context object needs to recognize.
     *
     * Not only the new context will recognize all the classes specified,
     * but it will also recognize any classes that are directly/indirectly
     * referenced statically from the specified classes.
     *
     * For example, in the following Java code, if you do
     * <tt>newInstance(Foo.class)</tt>, the newly created {@link JAXBContext}
     * will recognize both <tt>Foo</tt> and <tt>Bar</tt>, but not <tt>Zot</tt>:
     * <pre><xmp>
     * class Foo {
     *      Bar b;
     * }
     * class Bar { int x; }
     * class Zot extends Bar { int y; }
     * </xmp></pre>
     *
     * Therefore, a typical client application only needs to specify the
     * top-level classes, but it needs to be careful.
     *
     * TODO: if we are to define other mechanisms, refer to them.
     *
     * @param classesToBeBound
     *      list of java classes to be recognized by the new {@link JAXBContext}.
     *      Can be empty, in which case a {@link JAXBContext} that only knows about
     *      spec-defined classes will be returned.
     *
     * @return
     *      A new instance of a <tt>JAXBContext</tt>. Always non-null valid object.
     *
     * @throws JAXBException
     *      if an error was encountered while creating the
     *      <tt>JAXBContext</tt>, such as (but not limited to):
     * <ol>
     *  <li>No JAXB implementation was discovered
     *  <li>Classes use JAXB annotations incorrectly
     *  <li>Classes have colliding annotations (i.e., two classes with the same type name)
     *  <li>The JAXB implementation was unable to locate
     *      provider-specific out-of-band information (such as additional
     *      files generated at the development time.)
     * </ol>
     *
     * @throws IllegalArgumentException
     *      if the parameter contains {@code null} (i.e., {@code newInstance(null);})
     *
     * @since 2.0
     */
    public static JAXBContext newInstance( Class... classesToBeBound )
        throws JAXBException {

        return newInstance(classesToBeBound,Collections.<String,Object>emptyMap());
    }

    /**
     * <p>
     * Obtain a new instance of a <tt>JAXBContext</tt> class.
     *
     * <p>
     * This is mostly the same as {@link JAXBContext#newInstance(Class[])},
     * but this version allows you to pass in an implementation-specific
     * 'properties' to configure the instanciation of {@link JAXBContext}.
     *
     * <p>
     * The interpretation of properties is up to implementations.
     *
     * @param classesToBeBound
     *      list of java classes to be recognized by the new {@link JAXBContext}.
     *      Can be empty, in which case a {@link JAXBContext} that only knows about
     *      spec-defined classes will be returned.
     *
     * @return
     *      A new instance of a <tt>JAXBContext</tt>. Always non-null valid object.
     *
     * @throws JAXBException
     *      if an error was encountered while creating the
     *      <tt>JAXBContext</tt>, such as (but not limited to):
     * <ol>
     *  <li>No JAXB implementation was discovered
     *  <li>Classes use JAXB annotations incorrectly
     *  <li>Classes have colliding annotations (i.e., two classes with the same type name)
     *  <li>The JAXB implementation was unable to locate
     *      provider-specific out-of-band information (such as additional
     *      files generated at the development time.)
     * </ol>
     *
     * @throws IllegalArgumentException
     *      if the parameter contains {@code null} (i.e., {@code newInstance(null);})
     *
     * @since 2.0
     */
    public static JAXBContext newInstance( Class[] classesToBeBound, Map<String,?> properties )
        throws JAXBException {

        // empty class list is not an error, because the context will still include
        // spec-specified classes like String and Integer.
        // if(classesToBeBound.length==0)
        //    throw new IllegalArgumentException();

        // but it is an error to have nulls in it.
        for( int i=classesToBeBound.length-1; i>=0; i-- )
            if(classesToBeBound[i]==null)
                throw new IllegalArgumentException();

        return ContextFinder.find(classesToBeBound,properties);
    }

    /**
     * Create an <tt>Unmarshaller</tt> object that can be used to convert XML
     * data into a java content tree.
     *
     * @return an <tt>Unmarshaller</tt> object
     *
     * @throws JAXBException if an error was encountered while creating the
     *                       <tt>Unmarshaller</tt> object
     */    
    public abstract Unmarshaller createUnmarshaller() throws JAXBException;
    
    
    /** 
     * Create a <tt>Marshaller</tt> object that can be used to convert a 
     * java content tree into XML data.
     *
     * @return a <tt>Marshaller</tt> object
     *
     * @throws JAXBException if an error was encountered while creating the
     *                       <tt>Marshaller</tt> object
     */    
    public abstract Marshaller createMarshaller() throws JAXBException;
    
    
    /**
     * {@link Validator} has been made optional and deprecated in JAXB 2.0.  Please
     * refer to the javadoc for {@link Validator} for more detail.
     * <p>
     * Create a <tt>Validator</tt> object that can be used to validate a
     * java content tree against its source schema.
     *
     * @return a <tt>Validator</tt> object
     *
     * @throws JAXBException if an error was encountered while creating the
     *                       <tt>Validator</tt> object
     * @deprecated since JAXB 2.0
     */    
    public abstract Validator createValidator() throws JAXBException;

    /**
     * Creates a <tt>JAXBIntrospector</tt> object that can be used to
     * introspect JAXB objects.
     *
     * @return
     *      always return a non-null valid <tt>JAXBIntrospector</tt> object.
     *
     * @throws JAXBException if an error was encountered while creating the
     *                       <tt>JAXBIntrospector</tt> object.
     *
     * @throws UnsupportedOperationException
     *      Calling this method on JAXB 1.0 implementations will throw
     *      an UnsupportedOperationException.
     *  
     * @since JAXB 2.0
     */
    public JAXBIntrospector createJAXBIntrospector() throws JAXBException {
        // to make JAXB 1.0 implementations work, this method must not be
        // abstract
        throw new UnsupportedOperationException();
    }

}