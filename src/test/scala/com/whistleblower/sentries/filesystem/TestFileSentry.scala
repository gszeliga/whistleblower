package com.whistleblower.sentries.filesystem

import java.io.{File, FileOutputStream, IOException}
import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes

import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by guillermo on 30/07/15.
 */
class TestFileSentry extends FlatSpec with Matchers {

  def withPaths(testCode: (Path,Path) => Any) ={

    val sampleDir = Files.createDirectory(Paths.get(System.getProperty("java.io.tmpdir"),"file-sentry"))
    val sampleFile = File.createTempFile("file_sentry","_test",sampleDir.toFile)

    try
    {
      testCode(sampleDir,sampleFile.toPath)
    }
    finally
    {
      Files.walkFileTree(sampleDir, new FileVisitor[Path] {
        def visitFileFailed(file: Path, exc: IOException) = {
          Files.delete(file)
          FileVisitResult.CONTINUE
        }

        def visitFile(file: Path, attrs: BasicFileAttributes) = {
          Files.delete(file)
          FileVisitResult.CONTINUE
        }

        def preVisitDirectory(dir: Path, attrs: BasicFileAttributes) = FileVisitResult.CONTINUE

        def postVisitDirectory(dir: Path, exc: IOException) = {
          if(exc == null)
          {
            Files.delete(dir)
            FileVisitResult.CONTINUE
          }
          else throw exc
        }

      })

    }
  }


  "A File Sentry" should "properly start monitoring a valid directory path" in withPaths{(dir,file) => {

    val sentry = FileSentry(dir)

    try
    {
      sentry shouldNot be(null)
    }
    finally sentry.stop
  }
  }

  it should "fail while trying to monitor an non-existing path" in {

    intercept[IllegalArgumentException]{
      FileSentry(Paths.get("i-dont-exist"))
    }
  }

  it should "notify an accurate event when single update takes place at monitored directory" in withPaths { (dir, file) => {

    val sentry = FileSentry(dir)

    try{
      var event: Option[FileSentryEvent] = None

      sentry.register(e => event = Option(e))

      val stream = new FileOutputStream(file.toFile)
      stream.write(256)
      stream.flush()
      stream.close()

      Thread.sleep(1000l)

      event should be(Some(FileUpdated(file)))
    }
    finally sentry.stop
  }
  }
}
