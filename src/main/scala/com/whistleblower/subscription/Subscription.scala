package com.whistleblower.subscription

import com.whistleblower.agent.Agent
import com.whistleblower.sentries.Sentry

/**
 * Created by guillermo on 27/07/15.
 */

trait Subscription[E,T]{


}

class SubscriptionB[E,T](val sentry: Sentry[E])(val route: E => List[Agent[T]])(f: E => T) extends Subscription[E,T]{

  private val ticket = sentry.register(e => route(e).map(a => a.push(f(e))))

}

class SubscriptionA[E,T](val sentry: Sentry[E])(val route: T => Option[Agent[T]])(f: E => T) extends Subscription[E,T]{

  private val ticket = sentry.register(f andThen (t => route(t).map(a => a.push(t))))

}

object Subscription
{
  def all[E,T](sentry: Sentry[E], agent: Agent[T])(f: E => T) = new SubscriptionB(sentry)(_ => List(agent))(f)
  def dispatchBefore[E,T](sentry: Sentry[E])(d: E => Option[Agent[T]])(f: E => T) = new SubscriptionB(sentry)(d andThen(_.map(a => List(a)).getOrElse(List.empty)))(f)
  /*def dispatchBefore[E,T](sentry: Sentry[E])(d: E => List[Agent[T]])(f: E => T) = new SubscriptionB(sentry)(d)(f)*/
  def dispatchAfter[E,T](sentry: Sentry[E])(f: E => T)(d: T => Option[Agent[T]]) = new SubscriptionA(sentry)(d)(f)
}