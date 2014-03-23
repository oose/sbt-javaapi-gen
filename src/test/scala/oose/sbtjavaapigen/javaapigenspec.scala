package oose.sbtjavaapigen

import oose.sbtjavaapigen.generator._
import org.specs2.mutable._

class  JavaApiGenSpec extends SpecificationWithJUnit {

  def wrapInPackage(packageName: String)(c: String) = (packageName + "." + c)

  val io = Set(
      "File") map wrapInPackage ("java.io")

  val (warnings, output)  = Generator(Seq.empty,
    io)
    
  "The API Generator" should {
    "run without warnings" in {
      warnings must beEmpty
    }
    
    "create output" in {
      output must not beEmpty
    }
    
    "create the correct package" in {
      output must contain("package java.io")
    }
    
    "contain an api object" in {
      output must contain("object api")
    }
    
    "create the correct class" in {
      output must contain("implicit class ScalaFile")
    }
    
    "treats getClass correctly" in {
      output must contain("def Class =")
    }
  }
  
}