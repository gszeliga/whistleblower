package com.whistleblower.sentries

import java.util.UUID

/**
 * Created by guillermo on 27/07/15.
 */


trait Sentry[E] {

  type Applicant[E] = E => Unit
  type Ticket = UUID

  def register(to: Applicant[E]): Ticket
  def stop
}
