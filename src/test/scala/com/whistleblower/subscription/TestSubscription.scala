package com.whistleblower.subscription

import com.whistleblower.agent.Agent
import com.whistleblower.sentries.Sentry
import com.whistleblower.sentries.filesystem.FileSentryEvent
import org.scalatest.{Matchers, FlatSpec}

/**
 * Created by guillermo on 4/08/15.
 */
class TestSubscription extends FlatSpec with Matchers {

  "A subscription" should "push all events to single agent" in {

    val sentry: Sentry[FileSentryEvent]=null
    val agent: Agent[String] = null

    Subscription.all(sentry,agent){ event => event.source.toString }

  }

  "A subscription" should "push only requested events to single agent" in {

    val sentry: Sentry[FileSentryEvent]=null
    val agent: Agent[String] = null

    Subscription.dispatchBefore(sentry)(e => Some(agent))(e => e.toString)

  }

  "A subscription" should "push only requested events to multiple agents" in {

    val sentry: Sentry[FileSentryEvent]=null
    val agent1: Agent[String] = null
    val agent2: Agent[String] = null

    /*Subscription.dispatchBefore(sentry)(e => List(agent1,agent2))(e => e.toString)*/

  }

}
