package com.github.hexx

import java.io.File
import scala.sys.process._
import scala.util.control.Exception._
import sbt.PlayExceptions.AssetCompilationException
import play.api._
import play.core.jscompile.JavascriptCompiler

object JsxCompiler {
  def compile(file: File, options: Seq[String]) = {
    val js = executeNativeCompiler(Seq("jsx"), file, options)
    val jsopt = executeNativeCompiler(Seq("jsx", "--release", "--optimize", "lto,no-assert,fold-const,return-if,inline,fold-const,array-length"), file, options)
    (js, minify(jsopt, file), Seq(file))
  }

  def minify(js: String, file: File) = {
    catching(classOf[AssetCompilationException]).opt(JavascriptCompiler.minify(js, Some(file.getName)))
  }

  def executeNativeCompiler(command: Seq[String], file: File, options: Seq[String]) = {
    val dir = new File(file.getParentFile.getAbsolutePath)
    val process = Process(command ++ options ++ Seq(file.getName), dir)
    var out = new StringBuilder
    var err = new StringBuilder
    val logger = ProcessLogger((s) => out.append(s + "\n"), (s) => err.append(s + "\n"))
    val exit = process ! logger
    if (exit != 0) {
      val regex = """(?s).*jsx:([0-9]+)\].*""".r
      val regex(line) = err.mkString
      throw AssetCompilationException(Some(file), err.mkString, line.toInt, 0)
    }
    out.mkString
  }
}
