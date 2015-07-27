package com.whistleblower.sentries

import java.util.UUID

/**
 * Created by guillermo on 27/07/15.
 */


trait Sentry[M] {

  type Applicant[M] = M => Unit
  type Ticket = UUID

  def report(to: Applicant[M]): Ticket
}
