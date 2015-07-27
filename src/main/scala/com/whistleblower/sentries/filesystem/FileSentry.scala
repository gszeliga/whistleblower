package com.whistleblower.sentries.filesystem

import java.nio.file.{StandardWatchEventKinds, Paths, Path}
import java.util.UUID

import com.whistleblower.sentries.Sentry

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}
import scala.async.Async.{async, await}
import scala.collection.JavaConversions._

/**
 * Created by guillermo on 27/07/15.
 */

class FileSentryEvent(val from: Path)
class FileSentryConfiguration(val from: Path)

class FileSentry(config: FileSentryConfiguration)(implicit executor: ExecutionContext) extends Sentry[FileSentryEvent]{

  private val _watchService = Try(Paths.get(config.from.toUri)) match {
    case Success(path) => path.getFileSystem.newWatchService()
    case _ => throw new IllegalArgumentException(s"Path '${config.from}' could not be found. Sentry was not started")
  }

  private var _applicants = Map.empty[Ticket, Applicant[FileSentryEvent]]

  async {

    def doWatch() = Try(Option(_watchService.poll()))

    doWatch match {
      case Success(Some(watchKey)) => {

        watchKey.pollEvents().map(event => {
          val file = event.context()

          event.kind match {
            case StandardWatchEventKinds.ENTRY_MODIFY =>
          }

        })
      }
      case Success(None) => doWatch
      case Failure(e) =>  {
        //TODO Trace log
        doWatch()
      }
    }
  }

  def report(to: Applicant[FileSentryEvent]) = {
    val ticket: Ticket = UUID.randomUUID()

    _applicants += (ticket -> to)

    ticket
  }
}
