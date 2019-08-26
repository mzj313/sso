package com.chinasofti.oauth2.cache.redis.conversion;

import java.beans.PropertyEditorSupport;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.StringUtils;

public class StringToDateEditor extends PropertyEditorSupport
{
  public void setAsText(String text)
    throws IllegalArgumentException
  {
    if ((!StringUtils.isEmpty(text)) && (text != null))
    {
      Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
      Matcher match = pattern.matcher(text);
      DateFormat sdf;
      if (match.matches())
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      else
        sdf = new SimpleDateFormat("yyyy-MM-dd");
      try
      {
        setValue(sdf.parse(text));
      } catch (ParseException e) {
        setValue(new Date(text));
      }
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