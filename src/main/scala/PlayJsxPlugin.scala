package com.github.hexx

import sbt._
import sbt.Keys._

object PlayJsxPlugin extends Plugin {
  val jsxEntryPoints = SettingKey[PathFinder]("play-jsx-entry-points")
  val jsxOptions = SettingKey[Seq[String]]("play-jsx-options")

  val JsxCompiler = PlayProject.AssetsCompiler("jsx",
    (_ ** "*.jsx"),
    jsxEntryPoints,
    { (name, min) => name.replace(".jsx", if (min) ".min.js" else ".js") },
    { (jsxFile, options) => com.github.hexx.JsxCompiler.compile(jsxFile, options) },
    jsxOptions
  )

  override val settings = Seq(
    jsxEntryPoints <<= (sourceDirectory in Compile)(_ / "assets" ** "*.jsx"),
    jsxOptions := Seq.empty[String],
    resourceGenerators in Compile <+= JsxCompiler
  )
}
