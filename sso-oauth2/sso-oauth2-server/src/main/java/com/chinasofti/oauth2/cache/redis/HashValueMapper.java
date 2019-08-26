package com.chinasofti.oauth2.cache.redis;

import java.beans.PropertyDescriptor;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.chinasofti.oauth2.cache.redis.conversion.InvalideMapException;
import com.chinasofti.oauth2.cache.redis.conversion.StringToDateEditor;
import com.chinasofti.oauth2.cache.redis.conversion.StringToTimestampEditor;

public class HashValueMapper<T>
{
  protected final Log logger = LogFactory.getLog(getClass());
  private Class<T> mappedClass;
  private boolean checkFullyPopulated = false;
  private boolean primitivesDefaultedForNullValue = false;
  private Map<String, PropertyDescriptor> mappedFields;
  private Set<String> mappedProperties;

  public HashValueMapper()
  {
  }

  public HashValueMapper(Class<T> mappedClass)
  {
    initialize(mappedClass);
  }

  public HashValueMapper(Class<T> mappedClass, boolean checkFullyPopulated) {
    initialize(mappedClass);
    this.checkFullyPopulated = checkFullyPopulated;
  }

  public void setMappedClass(Class<T> mappedClass) {
    if (this.mappedClass == null)
      initialize(mappedClass);
    else if (this.mappedClass.equals(mappedClass));
  }

  protected void initialize(Class<T> mappedClass)
  {
    this.mappedClass = mappedClass;
    this.mappedFields = new HashMap();
    this.mappedProperties = new HashSet();
    PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(mappedClass);
    PropertyDescriptor[] var3 = pds;
    int var4 = pds.length;

    for (int var5 = 0; var5 < var4; var5++) {
      PropertyDescriptor pd = var3[var5];
      if (pd.getWriteMethod() != null) {
        this.mappedFields.put(pd.getName().toLowerCase(), pd);
        String underscoredName = underscoreName(pd.getName());
        if (!pd.getName().toLowerCase().equals(underscoredName)) {
          this.mappedFields.put(underscoredName, pd);
        }

        this.mappedProperties.add(pd.getName());
      }
    }
  }

  private String underscoreName(String name)
  {
    if (!StringUtils.hasLength(name)) {
      return "";
    }
    StringBuilder result = new StringBuilder();
    result.append(name.substring(0, 1).toLowerCase());

    for (int i = 1; i < name.length(); i++) {
      String s = name.substring(i, i + 1);
      String slc = s.toLowerCase();
      if (!s.equals(slc))
        result.append("_").append(slc);
      else {
        result.append(s);
      }
    }

    return result.toString();
  }

  public final Class<T> getMappedClass()
  {
    return this.mappedClass;
  }

  public void setCheckFullyPopulated(boolean checkFullyPopulated) {
    this.checkFullyPopulated = checkFullyPopulated;
  }

  public boolean isCheckFullyPopulated() {
    return this.checkFullyPopulated;
  }

  public void setPrimitivesDefaultedForNullValue(boolean primitivesDefaultedForNullValue) {
    this.primitivesDefaultedForNullValue = primitivesDefaultedForNullValue;
  }

  public boolean isPrimitivesDefaultedForNullValue() {
    return this.primitivesDefaultedForNullValue;
  }

  public T mapRow(Map<String, String> map) {
    if (map.size() == 0) {
      return null;
    }
    Assert.state(this.mappedClass != null, "Mapped class was not specified");
    T mappedObject = BeanUtils.instantiate(this.mappedClass);
    BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(mappedObject);
    initBeanWrapper(bw);
    HashSet populatedProperties = isCheckFullyPopulated() ? new HashSet() : null;
    Set<String> columnSet = map.keySet();
    for (String column : columnSet) {
      PropertyDescriptor pd = (PropertyDescriptor)this.mappedFields.get(column.replaceAll(" ", "").toLowerCase());
      if (pd != null) {
        try {
          String ex = (String)map.get(column);
          if (this.logger.isDebugEnabled()) {
            this.logger.debug("Mapping column '" + column + "' to property '" + pd.getName() + "' of type " + pd.getPropertyType());
          }
          try
          {
            Object value = ex;
            Class clazz = bw.getPropertyType(pd.getName());
            if (!clazz.isInstance(ex));
            bw.registerCustomEditor(Timestamp.class, new StringToTimestampEditor());
            bw.registerCustomEditor(Date.class, new StringToDateEditor());
            bw.setPropertyValue(pd.getName(), value);
          } catch (TypeMismatchException var13) {
            if ((ex != null) || (!this.primitivesDefaultedForNullValue)) {
              throw var13;
            }

            this.logger.debug("Intercepted TypeMismatchException for column '" + column + "' with value " + ex + " when setting property '" + pd.getName() + "' of type " + pd.getPropertyType() + " on object: " + mappedObject);
          }

          if (populatedProperties != null)
            populatedProperties.add(pd.getName());
        }
        catch (NotWritablePropertyException var14) {
          throw new InvalideMapException("Unable to map column " + column + " to property " + pd.getName(), var14);
        }
      }
    }

    if ((populatedProperties != null) && (!populatedProperties.equals(this.mappedProperties))) {
      throw new InvalideMapException("Given Map does not contain all fields necessary to populate object of class [" + this.mappedClass + "]: " + this.mappedProperties);
    }
    return mappedObject;
  }

  protected void initBeanWrapper(BeanWrapper bw)
  {
  }

  public static <T> HashValueMapper<T> newInstance(Class<T> mappedClass)
  {
    HashValueMapper newInstance = new HashValueMapper();
    newInstance.setMappedClass(mappedClass);
    return newInstance;
  }
}