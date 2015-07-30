package com.whistleblower.subscription

import com.whistleblower.agent.Agent
import com.whistleblower.sentries.Sentry

/**
 * Created by guillermo on 27/07/15.
 */
class Subscription[E,T](val sentry: Sentry[E], val agent: Agent[T])(f: E => T) {

  private val ticket = sentry.register(e => agent.push(f(e)))

}
