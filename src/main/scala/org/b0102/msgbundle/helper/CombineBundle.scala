package org.b0102.msgbundle.helper

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.Properties

import org.apache.commons.io.IOUtils
import java.io.FilenameFilter


class CombineBundle 
{
  
  @throws(classOf[IOException])
  def combine(paths:Array[String], outputPath:String):Int = 
  {
    val ret = new Properties
    paths.toList.foreach{p=>
       
      var fis:FileInputStream = null
      try
      {
        fis = new FileInputStream(p)
        ret.load(fis)
        
      }finally
      {
        IOUtils.closeQuietly(fis)
      }
    }
    
    var fos:FileOutputStream = null
    try
    {
      fos = new FileOutputStream(outputPath)
      ret.store(fos, null)
      
    }finally
    {
      IOUtils.closeQuietly(fos) 
    }
    return paths.length
  }
}

object CombineBundle extends FilenameFilter
{
  private def showHelp():Unit = 
  {
    println("A utility for combing message bundles")
    println("Author: bugbug0102")
    println
    println(s"Usage: ${classOf[CombineBundle].getName} [directory] [output]")
  }
  
  override def accept(dir:File, name:String):Boolean = name.toLowerCase().endsWith(".properties") 
  
  def main(args:Array[String]):Unit = 
  {
    if(args.length>=2)
    {
      val directory = args(0)
      val f = new File(directory)
      new CombineBundle().combine(f.list(this).map(s=>new File(f, s).getAbsolutePath), args(1))
      
    }else
    {
      showHelp() 
    }
  }
  
  
}