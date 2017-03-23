package org.b0102.msgbundle.helper

import java.io.FileInputStream
import java.io.InputStream
import java.util.Properties
import java.util.regex.Pattern

import scala.collection.mutable.ListBuffer

import org.apache.commons.io.IOUtils
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.io.FileOutputStream

class XlsBundle 
{
  private val pattern = Pattern.compile("(.+)_([a-zA-Z]{2})_([a-zA-Z]{1,2})")
  
  @throws(classOf[Exception])
  def convert(xlsPath:String):Unit =
  {
    var ret = 0
    val properties = new ListBuffer[Properties]()
    
    var is:InputStream = null
    try
    {
      is = new FileInputStream(xlsPath)
      val wb = WorkbookFactory.create(is)
      val sheet = wb.getSheetAt(0)
      
      val itr = sheet.iterator()
      val firstRow = itr.next()
      
      val nbColumns = firstRow.getLastCellNum
      if(nbColumns > 2)
      {
        /** Create Empty Properties **/
        val range = (1 until nbColumns).toStream
        properties ++= range.map(p=>new Properties())
        
        while(itr.hasNext())
        {
          val r = itr.next()
          val cellZero = r.getCell(0)
          if(Option(cellZero).isDefined)
          {
            val key = cellZero.getStringCellValue
            if(StringUtils.isNotBlank(key))
            {
              range.foreach{i =>
                
                val prop = properties(i-1)
                if(r.getLastCellNum>1)
                {
                  val v = r.getCell(i).getStringCellValue
                  prop.setProperty(key, v)
                }else
                {
                  prop.setProperty(key, "") 
                }
              }
            }
            ret +=1 
          }
        }
        
        val basePath = new File(xlsPath).getParentFile
        range.foreach{i=>
          
          val outputFilename = firstRow.getCell(i).getStringCellValue
          val parts = outputFilename.split("\\.")
          val outputFile = 
          {
            if(parts.length > 1)
            {
              val sb = new StringBuilder()
              val m = pattern.matcher(parts(0))
              if(m.find())
              {
                sb.append(s"${m.group(1)}_${m.group(2)}_${m.group(3).toUpperCase()}")
              }else
              {
                sb.append(parts(0))
              }
              sb.append(".").append(parts(1))
              new File(basePath, sb.toString())
            }else
            {
              new File(basePath, outputFilename) 
            }
          }
          
          val prop = properties(i-1)
          var fos:FileOutputStream = null
          try
          {
            fos = new FileOutputStream(outputFile)
            prop.store(fos, null)
            
          }finally
          {
            IOUtils.closeQuietly(fos)
          }
        }
      }
      
      
    }finally
    {
      IOUtils.closeQuietly(is)
    }
    
  }
  
}

object XlsBundle
{
  private def showHelp():Unit = 
  {
    println("A utility for converting XLSX file to message bundles")
    println("Author: bugbug0102")
    println
    println(s"Usage: ${classOf[XlsBundle].getName} [xls file]")
    
  }
  
  def main(args:Array[String]):Unit = 
  {
    if(args.length>=1)
    {
      new XlsBundle().convert(args(0))
      
    }else
    {
      showHelp() 
    }
  }
}