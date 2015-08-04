package com.whistleblower.sentries.filesystem

import java.nio.file.Files.exists
import java.nio.file.StandardWatchEventKinds.{ENTRY_MODIFY,ENTRY_DELETE}
import java.nio.file.WatchEvent.Kind
import java.nio.file._
import java.util.UUID

import com.typesafe.scalalogging.Logger
import com.whistleblower.sentries.Sentry
import org.slf4j.LoggerFactory

import scala.async.Async.async
import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

/**
 * Created by guillermo on 27/07/15.
 */

trait FileSentryEvent{
  def source: Path
}

case class FileUpdated(source: Path) extends FileSentryEvent
case class FileDeleted(source: Path) extends FileSentryEvent

class FileSentry(private val source: Path)(implicit executor: ExecutionContext) extends Sentry[FileSentryEvent]{

  private val log = Logger(LoggerFactory.getLogger(getClass))
  private val kinds: Array[Kind[_]] = Array(ENTRY_MODIFY,ENTRY_DELETE)

  private val (_watchService, _watchKey) =  Option(source) match {
    case Some(path) if exists(path) => {

      val watchService = FileSystems.getDefault.newWatchService()
      val watchKey = path.register(watchService, kinds)

      (watchService,watchKey)
    }
    case _ => throw new IllegalArgumentException(s"Path '${source}' could not be found. Sentry was not started")
  }

  log.debug(s"[file-sentry] Running watching service for '${source}'")

  private var _applicants = Map.empty[Ticket, Applicant[FileSentryEvent]]

  @volatile
  private var _keepWatching = true

  async {

    def doWatch() {

      if(_keepWatching)
      {
        log.debug("[file-sentry] Polling for events...")

        _watchKey.pollEvents().map(event => {
          val file = event.context().asInstanceOf[Path]
          val path = Paths.get(source.toString, file.toString)

          log.debug(s"[file-sentry] Event '${event.kind}' polled for path '${path}'")

          event.kind match {
            case ENTRY_MODIFY => _applicants.values.foreach(f => f(FileUpdated(path)))
            case ENTRY_DELETE => _applicants.values.foreach(f => f(FileDeleted(path)))
          }

        })

        Thread.`yield`()
        Thread.sleep(500)
        doWatch()
      }
    }

    doWatch()
  }

  def register(to: Applicant[FileSentryEvent]) = {
    val ticket: Ticket = UUID.randomUUID()

    _applicants += (ticket -> to)

    ticket
  }

  def stop = {
    _watchKey.cancel
    _watchService.close
    _keepWatching=false
  }

}

object FileSentry{
  def apply(from: Path)(implicit executor: ExecutionContext) = new FileSentry(from)
}