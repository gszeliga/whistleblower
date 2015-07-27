package com.whistleblower.agent

/**
 * Created by guillermo on 27/07/15.
 */
trait Agent[T] {
  def push(v: T)
}
