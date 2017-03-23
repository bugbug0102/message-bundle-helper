package org.b0102.msgbundle.helper

import java.io.File
import java.util.regex.Pattern

import org.apache.commons.io.FileUtils
import org.json.JSONObject
import java.util.Properties
import collection.JavaConverters._
import java.io.FileOutputStream
import org.apache.commons.io.IOUtils

class JsonBundle 
{
  private val KEY_PREFIX = "##prefix##"
  
  @throws(classOf[Exception])
  def convert(jsonPath:String):Unit =
  {
    val file = new File(jsonPath)
    val text = FileUtils.readFileToString(file, "UTF-8")
    val jo = new JSONObject(text)
    
    val prefix = jo.getString(KEY_PREFIX)
    
    val map = collection.mutable.HashMap[String,Properties]()

    val itr = jo.keys()
    while(itr.hasNext())
    {
      val msgKey = itr.next()
      
      val msgValues = jo.optJSONObject(msgKey)
      if(Option(msgValues).isDefined)
      {
        val langKeys = msgValues.keySet().asScala.foreach{lk=>
        
          val fullLangKey = s"${prefix}${lk}"
          val prop = map.getOrElseUpdate(fullLangKey, (new Properties))
          prop.setProperty(msgKey, msgValues.getString(lk))
        }
      }
    }
    
    val parent = file.getParentFile
    map.foreach{case(key,value)=>
      val propFile = new File(parent, s"${key}.properties")
      var fos:FileOutputStream = null
      try
      {
        fos = new FileOutputStream(propFile)
        value.store(fos, null)
        
      }finally
      {
        IOUtils.closeQuietly(fos)
      }
    }
  }
  
}

object JsonBundle
{
  private def showHelp():Unit = 
  {
    println("A utility for converting JSON file to message bundles")
    println("Author: bugbug0102")
    println
    println(s"Usage: ${classOf[JsonBundle].getName} [json file]")
    
  }
  
  def main(args:Array[String]):Unit = 
  {
    if(args.length>=1)
    {
      new JsonBundle().convert(args(0))
      
    }else
    {
      showHelp() 
    }
  }
}