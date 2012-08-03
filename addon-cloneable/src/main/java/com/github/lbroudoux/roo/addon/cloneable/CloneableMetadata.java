package com.github.lbroudoux.roo.addon.cloneable;

import static org.springframework.roo.model.JpaJavaType.MANY_TO_ONE;
import static org.springframework.roo.model.JpaJavaType.FETCH_TYPE;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.roo.classpath.PhysicalTypeIdentifierNamingUtils;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.details.FieldMetadata;
import org.springframework.roo.classpath.details.FieldMetadataBuilder;
import org.springframework.roo.classpath.details.MethodMetadata;
import org.springframework.roo.classpath.details.MethodMetadataBuilder;
import org.springframework.roo.classpath.details.annotations.AnnotatedJavaType;
import org.springframework.roo.classpath.details.annotations.AnnotationMetadataBuilder;
import org.springframework.roo.classpath.itd.AbstractItdTypeDetailsProvidingMetadataItem;
import org.springframework.roo.classpath.itd.InvocableMemberBodyBuilder;
import org.springframework.roo.metadata.MetadataIdentificationUtils;
import org.springframework.roo.model.EnumDetails;
import org.springframework.roo.model.JavaSymbolName;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.project.LogicalPath;

/**
 * This type produces metadata for a new ITD. It uses an {@link ItdTypeDetailsBuilder} provided by 
 * {@link AbstractItdTypeDetailsProvidingMetadataItem} to register a field in the ITD and a new method.
 * 
 * @since 1.1.0
 */
public class CloneableMetadata extends AbstractItdTypeDetailsProvidingMetadataItem {

    // Constants
    private static final String PROVIDES_TYPE_STRING = CloneableMetadata.class.getName();
    private static final String PROVIDES_TYPE = MetadataIdentificationUtils.create(PROVIDES_TYPE_STRING);

    private String entityName;
    private List<FieldMetadata> cloneableFields;
    
    public static final String getMetadataIdentiferType() {
        return PROVIDES_TYPE;
    }
    
    public static final String createIdentifier(JavaType javaType, LogicalPath path) {
        return PhysicalTypeIdentifierNamingUtils.createIdentifier(PROVIDES_TYPE_STRING, javaType, path);
    }

    public static final JavaType getJavaType(String metadataIdentificationString) {
        return PhysicalTypeIdentifierNamingUtils.getJavaType(PROVIDES_TYPE_STRING, metadataIdentificationString);
    }

    public static final LogicalPath getPath(String metadataIdentificationString) {
        return PhysicalTypeIdentifierNamingUtils.getPath(PROVIDES_TYPE_STRING, metadataIdentificationString);
    }

    public static boolean isValid(String metadataIdentificationString) {
        return PhysicalTypeIdentifierNamingUtils.isValid(PROVIDES_TYPE_STRING, metadataIdentificationString);
    }
    
    public CloneableMetadata(String identifier, JavaType aspectName, PhysicalTypeMetadata governorPhysicalTypeMetadata, List<FieldMetadata> cloneableFields) {
        super(identifier, aspectName, governorPhysicalTypeMetadata);
        Validate.isTrue(isValid(identifier), "Metadata identification string '" + identifier + "' does not appear to be a valid");

        // Initialize entity name.
        entityName = getJavaType(identifier).getSimpleTypeName();
        this.cloneableFields = cloneableFields;
        
        EnumDetails fetchType = (EnumDetails)governorPhysicalTypeMetadata.getMemberHoldingTypeDetails()
              .getAnnotation(new JavaType(RooCloneable.class)).getAttribute("fetch").getValue();
        
        // Add reference field.
        builder.addField(getCloneReferenceField(fetchType.getField().getSymbolName()));
            
        // Add methods.
        builder.addMethod(getCloneReferenceMethod());
        builder.addMethod(getClonesMethod());
        builder.addMethod(createCloneMethod());
        
        // Create a representation of the desired output ITD
        itdTypeDetails = builder.build();
    }
    
    /**
     * Create metadata for a field definition. 
     *
     * @return a FieldMetadata object
     */
    private FieldMetadata getCloneReferenceField(String fetch) {
        
        List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();
        AnnotationMetadataBuilder amb = new AnnotationMetadataBuilder(MANY_TO_ONE);
        amb.addEnumAttribute("fetch", FETCH_TYPE, new JavaSymbolName(fetch.toString().toUpperCase()));
        annotations.add(amb);
        
        // Using the FieldMetadataBuilder to create the field definition. 
        final FieldMetadataBuilder fieldBuilder = new FieldMetadataBuilder(getId(), // Metadata ID provided by supertype
            Modifier.PRIVATE, 
            annotations,
            new JavaSymbolName("cloneReference"), // Field name
            getJavaType(getId())); // Field type
        
        return fieldBuilder.build(); // Build and return a FieldMetadata instance
    }
    
    private MethodMetadata getCloneReferenceMethod() {
        // Specify the desired method name
        JavaSymbolName methodName = new JavaSymbolName("getCloneReference");
        
        // Check if a method with the same signature already exists in the target type
        final MethodMetadata method = methodExists(methodName, new ArrayList<AnnotatedJavaType>());
        if (method != null) {
            // If it already exists, just return the method and omit its generation via the ITD
            return method;
        }
        
        // Define method parameter types (none in this case)
        List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
        
        // Define method parameter names (none in this case)
        List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
        
        // Create the method body
        InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
        bodyBuilder.appendFormalLine("return this.cloneReference;");
        
        // Use the MethodMetadataBuilder for easy creation of MethodMetadata
        MethodMetadataBuilder methodBuilder = new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName, 
              getJavaType(getId()), parameterTypes, parameterNames, bodyBuilder);
        
        return methodBuilder.build(); // Build and return a MethodMetadata instance
    }
    
    private MethodMetadata getClonesMethod(){
       // Specify the desired method name
       JavaSymbolName methodName = new JavaSymbolName("getClones");
       
       // Check if a method with the same signature already exists in the target type
       final MethodMetadata method = methodExists(methodName, new ArrayList<AnnotatedJavaType>());
       if (method != null) {
           // If it already exists, just return the method and omit its generation via the ITD
           return method;
       }
       
       // Define method parameter types (none in this case)
       List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
       
       // Define method parameter names (none in this case)
       List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
       
       // Define return type.
       JavaType returnType = JavaType.listOf(destination);
       
       // Create the method body
       InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
       bodyBuilder.appendFormalLine("return entityManager().createQuery(\"SELECT o FROM " 
             + entityName + " o WHERE o.cloneReference=:reference\").setParameter(\"reference\", this).getResultList();");
       
       // Use the MethodMetadataBuilder for easy creation of MethodMetadata
       MethodMetadataBuilder methodBuilder = new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName, 
             returnType, parameterTypes, parameterNames, bodyBuilder);
       
       return methodBuilder.build(); // Build and return a MethodMetadata instance
    }
    
    private MethodMetadata createCloneMethod(){
       // Specify the desired method name
       JavaSymbolName methodName = new JavaSymbolName("createClone");
       
       // Check if a method with the same signature already exists in the target type
       final MethodMetadata method = methodExists(methodName, new ArrayList<AnnotatedJavaType>());
       if (method != null) {
           // If it already exists, just return the method and omit its generation via the ITD
           return method;
       }
       
       // Define method parameter types (none in this case)
       List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
       
       // Define method parameter names (none in this case)
       List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
       
       // Create the method body
       InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
       bodyBuilder.appendFormalLine(entityName + " clone = new " + entityName + "();");
       bodyBuilder.appendFormalLine("clone.cloneReference = this;");
       for (final FieldMetadata field : cloneableFields){ 
          bodyBuilder.appendFormalLine("clone." + field.getFieldName() + " = this." + field.getFieldName() + ";");
       }
       bodyBuilder.appendFormalLine("clone.persist();");
       bodyBuilder.appendFormalLine("return clone;");
       
       // Use the MethodMetadataBuilder for easy creation of MethodMetadata
       MethodMetadataBuilder methodBuilder = new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName, 
             destination, parameterTypes, parameterNames, bodyBuilder);
       
       return methodBuilder.build(); // Build and return a MethodMetadata instance
    }
        
    private MethodMetadata methodExists(JavaSymbolName methodName, List<AnnotatedJavaType> paramTypes) {
        // We have no access to method parameter information, so we scan by name alone and treat any match as authoritative
        // We do not scan the superclass, as the caller is expected to know we'll only scan the current class
        for (MethodMetadata method : governorTypeDetails.getDeclaredMethods()) {
            if (method.getMethodName().equals(methodName) && method.getParameterTypes().equals(paramTypes)) {
                // Found a method of the expected name; we won't check method parameters though
                return method;
            }
        }
        return null;
    }
    
    // Typically, no changes are required beyond this point
    
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("identifier", getId());
        builder.append("valid", valid);
        builder.append("aspectName", aspectName);
        builder.append("destinationType", destination);
        builder.append("governor", governorPhysicalTypeMetadata.getId());
        builder.append("itdTypeDetails", itdTypeDetails);
        return builder.toString();
    }
}
