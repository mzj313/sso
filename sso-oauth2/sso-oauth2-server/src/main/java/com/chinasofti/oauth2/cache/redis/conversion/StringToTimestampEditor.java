package com.chinasofti.oauth2.cache.redis.conversion;

import java.beans.PropertyEditorSupport;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.springframework.util.StringUtils;

public class StringToTimestampEditor extends PropertyEditorSupport
{
  public void setAsText(String text)
    throws IllegalArgumentException
  {
    if ((!StringUtils.isEmpty(text)) && (text != null)) {
      long time = Long.valueOf(text).longValue();

      setValue(new Timestamp(time));
    }
  }

  public String getAsText()
  {
    if (getValue() == null) return null;
    String tsStr = "";
    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    try
    {
      tsStr = sdf.format(getValue());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return tsStr;
  }
}